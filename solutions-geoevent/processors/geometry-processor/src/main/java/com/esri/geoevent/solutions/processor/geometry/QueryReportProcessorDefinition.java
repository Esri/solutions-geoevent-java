package com.esri.geoevent.solutions.processor.geometry;

/*
 * #%L
 * Esri :: AGES :: Solutions :: Processor :: Geometry
 * $Id:$
 * $HeadURL:$
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



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.datastore.agsconnection.Field;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.datastore.agsconnection.ArcGISServerConnection;
import com.esri.ges.datastore.agsconnection.ArcGISServerType;
import com.esri.ges.datastore.agsconnection.Layer;
import com.esri.ges.manager.datastore.agsconnection.ArcGISServerConnectionManager;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class QueryReportProcessorDefinition extends
		GeoEventProcessorDefinitionBase {
	public GeoEventDefinitionManager manager;
	public ArcGISServerConnectionManager connectionManager;
	private Tokenizer tokenizer = new Tokenizer();
	private static final Log LOG = LogFactory
			.getLog(QueryReportProcessorDefinition.class);

	public QueryReportProcessorDefinition() {

	}

	public void setManager(GeoEventDefinitionManager m) {
		this.manager = m;
	}

	public void setConnectionManager(ArcGISServerConnectionManager cm) {
		connectionManager = cm;
		GenerateProperties();
	}

	/*
	 * private void GeneratePropertiesold()throws PropertyException {
	 * PropertyDefinition procReportTitle = new PropertyDefinition("title",
	 * PropertyType.String, "", "Report Title", "Report Title", true, false);
	 * propertyDefinitions.put(procReportTitle.getPropertyName(),
	 * procReportTitle); PropertyDefinition procReportHeader = new
	 * PropertyDefinition("header", PropertyType.String, "", "Report Header",
	 * "Report Header", true, false);
	 * propertyDefinitions.put(procReportHeader.getPropertyName(),
	 * procReportHeader);
	 * 
	 * PropertyDefinition pdConn = new PropertyDefinition("connection",
	 * PropertyType.ArcGISConnection, "", "Data Store", "Data Store", true,
	 * false); propertyDefinitions.put(pdConn.getPropertyName(), pdConn);
	 * Collection<ArcGISServerConnection> serviceConnections =
	 * this.connectionManager.getArcGISServerConnections();
	 * Iterator<ArcGISServerConnection> it = serviceConnections.iterator();
	 * ArcGISServerConnection conn; while (it.hasNext()) { conn = it.next();
	 * String name=conn.getName(); //PropertyDefinition pdConn = new
	 * PropertyDefinition(name, PropertyType.Boolean, true, "Data Store",
	 * "Data Store", true, false);
	 * //propertyDefinitions.put(pdConn.getPropertyName(), pdConn);
	 * AddFolderProperties(conn); } }
	 * 
	 * private void AddFolderProperties(ArcGISServerConnection conn) throws
	 * PropertyException { String[] folders = conn.getFolders(); String connName
	 * = conn.getName(); for (int i=0; i< folders.length; ++i) { String folder =
	 * folders[i];
	 * 
	 * if(folder.equals("/")) { folder = "ROOT"; } if
	 * (conn.getFeatureServices(folder).length > 0) { String name =
	 * conn.getName() + folder; name = name.replace(" ", "_");
	 * PropertyDefinition pdFolder = new PropertyDefinition(name,
	 * PropertyType.ArcGISFolder, folder, "Folder: " + folder, "Folder", true,
	 * false); /*for(int j=0; j<folders.length; ++j) { if
	 * (conn.getFeatureServices(folders[j]).length > 0) {
	 * pdFolder.addAllowedValue(folders[j]); } } if(i > 0) {
	 * pdFolder.setMandatory(false); }
	 * pdFolder.setDependsOn("connection="+connName);
	 * propertyDefinitions.put(pdFolder.getPropertyName(), pdFolder);
	 * AddServiceProperties(name,folder,conn); } } }
	 * 
	 * private void AddServiceProperties(String parentName, String folder,
	 * ArcGISServerConnection conn) throws PropertyException { String[] services
	 * = conn.getFeatureServices(folder); for(int i=0; i < services.length; ++
	 * i) { String fs = services[i]; String name = parentName + fs; name =
	 * name.replace(" ", "_"); PropertyDefinition pdService= new
	 * PropertyDefinition(name, PropertyType.Boolean, false, "Service: " + fs,
	 * "Feature Service", true, false); pdService.setDependsOn(parentName + "="
	 * + folder); propertyDefinitions.put(pdService.getPropertyName(),
	 * pdService);
	 * 
	 * } }
	 */
	protected void GenerateProperties() {
		try {
			PropertyDefinition procReportName = new PropertyDefinition(
					"filename", PropertyType.String, "", "File name",
					"File name of generated report", true, false);
			propertyDefinitions.put(procReportName.getPropertyName(),
					procReportName);

			PropertyDefinition procReportTitle = new PropertyDefinition(
					"title", PropertyType.String, "", "Report Title",
					"Report Title", true, false);
			propertyDefinitions.put(procReportTitle.getPropertyName(),
					procReportTitle);

			PropertyDefinition procReportHeader = new PropertyDefinition(
					"header", PropertyType.String, "", "Report Header",
					"Report Header", true, false);
			propertyDefinitions.put(procReportHeader.getPropertyName(),
					procReportHeader);

			PropertyDefinition procGeometrySource = new PropertyDefinition(
					"geosrc", PropertyType.String, "",
					"Source of query geometry", "Source of query geometry",
					true, false);
			procGeometrySource.addAllowedValue("Geoevent");
			procGeometrySource.addAllowedValue("Event Definition");
			procGeometrySource.addAllowedValue("Buffer");
			propertyDefinitions.put(procGeometrySource.getPropertyName(),
					procGeometrySource);

			PropertyDefinition procGeometryEventDef = new PropertyDefinition(
					"geoeventdef", PropertyType.String, "",
					"Geometry Event Field",
					"Geoevent field containing buffer geometry data", false,
					false);
			procGeometryEventDef.setDependsOn("geosrc=Event Definition");
			SetGeoEventAllowedFields(procGeometryEventDef);
			propertyDefinitions.put(procGeometryEventDef.getPropertyName(),
					procGeometryEventDef);

			PropertyDefinition procSetAsEventGeo = new PropertyDefinition(
					"setgeo", PropertyType.Boolean, false,
					"Set events geometry to input",
					"Set geoevent's geometry to input geometry", false, false);
			procSetAsEventGeo.setDependsOn("geosrc=Event Definition");
			propertyDefinitions.put(procSetAsEventGeo.getPropertyName(),
					procSetAsEventGeo);

			PropertyDefinition procRadius = new PropertyDefinition("radius",
					PropertyType.Double, 1000, "Radius", "Query Radius", true,
					false);
			procRadius.setDependsOn("geosrc=Buffer");
			propertyDefinitions.put(procRadius.getPropertyName(), procRadius);

			PropertyDefinition procUnits = new PropertyDefinition("units",
					PropertyType.String, 0, "Units", "Units of measurement",
					true, false);
			procUnits.setDependsOn("geosrc=Buffer");
			procUnits.addAllowedValue("Meters");
			procUnits.addAllowedValue("Kilometers");
			procUnits.addAllowedValue("Feet");
			procUnits.addAllowedValue("Miles");
			procUnits.addAllowedValue("Nautical Miles");
			propertyDefinitions.put(procUnits.getPropertyName(), procUnits);

			PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin",
					PropertyType.Integer, 4326, "Input WKID",
					"Coordinate system of input feature", true, false);
			propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);

			PropertyDefinition procWKIDBuffer = new PropertyDefinition(
					"wkidbuffer", PropertyType.Integer, 3857, "Processor WKID",
					"Coordinate system to calculate the buffer", true, false);
			procWKIDBuffer.setDependsOn("geosrc=Buffer");
			propertyDefinitions.put(procWKIDBuffer.getPropertyName(),
					procWKIDBuffer);

			PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout",
					PropertyType.Integer, 4326, "Output WKID",
					"Output Coordinate system", true, false);
			propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);

			PropertyDefinition procUseTimeStamp = new PropertyDefinition(
					"usetimestamp",
					PropertyType.Boolean,
					false,
					"Generate timestamp token",
					"Generate a token for timestamp (must be defined in geoevent definition)",
					true, false);
			propertyDefinitions.put(procUseTimeStamp.getPropertyName(),
					procUseTimeStamp);

			PropertyDefinition procTimeStamp = new PropertyDefinition(
					"timestamp", PropertyType.String, "", "Timestamp field",
					"Geoevent definition field of timestamp", false, false);
			procTimeStamp.setDependsOn("usetimestamp=true");
			SetGeoEventAllowedFields(procTimeStamp);
			propertyDefinitions.put(procTimeStamp.getPropertyName(),
					procTimeStamp);

			PropertyDefinition procTimeStampToken = new PropertyDefinition(
					"timestamptoken",
					PropertyType.String,
					"[$TIMESTAMP]",
					"Timestamp token",
					"Timestamp token (add token to header and item configs to display timestam in reports)",
					false, true);
			procTimeStampToken.setDependsOn("usetimestamp=true");
			propertyDefinitions.put(procTimeStampToken.getPropertyName(),
					procTimeStampToken);

			PropertyDefinition procHost = new PropertyDefinition("host",
					PropertyType.String, "", "Host", "Geoevent Server Host",
					true, false);
			propertyDefinitions.put(procHost.getPropertyName(), procHost);
			
			PropertyDefinition pConn = new PropertyDefinition("connection", PropertyType.ArcGISConnection, null, "Connection", "Connection to registered server", true, false);
			propertyDefinitions.put(pConn.getPropertyName(), pConn);
			
			PropertyDefinition pFolder = new PropertyDefinition("folder", PropertyType.ArcGISFolder, null, "Folder", "Service Folder", true, false);
			propertyDefinitions.put(pFolder.getPropertyName(), pFolder);
			
			PropertyDefinition pService = new PropertyDefinition("service", PropertyType.ArcGISFeatureService, null, "Feature Service", "Service to query", true, false);
			propertyDefinitions.put(pService.getPropertyName(), pService);
			
			PropertyDefinition pLayer = new PropertyDefinition("layer", PropertyType.ArcGISLayer, null, "Layer", "layer to query", true, false);
			propertyDefinitions.put(pLayer.getPropertyName(), pLayer);
			
			PropertyDefinition pdLyrHeader = new PropertyDefinition("lyrheader", PropertyType.String, "", "Layer Heading", "", false, false);
			propertyDefinitions.put(pdLyrHeader.getPropertyName(), pdLyrHeader);
					  
			PropertyDefinition pdwc = new PropertyDefinition( "wc", PropertyType.String, "", "Whereclause","SQL whereclause string", false, false); 
			propertyDefinitions.put(pdwc.getPropertyName(), pdwc);

			PropertyDefinition pdCalculateDistance = new PropertyDefinition(
					"calcDistance", PropertyType.Boolean, false,
					"Calculate distance to feature",
					"Calculate distance to each feature", false, false);
			propertyDefinitions.put(pdCalculateDistance.getPropertyName(),
					pdCalculateDistance);

			PropertyDefinition procDistUnits = new PropertyDefinition(
					"dist_units", PropertyType.String, 0, "Units",
					"Units of measurement", true, false);
			procDistUnits.addAllowedValue("Meters");
			procDistUnits.addAllowedValue("Kilometers");
			procDistUnits.addAllowedValue("Feet");
			procDistUnits.addAllowedValue("Miles");
			procDistUnits.addAllowedValue("Nautical Miles");
			procDistUnits.setDependsOn("calcDistance=true");
			propertyDefinitions.put(procDistUnits.getPropertyName(),
					procDistUnits);
			String tk = tokenizer.tokenize("Distance", "dist");
			PropertyDefinition pdDistanceToken = new PropertyDefinition(
					"dist_token",
					PropertyType.String,
					tk,
					"Distance token",
					"Replace in item configuration to add distanc from event to feature",
					false, true);
			pdDistanceToken.setDependsOn("calcDistance=true");
			propertyDefinitions.put(pdDistanceToken.getPropertyName(),
					pdDistanceToken);

			PropertyDefinition pField = new PropertyDefinition("field",
					PropertyType.ArcGISField, null, "Layer", "layer to query",
					true, false);
			propertyDefinitions.put(pField.getPropertyName(), pField);

			tk = tokenizer.tokenize("item", null);
			PropertyDefinition fldTokenPd = new PropertyDefinition(
					"field-token", PropertyType.String, tk, "Field token",
					"String token representation of variable", true, true);
			propertyDefinitions.put(fldTokenPd.getPropertyName(), fldTokenPd);

			PropertyDefinition pdItemConfig = new PropertyDefinition(
					"item-config",
					PropertyType.String,
					"",
					"Item configuration",
					"Use generated tokens to configure each record returned by query",
					false, false);
			propertyDefinitions.put(pdItemConfig.getPropertyName(),
					pdItemConfig);
			
		} catch (PropertyException e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			return;
		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			return;
		}
	}

	/*
	 * protected void GenerateProperties() { try {
	 * 
	 * PropertyDefinition procReportName = new PropertyDefinition( "filename",
	 * PropertyType.String, "", "File name", "File name of generated report",
	 * true, false); propertyDefinitions.put(procReportName.getPropertyName(),
	 * procReportName);
	 * 
	 * PropertyDefinition procReportTitle = new PropertyDefinition( "title",
	 * PropertyType.String, "", "Report Title", "Report Title", true, false);
	 * propertyDefinitions.put(procReportTitle.getPropertyName(),
	 * procReportTitle);
	 * 
	 * PropertyDefinition procReportHeader = new PropertyDefinition( "header",
	 * PropertyType.String, "", "Report Header", "Report Header", true, false);
	 * propertyDefinitions.put(procReportHeader.getPropertyName(),
	 * procReportHeader);
	 * 
	 * PropertyDefinition procGeometrySource = new PropertyDefinition( "geosrc",
	 * PropertyType.String, "", "Source of query geometry",
	 * "Source of query geometry", true, false);
	 * procGeometrySource.addAllowedValue("Geoevent");
	 * procGeometrySource.addAllowedValue("Event Definition");
	 * procGeometrySource.addAllowedValue("Buffer");
	 * propertyDefinitions.put(procGeometrySource.getPropertyName(),
	 * procGeometrySource);
	 * 
	 * PropertyDefinition procGeometryEventDef = new PropertyDefinition(
	 * "geoeventdef", PropertyType.String, "", "Geometry Event Field",
	 * "Geoevent field containing buffer geometry data", false, false);
	 * procGeometryEventDef.setDependsOn("geosrc=Event Definition");
	 * SetGeoEventAllowedFields(procGeometryEventDef);
	 * propertyDefinitions.put(procGeometryEventDef.getPropertyName(),
	 * procGeometryEventDef);
	 * 
	 * PropertyDefinition procSetAsEventGeo = new PropertyDefinition( "setgeo",
	 * PropertyType.Boolean, false, "Set events geometry to input",
	 * "Set geoevent's geometry to input geometry", false, false);
	 * procSetAsEventGeo.setDependsOn("geosrc=Event Definition");
	 * propertyDefinitions.put(procSetAsEventGeo.getPropertyName(),
	 * procSetAsEventGeo);
	 * 
	 * PropertyDefinition procRadius = new PropertyDefinition("radius",
	 * PropertyType.Double, 1000, "Radius", "Query Radius", true, false);
	 * procRadius.setDependsOn("geosrc=Buffer");
	 * propertyDefinitions.put(procRadius.getPropertyName(), procRadius);
	 * 
	 * PropertyDefinition procUnits = new PropertyDefinition("units",
	 * PropertyType.String, 0, "Units", "Units of measurement", true, false);
	 * procUnits.setDependsOn("geosrc=Buffer");
	 * procUnits.addAllowedValue("Meters");
	 * procUnits.addAllowedValue("Kilometers");
	 * procUnits.addAllowedValue("Feet"); procUnits.addAllowedValue("Miles");
	 * procUnits.addAllowedValue("Nautical Miles");
	 * propertyDefinitions.put(procUnits.getPropertyName(), procUnits);
	 * 
	 * PropertyDefinition procWKIDIn = new PropertyDefinition("wkidin",
	 * PropertyType.Integer, 4326, "Input WKID",
	 * "Coordinate system of input feature", true, false);
	 * propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);
	 * 
	 * PropertyDefinition procWKIDBuffer = new PropertyDefinition( "wkidbuffer",
	 * PropertyType.Integer, 3857, "Processor WKID",
	 * "Coordinate system to calculate the buffer", true, false);
	 * procWKIDBuffer.setDependsOn("geosrc=Buffer");
	 * propertyDefinitions.put(procWKIDBuffer.getPropertyName(),
	 * procWKIDBuffer);
	 * 
	 * PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout",
	 * PropertyType.Integer, 4326, "Output WKID", "Output Coordinate system",
	 * true, false); propertyDefinitions.put(procWKIDOut.getPropertyName(),
	 * procWKIDOut);
	 * 
	 * PropertyDefinition procUseTimeStamp = new PropertyDefinition(
	 * "usetimestamp", PropertyType.Boolean, false, "Generate timestamp token",
	 * "Generate a token for timestamp (must be defined in geoevent definition)"
	 * , true, false);
	 * propertyDefinitions.put(procUseTimeStamp.getPropertyName(),
	 * procUseTimeStamp);
	 * 
	 * PropertyDefinition procTimeStamp = new PropertyDefinition( "timestamp",
	 * PropertyType.String, "", "Timestamp field",
	 * "Geoevent definition field of timestamp", false, false);
	 * procTimeStamp.setDependsOn("usetimestamp=true");
	 * SetGeoEventAllowedFields(procTimeStamp);
	 * propertyDefinitions.put(procTimeStamp.getPropertyName(), procTimeStamp);
	 * 
	 * PropertyDefinition procTimeStampToken = new PropertyDefinition(
	 * "timestamptoken", PropertyType.String, "[$TIMESTAMP]", "Timestamp token",
	 * "Timestamp token (add token to header and item configs to display timestam in reports)"
	 * , false, true); procTimeStampToken.setDependsOn("usetimestamp=true");
	 * propertyDefinitions.put(procTimeStampToken.getPropertyName(),
	 * procTimeStampToken);
	 * 
	 * PropertyDefinition procHost = new PropertyDefinition("host",
	 * PropertyType.String, "", "Host", "Geoevent Server Host", true, false);
	 * propertyDefinitions.put(procHost.getPropertyName(), procHost);
	 * 
	 * Collection<ArcGISServerConnection> serviceConnections =
	 * this.connectionManager .getArcGISServerConnections(); if
	 * (serviceConnections == null) { LOG.info("No service connections found:");
	 * return; }
	 * 
	 * 
	 * Iterator<ArcGISServerConnection> it = serviceConnections.iterator();
	 * ArcGISServerConnection conn; while (it.hasNext()) { conn = it.next();
	 * String[] folders = conn.getFolders(); // URL url = conn.getUrl();
	 * 
	 * for (int i = 0; i < folders.length; ++i) {
	 * 
	 * String folder = folders[i]; String[] fservices =
	 * conn.getFeatureServices(folder); for (int j = 0; j < fservices.length;
	 * ++j) { String fs = fservices[j];
	 * 
	 * String fqService = folder + "_" + fs; String pdName = "use_" + fqService;
	 * pdName = pdName.replace(" ", "_"); PropertyDefinition pd = new
	 * PropertyDefinition(pdName, PropertyType.Boolean, false, "Query Service: "
	 * + fs, "Set query on service", true, false);
	 * propertyDefinitions.put(pd.getPropertyName(), pd);
	 * 
	 * ArrayList<Layer> layers = (ArrayList<Layer>) conn .getLayers(folder, fs,
	 * ArcGISServerType.FeatureServer); for (int k = 0; k < layers.size(); ++k)
	 * {
	 * 
	 * String lyrName = fqService + "_" + ((Integer) k).toString(); lyrName =
	 * lyrName.replace(" ", "_"); PropertyDefinition pdLyr = new
	 * PropertyDefinition( lyrName, PropertyType.Boolean, false, "-->Layer: " +
	 * layers.get(k).getName(), "Set query on layer", true, false);
	 * pdLyr.setDependsOn(pdName + "=true");
	 * 
	 * propertyDefinitions.put(pdLyr.getPropertyName(), pdLyr);
	 * 
	 * PropertyDefinition pdLyrHeader = new PropertyDefinition( lyrName +
	 * "_header", PropertyType.String, "", "-->" + layers.get(k).getName() +
	 * " heading", "Set header on " + layers.get(k).getName(), false, false);
	 * pdLyrHeader.setDependsOn(lyrName + "=true"); propertyDefinitions.put(
	 * pdLyrHeader.getPropertyName(), pdLyrHeader);
	 * 
	 * PropertyDefinition pdwc = new PropertyDefinition( lyrName +
	 * "_whereclause", PropertyType.String, "", "-->Whereclause",
	 * "SQL whereclause string", false, false); pdwc.setDependsOn(lyrName +
	 * "=true"); propertyDefinitions.put(pdwc.getPropertyName(), pdwc);
	 * 
	 * PropertyDefinition pdCalculateDistance = new PropertyDefinition( lyrName
	 * + "_calcDistance", PropertyType.Boolean, false, "-->" +
	 * "Calculate distance to feature", "Calculate distance to each feature",
	 * false, false); pdCalculateDistance.setDependsOn(lyrName + "=true");
	 * propertyDefinitions.put( pdCalculateDistance.getPropertyName(),
	 * pdCalculateDistance);
	 * 
	 * PropertyDefinition procDistUnits = new PropertyDefinition( lyrName +
	 * "_dist_units", PropertyType.String, 0, "Units", "Units of measurement",
	 * true, false); procDistUnits.addAllowedValue("Meters");
	 * procDistUnits.addAllowedValue("Kilometers");
	 * procDistUnits.addAllowedValue("Feet");
	 * procDistUnits.addAllowedValue("Miles");
	 * procDistUnits.addAllowedValue("Nautical Miles");
	 * procDistUnits.setDependsOn(lyrName + "_calcDistance=true");
	 * propertyDefinitions.put( procDistUnits.getPropertyName(), procDistUnits);
	 * String tk = tokenizer.tokenize(lyrName, "dist"); PropertyDefinition
	 * pdDistanceToken = new PropertyDefinition( lyrName + "_dist_token",
	 * PropertyType.String, tk, "-->" + "Distance token",
	 * "Replace in item configuration to add distanc from event to feature",
	 * false, true); pdDistanceToken.setDependsOn(lyrName +
	 * "_calcDistance=true"); propertyDefinitions.put(
	 * pdDistanceToken.getPropertyName(), pdDistanceToken);
	 * 
	 * Field[] fields = conn.getFields(folder, fs, k,
	 * ArcGISServerType.FeatureServer); for (int l = 0; l < fields.length; ++l)
	 * { String fld = fields[l].getName(); String fldPropName = lyrName + fld;
	 * fldPropName = fldPropName.replace(" ", "_"); PropertyDefinition pdFld =
	 * new PropertyDefinition( fldPropName, PropertyType.Boolean, false,
	 * "---->Field: " + fld, "Use Field", true, false);
	 * pdFld.setDependsOn(lyrName + "=true"); propertyDefinitions.put(
	 * pdFld.getPropertyName(), pdFld);
	 * 
	 * String fldToken = fldPropName + "_token"; tk = lyrName + '@' + fld; tk =
	 * tokenizer.tokenize(tk, null); PropertyDefinition fldTokenPd = new
	 * PropertyDefinition( fldToken, PropertyType.String, tk, "------>" +
	 * lyrName + '@' + fld + " token",
	 * "String token representation of variable", true, true);
	 * fldTokenPd.setDependsOn(fldPropName + "=true"); propertyDefinitions.put(
	 * fldTokenPd.getPropertyName(), fldTokenPd); } PropertyDefinition
	 * pdItemConfig = new PropertyDefinition( lyrName + "_config",
	 * PropertyType.String, "", "-->Item configuration",
	 * "Use generated tokens to configure each record returned by query", false,
	 * false); pdItemConfig.setDependsOn(lyrName + "=true");
	 * propertyDefinitions.put( pdItemConfig.getPropertyName(), pdItemConfig); }
	 * 
	 * } } }
	 * 
	 * } catch(PropertyException e) { LOG.error("Geometry processor");
	 * LOG.error(e.getMessage()); LOG.error(e.getStackTrace()); return; } catch
	 * (Exception e) { LOG.error("Geometry processor");
	 * LOG.error(e.getMessage()); LOG.error(e.getStackTrace()); return; } }
	 */

	private void SetGeoEventAllowedFields(PropertyDefinition pd) {
		Collection<GeoEventDefinition> geodefs = this.manager
				.listAllGeoEventDefinitions();
		ArrayList<String> names = new ArrayList<String>();
		HashMap<String, GeoEventDefinition> defMap = new HashMap<String, GeoEventDefinition>();
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext()) {
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			names.add(defName);
			defMap.put(defName, geoEventDef);
		}
		Collections.sort(names);
		for (String name : names) {
			geoEventDef = defMap.get(name);
			List<FieldDefinition> fldDefs = geoEventDef.getFieldDefinitions();
			for (FieldDefinition f : fldDefs) {
				pd.addAllowedValue(name + ":" + f.getName());
			}
		}
	}

	@Override
	public String getName() {
		return "QueryReportProcessor";
	}

	@Override
	public String getDomain() {
		return "com.esri.geoevent.solutions.processor.geometry";
	}

	@Override
	public String getVersion() {
		return "10.2.0";
	}

	@Override
	public String getLabel() {
		return "Query Report Processor";
	}

	@Override
	public String getDescription() {
		return "Executes spatial query and returns configured report";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
	}

}
