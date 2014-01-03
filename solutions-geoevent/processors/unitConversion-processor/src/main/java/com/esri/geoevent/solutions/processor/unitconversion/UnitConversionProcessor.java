package com.esri.geoevent.solutions.processor.unitconversion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class UnitConversionProcessor extends GeoEventProcessorBase {
	HashMap<String, Double> convertDistance = new HashMap<String, Double>();
	HashMap<String, Double> convertTime = new HashMap<String, Double>();
	HashMap<String, String> timeUnits = new HashMap<String, String>();
	HashMap<String, String> distanceUnits= new HashMap<String, String>();
	HashMap<String, String> freqUnits= new HashMap<String, String>();
	HashMap<String, Double> convertMetric = new HashMap<String, Double>();
	Messaging messaging;
	GeoEventDefinitionManager manager;
	private String altOut;
	private String dOut;
	private String tOut;
	private String freqOut;
	private String altIn;
	private String dIn;
	private String tIn;
	private String freqIn;
	private static final Log LOG = LogFactory
			.getLog(UnitConversionProcessor.class);

	public UnitConversionProcessor(GeoEventProcessorDefinition definition,
			GeoEventDefinitionManager m, Messaging msg)
			throws ComponentException {
		super(definition);
		manager = m;
		messaging = msg;
		geoEventMutator= true;
		convertDistance.put("km-km", 1.0);
		convertDistance.put("km-hm", 10.0);
		convertDistance.put("km-nm", 0.539957);
		convertDistance.put("km-ft", 3280.84);
		convertDistance.put("km-mi", 0.621371);
		convertDistance.put("km-m", 1000.0);
		convertDistance.put("hm-km", 0.1);
		convertDistance.put("hm-hm", 1.0);
		convertDistance.put("hm-nm", 0.0539957);
		convertDistance.put("hm-ft", 328.084);
		convertDistance.put("hm-mi", 0.0621371);
		convertDistance.put("hm-m", 100.0);
		convertDistance.put("nm-km", 1.852);
		convertDistance.put("nm-hm", 18.52);
		convertDistance.put("nm-nm", 1.0);
		convertDistance.put("nm-ft", 6076.12);
		convertDistance.put("nm-mi", 1.15078);
		convertDistance.put("nm-m", 1852.0);	
		convertDistance.put("ft-km", 0.0003048);
		convertDistance.put("ft-hm", 0.003048);
		convertDistance.put("ft-nm", 0.000164579);
		convertDistance.put("ft-ft", 1.0);
		convertDistance.put("ft-mi", 0.000189394);
		convertDistance.put("ft-m", 0.3048);		
		convertDistance.put("mi-km", 1.0);
		convertDistance.put("mi-hm", 10.0);
		convertDistance.put("mi-nm", 0.539957);
		convertDistance.put("mi-ft", 5280.0);
		convertDistance.put("mi-mi", 1.0);
		convertDistance.put("mi-m", 1609.34);	
		convertDistance.put("m-km", 0.001);
		convertDistance.put("m-hm", 0.01);
		convertDistance.put("m-nm", 0.000539957);
		convertDistance.put("m-ft", 3.28084);
		convertDistance.put("m-mi", 0.000621371);
		convertDistance.put("m-m", 1.0);
		
		convertTime.put("sec-sec", 1.0);
		convertTime.put("sec-min", 0.0166667);
		convertTime.put("sec-hr", 0.000277778);
		convertTime.put("min-sec", 60.0);
		convertTime.put("min-min", 1.0);
		convertTime.put("min-hr", 0.0166667);
		convertTime.put("hr-sec", 3600.0);
		convertTime.put("hr-min", 60.0);
		convertTime.put("hr-hr", 1.0);

		convertMetric.put("giga", 1000000000.0);
		convertMetric.put("mega", 1000000.0);
		convertMetric.put("kilo", 1000.0);
		convertMetric.put("none", 1.0);
		convertMetric.put("hecto", 100.0);
		
		timeUnits.put("Second", "sec");
		timeUnits.put("Minute", "min");
		timeUnits.put("Hour", "hr");
		
		distanceUnits.put("Meters", "m");
		distanceUnits.put("Kilometers", "km");
		distanceUnits.put("Hectometers", "hm");
		distanceUnits.put("Miles", "mi");
		distanceUnits.put("Nautical Miles", "nm");
		distanceUnits.put("Feet", "ft");
		
		freqUnits.put("GHz", "giga");
		freqUnits.put("MHz", "mega");
		freqUnits.put("KHz", "kilo");
		freqUnits.put("Hz", "none");
	}

	@Override
	public GeoEvent process(GeoEvent evt) throws Exception {

		try {
			//Integer validation;
			String input = properties.get("input").getValueAsString();
			String tag;
			Object vtag = null;
			Object ftag = null;
			Object atag = null;
			if(input.equals("TAG"))
			{
				vtag = properties.get("velocity-tag").getValue();
				ftag = properties.get("frequency-tag").getValue();
				atag = properties.get("altitude-tag").getValue();
			}
			else
			{
				vtag = properties.get("velocity-manual").getValue();
				ftag = properties.get("freq-manual").getValue();
				atag = properties.get("alt-manual").getValue();
			}
			
			Double v = null;
			if(vtag != null)
			{
				tag = vtag.toString();
				String strVelocity = (String) evt.getField(tag);
				if(evt.getGeoEventDefinition().getFieldDefinition(tag)!=null)
				{
					String vout = properties.get("vout").getValueAsString();
					parseVelocityOut(vout);
					String vin = properties.get("vin").getValueAsString();
					parseVelocityIn(vin);
					v = calculateVelocity(strVelocity);
					evt.setField(tag, v.toString());
				}
				//vFld = new DefaultFieldDefinition(vFldName, FieldType.Double);
			}
					

			Double a = null;
			if(atag != null)
			{
				tag = atag.toString();
				String strAlt = (String) evt.getField(tag);
				if(evt.getGeoEventDefinition().getFieldDefinition(tag)!=null)
				{
					String aout = properties.get("altout").getValueAsString();
					parseAltOut(aout);
					String ain = properties.get("altin").getValueAsString();
					parseAltIn(ain);
					a = calculateAltitude(strAlt);
					evt.setField(tag, a.toString());
				}
				//aFld = new DefaultFieldDefinition(aFldName, FieldType.Double);
			}
			
			Double freq = null;
			if(ftag !=null)
			{
				tag = ftag.toString();
				String strFreq = (String) evt.getField(tag);
				if(evt.getGeoEventDefinition().getFieldDefinition(tag)!=null)
				{
					String fout = properties.get("freqout").getValueAsString();
					parseFreqOut(fout);
					String fin = properties.get("freqin").getValueAsString();
					parseFreqIn(fin);
					freq = calculateFrequency(strFreq);
					evt.setField(tag, freq.toString());
				}
				//fFld = new DefaultFieldDefinition(fFldName, FieldType.Double);
			}
			
			
			//GeoEventDefinition geoDef = evt.getGeoEventDefinition();
			/*String name = geoDef.getName() + "_converted";
			GeoEventDefinition edOut;
			if(manager.searchGeoEventDefinition(name, getId())==null)
			{
				//FieldDefinition[] fDefs = { vFld, aFld, fFld };
				//List<FieldDefinition> fds = Arrays.asList(fDefs);
				List<FieldDefinition> fldDefs = geoDef.getFieldDefinitions();
				List<FieldDefinition> newFldDefs = geoDef.getFieldDefinitions();
				List <String> reduce = new ArrayList<String>();
				for(FieldDefinition f: fldDefs)
				{
					String fname = f.getName();
					if(fname.equals(vFldName))
					{
						newFldDefs.add(vFld);
						//reduce.add(fname);
					}
					else if (fname.equals(aFldName))
					{
						newFldDefs.add(aFld);
						//reduce.add(fname);
					}
					else if(fname.equals(fFldName))
					{
						newFldDefs.add(fFld);
						//reduce.add(fname);
					}
					else
					{
						newFldDefs.add(f);
					}
					
				}
				edOut = new DefaultGeoEventDefinition();
				edOut.setName(name);
				edOut.setOwner(getId());
				edOut.setFieldDefinitions(newFldDefs);
				manager.addGeoEventDefinition(edOut);
			}
			else
			{
				edOut=manager.getGeoEventDefinition(name);
			}
			
			List<Object> vals = new ArrayList<Object>();
			for(FieldDefinition fdef: edOut.getFieldDefinitions())
			{
				String fname = fdef.getName();
				if(fname.equals(vFldName))
				{
					vals.add(v);
				}
				else if (fname.equals(aFldName))
				{
					vals.add(a);
				}
				else if(fname.equals(fFldName))
				{
					vals.add(freq);
				}
				else
				{
					vals.add(evt.getField(fname));
				}
			}
			Object[] newVals = vals.toArray();
			GeoEventCreator geoEventCreator = messaging.createGeoEventCreator();
			GeoEvent geOut = geoEventCreator.create(edOut.getGuid(), newVals);
			
			for (Map.Entry<GeoEventPropertyName, Object> property : evt
					.getProperties()) {
				if (!geOut.hasProperty(property.getKey())) {
					geOut.setProperty(property.getKey(), property.getValue());
				}
			}*/

			//return geOut;
			return evt;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}

	}
	private void parseVelocityIn(String v)
	{
		if(v.equals("Knots"))
		{
			dIn = "nm";
			tIn = "hr";
		}
		else
		{
			String[] vel = v.split("/");
			String d=vel[0];
			String t=vel[1];
			dIn = distanceUnits.get(d);
			tIn = timeUnits.get(t);
		}
	}

	private void parseAltIn(String a)
	{
		altIn = distanceUnits.get(a);
	}
	
	private void parseFreqIn(String f)
	{
		freqIn = freqUnits.get(f); 
	}
	private void parseVelocityOut(String v)
	{
		if(v.equals("Knots"))
		{
			dOut = "nm";
			tOut = "hr";
		}
		else
		{
			String[] vel = v.split("/");
			String d=vel[0];
			String t=vel[1];
			dOut = distanceUnits.get(d);
			tOut = timeUnits.get(t);
		}
	}

	private void parseAltOut(String a)
	{
		altOut = distanceUnits.get(a);
	}
	
	private void parseFreqOut(String f)
	{
		freqOut = freqUnits.get(f); 
	}
	
	private Double calculateVelocity(String v) throws Exception {
		try {
			//String[] vArr = v.split(" ");
			int i = v.indexOf(" ");
			Double cv = 0.0;
			if (i > -1) {
				int l = v.length();
				Double ov = Double.valueOf(v.substring(0, i));
				String unit = v.substring(i+1, l);
				Double dist = calculateDist(ov, unit);
				Double time = calculateTime(unit);
				cv = dist / time;
			} else {
				Double ov = Double.valueOf(v);
				Double dist = calculateDist(ov, null);
				Double time = calculateTime(null);
				cv = dist / time;
			}
			return cv;
		} catch (Exception e) {
			LOG.error("error calculating velocity value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private double calculateDist(Double d, String unit) throws Exception {
		try {
			String dKey;
			if(StringIsNullOrEmpty(unit))
			{
				dKey = dIn + "-" + dOut;
			}
			else
			{
				String distUnit = findDistance(unit);
				dKey= distUnit + "-" + dOut;
			}
			double factor = convertDistance.get(dKey);
			return d * factor;
		} catch (IOException e) {
			LOG.error("ioerror calculating velocity value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		} catch (Exception e) {
			LOG.error("error calculating distance value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}

	}

	private Double calculateTime(String unit) throws Exception {
		try {
			String tKey;
			if(StringIsNullOrEmpty(unit))
			{
				tKey = tIn + "-" + tOut;
			}
			else
			{
				String tUnit = findTime(unit);
				tKey = tUnit + "-" + tOut;
			}
			return convertTime.get(tKey);
		} catch (IOException e) {
			LOG.error("ioerror calculating time value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		} catch (Exception e) {
			LOG.error("error calculating time value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private Double calculateAltitude(String a) throws Exception {
		try {
			int i = a.indexOf(" ");
			Double ca = 0.0;
			if (i > -1) {
				Double oa = Double.valueOf(a.substring(0,i));
				int l = a.length();
				String unit = a.substring(i+1,l);
				try {
					String altKey;
					if(StringIsNullOrEmpty(unit))
					{
						altKey = altIn + "-" + altOut;
					}
					else
					{
						String dUnit = findDistance(unit);
						altKey = dUnit + "-" + altOut;
					}
					Double factor = convertDistance.get(altKey);
					ca = oa * factor;
				} catch (IOException e) {
					LOG.error("ioerror calculating altitude value");
					LOG.error(e.getMessage());
					LOG.error(e.getStackTrace());
				}

			} else {
				
				Double oa = Double.valueOf(a);
				String altKey = altIn + "-" + altOut;
				Double factor = convertDistance.get(altKey);
				ca = oa * factor;
			}
			return ca;
		} catch (Exception e) {
			LOG.error("error calculating time value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private Double calculateFrequency(String f) throws Exception {
		try {
			//String[] vArr = f.split(" ");
			int i = f.indexOf(" ");
			Double cf = 0.0;
			if (i > -1) {
				Double of = Double.valueOf(f.substring(0,i));
				int l = f.length();
				String unit = f.substring(i+1,l);
				String dUnit;
				try {
					Double from;
					if(StringIsNullOrEmpty(unit))
					{
						from = convertMetric.get(freqIn);
					}
					else
					{
						dUnit = findFrequencyModifier(unit);
						from = convertMetric.get(dUnit);
					}
					Double to = convertMetric.get(freqOut);
					Double factor = from/to;
					cf = of * factor;
				} catch (IOException e) {
					LOG.error("ioerror calculating frequency value");
					LOG.error(e.getMessage());
					LOG.error(e.getStackTrace());
					throw(e);
				}

			} else {
				Double of = Double.valueOf(f);
				Double from = convertMetric.get(freqIn);
				Double to = convertMetric.get(freqOut);
				Double factor = from / to;
				cf = of * factor;
			}
			return cf;
		} catch (Exception e) {
			LOG.error("ioerror calculating frequency value");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private String findTime(String s) throws Exception {
		try {
			if(s.toLowerCase().equals("kp") || s.toLowerCase().equals("mi") || s.toLowerCase().equals("nm"))
			{
				return "hr";
			}
			String time = "none";
			String regex1 = "(pm)|(((m)|(M))((inutes?)|(INUTES?)))|(/m)|(\\\\m)|(MIN)|(min)|(Min)";
			Pattern p1 = java.util.regex.Pattern.compile(regex1);
			Matcher m1 = p1.matcher(s);
			String regex2 = "(PH)|(ph)|(((h)|H))((ours?)|(OURS?))|(/h)|(\\\\h)";
			Pattern p2 = java.util.regex.Pattern.compile(regex2);
			Matcher m2 = p2.matcher(s);
			String regex3 = "(PS)|(ps)|(((s)|S))((econds?)|(ECONDS?))|(/s)|(\\\\s)|(MIS)|(mis)";
			Pattern p3 = java.util.regex.Pattern.compile(regex3);
			Matcher m3 = p3.matcher(s);
			String regex4 = "(k|K)(nots|NOTS)|(k|K)(t|T)";
			Pattern p4 = java.util.regex.Pattern.compile(regex4);
			Matcher m4 = p4.matcher(s);
			if (m1.find()) {
				time = "min";
			} else if (m2.find()) {
				time = "hr";
			} else if (m3.find()) {
				time = "sec";
			} else if (m4.find()){
				time="hr";
			} else {
				throw (new IOException("cannot handle time unit " + s));
			}
			return time;
		} catch (Exception e) {
			LOG.error("Error parsing time unit " + s);
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private String findDistance(String s) throws Exception {
		try {
			String d = null;
			
			String regex1 = "((M)|(m))((iles?)|(ILES?))|(((m)|(M))((p)|(P))((h)|(H)))|((M|m)/(H|h))|(mi)|(MI)";
			Pattern p1 = java.util.regex.Pattern.compile(regex1);
			Matcher m1 = p1.matcher(s);
			String regex2 = "(NM)|(nmi)|((n)|(N))((autical)|(AUTICAL))";
			Pattern p2 = java.util.regex.Pattern.compile(regex2);
			Matcher m2 = p2.matcher(s);
			String regex3 = "(((f)|(F))((o{2})|(O{2})|(e{2})|(E{2}))((t)|(T)))|(f|F)";
			Pattern p3 = java.util.regex.Pattern.compile(regex3);
			Matcher m3 = p3.matcher(s);
			String regex4 = "(mps)|((m/))|(m|M(eter?|ETER?))";
			Pattern p4 = java.util.regex.Pattern.compile(regex4);
			Matcher m4 = p4.matcher(s);
			String regex5 = "(knots)|(kt)";
			Pattern p5 = java.util.regex.Pattern.compile(regex5,
					Pattern.CASE_INSENSITIVE);
			Matcher m5 = p5.matcher(s);
			String regex6 = "k|km";
			Pattern p6 = java.util.regex.Pattern.compile(regex6,
					Pattern.CASE_INSENSITIVE);
			Matcher m6 = p6.matcher(s);
			String regex7 = "hm";
			Pattern p7 = java.util.regex.Pattern.compile(regex7,
					Pattern.CASE_INSENSITIVE);
			Matcher m7 = p7.matcher(s);
			String regex8 = "min";
			Pattern p8 = java.util.regex.Pattern.compile(regex8,
					Pattern.CASE_INSENSITIVE);
			Matcher m8 = p8.matcher(s);
			if (m1.find()) {
				if (m6.find()) {
					d = "km";
				} else if (m7.find()) {
					d = "hm";
				} else if (m8.find()) {
					d = null;
				} else {
					d = "mi";
				}
			}
			if (d == null) {
				if (m2.find()) {
					d = "nm";
				} else if (m3.find()) {
					d = "ft";
				} else if (m5.find()) {
					d = "nm";
				}else if(m6.find())
				{
					d="km";
				} else if(m4.find()) {
					String mod = findMetricModifierDistance(s);
					if (mod.equals("kilo")) {
						d = "km";
					} else if (mod.equals("hecto")) {
						d = "hm";
					} else {
						d = "m";
					}
				} 
				else
				{
					throw(new IOException("Cannot handle distance unit " + s));
				}
			}

			return d;
		} catch (Exception e) {
			LOG.error("Error parsing distance unit " + s);
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}
	private String findMetricModifierDistance(String s) throws Exception
	{
		try {
			String modifier = "none";
			String regex1 = "k(ilo)|k";
			Pattern p1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
			Matcher m1 = p1.matcher(s);

			String regex2 = "h(ecto)|h";
			Pattern p2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
			Matcher m2 = p2.matcher(s);
			if (m1.find()) {
				modifier = "kilo";
			} else if (m2.find()) {
				modifier = "hecto";
			}
			else
			{
				modifier = "none";
			}
			return modifier;
		} catch (Exception e) {
			LOG.error("Error parsing metric modifier " + s);
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}
	private String findFrequencyModifier(String s) throws Exception {
		try {
			String modifier = "none";
			String regex1 = "(k([a-ln-z]{3}[^(meter)]))|(k )|(khz)|(k$)";
			Pattern p1 = java.util.regex.Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
			Matcher m1 = p1.matcher(s);
			String regex2 = "(M(hz))|(M(Hz))|(M(HZ))|((megahertz)|(MEGAHERTZ))";
			Pattern p2 = java.util.regex.Pattern.compile(regex2);
			Matcher m2 = p2.matcher(s);
			String regex3 = "(g(hz))|(giga(hertz))";
			Pattern p3 = java.util.regex.Pattern.compile(regex3, Pattern.CASE_INSENSITIVE);
			Matcher m3 = p3.matcher(s);
			String regex4 = "([^a-zA-Z](hz))|([^a-zA-Z](HZ))|(([^(MEGA)|(GIGA)|(KILO)])HERTZ)|(([^(mega)|(giga)|(kilo)])hertz)";
			Pattern p4 = java.util.regex.Pattern.compile(regex4);
			Matcher m4 = p4.matcher(s);
			
			if (m1.find()) {
				modifier = "kilo";
			} else if (m2.find()) {
				modifier = "mega";
			} else if (m3.find()) {
				modifier = "giga";
			//} //else if (m4.find()) {
				//modifier = "none";
			} else {
				modifier = "none";
			}
			return modifier;
		} catch (Exception e) {
			LOG.error("Error parsing frequency unit " + s);
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}
	
	private Boolean StringIsNullOrEmpty(String s)
	{
		if(s != null)
		{
			if(s.equals(""))
			{
				return true;
			}
			else return false;
		}
		else return true;
	}

}
