package eu.scape_project.tabdatanorm.utilities;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.PasswordProvider;

public class PasswordFinder implements PasswordProvider {

	public String getPassword(Metadata metadata) {
		System.out.println("Getting password for " + metadata.get(Metadata.RESOURCE_NAME_KEY));
		
		return "passw0rd";
	
	}
}
