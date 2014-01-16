package com.esri.geoevent.solutions.processor.evc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.property.Property;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class EVCProcessor extends GeoEventProcessorBase {
	private long eventsPerInterval;
	private long intervalInMillis; // 1000 milliseconds is a second.
	private static final Log LOG = LogFactory.getLog(EVCProcessor.class);
	private Map<String, TrackRecord> trackCache = new HashMap<String, TrackRecord>();
	//private Map<Long, String> timeCache = new HashMap<Long, String>();
	private TrackRecord defaultTrack = new TrackRecord();
	private long lastCleanup = System.currentTimeMillis();

	class TrackRecord {
		long count;
		long lastTime = System.currentTimeMillis();
	}

	public EVCProcessor(GeoEventProcessorDefinition definition)
			throws ComponentException {
		super(definition);
		
	}
	@Override
	public GeoEvent process(GeoEvent dataMessage) throws Exception {
		intervalInMillis = (Long) properties.get("interval").getValue();
		eventsPerInterval = (Long) properties.get("epi").getValue();
		String filter = properties.get("filterType").getValueAsString();
		if(filter.equals("Max per Interval"))
		{
			return filterMaxPerInterval(dataMessage);
		}
		else
		{
			return filterByInterval(dataMessage);
		}
	}
	
	public GeoEvent filterMaxPerInterval(GeoEvent dataMessage) throws Exception {

		try {
			
			TrackRecord track = defaultTrack;
			String trackID = dataMessage.getTrackId();
			String guid = dataMessage.getGeoEventDefinition().getGuid();
			if (trackID != null) {
				String uid = guid + "_" + trackID;
				if (!trackCache.containsKey(uid))
					trackCache.put(uid, new TrackRecord());
				track = trackCache.get(uid);
			}

			long now = System.currentTimeMillis();
			if (now - track.lastTime < intervalInMillis) {
				track.count++;
				if (track.count > eventsPerInterval)
					return null;
			} else {
				track.lastTime = now;
				track.count = 1;
			}

			if (now - lastCleanup > 2 * intervalInMillis)
				cleanup();

			return dataMessage;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}
	
	private GeoEvent filterByInterval(GeoEvent dataMessage) throws Exception
	{
		try {
			
			TrackRecord track = defaultTrack;
			String trackID = dataMessage.getTrackId();
			String guid = dataMessage.getGeoEventDefinition().getGuid();
			if (trackID != null) {
				String uid = guid + "_" + trackID;
				if (!trackCache.containsKey(uid)) {
					trackCache.put(uid, new TrackRecord());
					return dataMessage;
				}
				track = trackCache.get(uid);
			}
			long now = System.currentTimeMillis();
			if (now - track.lastTime >= intervalInMillis) {
				track.lastTime = now;
			} else {
				return null;
			}
			if (now - lastCleanup > 2 * intervalInMillis)
				cleanup();
			return dataMessage;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
			throw (e);
		}
	}

	private void cleanup() {
		try {
			long now = System.currentTimeMillis();
			for (String track : trackCache.keySet()) {
				if (now - trackCache.get(track).lastTime > 4 * intervalInMillis)
					trackCache.remove(track);
			}
			lastCleanup = System.currentTimeMillis();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error(e.getStackTrace());
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(definition.getName());
		sb.append("/");
		sb.append(definition.getVersion());
		sb.append("[");
		for (Property p : getProperties()) {
			sb.append(p.getDefinition().getPropertyName());
			sb.append(":");
			sb.append(p.getValue());
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}

}