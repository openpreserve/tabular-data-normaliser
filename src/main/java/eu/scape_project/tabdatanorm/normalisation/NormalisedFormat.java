package eu.scape_project.tabdatanorm.normalisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.scape_project.tabdatanorm.utilities.Constants;

public class NormalisedFormat {

	private ArrayList<NormalisedDataItem> dataItems = new ArrayList<NormalisedDataItem>();
	private ArrayList<String> inputHeaderKeywords = new ArrayList<String>();
	//private String inputDelimiter;
	private String normalisedOutputQuotes = "\"";
	private String normalisedOutputDelimiter = Constants.DEFAULT_OUTPUT_FILE_DELIMITER;
	private String normalisedOutputNewLine = Constants.DEFAULT_OUTPUT_FILE_NEWLINE;
	private String outputFileType = Constants.DEFAULT_OUTPUT_FILE_TYPE;
	private Map<String, ArrayList<String>> validationMessages = new HashMap<String, ArrayList<String>>();
	//private String auditLog;
	
	public int validateInput() {
		boolean inputValid = true;
		int errorCount = 0;
		validationMessages.clear();
		for (NormalisedDataItem dataItem: dataItems) {
			inputValid = dataItem.validate();	
			if ( !inputValid) {
				errorCount++;
				validationMessages.put(dataItem.getDataItemName(), dataItem.getValidationMessages());
			}
		}
		return errorCount;
	}
	
	public String generateOutput() {
		String outputString = "";
		for (NormalisedDataItem dataItem : dataItems) {
			dataItem.generateOutputData();
			outputString = outputString + normalisedOutputQuotes + dataItem.getOutputDataAsString() + normalisedOutputQuotes + normalisedOutputDelimiter;
		}
		return outputString;
	}
	
	public void clear() {
		for (NormalisedDataItem dataItem : dataItems) {
			dataItem.clear();
		}
		
	}
	
	public ArrayList<NormalisedDataItem> getDataItems() {
		return dataItems;
	}
	
	public void setDataItems(
			ArrayList<NormalisedDataItem> items) {
		this.dataItems = items;
	}
	
	public void addDataItem(NormalisedDataItem item) {
		this.dataItems.add(item);;
	}
	
	public String getNormalisedOutputDelimiter() {
		return normalisedOutputDelimiter;
	}
	
	public void setNormalisedOutputDelimiter(String normalisedOutputDelimiter) {
		this.normalisedOutputDelimiter = normalisedOutputDelimiter;
	}

	public ArrayList<String> getInputHeaderKeywords() {
		return inputHeaderKeywords;
	}

	public void setInputHeaderKeywords(ArrayList<String> inputHeaderKeywords) {
		this.inputHeaderKeywords = inputHeaderKeywords;
	}
	
	public void addInputHeaderKeyword(String inputHeaderKeyword) {
		this.inputHeaderKeywords.add(inputHeaderKeyword);
	}

	public String getOutputFileType() {
		return outputFileType;
	}

	public void setOutputFileType(String outputFileType) {
		this.outputFileType = outputFileType;
	}

	public ArrayList<String> getDataItemNames() {
		ArrayList<String> dataItemNames = new ArrayList<String>();
		for (NormalisedDataItem dataItem: dataItems) {
			dataItemNames.add(dataItem.getDataItemName());
		}
		return dataItemNames;
	}
	
	public Map<String, ArrayList<String>> getValidationMessages() {
		return validationMessages;
	}
	
	public ArrayList<String> getValidationMessagesForDataItem(String dataItem) {
		ArrayList<String> messages = validationMessages.get(dataItem);
		return messages;
	}

	public void setValidationMessages(
			Map<String, ArrayList<String>> validationMessages) {
		this.validationMessages = validationMessages;
	}

	public String getNormalisedOutputNewLine() {
		return normalisedOutputNewLine;
	}

	public void setNormalisedOutputNewLine(String normalisedOutputNewLine) {
		this.normalisedOutputNewLine = normalisedOutputNewLine;
	}
	
	//public String getInputDelimiter() {
	//	return inputDelimiter;
	//}

	//public void setInputDelimiter(String inputDelimiter) {
	//	this.inputDelimiter = inputDelimiter;
	//}
	
	public String getNormalisedOutputQuotes() {
		return normalisedOutputQuotes;
	}

	public void setNormalisedOutputQuotes(String normalisedOutputQuotes) {
		this.normalisedOutputQuotes = normalisedOutputQuotes;
	}

	@Override
	public String toString() {
		return "NormalisedFormat [ inputHeaderKeywords="
				+ inputHeaderKeywords + ", normalisedOutputDelimiter="
				+ normalisedOutputDelimiter + ", normalisedOutputNewLine="
				+ normalisedOutputNewLine + ", outputFileType="
				+ outputFileType + ", validationMessages=" + validationMessages
				+ "]";
	}

	public String toStringFull() {
		String outputString = "NormalisedFormat [inputHeaderKeywords="
				+ inputHeaderKeywords + ", normalisedOutputDelimiter="
				+ normalisedOutputDelimiter + ", normalisedOutputNewLine="
				+ normalisedOutputNewLine + ", outputFileType="
				+ outputFileType + ", validationMessages=" + validationMessages
				+ "]";
						
		for (NormalisedDataItem e : dataItems) {
		    outputString = outputString + e.toString();      
		}
		return outputString;
		
	}
		
}
