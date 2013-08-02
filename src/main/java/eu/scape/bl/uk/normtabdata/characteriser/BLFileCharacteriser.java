package eu.scape.bl.uk.normtabdata.characteriser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.scape.bl.uk.normtabdata.utilities.Constants;

public class BLFileCharacteriser extends FileCharacteriser {

	public BLFileCharacteriser(String fileName) {
		this.fileName = fileName;
	}
	
	public BLFileCharacteriser(String fileName, Map<String, ArrayList<String>> headerColumnKeywords) {
		this.fileName = fileName;
		this.headerColumnKeywords = headerColumnKeywords;
	}

	public void process() throws IOException, Exception {
		// Open input file 
		System.out.println("Starting characterisation of " + fileName);
		
		super.process();
		
		/*
		// Determine if data is enclosed by quotes
		this.quoteCharacter = identifyQuoteCharacter(textArray.subList(0,  linesInFile>7?7:linesInFile));
		System.out.println("Quote character is [" + this.quoteCharacter + "]");
		
		// Identify the delimiter
		this.delimiter = identifyDelimiter(textArray);
		System.out.println("Delimiter character is [" + this.delimiter + "]");
		
		// Determine if content is quoted by checking number of quotes
		if (this.quoteCharacter != null) {
			if (getQuoteCharacterCount(textArray.subList(0,  linesInFile>7?7:linesInFile), this.quoteCharacter) > (headerColumnKeywords.size()*2)) {
				this.quotedStrings = true;
			}
		} 
		*/
		
		System.out.println("Quoted content [" + this.quotedStrings + "]");
		// If delimiter found identify the header line 
		if (delimiter != null) {
			headerLine = identifyHeaderLine(textArray);
			if (headerLine <= 0 || this.keywordMatches < (columnIdentificationValues.size()/2)) {
				// If header line not found attempt to use column content regex values 
				// to identify the columns	
				headerLine = -1;
				messages.add("Unable to identify a header line. Attempting to use content to identify data");
				// Identify columns containing first name, surname, title and postcode by regex strings or values
				headerColumnMapping = identifyHeaderColumnsByContent(textArray);
				// Identify the columns that make up the address
				headerColumnMapping.put("ADDRESS", identifyAddressColumns(textArray));
			} else {
				// Header line found, map columns in input file to those required in normalised output
				headerColumnMapping = identifyColumnHeaders(textArray.get(headerLine-1));
			} 
			printHeaderColumnMappings();
			characterisationCompleted = true;
		} else {
			messages.add("Unable to identify delimiter. Unable to process file");
		}
	}

	// Identify the address columns from the postcode column
	// Expect that address lines will be the columns preceding the POSTCODE
	private ArrayList<Integer> identifyAddressColumns(ArrayList<String> textArray) {
		
		ArrayList<Integer> addressColumns = new ArrayList<Integer>();

		// Need the postcode column as a reference point
		ArrayList<Integer> postcodeColumn = headerColumnMapping.get("POSTCODE");
		int postcodeColumnInt = -1;
		if (postcodeColumn != null) {
			postcodeColumnInt = postcodeColumn.get(0);
		} else {
			return addressColumns;
		}
		
		// Look for a column containing building names, this will be assumed to be the first 
		// address columns;
		int firstAddressColumn = getAddressColumn(Constants.BUILDING_ADDRESS, Constants.BUILDING_ADDRESS_REGEX, textArray);
		if (firstAddressColumn <= 0) {
			firstAddressColumn = getAddressColumn(Constants.STREET_ADDRESS, Constants.STREET_ADDRESS_REGEX, textArray);
		}
		
		// Determine if we've found the street column, if so add all the 
		if (firstAddressColumn>0) {
			for (int i=firstAddressColumn; i<=postcodeColumnInt; i++) {
				addressColumns.add(i);
			}
		} 

		return addressColumns;
	}
	
	private int getAddressColumn(String columnName, String regex, ArrayList<String> textArray) {
		System.out.println("Looking for address column " + columnName);
		Map<String, String> addressColumnRegex = new HashMap<String, String>();
		addressColumnRegex.put(columnName, regex);
		IdentifyContents identifyContents = new IdentifyContents(null, addressColumnRegex, this.delimiter, this.quoteCharacter);
		int linesToProcess = 1000;
		Map<String, ArrayList<Integer>> columnMatches = identifyContents.identifyColumns(textArray.subList(0, linesInFile>linesToProcess?linesToProcess:linesInFile));
		int maxMatchCount = 0;
		int maxMatchColumn = 0;
		if (columnMatches != null) {
			ArrayList<Integer> matches = columnMatches.get(columnName);
			if (matches != null) {
				for (int i=0; i<matches.size(); i++) {
					if (matches.get(i) > maxMatchCount) {
						maxMatchCount = matches.get(i);
						maxMatchColumn = i+1;
					}
				}
			}
		}
		printHeaderColumnsByContent(columnMatches);
		return maxMatchColumn;
	}

}
