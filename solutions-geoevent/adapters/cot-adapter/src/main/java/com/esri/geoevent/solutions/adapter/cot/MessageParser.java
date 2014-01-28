package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * MessageParser.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
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
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.messaging.MessagingException;

public class MessageParser extends DefaultHandler {
	private static final String MESSAGES = "messages";
	private static final String MESSAGE = "message";
	private static final Log LOG = LogFactory.getLog(MessageParser.class);

	private enum MessageLevel {
		root, inMessages, inMessage, inAttribute;
	}

	private MessageLevel messageLevel = MessageLevel.root;

	private String attributeName;
	private String attribute;
	private String text;
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private CoTAdapterInbound adapter;

	private String how = null;
	private String opex = null;
	private String qos = null;
	private String type = null;
	private String uid = null;
	private String version = null;
	private String stale = null;
	private String start = null;
	private String time = null;
	private String access = null;
	private StringBuilder detail = new StringBuilder();
	private StringBuilder point = new StringBuilder();
	private boolean inDetails = false;
	private int tabLevel = 0;

	public MessageParser(CoTAdapterInbound adapter) {
		super();
		this.adapter = adapter;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		try {
			if (qName == null)
				return;

			if (messageLevel == MessageLevel.root
					&& (qName.equalsIgnoreCase(MESSAGES) || qName
							.equalsIgnoreCase("geomessages"))) {
				messageLevel = MessageLevel.inMessages;
			} else if (messageLevel == MessageLevel.inMessages
					&& (qName.equalsIgnoreCase(MESSAGE) || qName
							.equalsIgnoreCase("geomessage"))) {
				messageLevel = MessageLevel.inMessage;
			} else if (messageLevel == MessageLevel.inMessage) {
				messageLevel = MessageLevel.inAttribute;
				attribute = "";
				attributeName = qName;
			} else if (messageLevel == MessageLevel.inAttribute) {
				throw new SAXException(
						"Problem parsing message, cannot handle nested attributes. ("
								+ qName + " inside " + attributeName + ")");
			} else if (qName.equalsIgnoreCase("event")) {
				// Event element was found. Store all available CoT attributes.
				for (int i = 0; attributes.getLength() > i; i++) {
					if (attributes.getLocalName(i).equalsIgnoreCase("how")) {
						this.how = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"opex")) {
						this.opex = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"qos")) {
						this.qos = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"type")) {
						this.type = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"uid")) {
						this.uid = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"stale")) {
						this.stale = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"start")) {
						this.start = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"time")) {
						this.time = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"version")) {
						this.version = attributes.getValue(i);
					} else if (attributes.getLocalName(i).equalsIgnoreCase(
							"access")) {
						this.access = attributes.getValue(i);
					}
				}
			} else if (!inDetails && qName.equalsIgnoreCase("detail")) {
				// <detail> element started
				tabLevel++;
				inDetails = true;
				detail.append("\n" + makeTabs(tabLevel)
						+ "<eventWrapper><detail");
				// (NOTE: detail should NOT have any attributes but search just
				// in case)
				for (int i = 0; attributes.getLength() > i; i++) {
					detail.append("\n" + makeTabs(tabLevel + 1)
							+ attributes.getLocalName(i) + "=" + "\""
							+ attributes.getValue(i) + "\"");
				}
				// close the tag
				detail.append(">");
			} else if (inDetails && !qName.equalsIgnoreCase("detail")) {
				// some new child element inside the Detail section
				tabLevel++;
				detail.append("\n" + makeTabs(tabLevel) + "<" + qName);
				// search for any attributes
				for (int i = 0; attributes.getLength() > i; i++) {
					detail.append("\n" + makeTabs(tabLevel + 1)
							+ attributes.getLocalName(i) + "=" + "\""
							+ attributes.getValue(i) + "\"");
				}
				// close the tag
				detail.append(">");
			} else if (!inDetails && qName.equalsIgnoreCase("point")) {
				// <point> element started
				tabLevel++;
				point.append("\n" + makeTabs(tabLevel) + "<point");
				// search for any attributes
				for (int i = 0; attributes.getLength() > i; i++) {
					point.append("\n" + makeTabs(tabLevel + 1)
							+ attributes.getLocalName(i) + "=" + "\""
							+ attributes.getValue(i) + "\"");
				}
				// close the tag
				point.append(" />");
				tabLevel--;
			}
		} catch (Exception e) {
			LOG.error(e);
			LOG.error(e.getStackTrace());
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			if (messageLevel == MessageLevel.inMessages
					&& (qName.equalsIgnoreCase(MESSAGES) || qName
							.equalsIgnoreCase("geomessages"))) {
				messageLevel = MessageLevel.root;
			} else if (messageLevel == MessageLevel.inAttribute
					&& qName.equalsIgnoreCase(attributeName)) {
				messageLevel = MessageLevel.inMessage;
				attributes.put(attributeName, attribute);
				attributeName = null;
			} else if (messageLevel == MessageLevel.root
					&& qName.equalsIgnoreCase("event")) {
				/*
				 * Event tag was just closed. All available information has been
				 * compiled. Send data via msgFromStream
				 */
				try {
					System.out.println(detail.toString());
					adapter.msgFromStream(how, opex, qos, type, uid, version,
							stale, start, time, access, detail.toString(),
							point.toString());

				} catch (MessagingException e) {
					LOG.error(e);
					LOG.error(e.getStackTrace());
				} catch (ParserConfigurationException e) {
					LOG.error(e);
					LOG.error(e.getStackTrace());
				} catch (IOException e) {
					LOG.error(e);
					LOG.error(e.getStackTrace());
				} catch (FieldException e) {
					LOG.error(e);
					LOG.error(e.getStackTrace());
				}
				resetData();
			} else if (inDetails && qName.equals("detail")) {
				detail.append("\n" + makeTabs(tabLevel)
						+ "</detail></eventWrapper>");
				inDetails = false;
				tabLevel--;
			} else if (inDetails && !qName.equals("detail")) {
				detail.append("\n" + text + makeTabs(tabLevel) + "</" + qName
						+ ">");
				tabLevel--;
			} else if (!inDetails && !qName.equals("point")) {
				// apparently this is never true because point is a solo tag
				// " />"
				point.append("\n" + makeTabs(tabLevel) + "</point>");
				tabLevel--;
			}
		} catch (Exception e) {
			LOG.error(e);
			LOG.error(e.getStackTrace());
		}

	}

	private String makeTabs(int desiredNumberOfTabs) throws Exception {
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= desiredNumberOfTabs; i++) {
				sb.append("  ");
			}
			return sb.toString();
		} catch (Exception e) {
			LOG.error(e);
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private void resetData() {
		try {
			this.how = null;
			this.opex = null;
			this.qos = null;
			this.type = null;
			this.uid = null;
			this.version = null;
			this.stale = null;
			this.start = null;
			this.time = null;
			this.access = null;
			this.detail = new StringBuilder();
			this.point = new StringBuilder();
			this.inDetails = false;
			this.tabLevel = 0;
		} catch (Exception e) {
			LOG.error(e);
			LOG.error(e.getStackTrace());
		}

	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		try {
			String str = new String(ch, start, length);
			if (messageLevel == MessageLevel.inAttribute) {
				attribute = str;
			} else {
				text = str;
			}
		} catch (Exception e) {
			LOG.error(e);
			LOG.error(e.getStackTrace());
		}
	}

	public void setAdapter(CoTAdapterInbound adapter) {
		this.adapter = adapter;
	}

}

