package eu.scape.bl.uk.normtabdata.identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.tika.Tika;

import eu.scape.bl.uk.normtabdata.dto.DataFileDetailsDto;
import eu.scape.bl.uk.normtabdata.normalisation.NormalisedFormat;
import eu.scape.bl.uk.normtabdata.parser.PDFFileDataParser;

public class PDFFileProcessor {

	private DataFileDetailsDto inputDataFile;
	private NormalisedFormat normalisedFormat;
	private PDFFileDataParser parser;
	
	public PDFFileProcessor(DataFileDetailsDto inputDataFile,
							NormalisedFormat normalisedFormat) 
		throws IOException {
		this.inputDataFile = inputDataFile;
		this.normalisedFormat = normalisedFormat;
		this.parser = new PDFFileDataParser(inputDataFile, normalisedFormat);
	}
	
	public void process() throws IOException {
		
		File inputFile = new File(inputDataFile.getInputFilePath());
    	Tika tika = new Tika();
    	
    	System.out.println("Contents of " + inputDataFile.getInputFilePath());
    	Reader reader = tika.parse(inputFile);

    	BufferedReader inputReader = new BufferedReader(reader);
    	String text = null;
		int lineCounter = 0;
    	while ((text = inputReader.readLine()) != null) {
			lineCounter++;
			System.out.println("Line " + lineCounter  + " " + text);
			if (lineCounter == 1) {
	    		parser.startOfFile();
			}
			processLine(text);
	    	if (lineCounter >= 200) {
	    		break;
	    	}
		}
    	inputDataFile.setInputLineCount(lineCounter);
	    parser.endOfFile();
	    parser.finish();
	}
	
	private void processLine(String text) throws IOException {
		String delimiterString = inputDataFile.getDelimiter();
		if (inputDataFile.getQuoteCharacter() != null) delimiterString = inputDataFile.getQuoteCharacter() + delimiterString + inputDataFile.getQuoteCharacter();
		System.out.println("Delimiter string is " + delimiterString);
		if (text != null && !text.isEmpty()) {
			String[] elements = text.split(delimiterString);
			System.out.println("Number of elements is " + elements.length);
			for ( int i=0; i<elements.length; i++) {
				System.out.println("  Element " + i + " is " + elements[i]);
				//parser.addElement(elements[i]);
			} 
			//parser.endOfLine();
		}
	}
	
}