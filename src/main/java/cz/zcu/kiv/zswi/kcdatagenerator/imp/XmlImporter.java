package cz.zcu.kiv.zswi.kcdatagenerator.imp;

import java.io.InputStream;
import microsoft.exchange.webservices.data.core.EwsXmlReader;

/**
 * Class handles import of XML files on server.
 * @author Daniel Holubář
 *
 */
public class XmlImporter  extends EwsXmlReader {

	/**
	 * Stream with XML file.
	 */
	private InputStream stream;
	
	/**
	 * Constructor.
	 * @param stream stream with XML file
	 * @throws Exception
	 */
	public XmlImporter(InputStream stream) throws Exception { 
	    super(stream); 
	    this.stream = stream;
	}

	/**
	 * Imports XML.
	 * @throws Exception
	 */
	public void importXml() throws Exception {
		
	}
}
