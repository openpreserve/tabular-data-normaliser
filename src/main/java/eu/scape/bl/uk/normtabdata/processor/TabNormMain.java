package eu.scape.bl.uk.normtabdata.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

public class TabNormMain {

	public static void main(String[] args) throws Exception {
		
		Log log = LogFactory.getLog(TabNormMain.class);
		Logger logger = Logger.getLogger(TabNormMain.class);
		System.out.println("Main - checking number of args");
		log.debug("Main - checking number of args");
		logger.debug("Main - checking number of args");
		
		if ( args.length < 2) {
			System.out.println("Usage: ");
			System.out.println("   Properties file - file containing values that will direct the processing of the input/output files");
			System.out.println("   Input file/directory name - the path of the raw register file, or directory containing the raw register files");
			System.exit(1);
		} 

		// Validate input arguments 
		// First argument is the properties file
		String propertiesFileName = args[0];
		System.out.println("  Properties File = " + propertiesFileName);
		File propFile = new File(propertiesFileName);
		if (!propFile.exists()) { 
			throw new IOException("Unable to open properties file " + propertiesFileName);	
		}	
	
		// Remaining arguments are list of input files and/or directories
		ArrayList<String> inputFiles = new ArrayList<String>();
		for (int i=1; i<args.length; i++) {
			inputFiles.add(args[i]);	
			System.out.println("\tInput file/dir " + (i-1) + " = " + args[i]);
		}
		
		if (inputFiles == null || inputFiles.isEmpty()) {
			throw new Exception("No files to process");
		}

		TabNormProcessor processor = 
				new TabNormProcessor(propertiesFileName, inputFiles);
		
		processor.processRegister();
		
		System.exit(0);
	}	

}
