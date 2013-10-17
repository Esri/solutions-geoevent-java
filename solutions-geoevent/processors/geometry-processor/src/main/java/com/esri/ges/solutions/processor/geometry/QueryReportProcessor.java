package com.esri.ges.solutions.processor.geometry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.joda.time.DateTime;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.datastore.agsconnection.ArcGISServerConnection;
import com.esri.ges.datastore.agsconnection.ArcGISServerType;
import com.esri.ges.datastore.agsconnection.Field;
import com.esri.ges.datastore.agsconnection.Layer;
import com.esri.ges.manager.datastore.agsconnection.ArcGISServerConnectionManager;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.Spatial;

public class QueryReportProcessor extends GeoEventProcessorBase {

	private static final Log LOG = LogFactory.getLog(RangeFanProcessor.class);
	public Spatial spatial;
	public GeoEventDefinitionManager manager;
	public ArcGISServerConnectionManager connectionManager;
	public Messaging messaging;
	private SpatialReference srIn;
	private SpatialReference srBuffer;
	private SpatialReference srOut;
	private ArrayList<Object>queries = new ArrayList<Object>();
	private HashMap<String, Object>responseMap = new HashMap<String, Object>();
	com.esri.core.geometry.Geometry inGeometry;
	public QueryReportProcessor(GeoEventProcessorDefinition definition, Spatial s, GeoEventDefinitionManager m, ArcGISServerConnectionManager cm, Messaging msg)
			throws ComponentException {
		super(definition);
		spatial = s;
		manager = m;
		connectionManager = cm;
		messaging = msg;
		geoEventMutator= true;
		
	}

	@Override
	public GeoEvent process(GeoEvent ge) throws Exception {
		CreateQueryMap();
		double radius = (Double)properties.get("radius").getValue();
		String units = properties.get("units").getValue().toString();
		int inwkid = (Integer) properties.get("wkidin").getValue();
		int outwkid = (Integer) properties.get("wkidout").getValue();
		int bufferwkid = (Integer) properties.get("wkidbuffer").getValue();
		srIn = SpatialReference.create(inwkid);
		srBuffer = SpatialReference.create(bufferwkid);
		srOut = SpatialReference.create(outwkid);

		Geometry geo = ge.getGeometry();
		
		Geometry inGeo = null;
		if(properties.get("geosrc").getValueAsString().equals("Buffer"))
		{
			inGeo = constructBuffer(geo,radius,units);
		}
		else if(properties.get("geosrc").getValueAsString().equals("Event Definition"))
		{
			
			String eventfld = properties.get("geoeventdef").getValue().toString();
			String[] arr = eventfld.split(":");
			String geostr = (String)ge.getField(arr[1]);
			inGeo = constructGeometryFromString(geostr);
		}
		else
		{
			inGeo = geo;
		}
		String jsonGeo =  inGeo.toJson();
		String geotype = GeometryUtility.parseGeometryType(inGeo.getType());
		ExecuteRestQueries(jsonGeo, geotype);
		String timestamp = "";
		if((Boolean)properties.get("usetimestamp").getValue())
	       {
			String eventfld = properties.get("timestamp").getValueAsString();
			String[] arr = eventfld.split(":");
			timestamp = ge.getField(arr[1]).toString();
	       }
		DateTime dt = DateTime.now();
		String ts = ((Integer) dt.getYear()).toString()
				+ ((Integer) dt.getMonthOfYear()).toString()
				+ ((Integer) dt.getDayOfMonth()).toString()
				+ ((Integer) dt.getHourOfDay()).toString()
				+ ((Integer) dt.getMinuteOfHour()).toString()
				+ ((Integer) dt.getSecondOfMinute()).toString();
		String file = properties.get("filename").getValueAsString() + ts + ".html";
	    
		ParseResponses(timestamp, file);
		String host = properties.get("host").getValueAsString();
		String url = "http://" + host + ":6180/geoevent/assets/reports/" + file;
		GeoEventDefinition geoDef = ge.getGeoEventDefinition();	
		List<FieldDefinition>fds = Arrays.asList(((FieldDefinition)new DefaultFieldDefinition("url", FieldType.String)));
		GeoEventDefinition edOut = geoDef.augment(fds);
		edOut.setOwner(getId());
		manager.addTemporaryGeoEventDefinition(edOut, true);
		GeoEventCreator geoEventCreator = messaging.createGeoEventCreator();
		GeoEvent geOut = geoEventCreator.create(edOut.getGuid(), new Object[] { ge.getAllFields(), url }); 
		geOut.setProperty(GeoEventPropertyName.TYPE, "message");
		geOut.setProperty(GeoEventPropertyName.OWNER_ID, getId());
		geOut.setProperty(GeoEventPropertyName.OWNER_ID, definition.getUri());
		for (Map.Entry<GeoEventPropertyName, Object> property : ge.getProperties())
	        if (!geOut.hasProperty(property.getKey()))
	          geOut.setProperty(property.getKey(), property.getValue());
		queries.clear();
		responseMap.clear();
		return geOut;

	}
	
	private com.esri.ges.spatial.Geometry constructGeometryFromString(String geoString) throws GeometryException
	{
		String[] pairs = geoString.split(" ");
		
		Polygon polygon = new Polygon();
		Boolean firstit = true;
		for(String coords: pairs)
		{
			
			String[] tuple = coords.split(",");
			Double x = Double.parseDouble(tuple[0]);
			Double y = Double.parseDouble(tuple[1]);
			Point p = new Point(x,y);
			Double z = Double.NaN;
			if (tuple.length>2)
			{
				z = Double.parseDouble(tuple[2]);
				p.setZ(z);
			}
			if(firstit)
			{
				polygon.startPath(p);
				firstit=false;
			}
			else
			{
				polygon.lineTo(p);
			}
		}
		polygon.closeAllPaths();
		String json = GeometryEngine.geometryToJson(srIn, polygon);
		return spatial.fromJson(json);
	}
	private com.esri.ges.spatial.Geometry constructBuffer(Geometry geo, double radius, String units) throws GeometryException, JsonParseException, IOException
	{
		
		String jsonIn = geo.toJson();
		JsonFactory jf = new JsonFactory();
		JsonParser jp = jf.createJsonParser(jsonIn);
		MapGeometry mgeo = GeometryEngine.jsonToGeometry(jp);
		com.esri.core.geometry.Geometry geoIn= mgeo.getGeometry();
		UnitConverter uc = new UnitConverter();
		String cn = uc.findConnonicalName(units);
		int unitout = uc.findWkid(cn);
		Unit  u = new LinearUnit(unitout);
		inGeometry = GeometryEngine.project(geoIn, srIn, srBuffer);
		com.esri.core.geometry.Geometry buffer = GeometryEngine.buffer(inGeometry, srBuffer, radius, u);
		com.esri.core.geometry.Geometry bufferout = GeometryEngine.project(buffer, srBuffer, srOut);
		String json = GeometryEngine.geometryToJson(srOut, bufferout);
		return spatial.fromJson(json);
		
	}
	public void CreateQueryMap()
	{
		Collection<ArcGISServerConnection> serviceConnections = this.connectionManager
				.getArcGISServerConnections();

		Iterator<ArcGISServerConnection> it = serviceConnections.iterator();
		ArcGISServerConnection conn;
		while (it.hasNext()) {
			conn = it.next();
			
			//String connName = conn.getName();
			String[] folders = conn.getFolders();

			URL url = conn.getUrl();
			
			String baseUrl = url.getProtocol() +"://"+ url.getHost() + ":" + url.getPort()
					+ url.getPath() + "rest/services/";
			//HashMap<String, Object> folderMap = new HashMap<String, Object>();
			for (int i = 0; i < folders.length; ++i) {
				String path = baseUrl + folders[i] + "/";
				String folder = folders[i];
				String[] fservices = conn.getFeatureServices(folder);
				//HashMap<String,Object>serviceMap = new HashMap<String,Object>();
				for (int j = 0; j < fservices.length; ++j) {
					String fs = fservices[j];
					path += fs + "/FeatureServer/";
					String fqService = folder + "_" + fs;
					String pdName = "use_" + fqService;
					pdName=pdName.replace(" ", "_");
					if ((Boolean) properties.get(pdName).getValue()) {
						
						ArrayList<Layer> layers = (ArrayList<Layer>) conn
								.getLayers(folder, fs,
										ArcGISServerType.FeatureServer);
						//HashMap<String, Object> layerMap = new HashMap<String, Object>();
						
						
						for (int k = 0; k < layers.size(); ++k) {
							HashMap<String, Object> query = new HashMap<String, Object>();
							HashMap<String, String> fieldMap = new HashMap<String, String>();
							String fldsString = "";
							Field[] fields = conn.getFields(folder, fs, k, ArcGISServerType.FeatureServer);
							Boolean usingDist=false;
							String curPath="";
							String restpath="";
							String lyrHeaderCfg = "";
							String distToken="";
							String distUnits="";
							String wc="";
							String itemConfig = "";
							String curLyr = layers.get(k).getName();
							String lyrName = fqService + "_"
									+ ((Integer) k).toString();
							lyrName = lyrName.replace(" ", "_");
							if ((Boolean) properties.get(lyrName).getValue()) {
								curPath = path + ((Integer) k).toString();
								restpath = path + ((Integer) k).toString() + "/query?";
								wc = properties.get(lyrName + "_whereclause")
										.getValueAsString();
								lyrHeaderCfg = properties.get(lyrName + "_header").getValueAsString();
								usingDist = (Boolean)properties.get(lyrName + "_calcDistance").getValue();
								if(usingDist)
								{
									distToken=properties.get(lyrName + "_dist_token").getValueAsString();
									distUnits=properties.get(lyrName + "_dist_units").getValueAsString();
								}
								
								
								itemConfig = properties.get(lyrName + "_config").getValueAsString();
								
								Boolean first = true;
								for (int l = 0; l < fields.length; ++l) {
									String fld = fields[l].getName();
									
									String fldPropName = lyrName + fld;
									fldPropName = fldPropName.replace(" ", "_");
									
									if ((Boolean) properties.get(fldPropName)
											.getValue()) {
										if (!first) {
											fldsString += ",";
										} else {
											first = false;
										}
										fldsString += fld;
										String fldToken = fldPropName
												+ "_token";
										String token = properties.get(fldToken)
												.getValueAsString();
										fieldMap.put(fld, token);
									}
								}
								query.put("restpath", restpath);
								query.put("path", curPath);
								query.put("whereclause", wc);
								query.put("fields", fldsString );
								query.put("outfields", fields);
								query.put("tokenMap", fieldMap);
								query.put("headerconfig", lyrHeaderCfg);
								query.put("usingdist", usingDist);
								query.put("distunits", distUnits);
								query.put("disttoken", distToken);
								query.put("itemconfig", itemConfig);
								query.put("layer", curLyr);
								UUID uid = UUID.randomUUID();
								query.put("id", uid);
								queries.add(query);
								//layerMap.put(layers.get(k).getName(), fieldMap);
							}
							
						}
						
					}
					
					
				}
				//folderMap.put(folder, serviceMap);
			}
			//connmap.put(connName,folderMap);
		}
		
	}
	/*private void ExecuteQueries(String jsonGeometry, String geoType) throws Exception
	{
		JsonFactory jf = new JsonFactory();
		JsonParser jp=null;
		try {
			jp = jf.createJsonParser(jsonGeometry);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MapGeometry mgeo = GeometryEngine.jsonToGeometry(jp);
		com.esri.core.geometry.Geometry geo = mgeo.getGeometry();
		for (int i = 0; i < queries.size(); ++i) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> query = (HashMap<String, Object>) queries
					.get(i);
			String path = (String) query.get("path");
			
			Query q = new Query();
			String wc = (String)query.get("whereclause");
			String fields = (String)query.get("fields");
			//String[] outfields = fields.split(",");
			//String[] outfields=new String[]{"*"};
			q.setOutFields(new String[]{"*"});
			if(wc.length()>0)
			{
				q.setWhere(wc);
			}
			else
			{
				q.setWhere("objectid>0");
			}
			//q.setGeometry(geo);
			//q.setReturnZ(true);
			//q.setReturnGeometry(true);
			//q.setSpatialRelationship(SpatialRelationship.INTERSECTS);
			try {
				QueryTask qt = new QueryTask(path);
				FeatureSet fset = qt.execute(q);
				HashMap<String, Object> tuple = new HashMap<String, Object>();
				@SuppressWarnings("unchecked")
				HashMap<String, String> tokenMap = (HashMap<String, String>) query
						.get("tokenMap");
				String lyr = (String)query.get("layer");
				String itemConfig = (String) query.get("itemconfig");
				String id = (String) query.get("id");
				tuple.put("fset",  fset);
				tuple.put("tokenmap", tokenMap);
				tuple.put("config", itemConfig);
				tuple.put("layer", lyr);
				responseMap.put(id.toString(), tuple);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e);
				throw e;
			}
			
			qt.executeAsync(q, new CallbackListener<FeatureSet>()
			{

				@Override
				public void onCallback(FeatureSet fset) {
					HashMap<String, Object> tuple = new HashMap<String, Object>();
					@SuppressWarnings("unchecked")
					HashMap<String, String> tokenMap = (HashMap<String, String>) query
							.get("tokenMap");
					String lyr = (String)query.get("layer");
					String itemConfig = (String) query.get("itemconfig");
					String id = (String) query.get("id");
					tuple.put("fset",  fset);
					tuple.put("tokenmap", tokenMap);
					tuple.put("config", itemConfig);
					tuple.put("layer", lyr);
					responseMap.put(id.toString(), tuple);
					
				}

				@Override
				public void onError(Throwable e) {
					e.printStackTrace();
					LOG.error(e);
				}
				
			}
			);
			
		}
	}*/
	
	
	private void ExecuteRestQueries(String jsonGeometry, String geoType)
			throws GeometryException, UnsupportedEncodingException {
		String contentType = "application/json";
		HttpClient httpclient = new DefaultHttpClient();
		for (int i = 0; i < queries.size(); ++i) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> query = (HashMap<String, Object>) queries
					.get(i);
			String path = (String) query.get("restpath");
			String wc = URLEncoder.encode((String) query.get("whereclause"),
					"UTF-8");
			String geo = URLEncoder.encode(jsonGeometry, "UTF-8");
			String fields = (String) query.get("fields");
			@SuppressWarnings("unchecked")
			HashMap<String, String> tokenMap = (HashMap<String, String>) query
					.get("tokenMap");
			String itemConfig = (String) query.get("itemconfig");
			//
			String args = "where="
					+ wc
					+ "&objectIds=&time=&geometry="
					+ geo
					+ "&geometryType="
					+ geoType
					+ "&inSR=&spatialRel=esriSpatialRelIntersects&relationParam=&outFields="
					+ fields
					+ "&returnGeometry=true&maxAllowableOffset=&geometryPrecision=&outSR=&gdbVersion=&returnDistinctValues=false&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&f=json";
			String uri = path + args;
			try {
				HttpPost httppost = new HttpPost(uri);
				httppost.setHeader("Accept", contentType);
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						// instream.read();
						BufferedReader br = new BufferedReader(
								new InputStreamReader((instream)));
						String output = "";
						String ln;
						while ((ln = br.readLine()) != null) {
							output += ln;
						}
						JsonFactory jf = new JsonFactory();
						JsonParser jp = jf.createJsonParser(output);
						FeatureSet fset = FeatureSet.fromJson(jp);
						HashMap<String, Object> tuple = new HashMap<String, Object>();
						String lyr = (String)query.get("layer");
						String lyrheadercfg = (String)query.get("headerconfig");
						Boolean calcdist = (Boolean)query.get("usingdist");
						String distToken = (String)query.get("disttoken");
						String distUnits = (String)query.get("distunits");
						String id = query.get("id").toString();
						tuple.put("fset",  fset);
						tuple.put("tokenmap", tokenMap);
						tuple.put("config", itemConfig);
						tuple.put("layer", lyr);
						tuple.put("lyrheader", lyrheadercfg);
						tuple.put("calcdist", calcdist);
						tuple.put("distunits", distUnits);
						tuple.put("disttoken", distToken);
						responseMap.put(id, tuple);
						

					} catch (IOException ex) {
						// In case of an IOException the connection will be
						// released
						// back to the connection manager automatically
						LOG.error(ex);
						throw ex;
					} catch (RuntimeException ex) {
						// In case of an unexpected exception you may want to
						// abort
						// the HTTP request in order to shut down the underlying
						// connection immediately.
						LOG.error(ex);
						httppost.abort();
						throw ex;
					} finally {
						// Closing the input stream will trigger connection
						// release
						try {
							instream.close();
						} catch (Exception ignore) {
						}
					}
				}

			} catch (Exception ex) {
				LOG.error(ex);
				ex.printStackTrace();
			}

		}
	}
	
	private String GetDistAsString(Graphic g, SpatialReference inputSr, String units)
	{
		com.esri.core.geometry.Geometry geo = g.getGeometry();
		com.esri.core.geometry.Geometry curGeo;
		
		if(!inputSr.equals(srBuffer))
		{
			curGeo = GeometryEngine.project(geo, inputSr, srBuffer);
		}
		else
		{
			curGeo=geo;
		}
		double tmpDist = GeometryEngine.distance(inGeometry, curGeo, srBuffer);
		UnitConverter uc = new UnitConverter();
		int inUnitWkid = uc.findWkid(srBuffer.getUnit().getName());
		String cn = uc.findConnonicalName(units);
		int outUnitWkid = uc.findWkid(cn);
		double dist;
		if(inUnitWkid!=outUnitWkid)
		{
			dist = uc.Convert(tmpDist, inUnitWkid, outUnitWkid);
		}
		else
		{
			dist=tmpDist;
		}
		
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(dist);
	}
	
	private void ParseResponses(String timestamp, String file)
	{
		Set<String> keys = responseMap.keySet();
		Iterator<String> it = keys.iterator();
		String body = "";
		while(it.hasNext())
		{
			String k = it.next();
			@SuppressWarnings("unchecked")
			HashMap<String, Object> response = (HashMap<String, Object>) responseMap.get(k);
			FeatureSet fset = (FeatureSet)response.get("fset");
			String cfg = (String)response.get("config");
			@SuppressWarnings("unchecked")
			HashMap<String,Object>tokenmap=(HashMap<String, Object>)response.get("tokenmap");
			String layer = (String)response.get("layer");
			Graphic[] features = fset.getGraphics();
			String lyrHeader = (String)response.get("lyrheader");
			if(!lyrHeader.isEmpty())
			{
				lyrHeader = "<h3>"+lyrHeader+"</h3>";
			}
			String items = "";
			
			
			items += "<b>"+layer.toUpperCase() + ": </b>";
			
			Boolean usingDist = (Boolean)response.get("calcdist");
			String distUnits = (String)response.get("distunits");
			String distToken = (String)response.get("disttoken");
			SpatialReference fsetSr = fset.getSpatialReference();
			for (int i=0; i<features.length;++i)
			{
				Graphic f = features[i];
				Map<String, Object> att = f.getAttributes();
				Set<String> fields = tokenmap.keySet();
				Iterator<String> itFields = fields.iterator();
				String item = cfg;
				if(usingDist)
				{
					String d = this.GetDistAsString(f, fsetSr, distUnits);
					item = item.replace(distToken, d);
				}
				while(itFields.hasNext())
				{
					String fldname = itFields.next();
					String token = (String) tokenmap.get(fldname);
					item = item.replace(token, att.get(fldname).toString());
					
					if(i>0)
					{
						items += ", ";
					}
					items += item;
					
				}
			}
			items = "<p>"+items+"</p>";
			body += lyrHeader + items;
		}
		String content = "";
		   //File file = new File("C:/Dev/Java/DefenseSolution/defense-geometry-processor/src/main/resources/ReportTemplate.html"); //for ex foo.txt
		   try {
			   //String name = this.getClass().getName();
		       InputStream is = this.getClass().getClassLoader().getResourceAsStream("ReportTemplate.html");
		       //FileInputStream is = new FileInputStream(file);
			   BufferedReader br = new BufferedReader(new InputStreamReader(is));
		       String ln;
		       while ((ln = br.readLine()) != null) {
					content += ln;
				}
		       String title = properties.get("title").getValueAsString();
		       String header = properties.get("header").getValueAsString();
		       if(!timestamp.isEmpty())
		       {
		    	   String tsToken = properties.get("timestamptoken").getValueAsString();
		    	   title = title.replace(tsToken, timestamp);
		    	   header = header.replace(tsToken, timestamp);
		    	   body = body.replace(tsToken,  timestamp);
		       }
		       content = content.replace("[$TITLE]", "<h1>" +title+"</h1>");
		       content = content.replace("[$HEADING]", "<h2>" +header+"</h2>");
		       content = content.replace("[$BODY]", body);
		       br.close();   
		       String filename = "assets/reports/" + file;
		       File outfile = new File(filename);
		       FileOutputStream fos = new FileOutputStream(outfile);
		       OutputStreamWriter osw = new OutputStreamWriter(fos);    
		       Writer w = new BufferedWriter(osw);
		       w.write(content);
		       w.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		   
	}

}
