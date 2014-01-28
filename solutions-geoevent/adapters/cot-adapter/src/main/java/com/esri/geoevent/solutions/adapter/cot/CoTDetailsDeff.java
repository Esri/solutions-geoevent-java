package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * CoTDetailsDeff.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.FieldCardinality;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;

public class CoTDetailsDeff {

	private ArrayList<String> newAttribs;

	public CoTDetailsDeff(String xsdContent) {



	}

	private ArrayList<String> getNewNames(Node n) {
		// drilldown to get the names and their corresponding types.
		ArrayList<String> myTempNames = new ArrayList<String>();
		ArrayList<String> myNewNames = new ArrayList<String>();

		// now parse through them to append the types to the preceding item.

		// note we dont want to grab the very last element yet
		for (int i = 0; i < myTempNames.size() - 1; i++) {
			System.out.println(myTempNames.get(i));

			if (myTempNames.get(i).startsWith(".")
					&& myTempNames.get(i + 1).startsWith("&")) {
				myNewNames.add("detail"
						+ myTempNames.get(i).concat(myTempNames.get(i + 1)));

			} else if (myTempNames.get(i).startsWith(".")
					&& !myTempNames.get(i + 1).startsWith("&")) {
				myNewNames
				.add("detail" + myTempNames.get(i).concat("&String&"));
			}
		}

		// now check on the last element (special case)
		if (myTempNames.get(myTempNames.size() - 1).startsWith(".")) {
			myNewNames.add("detail"
					+ myTempNames.get(myTempNames.size() - 1)
					.concat("&String&"));

		}

		return myNewNames;
	}

	public ArrayList<String> getNewAttribs()
	{
		return this.newAttribs;
	}

	private static void nodeDrillDown(Node n, FieldDefinition fieldDef, HashMap<String,String> typeTable ) throws ConfigurationException
	{
		String name = n.getNodeName();
		short type = n.getNodeType();
		if (!(name.startsWith("#") || Node.CDATA_SECTION_NODE == type))
		{
			if( name.equals("xs:simpleType"))
			{
				NamedNodeMap nnm = n.getAttributes();
				String key = null;
				for( int i = 0; i < nnm.getLength(); i++ )
				{
					Node no = nnm.item(i);
					if( no.getNodeName().equals("name") )
						key = no.getNodeValue();
				}
				if( key == null )
					return;
				NodeList list = n.getChildNodes();
				if( list == null )
					return;
				for( int i = 0; i < list.getLength(); i++ )
				{
					Node child = list.item(i);
					if( child.getNodeName().equals("xs:restriction") )
					{
						String value = child.getAttributes().getNamedItem("base").getTextContent();
						typeTable.put(key, value);
						return;
					}
				}
				return;
			}else if( name.equals("xs:element"))
			{
				String elementName = n.getAttributes().getNamedItem("name").getTextContent();
				String maxOccurs = getMaxOccurs(n);
				FieldDefinition fd = new DefaultFieldDefinition( elementName, FieldType.Group );
				if( maxOccurs.equals("unbounded") )
					fd.setCardinality(FieldCardinality.Many);
				if( elementName.equals("shape"))
					fd.setType(FieldType.Geometry);
				fieldDef.addChild(fd);
				fieldDef = fd;
				
				fd = new DefaultFieldDefinition( "#text", FieldType.String );
				fd.setCardinality(FieldCardinality.One);
				fd.setType(FieldType.String);
				fieldDef.addChild(fd);
			}else if (name == "xs:attribute" )
			{
				String attributeName = n.getAttributes().getNamedItem("name").getTextContent();
				String attributeType = "";
				if( n.getAttributes().getNamedItem("type") != null )
					attributeType = n.getAttributes().getNamedItem("type").getTextContent();
				FieldType t = lookupType(attributeType,typeTable);
				FieldDefinition fd = new DefaultFieldDefinition( attributeName, t );
				fieldDef.addChild(fd);
			}else if (name == "xs:restriction" )
			{
				String baseName = n.getAttributes().getNamedItem("base").getTextContent();
				FieldType t = lookupType(baseName, typeTable);
				fieldDef.setType(t);
			}
			NodeList list = n.getChildNodes();
			if( list == null )
				return;
			for( int i = 0; i < list.getLength(); i++ )
			{
				nodeDrillDown( list.item(i), fieldDef, typeTable );
			}
		}
	}

	private static String getMaxOccurs(Node n)
	{
		Node maxOccursNode = n.getAttributes().getNamedItem("maxOccurs");
		if( maxOccursNode == null )
			return "";
		String s = maxOccursNode.getTextContent();
		if( s == null )
			return "";
		return s;
	}

	private static FieldType lookupType(String attributeType, HashMap<String,String> typeTable )
	{
		if( typeTable.containsKey(attributeType) )
			attributeType = typeTable.get(attributeType);
		FieldType t = FieldType.String;
		if( attributeType.equals("") )
			t = FieldType.String;
		else if( attributeType.equals("xs:string"))
			t = FieldType.String;
		else if( attributeType.equals("xs:dateTime"))
			t = FieldType.Date;
		else if( attributeType.equals("xs:integer"))
			t = FieldType.Integer;
		else if( attributeType.equals("xs:nonNegativeInteger"))
			t = FieldType.Integer;
		else if( attributeType.equals("xs:decimal"))
			t = FieldType.Double;
		else if( attributeType.equals("xs:boolean"))
			t = FieldType.Boolean;
		else
			System.out.println("Cannot recognize the type \""+attributeType+"\"");
		return t;
	}

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

	public static void parseXSD( InputSource source, FieldDefinition detailsDef) throws ConfigurationException
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(source);
			NodeList nodeList = doc.getElementsByTagName("xs:schema");
			nodeDrillDown(nodeList.item(0), detailsDef, new HashMap<String,String>() );
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
