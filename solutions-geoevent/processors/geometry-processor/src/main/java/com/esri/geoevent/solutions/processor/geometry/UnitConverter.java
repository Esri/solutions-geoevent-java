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


import java.io.IOError;
import java.util.HashMap;

import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;

public class UnitConverter {
	private HashMap<String, Integer> wkidLookup = new HashMap<String, Integer>();
	private HashMap<String, String> cannonicalNameLookup = new HashMap<String, String>();
	public UnitConverter() {
		wkidLookup.put("Meter", 9001);
		wkidLookup.put("Kilometer", 9036);
		wkidLookup.put("Foot_US", 9003);
		wkidLookup.put("Mile_US", 9035);
		wkidLookup.put("Nautical_Mile", 9030);
		
		cannonicalNameLookup.put("Meters", "Meter");
		cannonicalNameLookup.put("Kilometers", "Kilometer");
		cannonicalNameLookup.put("Feet", "Foot_US");
		cannonicalNameLookup.put("Miles", "Mile_US");
		cannonicalNameLookup.put("Nautical Miles", "Nautical_Mile");
		
	}

	public int findWkid(String unit) {
		if (!wkidLookup.containsKey(unit)) {
			throw new IllegalArgumentException();
		}
		return wkidLookup.get(unit);
	}
	
	public String findConnonicalName(String unit) {
		if (!cannonicalNameLookup.containsKey(unit)) {
			throw new IllegalArgumentException();
		}
		return cannonicalNameLookup.get(unit);
	}

	public double Convert( double value, int wkidin, int wkidout)
	{
		Unit uin = new LinearUnit(wkidin);
		Unit uout = new LinearUnit(wkidout);
		value = Unit.convertUnits(value, uin, uout);
		return value;
	}
	
	public double Convert(double v, String unitsin, SpatialReference srout)
	{
		Unit u = srout.getUnit();
		String srcannonicalName = u.getName();
		int srwkid = findWkid(srcannonicalName);
		String cannonicalName = findConnonicalName(unitsin);
		int unitwkid = findWkid(cannonicalName);
		if(srwkid != unitwkid)
		{
			v = Convert(v, unitwkid, srwkid);
		}
		return v;
	}

}

