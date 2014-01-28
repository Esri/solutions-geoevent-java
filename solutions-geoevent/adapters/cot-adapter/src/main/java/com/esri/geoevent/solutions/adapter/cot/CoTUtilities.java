package com.esri.geoevent.solutions.adapter.cot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoTUtilities {

	private static final Log log = LogFactory.getLog(CoTUtilities.class);
	public static String UNKNOWN_SYMBOL = "SUZP-----------";

	// https://svn.ultra-prologic.com/svn/repos/trunk/group/igs/AViz/AWS%204.0/AWSData/utilities/CoTEventUtilities.cs

	// / <summary>
	// / This function converts the type field from Cursor On Target Event
	// / format to MIL-STD 2525B 15 character symbol codes
	// / </summary>
	// / <param name="cotType">The cursor on target type field data</param>
	// / <returns>A 15 character string representing the 2525B equivalent of the
	// / CoT event type if successful, <see
	// cref="UNKNOWN_SYMBOL">UNKNOWN_SYMBOL</see>
	// / otherwise.</returns>
	public static String getSymbolFromCot(String cotType) {
		try {
			if (cotType == null || cotType.isEmpty())
				return UNKNOWN_SYMBOL;

			StringBuilder retVal = new StringBuilder();
			String recognizedAffiliations = "fhupansjku".toUpperCase();
			String recognizedBattleSpaces = "PAGSUF";

			// Must be of the atom type or it is not supported
			if (cotType.substring(0, 1).equals("a")) {
				retVal.append("S");

				// Convert affiliation
				String cotAffiliation = cotType.substring(2, 3).toUpperCase();

				// If the given CoT affiliation is within the set of recognized
				// affiliations
				// then the 2525 equivalent is simply the upper case of the same
				// character
				if (recognizedAffiliations.indexOf(cotAffiliation) > -1) {
					retVal.append(cotAffiliation.toUpperCase());
				} else {
					retVal.append("O"); // O=None specified
				}

				// Convert battle space dimension
				if (cotType.length() > 3) {
					String cotBattleSpace = cotType.substring(4, 5)
							.toUpperCase();
					if (recognizedBattleSpaces.indexOf(cotBattleSpace) > -1) {
						retVal.append(cotBattleSpace);
					} else {
						retVal.append("X"); // X=Other (no frame)
					}
				} else
					retVal.append("X");

				// All CoT types assumed Present (as opposed to
				// anticipated/planned)
				retVal.append("P");

				// All remaining capital letters in the string are 1:1
				// equivalents of 2525(b) codes (although not all 2525b codes
				// are used in CoT).
				for (int i = 6; i < cotType.length(); i += 2) {
					String next = cotType.substring(i, i + 1);
					if (next.equals(next.toUpperCase())) {
						retVal.append(next);
					} else {
						break;
					}
				}

				// Fill out the remainder of the string
				int fillLength = 15 - retVal.toString().length();
				for (int i = 0; i < fillLength; i++) {
					retVal.append("-");
				}

				return retVal.toString();
			} else {
				return UNKNOWN_SYMBOL;
			}
		} catch (Exception generalException) {
			generalException.printStackTrace();
		} finally {
		}

		return UNKNOWN_SYMBOL;
	}

	/*
	 * load in the Type descriptions from C:\Program Files (x86)\Cursor on
	 * Target\fbcb2d\CoTtypes.xml As an example we will go from: a-.-A-M-F-D To
	 * something like this: AIRBORNE COMMAND POST (C2)
	 */
	public static ArrayList<CoTTypeDef> getCoTTypeMap(InputStream mapInputStream) throws ParserConfigurationException, SAXException, IOException
	{

		ArrayList<CoTTypeDef> types = null;

		String content = getStringFromFile(mapInputStream);


		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource source = new InputSource();
		source.setCharacterStream(new StringReader(content));
		Document doc = db.parse(source);
		NodeList nodeList = doc.getElementsByTagName("types");
		types = typeBreakdown(nodeList.item(0));
		return types;
	}

	/*
	 * load in the descriptions from the specified file
	 */
	private static String getStringFromFile(InputStream is) {
		StringBuffer strContent = new StringBuffer("");

		try {
			int ch;

			while ((ch = is.read()) != -1)
				strContent.append((char) ch);
			is.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return strContent.toString();

	}

	private static ArrayList<CoTTypeDef> typeBreakdown(Node n) {

		ArrayList<CoTTypeDef> hash = new ArrayList<CoTTypeDef>();

		try {

			String name = n.getNodeName();

			if (name.startsWith("#")) {
				// no match here
				return new ArrayList<CoTTypeDef>();

			}
			StringBuffer sb = new StringBuffer();
			sb.append('<').append(name);

			NamedNodeMap attrs = n.getAttributes();
			if (attrs != null) {

				if (attrs.getLength() >= 3 && name.equals("cot")) {
					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					String two = attrs.item(2).getNodeName();

					// for some reason the attributes are not coming back in
					// order. make sure we get the ones we want
					int k = 0;
					int v = 1;
					if (zero.equals("cot")) {
						k = 0;
					} else if (one.equals("cot")) {
						k = 1;
					} else if (two.equals("cot")) {
						k = 2;
					}

					if (zero.equals("desc")) {
						v = 0;
					} else if (one.equals("desc")) {
						v = 1;
					} else if (two.equals("desc")) {
						v = 2;
					}
					hash.add(new CoTTypeDef("^" + attrs.item(k).getNodeValue()
							+ "$", attrs.item(v).getNodeValue(), false));

				} else if (attrs.getLength() == 2 && name.equals("cot")) {
					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("cot")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}
					hash.add(new CoTTypeDef("^" + attrs.item(k).getNodeValue()
							+ "$", attrs.item(v).getNodeValue(), false));

				} else if (attrs.getLength() == 2 && name.equals("weapon")) {

					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("cot")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}
					hash.add(new CoTTypeDef("^" + attrs.item(k).getNodeValue()
							+ "$", attrs.item(v).getNodeValue(), false));

				} else if (attrs.getLength() == 2 && name.equals("relation")) {

					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("cot")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}
					hash.add(new CoTTypeDef("^" + attrs.item(k).getNodeValue()
							+ "$", attrs.item(v).getNodeValue(), false));

				} else if (attrs.getLength() == 2 && name.equals("how")) {

					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("value")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}

					hash.add(new CoTTypeDef("^" + attrs.item(k).getNodeValue()
							+ "$", attrs.item(v).getNodeValue(), false));

				} else if (attrs.getLength() == 2 && name.equals("is")) {
					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("match")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}
					String s = attrs.item(v).getNodeValue();
					if (!(s.equals("true") || s.equals("false")
							|| s.equals("spare") || s.equals("any") || s
							.equals("atoms"))) {
						hash.add(new CoTTypeDef(attrs.item(k).getNodeValue(),
								attrs.item(v).getNodeValue(), true));

					}
				} else if (attrs.getLength() == 2 && name.equals("how")) {
					String zero = attrs.item(0).getNodeName();
					String one = attrs.item(1).getNodeName();
					// make sure we are grabbing the elements in the right order
					int k = 0;
					int v = 1;
					if (zero.equals("value")) {
						k = 0;
						v = 1;
					} else {
						k = 1;
						v = 0;
					}
					String s = attrs.item(v).getNodeValue();
					if (!(s.equals("true") || s.equals("false")
							|| s.equals("spare") || s.equals("any") || s
							.equals("atoms"))) {
						hash.add(new CoTTypeDef(attrs.item(k).getNodeValue(),
								attrs.item(v).getNodeValue(), false));

					}
				}

			}

			String textContent = null;
			NodeList children = n.getChildNodes();

			if (children.getLength() == 0) {
				if ((textContent = n.getTextContent()) != null
						&& !"".equals(textContent)) {

				} else {

				}
			} else {

				boolean hasValidChildren = false;
				for (int i = 0; i < children.getLength(); i++) {
					ArrayList<CoTTypeDef> childHash = typeBreakdown(children
							.item(i));

					if (childHash.size() > 0) {

						hash.addAll(childHash);
						hasValidChildren = true;
					}
				}

				if (!hasValidChildren
						&& ((textContent = n.getTextContent()) != null)) {
					
				}

				
			}

			return hash;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return hash;
		}

	}
	public static Date parseCoTDate(String dateString) throws Exception
	{
		if (!dateString.isEmpty())
		{
			DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
			formatter1.setTimeZone(TimeZone.getTimeZone("Zulu"));
			DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			formatter2.setTimeZone(TimeZone.getTimeZone("Zulu"));
			Date date = null;
			try
			{
				if( date == null )
					date = (Date) formatter1.parse(dateString);
			}catch( ParseException ex )
			{
			}
			try
			{
				if( date == null )
					date = (Date) formatter2.parse(dateString);
			}catch( ParseException ex )
			{
			}
			return date;
		}
		return null;
	}

}
