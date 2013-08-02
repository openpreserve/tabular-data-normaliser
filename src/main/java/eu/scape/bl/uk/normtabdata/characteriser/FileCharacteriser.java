package eu.scape.bl.uk.normtabdata.characteriser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileCharacteriser {

	protected Map<String, ArrayList<String>> headerColumnKeywords = new HashMap<String, ArrayList<String>>();
	protected Map<String, ArrayList<String>> columnIdentificationValues = new HashMap<String, ArrayList<String>>();
	protected Map<String, String> columnIdentificationRegex = new HashMap<String, String>();
	protected int keywordMatches = 0;
	
	protected String fileName;
	protected ArrayList<String> delimiterCharacters = new ArrayList<String>();	
	protected Map<String, ArrayList<Integer>> headerColumnMapping = new HashMap<String, ArrayList<Integer>>();
	protected int linesInFile = 0;
	protected int headerLine = -1;
	protected boolean quotedStrings = false;
	protected Character quoteCharacter = null;
	protected String delimiter = null;
	protected ArrayList<String> messages = new ArrayList<String>();
	protected boolean characterisationCompleted = false;
	protected ArrayList<String> textArray = new ArrayList<String>();
	
	public void process() throws IOException, Exception {

		File inputFile = new File (fileName);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(inputFile));
		String textIn = null;
		while ((textIn = reader.readLine()) != null) {
			linesInFile++;
	    	textArray.add(textIn);
		}
		reader.close();
			
		// Identify the delimiter
		this.delimiter = identifyDelimiter(textArray);
		System.out.println("Delimiter character is [" + this.delimiter + "]");
		
		// Determine if t
		//Character mostLikelyQuoteCharacter = identifyQuoteCharacter(textArray.subList(0,  linesInFile>7?7:linesInFile));
		System.out.println("Quote character is [" + this.quoteCharacter + "]");
	}

	public int getQuoteCharacterCount(List<String> textArray, Character quoteCharacter) {
		IdentifyQuotes identifyQuotes = new IdentifyQuotes(); 
		return identifyQuotes.getQuoteCharacterCount(textArray.get(textArray.size()-1), quoteCharacter);
	}
	
	protected Character identifyQuoteCharacter(List<String> textArray) {	
		IdentifyQuotes identifyQuotes = new IdentifyQuotes(); 
		Character quoteCharacter = identifyQuotes.identifyQuoteCharacter(textArray);
		return quoteCharacter;
	}
	
	protected String identifyDelimiter(ArrayList<String> textArray) {
		Map<String, Integer> delimiters = new HashMap<String, Integer>();
		Map<String, Integer> allDelimiters = new HashMap<String, Integer>();
		IdentifyDelimiter identifyDelimiter = new IdentifyDelimiter();
		
		// Get the delimiter counts for the first n lines of the file
		int i=0;
		for (String text: textArray) {
			i++;
			delimiters = identifyDelimiter.getDelimiters(text);
			Iterator delim = delimiters.entrySet().iterator();
			while (delim.hasNext()) {
				Map.Entry pairs = (Map.Entry)delim.next();
				Integer delimiterCount = 0;
				if ( delimiters != null ) {
					delimiterCount = 
						((Integer)delimiters.get((String)pairs.getKey()) == null ? 0: (Integer)delimiters.get((String)pairs.getKey())) +
						((Integer)allDelimiters.get((String)pairs.getKey()) == null ? 0: (Integer)allDelimiters.get((String)pairs.getKey()));
				}
				allDelimiters.put((String)pairs.getKey(),delimiterCount);		
			}
			if (i >= 10) break;
		}
		// Use the delimiter counts to determine which of the delimiters, if any,
		// is the most likely to be used
		Iterator allDelim = allDelimiters.entrySet().iterator();
		int delimiterCount = 0;
		String mostCommonDelimiter = null;
		while (allDelim.hasNext()) {
			Map.Entry pairs = (Map.Entry)allDelim.next();
			//System.out.println("Delimiter [" + pairs.getKey() + "] occurs " + pairs.getValue() + " times");
			if ((Integer) pairs.getValue() > delimiterCount) {
				mostCommonDelimiter = (String)pairs.getKey();
				delimiterCount = (Integer) pairs.getValue();
			}
		}
		System.out.println("Most common delimiter = [" + mostCommonDelimiter + "]");
		return mostCommonDelimiter;
	}
		
	protected int identifyHeaderLine(ArrayList<String> textArray) {
		System.out.println("Identifying header line using delimiter [" + delimiter + "] and header keywords ");
		IdentifyHeader identifyHeader = new IdentifyHeader(headerColumnKeywords, delimiter);
		int headerLine = identifyHeader.identifyHeaderLine(textArray.subList(0, linesInFile>7?7:linesInFile));
		this.keywordMatches = identifyHeader.getHeaderKeywordMatches();
		System.out.println("Header line is " + headerLine);
		return headerLine;
	}
	
	protected Map<String, ArrayList<Integer>> identifyColumnHeaders (String text) {
		Map<String, ArrayList<Integer>> headerColumns = new HashMap<String, ArrayList<Integer>>();
		IdentifyHeader identifyHeader = new IdentifyHeader(headerColumnKeywords, delimiter);
		headerColumns = identifyHeader.identifyColumnHeaders(text);
		return headerColumns;
	}
		
	protected Map<String, ArrayList<Integer>> identifyHeaderColumnsByContent(ArrayList<String> textArray) {
		System.out.println("Identifying input columns by content");
		Map<String, ArrayList<Integer>> columnMappings = new HashMap<String, ArrayList<Integer>>();
		int linesToProcess = 1000;
		IdentifyContents identifyContents = new IdentifyContents(columnIdentificationValues, columnIdentificationRegex, this.delimiter, this.quoteCharacter); 
		Map<String, ArrayList<Integer>> columnMatches = identifyContents.identifyColumns(textArray.subList(0, linesInFile>linesToProcess?linesToProcess:linesInFile));
		
		printHeaderColumnsByContent(columnMatches);
		
		Iterator headers = headerColumnKeywords.entrySet().iterator();
		while (headers.hasNext()) {
			Map.Entry pairs = (Map.Entry)headers.next();
			String headerName = (String)pairs.getKey();
			if (headerName != null) {
				ArrayList<Integer> matchCounts = columnMatches.get(headerName);
				int highestMatchCount = 0;
				int highestMatchColumn = -1;
				if (matchCounts != null) {
					for (int i=0; i<matchCounts.size(); i++) {
						if (matchCounts.get(i) > highestMatchCount) {
							highestMatchCount = matchCounts.get(i);
							highestMatchColumn = i+1;
						}
					}
				}
				ArrayList<Integer> columnList = new ArrayList<Integer>();
				if (highestMatchCount > 0 && highestMatchColumn > 0) {
					columnList.add(highestMatchColumn);
				}
				columnMappings.put(headerName, columnList);
			}
    		//System.out.println("  Mappings for header " + headerName + " are [" + matchingColumns.size() + "]");
		}
			
		return columnMappings;
	}

	
	public void printHeaderColumnMappings() {
		Iterator columnMappings = headerColumnMapping.entrySet().iterator();
		while (columnMappings.hasNext()) {
			Map.Entry columnMapPairs = (Map.Entry)columnMappings.next();
			ArrayList<Integer> matchingColumns = (ArrayList<Integer>)columnMapPairs.getValue();
			String outputString = (String)columnMapPairs.getKey() + " - \t";
			for (Integer matchingColumn: matchingColumns) {
				outputString = outputString + matchingColumn + "\t";
			}
			System.out.println(outputString);
		}	
	}
	
	public void printHeaderColumnsByContent(Map<String, ArrayList<Integer>> columnMatches) {
		Iterator columnMatchCounts = columnMatches.entrySet().iterator();
		while (columnMatchCounts.hasNext()) {
			Map.Entry columnMatchPairs = (Map.Entry)columnMatchCounts.next();
			ArrayList<Integer> matchCounts = (ArrayList<Integer>)columnMatchPairs.getValue();
			String outputString = (String)columnMatchPairs.getKey() + " - \t";
			for (Integer matchCount: matchCounts) {
				outputString = outputString + matchCount + "\t";
			}
			System.out.println(outputString);
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<String> getDelimiterCharacters() {
		return delimiterCharacters;
	}
	public void setDelimiterCharacters(ArrayList<String> delimiterCharacters) {
		this.delimiterCharacters = delimiterCharacters;
	}
	public Map<String, ArrayList<Integer>> getHeaderColumnMapping() {
		return headerColumnMapping;
	}
	public void setHeaderColumnMapping(
			Map<String, ArrayList<Integer>> headerColumnMapping) {
		this.headerColumnMapping = headerColumnMapping;
	}
	public int getLinesInFile() {
		return linesInFile;
	}
	public void setLinesInFile(int linesInFile) {
		this.linesInFile = linesInFile;
	}
	public int getHeaderLine() {
		return headerLine;
	}
	public void setHeaderLine(int headerLine) {
		this.headerLine = headerLine;
	}
	public boolean isQuotedStrings() {
		return quotedStrings;
	}
	public void setQuotedStrings(boolean quotedStrings) {
		this.quotedStrings = quotedStrings;
	}
	public Character getQuoteCharacter() {
		return quoteCharacter;
	}
	public void setQuoteCharacter(Character quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public ArrayList<String> getMessages() {
		return messages;
	}
	public void setMessages(ArrayList<String> messages) {
		this.messages = messages;
	}
	public boolean isCharacterisationCompleted() {
		return characterisationCompleted;
	}
	public void setCharacterisationCompleted(boolean characterisationCompleted) {
		this.characterisationCompleted = characterisationCompleted;
	}

	public Map<String, ArrayList<String>> getColumnIdentificationValues() {
		return columnIdentificationValues;
	}

	public void setColumnIdentificationValues(Map<String, ArrayList<String>> columnIdentificationValues) {
		this.columnIdentificationValues = columnIdentificationValues;
	}

	public Map<String, String> getColumnIdentificationRegex() {
		return columnIdentificationRegex;
	}

	public void setColumnIdentificationRegex(Map<String, String> columnIdentificationRegex) {
		this.columnIdentificationRegex = columnIdentificationRegex;
	}
	
}
