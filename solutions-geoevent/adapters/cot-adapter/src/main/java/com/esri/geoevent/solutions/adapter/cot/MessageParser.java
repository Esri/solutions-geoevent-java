package com.esri.geoevent.solutions.adapter.cot;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.messaging.MessagingException;

public class MessageParser extends DefaultHandler
{
	private static final String MESSAGES  = "messages";
	private static final String MESSAGE   = "message";

	private enum MessageLevel
	{
		root, inMessages, inMessage, inAttribute;
	}

	private MessageLevel messageLevel = MessageLevel.root;

	private String attributeName;
	private String attribute;
	private String text;
	private HashMap<String,String> attributes = new HashMap<String,String>();
	private CoTAdapterInbound adapter;

	private String how=null;
	private String opex=null;
	private String qos=null;
	private String type=null;
	private String uid=null;
	private String version=null;
	private String stale=null;
	private String start=null;
	private String time=null;
	private String access=null;
	private StringBuilder detail=new StringBuilder();
	private StringBuilder point=new StringBuilder();
	private boolean inDetails=false;
	private int tabLevel=0;

	public MessageParser( CoTAdapterInbound adapter )
	{
		super();
		this.adapter = adapter;
	}

	@Override
	public void startElement(String uri, String localName,String qName,	Attributes attributes) throws SAXException
	{
		if(qName == null)
			return;

		if (messageLevel == MessageLevel.root && (qName.equalsIgnoreCase(MESSAGES) || qName.equalsIgnoreCase("geomessages")))
		{
			messageLevel = MessageLevel.inMessages;
		}
		else if(messageLevel == MessageLevel.inMessages && (qName.equalsIgnoreCase(MESSAGE) || qName.equalsIgnoreCase("geomessage")))
		{
			messageLevel = MessageLevel.inMessage;
		}
		else if(messageLevel == MessageLevel.inMessage)
		{
			messageLevel = MessageLevel.inAttribute;
			attribute = "";
			attributeName = qName;
		}
		else if(messageLevel == MessageLevel.inAttribute)
		{
			throw new SAXException("Problem parsing message, cannot handle nested attributes. ("+qName+" inside "+attributeName+")");
		}else if(qName.equalsIgnoreCase("event")){
			//Event element was found.  Store all available CoT attributes.
			for (int i =0;attributes.getLength()>i;i++){
				if(attributes.getLocalName(i).equalsIgnoreCase("how")){
					this.how=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("opex")){
					this.opex=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("qos")){
					this.qos=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("type")){
					this.type=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("uid")){
					this.uid=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("stale")){
					this.stale=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("start")){
					this.start=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("time")){
					this.time=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("version")){
					this.version=attributes.getValue(i);
				}else if(attributes.getLocalName(i).equalsIgnoreCase("access")){
					this.access=attributes.getValue(i);
				}
			}
		}else if(!inDetails && qName.equalsIgnoreCase("detail")){
			//<detail> element started
			tabLevel++;
			inDetails=true;
			detail.append("\n"+makeTabs(tabLevel)+"<eventWrapper><detail");
			//(NOTE: detail should NOT have any attributes but search just in case)
			for (int i =0;attributes.getLength()>i;i++){
				detail.append("\n"+makeTabs(tabLevel+1)+attributes.getLocalName(i)+"="+"\""+attributes.getValue(i)+"\"");
			}
			//close the tag
			detail.append(">");
		}else if(inDetails && !qName.equalsIgnoreCase("detail")){
			// some new child element inside the Detail section
			tabLevel++;
			detail.append("\n"+makeTabs(tabLevel)+"<"+qName);
			//search for any attributes
			for (int i =0;attributes.getLength()>i;i++){
				detail.append("\n"+makeTabs(tabLevel+1)+attributes.getLocalName(i)+"="+"\""+attributes.getValue(i)+"\"");
			}
			//close the tag
			detail.append(">");
		}else if(!inDetails && qName.equalsIgnoreCase("point")){
			//<point> element started
			tabLevel++;
			point.append("\n"+makeTabs(tabLevel)+"<point");
			//search for any attributes
			for (int i =0;attributes.getLength()>i;i++){
				point.append("\n"+makeTabs(tabLevel+1)+attributes.getLocalName(i)+"="+"\""+attributes.getValue(i)+"\"");
			}
			//close the tag
			point.append(" />");
			tabLevel--;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (messageLevel == MessageLevel.inMessages && (qName.equalsIgnoreCase(MESSAGES) || qName.equalsIgnoreCase("geomessages")))
		{
			messageLevel = MessageLevel.root;
		}
		else if (messageLevel == MessageLevel.inAttribute && qName.equalsIgnoreCase(attributeName))
		{
			messageLevel = MessageLevel.inMessage;
			attributes.put( attributeName, attribute );
			attributeName = null;
		}else if (messageLevel == MessageLevel.root && qName.equalsIgnoreCase("event")){
			/*
			 * Event tag was just closed.  All available information has been compiled.
			 * Send data via msgFromStream
			 */
			try {
				System.out.println(detail.toString());
				adapter.msgFromStream( how,
						opex,
						qos,
						type,
						uid,
						version,
						stale,
						start,
						time,
						access,
						detail.toString(),
						point.toString());


			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();					
			} catch (FieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resetData();
		}else if(inDetails && qName.equals("detail")){
			detail.append("\n"+makeTabs(tabLevel)+"</detail></eventWrapper>");
			inDetails=false;
			tabLevel--;
		}else if(inDetails && !qName.equals("detail")){
			detail.append("\n"+ text +makeTabs(tabLevel)+"</"+qName+">");
			tabLevel--;
		}else if(!inDetails && !qName.equals("point")){
			//apparently this is never true because point is a solo tag " />"
			point.append("\n"+makeTabs(tabLevel)+"</point>");
			tabLevel--;
		}

	}

	private String makeTabs(int desiredNumberOfTabs){
		StringBuilder sb=new StringBuilder();
		for (int i =0; i<=desiredNumberOfTabs;i++){
			sb.append("  ");
		}
		return sb.toString();
	}

	private void resetData() {
		this.how=null;
		this.opex=null;
		this.qos=null;
		this.type=null;
		this.uid=null;
		this.version=null;
		this.stale=null;
		this.start=null;
		this.time=null;
		this.access=null;
		this.detail=new StringBuilder(); 
		this.point=new StringBuilder();
		this.inDetails=false;
		this.tabLevel=0;


	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
		String str = new String(ch, start, length);
		if (messageLevel == MessageLevel.inAttribute)
		{
			attribute = str;
		}
		else
		{
			text = str;
		}
	}

	public void setAdapter( CoTAdapterInbound adapter )
	{
		this.adapter = adapter;
	}

}

