package eu.scape_project.tabdatanorm.identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.tika.Tika;

import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;

public class FileIdentifier {

	private ArrayList<String> inputFileList = new ArrayList<String>() ;
	private String outputLocation;
	private String auditLocation;

    public FileIdentifier(ArrayList<String> inputFileList, String outputLocation, String auditLocation) {     
    	this.inputFileList = inputFileList;   
    	this.outputLocation = outputLocation;
    	this.auditLocation = auditLocation;
    }
    
    public Hashtable<String, DataFileDetailsDto> identify() {   	
     	Hashtable<String, DataFileDetailsDto> dataFiles = identifyFiles(inputFileList, outputLocation, auditLocation);
    	return dataFiles;
    }
    

    private Hashtable<String, DataFileDetailsDto> identifyFiles(ArrayList<String> inputFileList, String outputLocation, String auditLocation) {
    	
    	System.out.println("  Identifying list of input files :-");
    	Hashtable<String, DataFileDetailsDto> dataFiles = 
    									new Hashtable<String, DataFileDetailsDto>();
    	
    	Tika tika = new Tika();
		Iterator<String> fileIterator = inputFileList.iterator();
		while ( fileIterator.hasNext() ) {
			File fileToIdentify = new File(fileIterator.next());
			DataFileDetailsDto dataFile = 
					new DataFileDetailsDto(fileToIdentify.getParent()+File.separator, fileToIdentify.getName(), outputLocation, auditLocation );
			try {
				String filetype = tika.detect(fileToIdentify);
				System.out.println("    File " + fileToIdentify + "=" + filetype);
				dataFile.setInputFileType(filetype);
			} catch ( IOException ioe ) {
				dataFile.addErrorMessage(ioe.getMessage());
				System.out.println("IOException: Failed to process " + fileToIdentify.getName() + " - " + ioe.getMessage());
			} catch ( Exception e ) {
				dataFile.addErrorMessage(e.getMessage());
				System.out.println("Exception: Failed to process " + fileToIdentify.getName() + " - " + 
						e.getClass() + ":" + e.getMessage());
			} finally {
				dataFiles.put(fileToIdentify.getPath(), dataFile);
			}
		}
		return dataFiles;
    }

}

