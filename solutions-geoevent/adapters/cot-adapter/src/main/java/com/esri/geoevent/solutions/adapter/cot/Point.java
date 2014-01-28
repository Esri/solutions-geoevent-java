package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * Point.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
 * org.codehaus.mojo-license-maven-plugin-1.5
 * $Id: update-file-header-config.apt.vm 17764 2012-12-12 10:22:04Z tchemit $
 * $HeadURL: https://svn.codehaus.org/mojo/tags/license-maven-plugin-1.5/src/site/apt/examples/update-file-header-config.apt.vm $
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
