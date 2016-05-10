package cz.zcu.kiv.zswi.kcdatagenerator.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.mail.internet.MimeMessage;

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
	 * Constructor
	 * @param files .eml files with emails
	 */
	public EmlImporter(ArrayList<File> files) {
		this.emailsFiles = files;
	}

	/**
	 * Imports emails from files.
	 */
	public void importEml() {
		
		for(File file: emailsFiles) {
			try
	        {
			 	InputStream is = new FileInputStream(file);

	            MimeMessage message = new MimeMessage(null, is);
	            
	            System.out.println(message.getContent());
	            System.out.println(message.getSubject());
	            
	            is.close();
	        }
	        catch (Exception e)
	        {
	            System.out.println(e.getMessage());

	            e.printStackTrace();
	        }
		}
	}
}
