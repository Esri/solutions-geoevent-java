package com.esri.geoevent.solution.adapter.t2g;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.geoevent.solution.adapter.t2g.T2GTweetStatusAdapterIn;
import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;


public class T2GTweetStatusAdapterService extends AdapterServiceBase {
	private static final Log LOG    = LogFactory.getLog(T2GTweetStatusAdapterService.class);
	private  AdapterDefinition t2ggeotagdef;
	public T2GTweetStatusAdapterService() throws ClassNotFoundException {
		try
		{
			Class.forName("org.apache.lucene.codecs.lucene40.Lucene40Codec");
		}
		catch(ClassNotFoundException e)
		{
			LOG.info("Cant find Lucene40Codec");
		}
		catch(NoClassDefFoundError e)
		{
			LOG.info("No class Def Found Lucene40Codec");
			LOG.info(e.getStackTrace());
		}
		LOG.info("Successfully Loaded Lucene40Codec!");
		try
		{
			Class.forName("org.apache.lucene.codecs.Codec");
		}
		catch(ClassNotFoundException e)
		{
			LOG.info("Cant find Codec Class");
		}
		LOG.info("Successfully Loaded Codec Class!");
		XmlAdapterDefinition xmlAdapterDefinition = new XmlAdapterDefinition(getResourceAsStream("t2gtweetstatus-adapter-definition.xml"));
		XmlAdapterDefinition xmlAdapterDefinition2 = new XmlAdapterDefinition(getResourceAsStream("t2ggeotagstatus-definition.xml"));
	    try
	    {
	      xmlAdapterDefinition.loadConnector(getResourceAsStream("input-connector-definition.xml"));
	      xmlAdapterDefinition.loadConnector(getResourceAsStream("output-connector-definition.xml"));
	    }
	    catch (JAXBException e)
	    {
	      throw new RuntimeException(e);
	    }
	    definition = xmlAdapterDefinition;
	    t2ggeotagdef = xmlAdapterDefinition2;
	}

	@Override
	public Adapter createAdapter() throws ComponentException {
		return new T2GTweetStatusAdapterIn(definition, t2ggeotagdef);
	}

}
