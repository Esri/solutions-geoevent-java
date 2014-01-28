package com.esri.geoevent.solutions.adapter.cot;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.spatial.Spatial;

public class CoTAdapterServiceInbound extends AdapterServiceBase
{
	Spatial spatial;
	String guid;
	GeoEventDefinitionManager geoEventDefManager;
	private ArrayList<CoTDetailsDeff> dynamicMessageAttributes;
	private List<FieldDefinition> fieldDefinitions;
	String xsdDirectory = null;
	public static final String COT_TYPES_PATH_LABEL = "CoT_Types_Path";
	public static final String XSD_PATH_LABEL = "XSD_Path";
	public static final String MAXIMUM_BUFFER_SIZE_LABEL = "Max_Buffer_Size";

	public CoTAdapterServiceInbound()
	{
		
		XmlAdapterDefinition xmlAdapterDef = new XmlAdapterDefinition(getResourceAsStream("input-adapter-definition.xml"));
		try {
			xmlAdapterDef.loadConnector(getResourceAsStream("input-connector-definition.xml"));
			definition = xmlAdapterDef;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setGeoEventDefManager(GeoEventDefinitionManager g)
	{
		geoEventDefManager = g;
	}
	
	public List<FieldDefinition> getFieldDefinitions() {
		return this.fieldDefinitions;

	}

	public ArrayList<CoTDetailsDeff> getDynamicMessageAttributes() {
		return this.dynamicMessageAttributes;
	}

	private String extractName(String nameAndType) {
		return nameAndType.substring(0, nameAndType.indexOf("&"));

	}

	private FieldType extractType(String nameAndType) {

		/*
		 * NEED TO ACCOUNT FOR THE FOLLOWING TYPES: FieldType.Date;
		 * FieldType.Boolean; FieldType.Double; DONE
		 * FieldType.Geometry; FieldType.Integer; DONE
		 * FieldType.Long; FieldType.Short; FieldType.String; DONE
		 */

		if (nameAndType.toLowerCase().contains("decimal")) {
			return FieldType.Double;

		} else if (nameAndType.toLowerCase().contains("integer")) {
			return FieldType.Integer;

		} else {
			return FieldType.String;

		}

	}
	@Override
	public Adapter createAdapter() throws ComponentException{
		try {
			return new CoTAdapterInbound(definition, guid);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}