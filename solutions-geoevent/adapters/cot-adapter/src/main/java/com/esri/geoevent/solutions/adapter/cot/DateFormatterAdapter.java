package com.esri.geoevent.solutions.adapter.cot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateFormatterAdapter extends XmlAdapter<String, Date> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");

    @Override
    public Date unmarshal(final String v) throws Exception {
    	dateFormat.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return dateFormat.parse(v);
    }

    @Override
    public String marshal(final Date v) throws Exception {
    	dateFormat.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return dateFormat.format(v);
    }
}