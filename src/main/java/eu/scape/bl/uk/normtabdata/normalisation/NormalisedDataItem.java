package eu.scape.bl.uk.normtabdata.normalisation;

import java.util.ArrayList;

public class NormalisedDataItem {

	private String dataItemName;
	private ArrayList<String> dataItemHeaderKeywords = new ArrayList<String>();
	private boolean mandatory = true;
	private boolean validationRequired;
	private boolean modificationRequired = true;
	private boolean translationRequired;
	private boolean removeDuplicates = true;
	private boolean replaceNonPrintableCharacters = false;
	private String replaceNonPrintableCharactersWith;
	private String validationRegex = null;
	private ArrayList<String> validationValues = new ArrayList<String>();
	private int minLength = -1;
	private int maxLength = -1;
	private String defaultValue;
	private ArrayList<Integer> inputDataColumnIndex = new ArrayList<Integer>();
	private ArrayList<String> inputDataValues = new ArrayList<String>();
	private ArrayList<String> outputDataValues = new ArrayList<String>();
	private ArrayList<String> validationMessages = new ArrayList<String>();
	private ArrayList<String> columnIdentificationValues = new ArrayList<String>();
	private String columnIdentificationRegex;	
	
	public NormalisedDataItem(String dataItemName) {
		this.dataItemName = dataItemName;
	}
	
	public boolean validate() {
		System.out.println("Validating " + dataItemName + "[ Mandatory=" + mandatory + 
			", Validation required =" + validationRequired + ", Min length=" + minLength +
			" Max length=" + maxLength + " Validation regex=" + validationRegex +
			" Replace non printable characters=" + replaceNonPrintableCharacters + 
			"[" + replaceNonPrintableCharactersWith + "]");
		boolean isValid = true;
		generateOutputData();
		String outputDataValue = getOutputDataAsString();
		if (mandatory) {
			if (outputDataValue.isEmpty()) {
				isValid = false;
				if (defaultValue.isEmpty()) {		
					validationMessages.add("Mandatory value is empty");
				} else {		
					validationMessages.add("Mandatory value is empty, using default value [" + defaultValue + "]");
				}
				return isValid;
			}
		}
		if (validationRequired) {
			if (minLength >= 0) {
				if (outputDataValue.isEmpty() || outputDataValue.length() < minLength) {
					isValid = false;
					validationMessages.add("Too short; min length="+minLength);
				}	
			}
			if (maxLength >= 0) {
				if (outputDataValue.isEmpty() || outputDataValue.length() > maxLength) {
					isValid = false;
					validationMessages.add("Too long; max length="+maxLength);
				}
			}
			if (validationRegex != null && !validationRegex.isEmpty() ) {
				if (outputDataValue != null) {
					if (!outputDataValue.matches(validationRegex)) {
						isValid = false;
						validationMessages.add("Does not match regex [ " + validationRegex +" ]");
					}
				}
			}
		}
		return isValid;
	}
	
	public String modify(String dataValueToModify) {
		
		// Carry out modification on the input value, e.g. remove non alphanumeric chars, 
		// or convert to upper/lower case
		String dataValue = dataValueToModify;
		if (dataValueToModify != null && !dataValueToModify.isEmpty()) {
			dataValue = dataValueToModify.replaceAll("\"", "\'");	
		}

		return dataValue;
	}

	public String translate(String dataValueToTranslate) {
		
		// Carry out translation on the input value, e.g. convert a code to it's 
		// corresponding value
		
		String dataValue = dataValueToTranslate;
		if (!dataValueToTranslate.isEmpty()) {
			// Translation goes here			
		}
		return dataValue;
	}
	
	public String replaceNonPrintableCharacters(String dataValueToTranslate) {
		
		// TODO - Replace non-printable characters by those specified in relaceNonPrintableCharactersWith
		System.out.println("Replacing non-printable characters in [" + dataValueToTranslate + "] with [" + replaceNonPrintableCharactersWith + "]");
		String dataValue = dataValueToTranslate;
		if (!dataValueToTranslate.isEmpty()) {
			//dataValueToTranslate.replaceAll("\\p{C}", replaceNonPrintableCharactersWith);
			String nonBreakingSpace = "\u00A0";
			dataValueToTranslate.replaceAll(nonBreakingSpace, replaceNonPrintableCharactersWith);
		}
		return dataValue;
	}
	
	public void generateOutputData() {
		//System.out.println("Generating output data for " + dataItemName);
		ArrayList<String> inputDataArray = new ArrayList<String>();
		if (removeDuplicates) {
			inputDataArray = removeDuplicates();
		} else {
			inputDataArray = inputDataValues;
		}
		
		outputDataValues.clear();
		String outputDataValue = "";
		if (inputDataArray.isEmpty()) {
			outputDataValue = getOutputDataValue("");
			outputDataValues.add(outputDataValue);
		} else {
			for (String inputDataValue: inputDataArray) {
				outputDataValue = getOutputDataValue(inputDataValue);
				outputDataValues.add(outputDataValue);
			}
		}
	}
	
	public String getOutputDataAsString() {
		String outputData = "";	
		for (String outputDataValue: outputDataValues) {
			if (!outputDataValue.isEmpty()) {
				outputData = outputData + outputDataValue + ", ";
			}
		}
		if (outputData.length() >=2) {
			outputData = outputData.substring(0, outputData.length()-2);
		}
		outputData.trim();
		return outputData;
	}

	public void clear() {
		inputDataValues.clear();
		outputDataValues.clear();
		validationMessages.clear();
	}
	
	public ArrayList<String> removeDuplicates() {
		ArrayList<String> deDuplicatedInputData = new ArrayList<String>();
		for (String inputDataValue: inputDataValues) {
			if (!deDuplicatedInputData.contains(inputDataValue)) {
				deDuplicatedInputData.add(inputDataValue);
			}
		}
		return deDuplicatedInputData;
	}
	
	public String getDataItemName() {
		return dataItemName;
	}

	public void setDataItemName(String dataItemName) {
		this.dataItemName = dataItemName;
	}

	public ArrayList<String> getDataItemHeaderKeywords() {
		return dataItemHeaderKeywords;
	}

	public void setDataItemHeaderKeywords(ArrayList<String> dataItemHeaderKeywords) {
		this.dataItemHeaderKeywords = dataItemHeaderKeywords;
	}
	
	public void addDataItemHeaderKeywords(String dataItemHeaderKeyword) {
		this.dataItemHeaderKeywords.add(dataItemHeaderKeyword);
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.mandatory = isMandatory;
	}
	
	public void setMandatory(String isMandatory) {
		//System.out.println(dataItemName + " mandatory flag is " + isMandatory);
		if (isMandatory != null) {
			if ("N".equals(isMandatory) || "n".equals(isMandatory)) {
				this.mandatory = false;
			} else if ("Y".equals(isMandatory) || "y".equals(isMandatory)) {
				this.mandatory = true;
			}
		}
		//System.out.println(dataItemName + " mandatory flag is " + this.mandatory);
	}

	public boolean isValidationRequired() {
		return validationRequired;
	}

	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}
	
	public void setValidationRequired(String validationRequired) {
		//System.out.println(dataItemName + " validation flag is " + validationRequired);
		if (validationRequired != null) {
			if ("N".equals(validationRequired) || "n".equals(validationRequired)) {
				this.validationRequired = false;
			} else if ("Y".equals(validationRequired) || "y".equals(validationRequired)) {
				this.validationRequired = true;
			}
		}
		//System.out.println(dataItemName + " validation flag is " + this.validationRequired);
	}

	public String getValidationRegex() {
		return validationRegex;
	}

	public void setValidationRegex(String validationRegex) {
		this.validationRegex = validationRegex;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	
	public void setMinLength(String minLength) throws NumberFormatException {
		if (minLength != null) {
			this.minLength = Integer.parseInt(minLength);
		}
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	public void setMaxLength(String maxLength) throws NumberFormatException {
		if (maxLength != null) {
			this.maxLength = Integer.parseInt(maxLength);
		}
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
	public ArrayList<Integer> getInputDataColumnIndex() {
		return inputDataColumnIndex;
	}

	public void setInputDataColumnIndex(ArrayList<Integer> inputDataColumnIndex) {
		this.inputDataColumnIndex = inputDataColumnIndex;
	}
	
	public void addInputDataColumnIndex(Integer inputDataColumnIndex) {
		this.inputDataColumnIndex.add(inputDataColumnIndex);
	}
	

	public ArrayList<String> getInputDataValues() {
		return inputDataValues;
	}

	public void setInputDataValues(ArrayList<String> inputDataValues) {
		this.inputDataValues.clear();
		for (String inputValue: inputDataValues) {
			this.addInputDataValue(inputValue);
		}
		this.inputDataValues = inputDataValues;
	}
	
	public void addInputDataValue(String inputDataValue) {
		//Strip off any quotation characters
		if (inputDataValue.startsWith("\"") || inputDataValue.startsWith("\'")) {
			inputDataValue = inputDataValue.substring(1);
		}
		if (inputDataValue.endsWith("\"") || inputDataValue.endsWith("\'")) {
			inputDataValue = inputDataValue.substring(0, inputDataValue.length()-1);
		}
		this.inputDataValues.add(inputDataValue);
	}
	
	public ArrayList<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(ArrayList<String> validationMessages) {
		this.validationMessages = validationMessages;
	}

	public void addValidationMessage(String validationMessage) {
		this.validationMessages.add(validationMessage);
	}

	public boolean isModificationRequired() {
		return modificationRequired;
	}

	public void setModificationRequired(boolean modificationRequired) {
		this.modificationRequired = modificationRequired;
	}

	public boolean isTranslationRequired() {
		return translationRequired;
	}

	public void setTranslationRequired(boolean translationRequired) {
		this.translationRequired = translationRequired;
	}

	private String getOutputDataValue(String inputData) {
		// Generate output data values by applying modification, translation 
		// and default values to input values
		//System.out.println("Getting output data value for " + dataItemName + " from " + inputData);
		String outputDataValue = inputData;
		if (!outputDataValue.isEmpty() && modificationRequired) {
			outputDataValue = modify(outputDataValue);
		}
		if (!outputDataValue.isEmpty() && translationRequired) {
			outputDataValue = translate(outputDataValue);
		}
		if (outputDataValue.isEmpty() && !defaultValue.isEmpty()) {
			outputDataValue = defaultValue;
		}
		if (!outputDataValue.isEmpty() && replaceNonPrintableCharacters) {
			outputDataValue = replaceNonPrintableCharacters(outputDataValue);
		}
		//System.out.println("Output data value for " + dataItemName + " is " + outputDataValue);	
		return outputDataValue;
	}
	
	public ArrayList<String> getValidationValues() {
		return validationValues;
	}

	public void setValidationValues(ArrayList<String> validationValues) {
		this.validationValues = validationValues;
	}
	
	public void addValidationValue(String validationValue) {
		this.validationValues.add(validationValue);
	}

	public ArrayList<String> getOutputDataValues() {
		return outputDataValues;
	}

	public void setOutputDataValues(ArrayList<String> outputDataValues) {
		this.outputDataValues = outputDataValues;
	}

	public ArrayList<String> getColumnIdentificationValues() {
		return columnIdentificationValues;
	}

	public void setColumnIdentificationValues(
			ArrayList<String> columnIdentificationValues) {
		this.columnIdentificationValues = columnIdentificationValues;
	}
	
	public void addColumnIdentificationValue(String columnIdentificationValue) {
		this.columnIdentificationValues.add(columnIdentificationValue);
	}

	public String getColumnIdentificationRegex() {
		return columnIdentificationRegex;
	}

	public void setColumnIdentificationRegex(String columnIdentificationRegex) {
		this.columnIdentificationRegex = columnIdentificationRegex;
	}

	public boolean isRemoveDuplicates() {
		return removeDuplicates;
	}

	public void setRemoveDuplicates(boolean removeDuplicates) {
		this.removeDuplicates = removeDuplicates;
	}

	public boolean isReplaceNonPrintableCharacters() {
		return replaceNonPrintableCharacters;
	}

	public void setReplaceNonPrintableCharacters(boolean relaceNonPrintableCharacters) {
		this.replaceNonPrintableCharacters = relaceNonPrintableCharacters;
	}
	
	public void setReplaceNonPrintableCharacters(String replaceNonPrintableCharacters) {
		//System.out.println(dataItemName + " mandatory flag is " + isMandatory);
		if (replaceNonPrintableCharacters != null) {
			if ("N".equals(replaceNonPrintableCharacters) || "n".equals(replaceNonPrintableCharacters)) {
				this.replaceNonPrintableCharacters = false;
			} else if ("Y".equals(replaceNonPrintableCharacters) || "y".equals(replaceNonPrintableCharacters)) {
				this.replaceNonPrintableCharacters = true;
			}
		}
		//System.out.println(dataItemName + " mandatory flag is " + this.mandatory);
	}

	public String getReplaceNonPrintableCharactersWith() {
		return replaceNonPrintableCharactersWith;
	}

	public void setReplaceNonPrintableCharactersWith(String replaceNonPrintableCharactersWith) {
		this.replaceNonPrintableCharactersWith = replaceNonPrintableCharactersWith;
	}

	@Override
	public String toString() {
		return "NormalisedDataItem [dataItemName="
				+ dataItemName + "columnIdentificationRegex=" + columnIdentificationRegex +
				", dataItemHeaderKeywords="
				+ dataItemHeaderKeywords + ", mandatory=" + mandatory
				+ ", validationRequired=" + validationRequired
				+ ", modificationRequired=" + modificationRequired
				+ ", translationRequired=" + translationRequired
				+ ", validationRegex=" + validationRegex + ", minLength="
				+ minLength + ", maxLength=" + maxLength + ", defaultValue="
				+ defaultValue + ", inputDataValues=" + inputDataValues
				+ ", validationMessages=" + validationMessages + "]";
	}
	
}
