package eu.scape_project.tabdatanorm.characteriser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BLPDFFileCharacteriser extends FileCharacteriser {

	//private Map<String, ArrayList<Integer>> headerColumnMapping = new HashMap<String, ArrayList<Integer>>();
	//private String fileName;
	//private boolean characterisationCompleted = false;
	//private int linesInFile = 0;
	//private int headerLine = -1;
	//private boolean quotedStrings = false;
	//private Character quoteCharacter = null;
	//private String delimiter = null;
	//private ArrayList<String> messages = new ArrayList<String>();
	
	public BLPDFFileCharacteriser(String fileName) {
		this.fileName = fileName;
	}
	
	public void process() throws IOException, Exception {
		 
		System.out.println("Starting characterisation of " + fileName);
		
		super.process();
		
		/*
		File inputFile = new File (fileName);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(inputFile));
		String textIn = null;
		ArrayList<String> textArray = new ArrayList<String>();
		while ((textIn = reader.readLine()) != null) {
			linesInFile++;
	    	textArray.add(textIn);
		} */
		
		// Determine if data is enclosed by quotes
		this.quoteCharacter = identifyQuoteCharacter(textArray);
		System.out.println("Quote character is [" + this.quoteCharacter + "]");
		
		// Identify the delimiter
		this.delimiter = identifyDelimiter(textArray);
		System.out.println("Delimiter character is [" + this.delimiter + "]");
		
		// Determine if content is quoted by checking number of quotes
		this.quotedStrings = false;

		this.headerLine = identifyHeaderLine(textArray);
		if (this.headerLine == -1) {
			messages.add("Unable to determine start of person information");
		}
		
		headerColumnMapping = identifyColumnHeaders();

		characterisationCompleted = true;
	}
	
	private Map<String, ArrayList<Integer>> identifyColumnHeaders () {
		Map<String, ArrayList<Integer>> headerColumns = new HashMap<String, ArrayList<Integer>>();
		
		ArrayList<Integer> columnNumbers = new ArrayList<Integer>();
		// Title
		columnNumbers.add(1);
		headerColumns.put("TITLE", columnNumbers);
		// First Name
		columnNumbers = new ArrayList<Integer>();
		columnNumbers.add(3);
		headerColumns.put("FIRSTNAME", columnNumbers);
		// Middle name
		columnNumbers = new ArrayList<Integer>();
		//columnNumbers.add(4);
		headerColumns.put("MIDDLENAME", columnNumbers);
		// Surname
		columnNumbers = new ArrayList<Integer>();
		columnNumbers.add(2);
		headerColumns.put("SURNAME", columnNumbers);
		// Address
		columnNumbers = new ArrayList<Integer>();
		columnNumbers.add(4);
		columnNumbers.add(5);
		columnNumbers.add(6);
		headerColumns.put("ADDRESS", columnNumbers);
		// POSTCODE
		columnNumbers = new ArrayList<Integer>();
		columnNumbers.add(7);
		headerColumns.put("POSTCODE", columnNumbers);
		// Year
		columnNumbers = new ArrayList<Integer>();
		headerColumns.put("YEAR", columnNumbers);

		return headerColumns;
	}
	
	/*
	
	public Map<String, ArrayList<Integer>> getHeaderColumnMapping() {
		return headerColumnMapping;
	}

	public boolean isCharacterisationCompleted() {
		return characterisationCompleted;
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

	*/	
	
}
