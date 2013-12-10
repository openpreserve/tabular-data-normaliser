package eu.scape_project.tabdatanorm.identifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import eu.scape_project.tabdatanorm.characteriser.BLFileCharacteriser;
import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;
import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;
import eu.scape_project.tabdatanorm.parser.PDFFileDataParser;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class FileProcessor {

	private DataFileDetailsDto inputDataFile;
	private NormalisedFormat normalisedFormat;
	
	public FileProcessor(DataFileDetailsDto inputDataFile, NormalisedFormat normalisedFormat) {
		this.inputDataFile = inputDataFile;
		this.normalisedFormat = normalisedFormat;
	}
	
    public void process() throws IOException, Exception {
    
		File fileToProcess = new File(inputDataFile.getInputFilePath());
		System.out.println("Processing " + fileToProcess.getPath());
		if (Constants.FILE_MIME_TYPE_PLAIN_TEXT.equals(inputDataFile.getInputFileType()) ||
			Constants.FILE_MIME_TYPE_CSV_TEXT.equals(inputDataFile.getInputFileType())) {
			System.out.println("File identified as plain text");
			
			// Process the file based on its identified characteristics
			TextFileProcessor textFileProcessor = new TextFileProcessor(inputDataFile, normalisedFormat);
			textFileProcessor.process();

		} else if (Constants.FILE_MIME_TYPE_PDF.equals(inputDataFile.getInputFileType())) {
			System.out.println("File identified as PDF format");
				
			PDFFileProcessor2 pdfFileProcessor = new PDFFileProcessor2(inputDataFile, normalisedFormat);
			pdfFileProcessor.process();
		
		} else if (Constants.FILE_MIME_TYPE_MSWORD.equals(inputDataFile.getInputFileType())) {
			System.out.println("File identified as MS Word format");
		
		} else if (Constants.FILE_MIME_TYPE_MSEXCEL.equals(inputDataFile.getInputFileType())) {
			System.out.println("File identified as MS Word format");
		
	
		
		} else if (Constants.FILE_MIME_TYPE_ZIP.equals(inputDataFile.getInputFileType())) {
			System.out.println("WARNING : File identified as zip format, this file will not be processed");
			
		} else {
			System.out.println("ERROR: Unable to process file " + inputDataFile.getInputFilePath() + ": File type " + inputDataFile.getInputFileType() + " is not supported");
			
		}

    }
	
}
