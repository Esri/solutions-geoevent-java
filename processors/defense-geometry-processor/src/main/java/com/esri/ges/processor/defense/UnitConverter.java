package com.esri.ges.processor.defense;

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
	public int findWkid(String unit)
	{
		return wkidLookup.get(unit);
	}
	
	public String findConnonicalName(String unit)
	{
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

