package eu.scape_project.tabdatanorm.dto;

import java.io.File;
import java.util.ArrayList;

import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class DataFileDetailsDto {

	private String inputLocation;
	private String inputFileName;
	private String outputLocation;
	private String auditLocation;
	
	private String inputFileType;
	private int headerLine = -1;
	private String delimiter = null;
	private Character quoteCharacter = null;
	private boolean encrypted = false;
	private String password;

	private int inputLineCount;
	//private int outputLineCount;
	private int inputRecordCount;
	private int outputRecordCount;
	private int recordsSuccessfullyProcessed;
	private int recordsFailedProcessing;
	private boolean processed;
	private ArrayList<String> errorMessages = new ArrayList<String>();
	//private ArrayList<String> keyData;
	//private ArrayList<FileProcessingErrorsDto> processingErrors;
	//private ArrayList<Integer> inputDataColumnIndex = new ArrayList<Integer>();
	
	public DataFileDetailsDto(  String inputLocation, String inputFileName, 
								String outputLocation, String auditLocation) {
		super();
		this.inputLocation = inputLocation;
		this.inputFileName = inputFileName;
		this.outputLocation = outputLocation;
		this.auditLocation = auditLocation;
	}

	public String getInputLocation() {
		return inputLocation;
	}

	public void setInputLocation(String inputLocation) {
		this.inputLocation = inputLocation;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public String getInputFilePath() {
		return inputLocation + File.separator + inputFileName;
	}

	public String getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}
	
	public String getOutputFilePath() {
		return outputLocation + this.getOutputFileName();
	}

	public String getAuditLocation() {
		return auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}
	
	public String getAuditFilePath() {
		return auditLocation + this.getAuditFileName();
	}

	public String getInputFileType() {
		return inputFileType;
	}

	public void setInputFileType(String inputFileType) {
		this.inputFileType = inputFileType;
	}

	public int getHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(int headerLine) {
		this.headerLine = headerLine;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInputLineCount() {
		return inputLineCount;
	}

	public void setInputLineCount(int inputLineCount) {
		this.inputLineCount = inputLineCount;
	}

	public int getRecordsSuccessfullyProcessed() {
		return recordsSuccessfullyProcessed;
	}

	public void setRecordsSuccessfullyProcessed(int recordsSuccessfullyProcessed) {
		this.recordsSuccessfullyProcessed = recordsSuccessfullyProcessed;
	}

	public int getRecordsFailedProcessing() {
		return recordsFailedProcessing;
	}

	public void setRecordsFailedProcessing(int recordsFailedProcessing) {
		this.recordsFailedProcessing = recordsFailedProcessing;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public ArrayList<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(ArrayList<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public void addErrorMessage(String errorMessage) {
		this.errorMessages.add(errorMessage);
	}

	public String getOutputFileName() {
		String outputFileName = inputFileName;
		if (inputFileName.indexOf('.') != -1) {
			outputFileName = outputFileName.substring(0, inputFileName.indexOf('.'));
		}
		outputFileName = outputFileName + "." + Constants.DEFAULT_OUTPUT_FILE_TYPE;		
		return outputFileName;
	}
	
	public String getAuditFileName() {
		String auditFileName = inputFileName;
		if (inputFileName.indexOf('.') != -1) {
			auditFileName = auditFileName.substring(0, inputFileName.indexOf('.'));
		}
		auditFileName = auditFileName + "." + Constants.DEFAULT_AUDIT_FILE_TYPE;
		return auditFileName;
	}
	
	//public int getOutputLineCount() {
	//	return outputLineCount;
	//}

	//public void setOutputLineCount(int outputLineCount) {
	//	this.outputLineCount = outputLineCount;
	//}

	public int getInputRecordCount() {
		return inputRecordCount;
	}

	public void setInputRecordCount(int inputRecordCount) {
		this.inputRecordCount = inputRecordCount;
	}

	public int getOutputRecordCount() {
		return outputRecordCount;
	}

	public void setOutputRecordCount(int outputRecordCount) {
		this.outputRecordCount = outputRecordCount;
	}

	public Character getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(Character quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}
	
	/*
	public ArrayList<Integer> getInputDataColumnIndex() {
		return inputDataColumnIndex;
	}

	public void setInputDataColumnIndex(ArrayList<Integer> inputDataColumnIndex) {
		this.inputDataColumnIndex = inputDataColumnIndex;
	}
	
	public void addInputDataColumnIndex(Integer inputDataColumnIndex) {
		this.inputDataColumnIndex.add(inputDataColumnIndex);
	}
*/
	
	
	
	@Override
	public String toString() {
		return "DataFileDetailsDto [inputLocation=" + inputLocation
				+ ", inputFileName=" + inputFileName + ", outputLocation="
				+ outputLocation + ", auditLocation=" + auditLocation
				+ ", inputFileType=" + inputFileType + ", headerLine="
				+ headerLine + ", delimiter=" + delimiter + ", encrypted="
				+ encrypted + ", password=" + password + ", inputLineCount="
				+ inputLineCount + ", outputRecordCount=" + outputRecordCount
				+ ", recordsSuccessfullyProcessed="
				+ recordsSuccessfullyProcessed + ", recordsFailedProcessing="
				+ recordsFailedProcessing + ", processed=" + processed
				+ ", errorMessages=" + errorMessages + "]";
	}

}
