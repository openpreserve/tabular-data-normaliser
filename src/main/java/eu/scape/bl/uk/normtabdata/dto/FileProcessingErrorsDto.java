package eu.scape.bl.uk.normtabdata.dto;

public class FileProcessingErrorsDto {

	private int recordNumber;
	private String severity;
	private String exception;
	private String errorMessage;
	
	public FileProcessingErrorsDto(String severity, String exception, String errorMessage) {
		this.severity = severity;
		this.exception = exception;
		this.errorMessage = errorMessage;
	}

}
