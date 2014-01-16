package com.esri.geoevent.solutions.adapter.cot;
import javax.xml.bind.annotation.XmlAttribute;


public class Point {
	double ce;
	double hae;
	double lat;
	double le;
	double lon;

	public Point() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Point(double ce, double hae, double lat, double le, double lon) {
		super();
		this.ce = ce;
		this.hae = hae;
		this.lat = lat;
		this.le = le;
		this.lon = lon;
	}
	
	@XmlAttribute
	public double getCe() {
		return ce;
	}
	public void setCe(double ce) {
		this.ce = ce;
	}
	
	@XmlAttribute
	public double getHae() {
		return hae;
	}
	public void setHae(double hae) {
		this.hae = hae;
	}
	
	@XmlAttribute
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	@XmlAttribute
	public double getLe() {
		return le;
	}
	public void setLe(double le) {
		this.le = le;
	}
	
	@XmlAttribute
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	

}
