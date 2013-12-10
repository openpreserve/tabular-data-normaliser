package eu.scape_project.tabdatanorm.characteriser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IdentifyContents {

	private Map<String, ArrayList<String>> columnIdentificationValues = new HashMap<String, ArrayList<String>>();
	private Map<String, String> columnIdentificationRegex = new HashMap<String, String>();
	private String delimiter;
	private String delimiterString;
	private Character quoteCharacter;

	public IdentifyContents ( Map<String, ArrayList<String>> columnContentValues, Map<String, String> columnContentRegex ) {	
		this.columnIdentificationValues = columnContentValues;
		this.columnIdentificationRegex = columnContentRegex;
	}
	
	public IdentifyContents ( Map<String, ArrayList<String>> columnContentValues, Map<String, String> columnContentRegex, String delimiter, Character quoteCharacter ) {	
		this.columnIdentificationValues = columnContentValues;
		this.columnIdentificationRegex = columnContentRegex;
		this.delimiter = delimiter;
		this.quoteCharacter = quoteCharacter;
	}
	
	public Map<String, ArrayList<Integer>> identifyColumns(List<String> inputText) {
		ArrayList<String> columnNames = getColumnNames();
		Map<String, ArrayList<Integer>> columnIdentificationResults = new HashMap<String, ArrayList<Integer>>(); 
		String delimiterString = this.delimiter;
		if (this.quoteCharacter != null) delimiterString = delimiter + this.quoteCharacter;
		for (String columnName: columnNames) {	
			//System.out.println(" column " + columnName);
			ArrayList<Integer> matchCounter = new ArrayList<Integer>();
			for (String inputLine: inputText) {
				ArrayList<String> inputElements = getInputElements(inputLine, delimiterString);
				// Identify columns by value
				if (columnIdentificationValues != null && !columnIdentificationValues.isEmpty()) {
					ArrayList<Integer> valueMatches = identifyColumnByValue(columnName, inputElements);
					for (int i=0; i<inputElements.size(); i++) {
						if (valueMatches != null && valueMatches.size() > i) {
							int matchCount = (Integer)valueMatches.get(i);
							if (!matchCounter.isEmpty() && matchCounter.size() > i) {
								matchCount+= (Integer)matchCounter.get(i);
								matchCounter.set(i, matchCount);
							} else {
								matchCounter.add(matchCount);
							}
						}
					}
				}
				// Identify columns by reg exp
				if (columnIdentificationRegex != null && !columnIdentificationRegex.isEmpty()) {
					ArrayList<Integer> regexMatches = identifyColumnByRegex(columnName, inputElements);
					for (int i=0; i<inputElements.size(); i++) {
						if (regexMatches != null && regexMatches.size() > i) {
							int matchCount = (Integer)regexMatches.get(i);
							if (!matchCounter.isEmpty() && matchCounter.size() > i) {
								matchCount+= (Integer)matchCounter.get(i);
								matchCounter.set(i, matchCount);
							} else {
								matchCounter.add(matchCount);
							}
						}
					}
				}
			}
			columnIdentificationResults.put(columnName, matchCounter);
		}
		return columnIdentificationResults;
	}
	
	
	private ArrayList<Integer> identifyColumnByValue(String columnName, ArrayList<String> inputValues) {
		System.out.println("Identifying column " + columnName + " by value");
		ArrayList<Integer> matches = new ArrayList<Integer>();
		int matchKey = 0;
		for (String inputVal: inputValues) {
			matches.add(0);
			ArrayList<String> columnValues = columnIdentificationValues.get(columnName);
			if (columnValues != null) {
				for (String columnValue: columnValues) {
					//System.out.println("Identifying column " + columnName + " by " + columnValue);
					String inputValue = inputVal.trim().toUpperCase();
					if (inputValue != null && (inputValue.startsWith("\"") || inputValue.startsWith("\'"))) {
						inputValue = inputValue.substring(1);
					}
					if (inputValue != null && (inputValue.endsWith("\"") || inputValue.endsWith("\'"))) {
						inputValue = inputValue.substring(0, inputValue.length()-1);
					}
					System.out.println("Comparing " + columnValue + " to " + inputValue);
					if (columnValue.equals(inputValue)) {
						matches.set(matchKey, 1);
						System.out.println("Match found for " + inputValue);
						break;
					}
				}
			}
			matchKey++;
		}	
		return matches;
	}
	
	private ArrayList<Integer> identifyColumnByRegex(String columnName, ArrayList<String> inputValues) {
		//System.out.println("Identifying column " + columnName + " by regex");
		ArrayList<Integer> matches = new ArrayList<Integer>();
		int matchKey = 0;
		for (String inputVal: inputValues)  {
			matches.add(0);
			String columnRegex = columnIdentificationRegex.get(columnName);
			if (columnRegex != null && !columnRegex.isEmpty() && inputVal != null && !inputVal.isEmpty()) {
				System.out.println("Identifying column " + columnName + " by " + columnRegex);
				String inputValue = inputVal.trim().toUpperCase();
				if (inputValue != null && (inputValue.startsWith("\"") || inputValue.startsWith("\'"))) {
					inputValue = inputValue.substring(1);
				}
				if (inputValue != null && (inputValue.endsWith("\"") || inputValue.endsWith("\'"))) {
					inputValue = inputValue.substring(0, inputValue.length()-1);
				}
				System.out.println("Comparing " + columnRegex + " to " + inputValue);
				if (inputValue.matches(columnRegex)) {
					matches.set(matchKey, 1);
					System.out.println("Match found");
				}
			}
			matchKey++;
		}	
		return matches;
	}
	

	
	private ArrayList<String> getColumnNames() {
		ArrayList<String> columnNames = new ArrayList<String>();
		if (columnIdentificationValues != null) {
			Iterator columnNameValues = columnIdentificationValues.entrySet().iterator();
			while (columnNameValues.hasNext()) {
				Map.Entry columnValuePairs = (Map.Entry)columnNameValues.next();
				columnNames.add((String)columnValuePairs.getKey());
				System.out.println("Added column " + columnValuePairs.getKey() + " identification values");
			}	
		}
		if (columnIdentificationRegex != null) {
			Iterator columnNameRegex = columnIdentificationRegex.entrySet().iterator();
			while (columnNameRegex.hasNext()) {
				Map.Entry columnRegexPairs = (Map.Entry)columnNameRegex.next();
				columnNames.add((String)columnRegexPairs.getKey());
				System.out.println("Added column " + columnRegexPairs.getKey() + " identification regex");
			}
		}
		return columnNames;
	}

	public int getNumberOfInputColumns (String text, String delimiter) {
		String delimiterString = this.delimiter;
		if (this.quoteCharacter != null) delimiterString = delimiter + this.quoteCharacter;
		String[] columns = text.split(delimiterString);
		return columns.length;
	} 

	private ArrayList<String> getInputElements(String inputLine, String delimiterString) {
		String[] elements = inputLine.split(delimiterString);
		ArrayList<String> elementList = new ArrayList<String>();
		String partialElement = "";
		
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
					System.out.println("Recombined partial element [" + partialElement + "]");
					elementList.add(partialElement);
					partialElement = "";
				}
			} else {			
				elementList.add(elements[i]);
			}
		} 

		return elementList;
	}
	
	public Map<String, ArrayList<String>> getColumnIdentificationValues() {
		return columnIdentificationValues;
	}

	public void setColumnContentValues(Map<String, ArrayList<String>> columnIdentificationValues) {
		this.columnIdentificationValues = columnIdentificationValues;
	}

	public Map<String, String> getColumnIdentificationRegex() {
		return columnIdentificationRegex;
	}

	public void setColumnIdentificationRegex(
			Map<String, String> columnIdentificationRegex) {
		this.columnIdentificationRegex = columnIdentificationRegex;
	}

	public void setColumnIdentificationValues(
			Map<String, ArrayList<String>> columnIdentificationValues) {
		this.columnIdentificationValues = columnIdentificationValues;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Character getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(Character quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}
	
	
}
