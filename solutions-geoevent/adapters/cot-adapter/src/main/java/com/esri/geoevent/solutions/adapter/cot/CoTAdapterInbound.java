package com.esri.geoevent.solutions.adapter.cot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.Property;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Geometry;

public class CoTAdapterInbound extends InboundAdapterBase
{
	private static final Log log = LogFactory.getLog(CoTAdapterInbound.class);

	private static final int GCS_WGS_1984 = 4326;
	private String guid;

	// this ArrayList contains ALL the type defs but it must be used differently
	// depending on what you are looking for.
	private ArrayList<CoTTypeDef> coTTypeMap;

	private ByteArrayOutputStream jsonBuffer;
	private JsonGenerator generator;
	private JsonFactory factory;
	private boolean firstVertex;
	private double cachedLat;
	private double cachedLon;
	private double cachedHae;
	private static final int CAPACITY = 1 * 1024 * 1024;
	private HashMap<String,String> buffers = new HashMap<String,String>();
	private int maxBufferSize;

	private SAXParserFactory saxFactory;
	private SAXParser saxParser;
	private MessageParser messageParser;



	public CoTAdapterInbound(AdapterDefinition adapterDefinition, String guid) throws ConfigurationException, ComponentException
	{
		super(adapterDefinition);

		this.guid = guid;
		CoTAdapterServiceInbound cotas = (CoTAdapterServiceInbound) adapterDefinition;

		//this.coTTypeMap = CoTUtilities.getCoTTypeMap(this.getClass().getResourceAsStream( cotas.getPropertyDefinitions().get("CoT Types Path").getDefaultValue().toString()));

		messageParser = new MessageParser(this);
		saxFactory = SAXParserFactory.newInstance();
		try
		{
			saxParser = saxFactory.newSAXParser();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			saxParser = null;
		} catch (SAXException e)
		{
			e.printStackTrace();
			saxParser = null;
		}

	}

	@Override
	public void afterPropertiesSet()
	{
		if (hasProperty(CoTAdapterServiceInbound.MAXIMUM_BUFFER_SIZE_LABEL))
		{
			Property bufSizeProperty = getProperty(CoTAdapterServiceInbound.MAXIMUM_BUFFER_SIZE_LABEL);
			if( bufSizeProperty != null )
			{
				int maxBufferSize = (Integer)bufSizeProperty.getValue();
				if( maxBufferSize <= 0 )
				{
					log.error("Cannot set the maximum buffer size to " + maxBufferSize );
				}
				else
					this.maxBufferSize = maxBufferSize;
			}
		}
		if (hasProperty(CoTAdapterServiceInbound.COT_TYPES_PATH_LABEL))
		{
			Property cotTypesPathProperty = getProperty(CoTAdapterServiceInbound.COT_TYPES_PATH_LABEL);
			if( cotTypesPathProperty != null )
			{
				String userDefinedPath = cotTypesPathProperty.getValueAsString();
				if( userDefinedPath != null && (!userDefinedPath.equals("")))
				{
					try
					{
						this.coTTypeMap = CoTUtilities.getCoTTypeMap(new FileInputStream(((Property) cotTypesPathProperty).getValueAsString()));
						log.info("CotTypes.xml path will be set to: "	+ userDefinedPath);
					} catch (Exception e)
					{
						this.coTTypeMap = null;
						log.error("Problem loading the user-specified CoTTypes.xml file.",e);
					}
				}
			}
		}
		if( this.coTTypeMap == null )
		{
			try
			{
				String defaultPath = "CoTTypes/CoTtypes.xml";
				this.coTTypeMap = CoTUtilities.getCoTTypeMap(this.getClass().getClassLoader().getResourceAsStream(defaultPath));
				log.info("Default CoTtypes.xml definitions were loaded successfully.");
			} catch (Exception e1)
			{
				log.error("Problem loading the default CoTTypes.xml file.",e1);
			}
		}

	}

	@Override
	public void receive(ByteBuffer buf, String channelId)
	{

		buf.mark();
		int size = buf.remaining();
		if (size < 1)
			return;
//				byte[] data = new byte[size];
//				buf.get(data, 0, size);

		//		System.out.println(" \n");
		//		System.out.println("Read " + size + " bytes");

//				String xml = new String(data);
//				parseUsingDocument(xml,channelId);


		parseUsingStream(buf);
	}

	
	private void parseUsingStream( ByteBuffer bb )
	{
		try
		{
			int remaining = bb.remaining();
			System.out.println("buf-copy remaining: "+ bb.remaining());
			if( remaining <= 0 )
				return;
			byte[] bytes = new byte[remaining];
			bb.get(bytes);
			saxParser.parse( new ByteArrayInputStream(bytes), messageParser);
			bytes = null;
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void parseUsingDocument( String xml, String channelId )
	{
		if( buffers.containsKey(channelId) )
		{
			String temp = buffers.remove(channelId);
			temp = temp + xml;
			if( temp.length() > maxBufferSize )
			{
				log.error("The size of the incoming xml message exceeds the configured maximum buffer size of "+maxBufferSize+".  The buffer contents will be discarded to make room for incoming data.");
				temp = scanForEvent(temp);  // Look for something that looks like a new message.
				if( temp == null ) // If we didn't find something that looks like a new message, just start with the xml that was passed in (discarding the buffered data).
					temp = xml;
			}
			xml = temp;
		}
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource source = new InputSource();
			source.setCharacterStream(new StringReader(xml));
			Document doc = db.parse(source);
			NodeList nodeList = doc.getElementsByTagName("event");

			if (nodeList != null)
			{
				for( int i = 0; i < nodeList.getLength(); i++ )
				{
					GeoEvent msg = geoEventCreator.create(guid);
					Element e = (Element) nodeList.item(i);

					String version = e.getAttribute("version");
					if (!version.isEmpty())
						msg.setField(0, Double.valueOf(version));

					msg.setField(1, e.getAttribute("uid"));

					String type = e.getAttribute("type");

					msg.setField(2, type);
					msg.setField(3, CoTUtilities.getSymbolFromCot(type));
					//System.out.println("2525b from type: "+ CoTUtilities.getSymbolFromCot(type));

					//System.out.println("type: " + e.getAttribute("type") " -> " + convertType(e.getAttribute("type")));
					msg.setField(4, convertType(e.getAttribute("type")));

					//System.out.println("how: " + e.getAttribute("how") + " -> "+ convertHow(e.getAttribute("how")));
					msg.setField(5, e.getAttribute("how"));
					msg.setField(6, convertHow(e.getAttribute("how")));

					msg.setField(7, CoTUtilities.parseCoTDate(e.getAttribute("time")));
					msg.setField(8, CoTUtilities.parseCoTDate(e.getAttribute("start")));
					msg.setField(9, CoTUtilities.parseCoTDate(e.getAttribute("stale")));

					msg.setField(10, e.getAttribute("access"));
					msg.setField(11, e.getAttribute("opex"));
					msg.setField(12, convertOpex(e.getAttribute("opex")));
					//System.out.println("opex: " + e.getAttribute("opex" + " is a:  " + convertOpex(e.getAttribute("opex")));

					msg.setField(13, e.getAttribute("qos"));
					msg.setField(14, convertQos(e.getAttribute("qos")));
					//System.out.println("qos: " + e.getAttribute("qos") + " -> " convertQos(e.getAttribute("qos")));

					NodeList points = e.getElementsByTagName("point");
					if (points.getLength() > 0)
					{
						Element pointElement = (Element) points.item(0);
						Geometry geom = createGeometry(pointElement);
						msg.setField( 15, geom.toJson() );
						System.out.println("pure geom: "+geom.toString());
					}

					GeoEventDefinition ged = msg.getGeoEventDefinition();
					traverseBranch( findChildNodes(e, "detail" ).get(0), msg, ged.getFieldDefinition("detail") );

					geoEventListener.receive( msg );
					System.out.println(msg.getField(1).toString() + " --" + msg.getField(3).toString() + " -- "+ msg.getField(7).toString());

				}
			}
		} catch (Exception e)
		{
			if( e.getMessage() != null && e.getMessage().equals("XML document structures must start and end within the same entity.") )
			{
				if( xml != null )
					buffers.put(channelId, xml);
				return;
			}
			log.error("Error while parsing CoT message.  For details, set log level to DEBUG.",e);
			log.debug("Error while parsing the message : "+xml);
			return;
		}
	}

	/*
	 * Called from the MessageParser when the Event tag is closed.  
	 */
	public void msgFromStream(String how,
			String opex,
			String qos,
			String type,
			String uid,
			String version,
			String stale,
			String start,
			String time,
			String access,
			String detail,
			String point) throws MessagingException, ParserConfigurationException, SAXException, IOException, FieldException{
		GeoEvent msg = geoEventCreator.create(guid);
		try{
			System.out.println("version");
			msg.setField(0, Double.valueOf(version));	
		}catch (Exception e){
			msg.setField(0, fixVersionNumber(start));
		}
		msg.setField(1, uid);
		msg.setField(2, type);
		msg.setField(3, CoTUtilities.getSymbolFromCot(type));
		msg.setField(4, convertType(type));
		msg.setField(5, how);
		msg.setField(6, convertHow(how));
		try {
			msg.setField(7, CoTUtilities.parseCoTDate(time));
			msg.setField(8, CoTUtilities.parseCoTDate(start));
			msg.setField(9, CoTUtilities.parseCoTDate(stale));
		} catch (Exception e) {
			e.printStackTrace();
		}

		msg.setField(10, access);
		msg.setField(11, opex);
		msg.setField(12, convertOpex(opex));
		msg.setField(13, qos);
		msg.setField(14, convertQos(qos));
		NodeList points=unpackXML(point, "point");

		if (points.getLength() > 0)
		{
			Element pointElement = (Element) points.item(0);
			Geometry geom = createGeometry(pointElement);
			msg.setField( 15, geom.toJson() );
		}

		NodeList details=unpackXML(detail,"eventWrapper");
		GeoEventDefinition ged = msg.getGeoEventDefinition();
		try {
			traverseBranch( findChildNodes(details.item(0), "detail" ).get(0), msg, ged.getFieldDefinition("detail") );
		} catch (FieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		geoEventListener.receive( msg );
		System.out.println("* "+msg.getField(1).toString() + " .. " + msg.getField(3).toString() + 
				" .. "+ msg.getField(7).toString());

	}

	private NodeList unpackXML(String xml,String targetTag) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource source = new InputSource();
		source.setCharacterStream(new StringReader(xml));
		Document doc = db.parse(source);
		NodeList nodeList = doc.getElementsByTagName(targetTag);
		return nodeList;
	}

	/*
	 * fixVersionNumber will be called if there is an exception when evaluating the Version Number for 
	 * msg field 0.  CoT Developer's guide page 13 states:  "the only change 
	 * between the version 1.0 CoT schema and the version 2.0 schema was the addition of the third time 
	 * field, start. Prior to that there were only two times."  
	 */
	private Double fixVersionNumber(String start) {
		if(start == null){
			return 1.0;

		}else{
			return 2.0;
		}
	}

	private String scanForEvent(String buffer)
	{
		int index = buffer.indexOf( "<?xml version'", 1 );
		if( index > 0 )
			return buffer.substring(index);
		return null;
	}

	private void traverseBranch(Node node, FieldGroup fieldGroup, FieldDefinition fieldDefinition) throws FieldException
	{
		if( node == null ) return;
		//System.out.println("Examining node named \""+node.getNodeName()+"\"");
		FieldType fieldType = fieldDefinition.getType();
		switch( fieldType )
		{
		case Group:
			FieldGroup childFieldGroup = fieldGroup.createFieldGroup(fieldDefinition.getName());
			fieldGroup.setField( fieldDefinition.getName(), childFieldGroup);
			for( FieldDefinition childFieldDefinition : fieldDefinition.getChildren() )
			{
				String childName = childFieldDefinition.getName();
				List<Node> childNodes = findChildNodes( node, childName );
				if( childNodes.size() > 0 )
				{
					for( Node childNode : childNodes )
						traverseBranch( childNode, childFieldGroup, childFieldDefinition );
				}
				else
					traverseBranch( node, childFieldGroup, childFieldDefinition );
				//List<Node> textNode = findChildNodes( node, "#text");
				//String value = textNode.get(0).getNodeValue();
				//if (value != null)
				//	fieldGroup.setField(fieldDefinition.getName(), value);
			}
			break;
		case String:
			if (fieldDefinition.getName().equals("#text"))
			{
				String value = node.getNodeValue();
				if (value != null)
					fieldGroup.setField(fieldDefinition.getName(), value);
			}
			else
			{
				String value = getAttribute( node, fieldDefinition.getName() );
				if( value != null )
					fieldGroup.setField(fieldDefinition.getName(), value);
			}
			break;
		case Integer:
			String value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
				fieldGroup.setField(fieldDefinition.getName(), new Integer(value));
			break;
		case Double:
			value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
				fieldGroup.setField(fieldDefinition.getName(), new Double(value));
			break;
		case Boolean:
			value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
				fieldGroup.setField(fieldDefinition.getName(), new Boolean(value));
			break;
		case Date:
			value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
			{
				Date date = new Date();
				try
				{
					date = CoTUtilities.parseCoTDate(value);
				}catch(Exception ex)
				{
				}
				fieldGroup.setField(fieldDefinition.getName(), date);
			}
			break;
		case Geometry:
			Geometry geometry = createGeometry(node);
			if( geometry != null )
				fieldGroup.setField(fieldDefinition.getName(), geometry.toJson());
			break;
		case Long:
			value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
				fieldGroup.setField(fieldDefinition.getName(), new Long(value));
			break;
		case Short:
			value = getAttribute( node, fieldDefinition.getName() );
			if( value != null )
				fieldGroup.setField(fieldDefinition.getName(), new Integer(value));
			break;
		}
	}

	private List<Node> findChildNodes( Node node, String childName)
	{
		ArrayList<Node> children = new ArrayList<Node>();
		NodeList childNodes = node.getChildNodes();
		for( int i = 0; i < childNodes.getLength(); i++ )
		{
			Node child = childNodes.item(i);
			if( child.getNodeName().equals(childName) )
				children.add(child);
		}
		return children;
	}

	private String getAttribute( Node node, String attributeName )
	{
		NamedNodeMap attributes = node.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++ )
		{
			Node attributeNode = attributes.item(i);
			if( attributeNode.getNodeName().equals(attributeName))
			{
				return attributeNode.getNodeValue();
			}
		}
		return null;
	}

	private String convertQos(String type) {

		StringBuilder sb = new StringBuilder();
		try{

			Matcher matcher;
			for (CoTTypeDef cd : this.coTTypeMap) {
				if (cd.isPredicate() && cd.getValue().startsWith("q.")) {
					Pattern pattern = Pattern.compile(cd.getKey());
					matcher = pattern.matcher(type);
					if (matcher.find()) {

						sb.append(cd.getValue() + " ");

					}

				}

			}
		}catch (Exception e) {
			//e.printStackTrace();
			log.error("null pointer exception while converting Qos.");
		}
		return this.filterOutDots(sb.toString());

	}

	private String convertOpex(String type) {

		Matcher matcher;
		try {


			for (CoTTypeDef cd : this.coTTypeMap) {
				if (cd.isPredicate() && cd.getValue().startsWith("o.")) {
					Pattern pattern = Pattern.compile(cd.getKey());
					matcher = pattern.matcher(type);
					if (matcher.find()) {

						return this.filterOutDots(cd.getValue());

					}

				}

			}
		}catch (Exception e) {
			//e.printStackTrace();
			log.error("null pointer exception while converting Opex.");
		}
		// no match was found
		return "";

	}

	private String convertHow(String type) {

		Matcher matcher;
		try{
			for (CoTTypeDef cd : this.coTTypeMap) {
				if (!cd.isPredicate()) {
					Pattern pattern = Pattern.compile(cd.getKey());
					matcher = pattern.matcher(type);
					if (matcher.find()) {

						return this
								.filterOutDots(appendToHow(type) + cd.getValue());
					}
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
			log.error("null pointer exception while converting How.");
		}
		// no match was found
		return "";

	}

	private String appendToHow(String type) {
		Matcher matcher;
		StringBuffer sb = new StringBuffer();
		for (CoTTypeDef cd : this.coTTypeMap) {
			// now only consider the value if it is:
			// 1. a predicate
			if (cd.isPredicate()) {
				Pattern pattern = Pattern.compile(cd.getKey());
				matcher = pattern.matcher(type);
				if (matcher.find()) {
					// now only append the value if it is:
					// 1. not prefixed with a dot notation
					if (cd.getValue().startsWith("h.")) {
						sb.append(cd.getValue() + " ");
					}
				}
			}
		}

		return sb.toString();
	}

	private String convertType(String type) {

		Matcher matcher;
		try{
			for (CoTTypeDef cd : this.coTTypeMap) {
				if (!cd.isPredicate()) {
					Pattern pattern = Pattern.compile(cd.getKey());
					matcher = pattern.matcher(type);
					if (matcher.find()) {

						return this.filterOutDots(appendToType(type)
								+ cd.getValue());

					}
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
			log.error("null pointer exception while converting Type.");
		}
		// no match was found
		return "";
	}

	private String appendToType(String type) {
		Matcher matcher;
		StringBuffer sb = new StringBuffer();
		for (CoTTypeDef cd : this.coTTypeMap) {
			// now only consider the value if it is:
			// 1. a predicate
			// 2. not prefixed with a dot notation
			if (cd.isPredicate()
					&& !(cd.getValue().startsWith("h.")
							|| cd.getValue().startsWith("t.")
							|| cd.getValue().startsWith("r.")
							|| cd.getValue().startsWith("q.") || cd.getValue()
							.startsWith("o."))) {
				Pattern pattern = Pattern.compile(cd.getKey());
				matcher = pattern.matcher(type);
				if (matcher.find()) {

					sb.append(cd.getValue() + " ");

				}
			}
		}

		return sb.toString();
	}

	/*
	 * The following method's only purpose is to take out the dot notations eg:
	 * "h.whatever" will indicate that "whatever" is in the "how" category
	 */
	private String filterOutDots(String s) {
		String sStageOne = s.replace("h.", "").replace("t.", "")
				.replace("r.", "").replace("q.", "").replace("o.", "");

		String[] s2 = sStageOne.trim().split(" ");

		ArrayList<String> l1 = new ArrayList<String>();
		for (String item : s2) {
			l1.add(item);

		}
		ArrayList<String> l2 = new ArrayList<String>();

		Iterator<String> iterator = l1.iterator();

		while (iterator.hasNext()) {
			String o = (String) iterator.next();
			if (!l2.contains(o))
				l2.add(o);
		}

		StringBuffer sb = new StringBuffer();
		for (String item : l2) {
			sb.append(item);
			sb.append(" ");

		}

		return sb.toString().trim().toLowerCase();
	}

	// temp code to show the details field
	public static String elementToString(Node n) {

		String name = n.getNodeName();
		short type = n.getNodeType();

		if (Node.CDATA_SECTION_NODE == type) {
			return "<![CDATA[" + n.getNodeValue() + "]]&gt;";
		}

		if (name.startsWith("#")) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append('<').append(name);

		NamedNodeMap attrs = n.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				sb.append(' ').append(attr.getNodeName()).append("=\"")
				.append(attr.getNodeValue()).append("\"");
			}
		}

		String textContent = null;
		NodeList children = n.getChildNodes();

		if (children.getLength() == 0) {
			if ((textContent = n.getTextContent()) != null
					&& !"".equals(textContent)) {
				sb.append(textContent).append("</").append(name).append('>');
			} else {
				sb.append("/>").append('\n');
			}
		} else {
			sb.append('>').append('\n');
			boolean hasValidChildren = false;
			for (int i = 0; i < children.getLength(); i++) {
				String childToString = elementToString(children.item(i));
				if (!"".equals(childToString)) {
					sb.append(childToString);
					hasValidChildren = true;
				}
			}

			if (!hasValidChildren
					&& ((textContent = n.getTextContent()) != null)) {
				sb.append(textContent);
			}

			sb.append("</").append(name).append('>');
		}

		return sb.toString();
	}

	private Geometry createGeometry(Node node)
	{
		if( node.getNodeName().equals("point") )
		{
			String lat = getAttribute(node,"lat");
			String lon = getAttribute(node,"lon");
			String hae = getAttribute(node,"hae");
			if (!lat.isEmpty() && !lon.isEmpty()) {
				if (hae.isEmpty())
					return spatial.createPoint(
							Double.valueOf(lon),
							Double.valueOf(lat),
							GCS_WGS_1984);
				else
					return spatial.createPoint(
							Double.valueOf(lon),
							Double.valueOf(lat),
							Double.valueOf(hae),
							GCS_WGS_1984);
			}
		}

		if( node.getNodeName().equals("shape") )
		{
			try
			{
				List<Node> polylines = findChildNodes( node, "polyline" );
				for( Node polyline : polylines)
				{
					startNewPolygon();
					List<Node> vertices = findChildNodes( polyline, "vertex" );
					for( Node vertex : vertices )
					{
						double lat = 0;
						double lon = 0;
						double hae = 0;
						String s = null;

						s = getAttribute( vertex, "lat");
						if( s != null )
							lat = Double.parseDouble( s );

						s = getAttribute( vertex, "lon");
						if( s != null )
							lon = Double.parseDouble( s );

						s = getAttribute( vertex, "hae");
						if( s != null )
							hae = Double.parseDouble( s );

						addVertexToPolygon( lat, lon, hae );

					}
					String geometryString = closePolygon();
					String jsonString = geometryString.substring(0, geometryString.length()-1) + ",\"spatialReference\":{\"wkid\":"+GCS_WGS_1984+"}}";
					//System.out.println("json string = \""+jsonString+"\"");
					Geometry geometry = spatial.fromJson(jsonString);
					return geometry;
				}
			}catch(Exception ex)
			{
				generator = null;
				return null;
			}
		}
		return null;
	}

	private void startNewPolygon() throws IOException
	{
		if( generator == null )
			initializeJsonGenerator();
		generator.writeStartObject();
		generator.writeArrayFieldStart("rings");
		generator.writeStartArray();
		firstVertex = true;
	}

	private void initializeJsonGenerator() throws IOException
	{
		factory = new JsonFactory();
		jsonBuffer = new ByteArrayOutputStream(CAPACITY);
		generator = factory.createJsonGenerator(jsonBuffer);
	}

	private void addVertexToPolygon(double lat, double lon, double hae) throws JsonGenerationException, IOException
	{
		if( firstVertex )
		{
			firstVertex = false;
			cachedLat = lat;
			cachedLon = lon;
			cachedHae = hae;
			firstVertex = false;
		}

		generator.writeStartArray();
		generator.writeNumber(lon);
		generator.writeNumber(lat);
		//generator.writeNumber(hae);
		generator.writeEndArray();
	}

	private String closePolygon() throws IOException
	{
		if(!firstVertex)
			addVertexToPolygon( cachedLat, cachedLon, cachedHae );
		generator.writeEndArray();
		generator.writeEndArray();
		generator.writeEndObject();
		generator.flush();
		String jsonString = jsonBuffer.toString();
		jsonBuffer.reset();
		return jsonString;
	}

	@Override
	protected GeoEvent adapt(ByteBuffer buffer, String channelId)
	{
		// Do nothing, this will not be called since we have overloaded the receive() function.
		//return null;
//		buffer.mark();
//		int size = buffer.remaining();
//		
//
//		parseUsingStream(buffer);
		
		return null;
	}

}
