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


import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyException;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class PolygonProcessorDefinition extends GeoEventProcessorDefinitionBase {
	GeoEventDefinitionManager manager;
	private static final Log LOG = LogFactory
			.getLog(PolygonProcessorDefinition.class);
	public PolygonProcessorDefinition() {
		// TODO Auto-generated constructor stub
	}
	
	public void setManager(GeoEventDefinitionManager m) {
		try {
			this.manager = m;

			PropertyDefinition polygonEvtFld = new PropertyDefinition(
					"polyfld", PropertyType.String, "",
					"Field Containing polygon def",
					"Geoevent field containing polygon data", true, false);
			SetGeoEventAllowedFields(polygonEvtFld);
			propertyDefinitions.put(polygonEvtFld.getPropertyName(),
					polygonEvtFld);

			PropertyDefinition polygonFormat = new PropertyDefinition(
					"polyformat", PropertyType.String, "",
					"Format of incoming polygon", "Format of incoming polygon",
					true, false);
			polygonFormat.addAllowedValue("Json");
			polygonFormat.addAllowedValue("CAP");
			propertyDefinitions.put(polygonFormat.getPropertyName(),
					polygonFormat);

			PropertyDefinition procWKIDIn = new PropertyDefinition(
					"wkidin",
					PropertyType.Integer,
					4326,
					"Input WKID",
					"Coordinate system of input feature (overridden if defined in json geometry)",
					true, false);
			propertyDefinitions.put(procWKIDIn.getPropertyName(), procWKIDIn);

			PropertyDefinition procWKIDOut = new PropertyDefinition("wkidout",
					PropertyType.Integer, 4326, "Output WKID",
					"Output Coordinate system", true, false);
			propertyDefinitions.put(procWKIDOut.getPropertyName(), procWKIDOut);
		} catch (PropertyException e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		} catch (Exception e) {
			LOG.error("Geometry processor");
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		}
	}
	
	private void SetGeoEventAllowedFields(PropertyDefinition pd)
	{
		Collection<GeoEventDefinition> geodefs = this.manager.listAllGeoEventDefinitions();
		Iterator<GeoEventDefinition> it = geodefs.iterator();
		GeoEventDefinition geoEventDef;
		while (it.hasNext())
		{
			geoEventDef = it.next();
			String defName = geoEventDef.getName();
			for(int i = 0; i < geoEventDef.getFieldDefinitions().size(); ++i)
			{
				String fld = geoEventDef.getFieldDefinitions().get(i).getName();
				pd.addAllowedValue(defName + ":" + fld);
			}
		}
	}
	
	@Override
	public String getName() {
		return "PolygonProcessor";
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
		return "Polygon Processor";
	}

	@Override
	public String getDescription() {
		return "Sets event geometry to a polygon defined in a geoevent definition field";
	}

	@Override
	public String getContactInfo() {
		return "geoeventprocessor@esri.com";
  }

}
