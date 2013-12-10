package eu.scape_project.tabdatanorm.identifier;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;
import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;
import eu.scape_project.tabdatanorm.parser.PDFContentHandler;
import eu.scape_project.tabdatanorm.parser.PDFFileDataParser;

public class PDFFileProcessor2 {

	private DataFileDetailsDto inputDataFile;
	private NormalisedFormat normalisedFormat;
	private PDFFileDataParser parser;
	
	public PDFFileProcessor2(DataFileDetailsDto inputDataFile,
			NormalisedFormat normalisedFormat) 
					throws IOException {
		this.inputDataFile = inputDataFile;
		this.normalisedFormat = normalisedFormat;
		this.parser = new PDFFileDataParser(inputDataFile, normalisedFormat);
	}
	
	public void process() throws IOException, TikaException, SAXException {
		
		File inputFile = new File(inputDataFile.getInputFilePath());
    	InputStream is = TikaInputStream.get(inputFile);
    	
    	/*BufferedReader bf=  new BufferedReader(new FileReader(inputDataFile.getInputFilePath()));
    	String text;
    	int lineCount = 0;
    	while ((text = bf.readLine()) != null) {
    		lineCount++;
    		if (lineCount > 65 && lineCount < 500) {
    			if (text.contains("stream")) {
    				System.out.println("Line = " + lineCount);
    			}
    			System.out.println("Contents of input buffer = " + text);
    		}
    	}
    	
    	FileInputStream fis = new FileInputStream(inputFile);
    	BufferedInputStream bis = new BufferedInputStream(fis);
    	DataInputStream dis = new DataInputStream(bis);
    	while (dis.available() != 0 ) {
    		System.out.println("Contents of input buffer = " + dis.re
    		
    	}
    	*/
    	PDFParser pdfParser = new PDFParser();
    	pdfParser.setEnableAutoSpace(false);
    	ContentHandler handler = new PDFContentHandler();
    	((PDFContentHandler)handler).setParser(parser);
    	pdfParser.parse(is, handler, new Metadata(), new ParseContext());
    	
    	is.close();

    	
    	//System.out.println("Contents of " + inputDataFile.getInputFilePath());
    	//System.out.println("[" + text +"]");
    	//parser.parse();
/*
		int lineCounter = 0;
    	while (text  != null) {
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
	    parser.endOfFile(); */
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
