package eu.scape.bl.uk.normtabdata.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import eu.scape.bl.uk.normtabdata.dto.DataFileDetailsDto;
import eu.scape.bl.uk.normtabdata.normalisation.NormalisedDataItem;
import eu.scape.bl.uk.normtabdata.normalisation.NormalisedFormat;

public class TextFileDataParser extends DataFileParser {
	
		public TextFileDataParser( DataFileDetailsDto inputDataFile, 
				NormalisedFormat normalisedFormat) 
								throws IOException {
			super(inputDataFile, normalisedFormat);	
		}
		
		public void endOfLine() throws IOException {
			
			System.out.println("    TextFileDataParser - end of line " + inputRecordCounter + " header line=" + inputDataFile.getHeaderLine());		
			
			// Assume first data line is the line immediately after the header line, 
			// start processing at this point. 
			inputRecordCounter++;
			//if (inputRecordCounter > inputDataFile.getHeaderLine()) {
				parse();
				// Write any errors/warning to the audit log
				for (String dataItem : normalisedFormat.getDataItemNames()) {
					if (normalisedFormat.getValidationMessagesForDataItem(dataItem) != null) {
						for (String validationMessage: normalisedFormat.getValidationMessagesForDataItem(dataItem)) {
							auditLog("\t [Record " + inputRecordCounter + "] " + dataItem + " - " + validationMessage);
						}
					}	
				}
			//}
			
			elementList.clear();
			normalisedFormat.clear();
			
		}
		
		/*
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
		*/
		/*
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
		} */
		
		private void auditLog(String auditMessage) {
			try {
				auditBuffer.write(auditMessage + normalisedFormat.getNormalisedOutputNewLine());
				auditBuffer.flush();
			} catch ( IOException ioe ) {
				System.out.println("ERROR: Failed to open audit log file");
			}
			
		}
	
	
	
}
