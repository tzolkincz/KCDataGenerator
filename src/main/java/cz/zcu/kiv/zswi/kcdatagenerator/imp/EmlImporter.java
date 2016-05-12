package cz.zcu.kiv.zswi.kcdatagenerator.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

/**
 * Class handles upload of .eml files on server.
 * @author Daniel Holubář
 *
 */
public class EmlImporter {

	/**
	 * List of email files.
	 */
	private ArrayList<File> emailsFiles;
	
	/**
	 * Serializer for import of .eml files
	 */
	private Serializer serializer;
	
	
	/**
	 * Constructor
	 * @param files .eml files with emails
	 */
	public EmlImporter(ArrayList<File> files) {
		this.emailsFiles = files;
		serializer = new Serializer();
	}

	/**
	 * Imports emails from files.
	 * @param generatedUsers list of generated users
	 * @param ewsUrl url for import via ews
	 * @param domainName name of domain
	 */
	public void importEml(List<GeneratedUser> generatedUsers, String ewsUrl, String domainName) {
		InputStream is = null;
		BufferedReader br = null;
		for(File file: emailsFiles) {
			try
	        {
			 	is = new FileInputStream(file);

			 	StringBuilder sb = new StringBuilder();
			 	br = new BufferedReader(new InputStreamReader(is));
			 	String read;

			 	while((read=br.readLine()) != null) {
			 	    sb.append(read);   
			 	}
			 	serializer.sendEmlToServer(sb.toString(), generatedUsers, ewsUrl, domainName, WellKnownFolderName.Inbox);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
		try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
