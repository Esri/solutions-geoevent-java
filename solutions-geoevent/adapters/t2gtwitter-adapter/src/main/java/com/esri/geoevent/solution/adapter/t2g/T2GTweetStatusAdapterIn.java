package com.esri.geoevent.solution.adapter.t2g;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.esri.geoevent.solution.adapter.t2g.T2GTweet;
import com.esri.geoevent.solution.adapter.t2g.T2GTweetStatusAdapterIn;
import com.esri.geoevent.solution.adapter.t2g.T2GTweet.BoundingBox;
import com.esri.geoevent.solution.adapter.t2g.T2GTweet.Coordinates;
import com.esri.geoevent.solution.adapter.t2g.T2GTweet.Place;
import com.esri.geoevent.solution.adapter.t2g.T2GTweet.User;
import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Point;

public class T2GTweetStatusAdapterIn extends InboundAdapterBase {

	private static final Log LOG = LogFactory
			.getLog(T2GTweetStatusAdapterIn.class);
	private ObjectMapper mapper = new ObjectMapper();
	private SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss Z yyyy");
	private Charset charset;
	private CharsetDecoder decoder;

	public enum locType {
		GEOLOC, TEXT
	};

	public AdapterDefinition t2gAdapterDef;

	public T2GTweetStatusAdapterIn(AdapterDefinition definition,
			AdapterDefinition t2gdef) throws ComponentException {
		super(definition);
		LOG.debug("T2G Tweet Status Adapter created");
		charset = Charset.forName("UTF-8");
		decoder = charset.newDecoder();
		t2gAdapterDef = t2gdef;
	}

	private class T2GTweetEventBuilder implements Runnable {
		private StringBuilder sb;

		T2GTweetEventBuilder(String text) {
			this.sb = new StringBuilder(text);
		}

		private List<GeoEvent> buildGeoEvent() throws Exception {
			// 3 lines below are not necessary I think. These were added when I
			// was having problems with encoding
			// //byte[] strBytes = sb.toString().getBytes("UTF-8");
			// //String s = new String(strBytes,"UTF-8");
			// //Tweet jsonTweet = mapper.readValue(s, Tweet.class);

			// atempt to parse string to Tweet
			T2GTweet jsonTweet = mapper
					.readValue(sb.toString(), T2GTweet.class);
			// consoleDebugPrintLn(sb.toString());
			if (jsonTweet == null) {

				consoleDebugPrintLn("jsonTweet is null");
				return null;
			}
			String text = jsonTweet.getText();
			// consoleDebugPrintLn(jsonTweet.getText());

			// Create an instance of the message using the guid that we
			// generated when we started up.
			List<GeoEvent> messages = new ArrayList<GeoEvent>();
			GeoEvent msg;
			try {
				AdapterDefinition def = (AdapterDefinition) definition;
				// XmlAdapterDefinition tweetDef = (XmlAdapterDefinition)
				// definition;
				// String name = tweetDef.getName();
				GeoEventDefinition geoDef = def
						.getGeoEventDefinition("T2GTweetStatus");
				if (geoEventCreator.getGeoEventDefinitionManager()
						.searchGeoEventDefinition(geoDef.getName(),
								geoDef.getOwner()) == null) {
					geoEventCreator.getGeoEventDefinitionManager()
							.addGeoEventDefinition(geoDef);
				}
				msg = geoEventCreator.create(geoDef.getName(),
						geoDef.getOwner());
				LOG.debug("Created new TweetStatusMessage");
			} catch (MessagingException e) {
				LOG.error("Message Creation error in TweetStatusAdapter: "
						+ e.getMessage());
				return null;
			} catch (Exception ex) {
				LOG.error("Error creating initial tweet message: "
						+ ex.getMessage());
				return null;
			}

			// Populate the message with all the attribute values.
			// first is geometry - get from tweet coordinates or place
			double x = Double.NaN;
			double y = Double.NaN;
			int wkid = 4326;
			Coordinates coords = jsonTweet.getCoordinates();
			Place place = jsonTweet.getPlace();
			User user = jsonTweet.getUser();
			if (coords != null) {
				x = coords.getCoordinates().get(0);
				y = coords.getCoordinates().get(1);
			}
			if (place != null) {
				// if still need coordinates0..

				// get bounding box of place and figure center
				if (Double.isNaN(x) && Double.isNaN(y)) {
					BoundingBox bbox = place.getBounding_box();
					if (bbox != null) {
						ArrayList<Double> ll = bbox.getCoordinates().get(0)
								.get(0);
						ArrayList<Double> ur = bbox.getCoordinates().get(0)
								.get(2);
						Double xmin = ll.get(0);
						Double xmax = ur.get(0);
						Double ymin = ll.get(1);
						Double ymax = ur.get(1);
						x = xmin + ((xmax - xmin) / 2);
						y = ymin + ((ymax - ymin) / 2);
					}
				}
				// set attributes in message associated with place
				FieldGroup placeGrp = msg.createFieldGroup("place");
				placeGrp.setField(0, place.getId());
				placeGrp.setField(1, place.getFull_name());
				placeGrp.setField(2, place.getUrl());
				msg.setField(13, placeGrp);
			}

			// set geometry in message if an xy coordinate was found
			// and set geolocated attribute to true or false
			if (!Double.isNaN(x) && !Double.isNaN(y)) {
				Point pt = spatial.createPoint(x, y, wkid);
				msg.setField(5, pt);
				msg.setField(15, true);
				LOG.debug("Generated tweet with location");
				// consoleDebugPrintLn("tweet with location");
			} else {
				msg.setField(15, false);
			}
			// String text = jsonTweet.getText();

			// set rest of attributes in message
			msg.setField(0, jsonTweet.getPossibly_sensitive_editable());
			msg.setField(1, text);
			String createdAt = jsonTweet.getCreated_at();
			try {
				if (createdAt != null)
					msg.setField(2, sdf.parse(jsonTweet.getCreated_at()));
			} catch (Exception e) {
				LOG.warn("Parse date exception in TweetStatusAdapter: "
						+ e.getMessage());
				LOG.debug(e.getMessage(), e);
			}
			msg.setField(3, jsonTweet.getRetweeted());
			msg.setField(4, jsonTweet.getRetweet_count());
			msg.setField(6, jsonTweet.getId_str());
			msg.setField(7, jsonTweet.getIn_reply_to_screen_name());
			msg.setField(8, jsonTweet.getIn_reply_to_status_id_str());
			msg.setField(9, jsonTweet.getFavorited());
			msg.setField(10, jsonTweet.getTruncated());
			msg.setField(11, jsonTweet.getPossibly_sensitive());
			msg.setField(12, jsonTweet.getIn_reply_to_user_id_str());
			if (user != null) {
				FieldGroup userGrp = msg.createFieldGroup("user");
				userGrp.setField(0, user.getId_str());
				userGrp.setField(1, user.getName());
				userGrp.setField(2, user.getFollowers_count());
				userGrp.setField(3, user.getLocation());
				userGrp.setField(4, user.getScreen_name());
				msg.setField(14, userGrp);
			}
			msg.setField(16, 0);
			msg.setField(17, "GeoLoc");
			messages.add(msg);

			String contentType = "application/json";
			HttpClient httpclient = new DefaultHttpClient();
			String encodedText = URLEncoder.encode(text, "UTF-8");
			String args = "textContent=" + encodedText;
			String uri = "http://stlpatrick.esri.com:9090/t2gservice/t2g?"
					+ args;
			try {
				HttpPost httppost = new HttpPost(uri);
				httppost.setHeader("Accept", contentType);
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();
					try {
						// instream.read();
						BufferedReader br = new BufferedReader(
								new InputStreamReader((instream)));
						String output = "";
						String ln;
						while ((ln = br.readLine()) != null) {
							output += ln;
						}
						JsonFactory jf = new JsonFactory();
						JsonParser jp = jf.createJsonParser(output);
						ObjectMapper mapper = new ObjectMapper();
						@SuppressWarnings("unchecked")
						List<Map<String, Object>> list = (List<Map<String, Object>>) mapper
								.readValue(jp, List.class);
						for (Map<String, Object> m : list) {
							Map<String,Object> geoname = (Map<String, Object>) m.get("geoname");
							x = (Double) geoname.get("longitude");
							y = (Double) geoname.get("latitude");
							Point pt = spatial.createPoint(x, y, wkid);
							GeoEvent t2gmsg;
							try {

								GeoEventDefinition t2ggeoDef = t2gAdapterDef
										.getGeoEventDefinition("T2GGeoTagStatus");
								if (geoEventCreator
										.getGeoEventDefinitionManager()
										.searchGeoEventDefinition(
												t2ggeoDef.getName(),
												t2ggeoDef.getOwner()) == null) {
									geoEventCreator
											.getGeoEventDefinitionManager()
											.addGeoEventDefinition(t2ggeoDef);
								}
								t2gmsg = geoEventCreator.create(
										t2ggeoDef.getName(),
										t2ggeoDef.getOwner());
								LOG.debug("Created new TweetStatusMessage");
								t2gmsg.setField(0, jsonTweet.getId_str());
								t2gmsg.setField(1, "TEXT");
								try {
									if (createdAt != null)
										t2gmsg.setField(2, sdf.parse(jsonTweet
												.getCreated_at()));
								} catch (Exception e) {
									LOG.warn("Parse date exception in TweetStatusAdapter: "
											+ e.getMessage());
									LOG.debug(e.getMessage(), e);
								}
								t2gmsg.setField(3, pt);
								messages.add(t2gmsg);
							} catch (MessagingException e) {
								LOG.error("Message Creation error in T2GGeoTag: "
										+ e.getMessage());
								return null;
							} catch (Exception ex) {
								LOG.error("Error creating t2ggeotag message: "
										+ ex.getMessage());
								return null;
							}
						}

					} catch (IOException ex) {
						// In case of an IOException the connection will be
						// released
						// back to the connection manager automatically
						throw ex;
					} catch (RuntimeException ex) {
						// In case of an unexpected exception you may want to
						// abort
						// the HTTP request in order to shut down the underlying
						// connection immediately.
						httppost.abort();
						throw ex;
					} finally {
						// Closing the input stream will trigger connection
						// release
						try {
							instream.close();
						} catch (Exception ignore) {
						}
					}
				}

			} catch (Exception e) {

			}
			return messages;
		}

		@Override
		public void run() {
			try {
				List<GeoEvent> events = buildGeoEvent();
				for (GeoEvent event : events) {
					if (event != null) {
						geoEventListener.receive(event);
					}
				}
			} catch (Throwable t) {
				LOG.error("Unexpected error", t);
			}
		}

	}

	public void receive(ByteBuffer buffer, String channelId) {
		if (!buffer.hasRemaining())
			return;

		try {
			CharBuffer charBuffer = decoder.decode(buffer);
			String text = charBuffer.toString();
			T2GTweetEventBuilder builder = new T2GTweetEventBuilder(text);
			Thread t = new Thread(builder);
			t.setName("Twitter Event Builder "
					+ System.identityHashCode(buffer));
			t.start();
		} catch (CharacterCodingException e) {
			LOG.warn("Could not decode the incoming buffer - " + e);
			buffer.clear();
			return;
		}
	}

	@Override
	protected GeoEvent adapt(ByteBuffer buffer, String channelId) {
		return null;
	}

	public static void consoleDebugPrintLn(String msg) {
		String consoleOut = System.getenv("GEP_CONSOLE_OUTPUT");
		if (consoleOut != null && "1".equals(consoleOut)) {
			System.out.println(msg);
			LOG.debug(msg);
		}
	}

	public static void consoleDebugPrint(String msg) {
		String consoleOut = System.getenv("GEP_CONSOLE_OUTPUT");
		if (consoleOut != null && "1".equals(consoleOut)) {
			System.out.print(msg);
			LOG.debug(msg);
		}
	}

	/*
	 * private List<ResolvedLocation> parseLocations(String text) throws
	 * Exception { try { GeoParser parser =
	 * GeoParserFactory.getDefault("C:/Dev/github/CLAVIN/IndexDirectory");
	 * List<ResolvedLocation>resolvedLocations = parser.parse(text);
	 * 
	 * 
	 * return resolvedLocations; } catch(Throwable t) {
	 * LOG.error("Unexpected error", t); return null; } }
	 */

}
