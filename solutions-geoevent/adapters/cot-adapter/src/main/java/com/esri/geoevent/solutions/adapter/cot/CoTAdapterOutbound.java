/**
 * 
 */
package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * CoTAdapterOutbound.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
 * org.codehaus.mojo-license-maven-plugin-1.5
 * $Id: update-file-header-config.apt.vm 17764 2012-12-12 10:22:04Z tchemit $
 * $HeadURL: https://svn.codehaus.org/mojo/tags/license-maven-plugin-1.5/src/site/apt/examples/update-file-header-config.apt.vm $
 * %%
 * Copyright (C) 2013 - 2014 Esri
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */




import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.OutboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.*;

public class CoTAdapterOutbound extends OutboundAdapterBase {

	private StringBuffer stringBuffer = new StringBuffer(10*1024);
	private ByteBuffer byteBuffer = ByteBuffer.allocate(10*1024);
	private Charset charset = Charset.forName("UTF-8");
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);


	/**
	 * @param definition
	 * @throws ComponentException
	 */
	public CoTAdapterOutbound(AdapterDefinition definition)
			throws ComponentException {
		super(definition);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] processData(Map<String, List<GeoEvent>> geoevents)
			throws ComponentException {
		// TODO Auto-generated method stub
		return super.processData(geoevents);
	}


	/* (non-Javadoc)
	 * @see com.esri.ges.messaging.GeoEventListener#receive(com.esri.ges.core.geoevent.GeoEvent)
	 */
	@Override
	public void receive(GeoEvent geoEvent) {

		stringBuffer.setLength(0);
		GeoEventDefinition definition = geoEvent.getGeoEventDefinition();
		System.out.println("Creating Event to marshal...");
		Event event=new Event();
		for (FieldDefinition fieldDefinition : definition.getFieldDefinitions())
		{
			try{
				String attributeName = fieldDefinition.getName();
				Object value = geoEvent.getField(attributeName);
				//System.out.println("DEBUG: " + attributeName + " --- " + value.toString());
				if (attributeName.equalsIgnoreCase("version")){
					event.setVersion((Double) value);
				}else if(attributeName.equalsIgnoreCase("uid")){
					event.setUid(value.toString());
				}else if(attributeName.equalsIgnoreCase("type")){
					event.setType(value.toString());
				}else if(attributeName.equalsIgnoreCase("how")){
					event.setHow(value.toString());
				}else if(attributeName.equalsIgnoreCase("time")){
					event.setTime((Date) value);
				}else if(attributeName.equalsIgnoreCase("start")){
					event.setStart((Date) value);
				}else if(attributeName.equalsIgnoreCase("stale")){
					event.setStale((Date) value);
				}else if(attributeName.equalsIgnoreCase("access")){
					event.setAccess(value.toString());
				}else if(attributeName.equalsIgnoreCase("opex")){
					event.setOpex(value.toString());
				}else if(attributeName.equalsIgnoreCase("qos")){
					event.setQos(value.toString());
				}else if(attributeName.equalsIgnoreCase("detail")){
					event.setDetail(this.unpackDetial(fieldDefinition,geoEvent.getFieldGroup("detail")));

					//GETALLFIELDS 
					//CHECK ITS TYPE IF GROUP THEN INSPECT THEM
				}else if(attributeName.equalsIgnoreCase("point")){
					event.setPoint(pointFromJson(value));
				}

			}catch(Exception e){
			
			}

		}

		/////////////////////////
		String xmlResult=null;
		StringBuilder myResult=new StringBuilder();
		int content;

		System.out.println("Event created.");

		System.out.println("Marshalling Event into XML.");


		try {

			JAXBContext contextObj = JAXBContext.newInstance(Event.class);
			Marshaller marshallerObj=contextObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
			ByteArrayOutputStream os=new ByteArrayOutputStream();

			marshallerObj.marshal(event,os);
			ByteArrayInputStream bais=new ByteArrayInputStream(os.toByteArray());

			while ((content=bais.read()) != -1){
				myResult.append((char)content);
			}

			xmlResult=fixEscapeCharacters(myResult.toString());
			System.out.println("**** XML RESULTS ***");
			System.out.println(xmlResult);
			
			this.byteListener.receive(ByteBuffer.wrap(xmlResult.getBytes()), "");

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	private String unpackDetial(FieldDefinition detailObj, FieldGroup fieldGroup) {


			//System.out.println("&&&& inspecting detailObj....");
			StringBuilder sb=new StringBuilder();
			return traverseFieldGroup(detailObj,sb,fieldGroup);

	}
	
	private String traverseFieldGroup(FieldDefinition parentFieldDef, StringBuilder sb, FieldGroup fg){
		StringBuilder mine=new StringBuilder();
		//System.out.println("**Start: "+ parentFieldDef.getName());
		for (FieldDefinition fieldDef: parentFieldDef.getChildren()){
		
			if(fieldDef.getType()==FieldType.Group){
				//System.out.println("Found another Group:"+ fieldDef.getName());
				try {
					mine.append("<"+fieldDef.getName()+getAttributes(fieldDef,mine,fg.getFieldGroup(fieldDef.getName()))+">"+traverseFieldGroup(fieldDef,mine,fg.getFieldGroup(fieldDef.getName()))+"</"+fieldDef.getName()+">");
				} catch (FieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//System.out.println("not a group:" + fieldDef.getName()+"="+fg.getField(fieldDef.getName())+ " toString: "+ fieldDef.toString());
				if(fg.getField(fieldDef.getName())!=null){
					if (fieldDef.getName().equals("#text"))
						mine.append(fg.getField(fieldDef.getName()));
				}
				
			}
				
		}
		//System.out.println("**End: "+ parentFieldDef.getName());
		return mine.toString();
	}
	
	private String getAttributes(FieldDefinition parentFieldDef, StringBuilder sb, FieldGroup fg){
		StringBuilder mine=new StringBuilder();
		for (FieldDefinition fieldDef: parentFieldDef.getChildren()){
			if(fieldDef.getType()!=FieldType.Group){
				if(fg.getField(fieldDef.getName())!=null){
					if (!fieldDef.getName().equals("#text"))
						mine.append(" "+fieldDef.getName()+"=\""+fg.getField(fieldDef.getName())+"\"");				
				}
			}
		}
		return mine.toString();
	}
	
	private Point pointFromJson(Object value) {
		//JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		Point point=new Point();
		JsonFactory jf= new JsonFactory();
		try {
			JsonParser parser=jf.createJsonParser((String) value);
			parser.nextValue();//skips the initial JsonToken.START_OBJECT
			while (parser.nextToken() != JsonToken.END_OBJECT) {
				String field_name=parser.getCurrentName();
				parser.nextToken();
				if(field_name.equals("x")){
					Double data=parser.getDoubleValue();
					point.setLon(data);
				}else if(field_name.equals("y")){
					Double data=parser.getDoubleValue();
					point.setLat(data);
				}else if(field_name.equals("z")){
					Double data=parser.getDoubleValue();
					point.setHae(data);
				}
			}//while

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return point;
	}

	private static String fixEscapeCharacters(String marshalledXML) {
		return marshalledXML.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");

	}

}
