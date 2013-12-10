package eu.scape_project.tabdatanorm.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;
import eu.scape_project.tabdatanorm.normalisation.NormalisedDataItem;
import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;

public class DataFileParser {

	protected NormalisedFormat normalisedFormat;
	protected File normalisedOutputFile;
	protected File auditLog;
	protected Writer outputBuffer;
	protected Writer auditBuffer;
	protected int inputRecordCounter = 0;
	protected int outputRecordCounter = 0;
	protected int errorCounter = 0;
	protected DataFileDetailsDto inputDataFile;
	protected ArrayList<String> elementList = new ArrayList<String>();
	
	public DataFileParser( DataFileDetailsDto inputDataFile, 
			NormalisedFormat normalisedFormat) 
							throws IOException {
			this.inputDataFile = inputDataFile;
			this.normalisedFormat = normalisedFormat;
			this.normalisedOutputFile = new File(this.inputDataFile.getOutputLocation() + this.inputDataFile.getOutputFileName());	
			outputBuffer = new BufferedWriter(new FileWriter(normalisedOutputFile));
	}

	
	public void startOfFile()  {
		String outputFileHeader = "";
		String outputFileDelimiter = normalisedFormat.getNormalisedOutputDelimiter();
		String outputFileNewLine = normalisedFormat.getNormalisedOutputNewLine();
		
		for (NormalisedDataItem ndi : normalisedFormat.getDataItems()) {
		    outputFileHeader = outputFileHeader + ndi.getDataItemName() + outputFileDelimiter;
		}
		outputFileHeader = outputFileHeader.substring(0, outputFileHeader.lastIndexOf(outputFileDelimiter)) + outputFileNewLine;	
		
		// Write headers to the output file
		writeHeaders(outputFileHeader);
		
		// Open/create the audit log
		createAuditFile();
	}
	
	public void parse() throws IOException {
		System.out.println("DataFileParser - producing normalised output");
		
		for (NormalisedDataItem normalisedDataItem : normalisedFormat.getDataItems()) {
			// Get data in the order for output to normalised file
			for (Integer columnNumber: normalisedDataItem.getInputDataColumnIndex()) {
				if (elementList.size() > columnNumber-1) {	
					normalisedDataItem.addInputDataValue(elementList.get(columnNumber-1));
					System.out.println("    Adding " + elementList.get(columnNumber-1) + " to " + normalisedDataItem.getDataItemName());
				}
			}	
		}
		
		int errorCount = normalisedFormat.validateInput();
		if (errorCount > 0) {
			System.out.println("ERROR : Input data from record " + inputRecordCounter + " failed validation, refer to audit log for details" ); 
			errorCounter++;
		}
		String outputCSV = normalisedFormat.generateOutput();
		if (outputBuffer != null) {
			outputBuffer.write(outputCSV + normalisedFormat.getNormalisedOutputNewLine());
			outputBuffer.flush();
			outputRecordCounter++;
			System.out.println("OUTPUT " + outputRecordCounter + " " + outputCSV); 
		}
		
		// Write any errors/warning to the audit log
		for (String dataItem : normalisedFormat.getDataItemNames()) {
			if (normalisedFormat.getValidationMessagesForDataItem(dataItem) != null) {
				for (String validationMessage: normalisedFormat.getValidationMessagesForDataItem(dataItem)) {
					auditLog("\t [Record " + outputRecordCounter + "] " + dataItem + " - " + validationMessage);
				}
			}	
		}
		
		elementList.clear();
		normalisedFormat.clear();
	}
	
	public void addElement(String element) {
		elementList.add(element.trim());
	}

	public void endOfLine() throws IOException {
		
		elementList.clear();
		normalisedFormat.clear();		
	}	
	
	public void endOfFile() {
		inputDataFile.setInputRecordCount(inputRecordCounter);
		inputDataFile.setOutputRecordCount(outputRecordCounter);
		inputDataFile.setRecordsFailedProcessing(errorCounter);
		auditLog("\tRecords processed=" + inputDataFile.getInputRecordCount() + ", " +
					"\tfailures=" + inputDataFile.getRecordsFailedProcessing());	
		auditLog("Completed processing of " + inputDataFile.getInputFileName());
	}
	
	public void finish() {
		// Parsing complete - put any tidy up tasks here
		try {
			if (outputBuffer != null) {
				System.out.println("    DelimitedTextParser - closing " + normalisedOutputFile.getName());
				outputBuffer.flush();
				outputBuffer.close();
			}
			if (auditBuffer != null) {
				System.out.println("    DelimitedTextParser - closing auditBuffer");		
				auditBuffer.flush();
				auditBuffer.close();
			}
		} catch ( IOException ioe) {
			System.out.println("IOException : Unable to close output file " + normalisedOutputFile.getPath() + ", " + ioe.getMessage() ); 
		}	
	}	
	
	private void writeHeaders(String outputFileHeader) {
		
		try {
			if (outputBuffer != null) {
				outputBuffer.write(outputFileHeader);
				outputBuffer.flush();
			}
		} catch ( IOException ioe) {
			System.out.println("IOException : Unable to write to output file " + normalisedOutputFile.getPath() + ", " + ioe.getMessage() ); 
		}	
	}
	
	private void createAuditFile() {
		String auditLogFileName = inputDataFile.getAuditLocation() + inputDataFile.getAuditFileName();
		System.out.println("Creating audit file " + auditLogFileName);
		try {
			if (auditLogFileName != null && !auditLogFileName.isEmpty() ) {
				auditLog = new File(auditLogFileName);	
				auditBuffer = new BufferedWriter(new FileWriter(auditLog));
				auditLog("Started processing of " + inputDataFile.getInputFileName());
			}
		} catch ( IOException ioe ) {
			System.out.println("ERROR: Failed to open audit log file");
		}
	}
	
	private void auditLog(String auditMessage) {
		try {
			auditBuffer.write(auditMessage + normalisedFormat.getNormalisedOutputNewLine());
			auditBuffer.flush();
		} catch ( IOException ioe ) {
			System.out.println("ERROR: Failed to open audit log file");
		}
		
	}

	
	
}
