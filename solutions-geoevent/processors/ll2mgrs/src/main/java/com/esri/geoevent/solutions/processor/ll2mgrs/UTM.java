package com.esri.geoevent.solutions.processor.ll2mgrs;

public class UTM {
	private int northing;
	private int easting;
	private int zoneNumber;
	private char zoneLetter;
	private Integer accuracy;

	public UTM(int northing, int easting, int zoneNumber, char zoneLetter, Integer accuracy) {

		this.northing = northing;
		this.easting = easting;
		this.zoneNumber = zoneNumber;
		this.zoneLetter = zoneLetter;
		this.accuracy = accuracy;
	}
	
	public int getNorthing() {
		return northing;
	}

	public int getEasting() {
		return easting;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public char getZoneLetter() {
		return zoneLetter;
	}
	
	public Integer getAccuracy()
	{
		return accuracy;
	}
	

	public String findHemisphere(String zoneLetter) {
		String hemisphere = null;
		if (zoneLetter.equals("A") || zoneLetter.equals("B")
				|| zoneLetter.equals("C") || zoneLetter.equals("D")
				|| zoneLetter.equals("E") || zoneLetter.equals("F")
				|| zoneLetter.equals("G") || zoneLetter.equals("H")
				|| zoneLetter.equals("I") || zoneLetter.equals("J")
				|| zoneLetter.equals("K") || zoneLetter.equals("L")
				|| zoneLetter.equals("M")) {
			hemisphere = "southern";
		} else {
			hemisphere = "northern";
		}
		return hemisphere;
	}
}