package eu.scape.bl.uk.normtabdata.characteriser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.scape.bl.uk.normtabdata.utilities.Constants;

public class GovDataFileCharacteriser extends FileCharacteriser {

	public GovDataFileCharacteriser(String fileName) {
		this.fileName = fileName;
	}
	
	public GovDataFileCharacteriser(String fileName, Map<String, ArrayList<String>> headerColumnKeywords) {
		this.fileName = fileName;
		this.headerColumnKeywords = headerColumnKeywords;
	}
	
	public void process() throws IOException, Exception {
		// Open input file 
		System.out.println("Starting characterisation of " + fileName);
		
		super.process();
		
		System.out.println("Quoted content [" + this.quotedStrings + "]");
		// If delimiter found identify the header line 
		if (delimiter != null) {
			headerLine = identifyHeaderLine(textArray);
			if (headerLine <= 0 || this.keywordMatches < (columnIdentificationValues.size()/2)) {
				// If header line not found attempt to use column content regex values 
				// to identify the columns	
				headerLine = -1;
				messages.add("Unable to identify a header line. Attempting to use content to identify data");
				// Identify columns by regex strings or values
				headerColumnMapping = identifyHeaderColumnsByContent(textArray);
			} else {
				// Header line found, map columns in input file to those required in normalised output
				headerColumnMapping = identifyColumnHeaders(textArray.get(headerLine-1));
			} 
			printHeaderColumnMappings();
			characterisationCompleted = true;
		} else {
			messages.add("Unable to identify delimiter. Unable to process file");
		}
	}

}
