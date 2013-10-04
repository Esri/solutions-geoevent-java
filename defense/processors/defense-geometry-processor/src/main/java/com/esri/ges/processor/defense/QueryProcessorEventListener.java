package com.esri.ges.processor.defense;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.datastore.agsconnection.ArcGISServerConnection;
import com.esri.ges.datastore.agsconnection.ArcGISServerType;
import com.esri.ges.datastore.agsconnection.Field;
import com.esri.ges.datastore.agsconnection.Layer;
import com.esri.ges.manager.datastore.agsconnection.ArcGISServerConnectionManager;
import com.esri.ges.processor.GeoEventProcessorEvent;
import com.esri.ges.processor.GeoEventProcessorEventAction;
import com.esri.ges.processor.GeoEventProcessorEventListener;

public class QueryProcessorEventListener implements
		GeoEventProcessorEventListener {
	private QueryProcessorDefinition definition;
	private ArcGISServerConnectionManager connectionManager;
	public QueryProcessorEventListener(QueryProcessorDefinition d, ArcGISServerConnectionManager cm) {
		definition = d;
		connectionManager= cm;
	}

	@Override
	public void processorChanged(GeoEventProcessorEvent event) {
		if(event.getAction() == GeoEventProcessorEventAction.ADDED)
		{
			/*try {
				//AddQueryEventProcessor();
			} catch (PropertyException e) {
				e.printStackTrace();
			}*/
		}

	}
	
	private void AddQueryEventProcessor() throws PropertyException {
		GenerateProperties();
	}

	private void GenerateProperties() throws PropertyException {
		Map<String, PropertyDefinition> propertyDefinitions = definition
				.getPropertyDefinitions();
		Collection<ArcGISServerConnection> serviceConnections = this.connectionManager
				.getArcGISServerConnections();

		Iterator<ArcGISServerConnection> it = serviceConnections.iterator();
		ArcGISServerConnection conn;
		while (it.hasNext()) {
			conn = it.next();
			String[] folders = conn.getFolders();
			URL url = conn.getUrl();
			String baseUrl = url.getHost() + ":" + url.getPort()
					+ url.getPath() + "rest/services/";

			for (int i = 0; i < folders.length; ++i) {
				String path = baseUrl + folders[i] + "/";
				String folder = folders[i];
				String[] fservices = conn.getFeatureServices(folder);
				for (int j = 0; j < fservices.length; ++j) {
					String fs = fservices[j];
					path += fs + "/";
					String fqService = folder + "_" + fs;
					String pdName = "use_" + fqService;
					pdName = pdName.replace(" ", "_");
					PropertyDefinition pd = new PropertyDefinition(pdName,
							PropertyType.Boolean, false,
							"Query Service: " + fs, "Set query on service",
							true, false);
					propertyDefinitions.put(pd.getPropertyName(), pd);

					ArrayList<Layer> layers = (ArrayList<Layer>) conn
							.getLayers(folder, fs,
									ArcGISServerType.FeatureServer);
					for (int k = 0; k < layers.size(); ++k) {
						path += ((Integer) k).toString() + "/query?";
						String lyrName = fqService + "_"
								+ layers.get(k).getName();
						lyrName = lyrName.replace(" ", "_");
						PropertyDefinition pdLyr = new PropertyDefinition(
								lyrName, PropertyType.Boolean, false,
								"-->Layer: " + layers.get(k).getName(),
								"Set query on layer", true, false);
						pdLyr.setDependsOn(pdName + "=true");
						propertyDefinitions.put(pdLyr.getPropertyName(), pdLyr);

						PropertyDefinition pdwc = new PropertyDefinition(
								lyrName + "_whereclause", PropertyType.String,
								"", "-->Whereclause", "SQL whereclause string",
								false, false);
						pdwc.setDependsOn(lyrName + "=true");
						propertyDefinitions.put(pdwc.getPropertyName(), pdwc);
						Field[] fields = conn.getFields(folder, fs, k,
								ArcGISServerType.FeatureServer);
						for (int l = 0; l < fields.length; ++l) {
							String fld = fields[l].getName();
							String fldPropName = lyrName + fld;
							fldPropName = fldPropName.replace(" ", "_");
							PropertyDefinition pdFld = new PropertyDefinition(
									fldPropName, PropertyType.Boolean, false,
									"---->Field: " + fld, "Use Field", true,
									false);
							pdFld.setDependsOn(lyrName + "=true");
							propertyDefinitions.put(pdFld.getPropertyName(),
									pdFld);

							String fldToken = fldPropName + "_token";
							PropertyDefinition fldTokenPd = new PropertyDefinition(
									fldToken, PropertyType.String, lyrName
											+ '@' + fld, "------>" + lyrName
											+ '@' + fld + " token",
									"String token representation of variable",
									true, true);
							fldTokenPd.setDependsOn(fldPropName + "=true");
							propertyDefinitions.put(
									fldTokenPd.getPropertyName(), fldTokenPd);
						}
						PropertyDefinition pdItemConfig = new PropertyDefinition(
								lyrName + "_config",
								PropertyType.String,
								"",
								"-->Item configuration",
								"Use generated tokens to configure each record returned by query",
								false, false);
						pdItemConfig.setDependsOn(lyrName + "=true");
						propertyDefinitions.put(pdItemConfig.getPropertyName(),
								pdItemConfig);
					}

				}
			}
		}
	}

}
