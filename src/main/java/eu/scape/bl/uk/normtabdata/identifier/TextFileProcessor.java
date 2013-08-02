package eu.scape.bl.uk.normtabdata.identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import eu.scape.bl.uk.normtabdata.dto.DataFileDetailsDto;
import eu.scape.bl.uk.normtabdata.dto.FileProcessingErrorsDto;
import eu.scape.bl.uk.normtabdata.normalisation.NormalisedFormat;
import eu.scape.bl.uk.normtabdata.parser.TextFileDataParser;

public class TextFileProcessor {
	
	private DataFileDetailsDto inputDataFile;
	private NormalisedFormat normalisedFormat;
	private ArrayList<FileProcessingErrorsDto> messages = new ArrayList<FileProcessingErrorsDto>();
	private TextFileDataParser parser;
	
	public TextFileProcessor (DataFileDetailsDto inputDataFile, 
							  NormalisedFormat normalisedFormat) 
								throws IOException {
		
		this.inputDataFile = inputDataFile;
		this.normalisedFormat = normalisedFormat;
		this.parser = new TextFileDataParser(inputDataFile, normalisedFormat );
	}
	
	public void process() throws IOException {
		// Open input file and read each line, passing it to the 
		File inputFile = new File (inputDataFile.getInputFilePath());
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new FileReader(inputFile));
		    String text = null;
		    int lineCounter = 0;
		    while ((text = reader.readLine()) != null) {
		    	lineCounter++;
		    	if (lineCounter == 1) {
		    		parser.startOfFile();
		    	}
		    	if (lineCounter > inputDataFile.getHeaderLine()) {
			    	processLine(text);
		    	}
		    }
		    parser.endOfFile();
		    parser.finish();
		    
		} catch (FileNotFoundException e) {
			messages.add(new FileProcessingErrorsDto("FATAL", "FileNotFoundException", e.getMessage()));
		    e.printStackTrace();
		} catch (IOException e) {
			messages.add(new FileProcessingErrorsDto("FATAL", "IOException", e.getMessage()));
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
	}
	
	private void processLine(String text) throws IOException {
		String delimiterString = inputDataFile.getDelimiter();
		String partialElement = "";
		if (inputDataFile.getQuoteCharacter() != null) delimiterString = inputDataFile.getQuoteCharacter() + delimiterString + inputDataFile.getQuoteCharacter();
		String[] elements = text.split(delimiterString);

		// Send each element to the parser
		for ( int i=0; i<elements.length; i++) {
			// Need to handle situation where a data element contains the delimiter
			// as part of the data not as a delimiter
			System.out.println("ELEMENT " + i + "=[" + elements[i] + "]");
			if ((elements[i] != null) && 
				(elements[i].length() > 0 ) &&
				((elements[i].charAt(0) == '\"') || 
				(elements[i].charAt(elements[i].length()-1) == '\"') ||
				(partialElement.length() > 0))) {		
				System.out.println("Found partial element [" + elements[i] + "]");
				if (partialElement.length() > 0) partialElement = partialElement + delimiterString;
				partialElement = partialElement + elements[i];
				// If the last character of the element is a quote can send the complete
				// element to the parser, after first removing the quotes
				if ((elements[i].charAt(elements[i].length()-1) == '\"')) {
					partialElement = partialElement.replaceAll("\"", "");
					System.out.println("Sending partial element to parser [" + partialElement + "]");
					parser.addElement(partialElement);
					partialElement = "";
				}
			} else {			
				parser.addElement(elements[i]);
			}
		} 
		parser.endOfLine();
	}

}
