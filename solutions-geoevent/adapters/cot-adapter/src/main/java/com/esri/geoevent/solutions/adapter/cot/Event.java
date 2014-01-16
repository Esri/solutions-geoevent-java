package com.esri.geoevent.solutions.adapter.cot;
//import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Event {

	private String how=null;
	private String opex=null;
	private String qos=null;
	private Date stale=null;
	private Date start=null;
	private Date time=null;
	private String type=null;
	private String uid=null;
	private Double version=null;
	private String detail=null;
	private Point point=null;
	private String access=null;
	
	public Event() {

	}
	
	public Event(String how, String opex, String qos, Date stale,
			Date start, Date time, String type, String uid, Double version,
			String detail, Point point) {
		
		this.how = how;
		this.opex = opex;
		this.qos = qos;
		this.stale = stale;
		this.start = start;
		this.time = time;
		this.type = type;
		this.uid = uid;
		this.version = version;
		this.detail = trimDetailTag(detail);
		this.point = point;
	}



	@XmlAttribute
	public String getHow() {
		return how;
	}
	public void setHow(String how) {
		this.how = how;
	}
	@XmlAttribute
	public String getOpex() {
		return opex;
	}
	public void setOpex(String opex) {
		this.opex = opex;
	}
	@XmlAttribute
	public String getQos() {
		return qos;
	}
	public void setQos(String qos) {
		this.qos = qos;
	}
	@XmlAttribute
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getStale() {
		return stale;
	}
	public void setStale(Date stale) {
		this.stale = stale;
	}
	@XmlAttribute
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	@XmlAttribute
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	@XmlAttribute
	public Double getVersion() {
		return version;
	}
	public void setVersion(Double version) {
		this.version = version;
	}
	@XmlElement
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = trimDetailTag(detail);
	}
	
	@XmlElement
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	
	@XmlAttribute
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}

	
	private String trimDetailTag(String detail){
		String retStr=null;
		String trimmedDetail=detail.trim();
		String lowerCaseDetail=trimmedDetail.toLowerCase();
		/*
		 * if it starts and ends with detail tags (after being trimmed) then remove them and return result. 
		 * Otherwise leave the string as is.
		 */
		if (lowerCaseDetail.toLowerCase().startsWith("<detail>")&& lowerCaseDetail.endsWith("</detail>") ){
			retStr=trimmedDetail.substring(0,trimmedDetail.length()-9).substring(8);
			return retStr;	
		}
		else{
			return detail;
		}

	}
	
	 



	
	
}
