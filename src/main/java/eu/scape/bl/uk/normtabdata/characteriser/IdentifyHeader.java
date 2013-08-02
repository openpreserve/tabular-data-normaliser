package eu.scape.bl.uk.normtabdata.characteriser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IdentifyHeader {

	private Map<String, ArrayList<String>> headerColumnKeywords = new HashMap<String, ArrayList<String>>();
	private String delimiter;
	private int headerKeywordMatches = 0;

	public IdentifyHeader(Map<String, ArrayList<String>> headerColumnKeywords, String delimiter) {
		this.headerColumnKeywords = headerColumnKeywords;
		this.delimiter = delimiter;
	}
	
	public Map<String, ArrayList<Integer>> identifyColumnHeaders(String text) {
		//System.out.println("Identifying columns for headers");
		String[] dataElements = text.split(delimiter);
		Map<String, ArrayList<Integer>> columnMappings = new HashMap<String, ArrayList<Integer>>();
			
		Iterator headers = headerColumnKeywords.entrySet().iterator();
		while (headers.hasNext()) {
			Map.Entry pairs = (Map.Entry)headers.next();
			String headerName = (String)pairs.getKey();
			//System.out.println("Identifying columns for header " + headerName);
			ArrayList<String> headerKeywords = (ArrayList<String>)pairs.getValue();
			ArrayList<Integer> matchingColumns = new ArrayList<Integer>();
			for (String columnKeyword: headerKeywords) {
				for ( int i=0; i<dataElements.length; i++ ) {	
					if (dataElements[i].toUpperCase().matches(columnKeyword)) {
		    			matchingColumns.add(new Integer(i+1));
		    			//System.out.println("  Match found - data element " + i + " [" + dataElements[i] + "] for header " + headerName + " natchcount = " + matchingColumns.size());
					}	
				}
			}
    		columnMappings.put(headerName, matchingColumns);
    		//System.out.println("  Mappings for header " + headerName + " are [" + matchingColumns.size() + "]");
		}
		
		return columnMappings;
	}

	public void listHeaderKeywords() {
		
		Iterator columns = headerColumnKeywords.entrySet().iterator();
		System.out.println("Header keywords");
		while (columns.hasNext()) {
			Map.Entry pairs = (Map.Entry)columns.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
	}
	
	public int identifyHeaderLine(List<String> inputText) {

		System.out.println("IdentifyHeader - Identifying header line from content");
		for (String text: inputText) {
			System.out.println("Input element [" + text + "]");
		}
		int headerLineNumber = -1;
		int lineCounter = 0;
		int maxMatchesCount = 0;
		int matchCount = 0;
		for (String text: inputText) {
			Map<String, ArrayList<Integer>> columnHeaderMappings = identifyColumnHeaders(text);
			Iterator headers = columnHeaderMappings.entrySet().iterator();
			lineCounter++;
			matchCount = 0;
			while (headers.hasNext()) {
				Map.Entry pairs = (Map.Entry)headers.next();
				ArrayList<Integer> headerColumns = (ArrayList<Integer>)pairs.getValue();		
				if (headerColumns != null) {
					matchCount = matchCount + headerColumns.size();
				}
			}
			if (matchCount > maxMatchesCount) {
				maxMatchesCount = matchCount;
				headerLineNumber = lineCounter;
			}
		}
		this.headerKeywordMatches = maxMatchesCount;
		System.out.println("Header line is " + headerLineNumber + " with " + maxMatchesCount + " matches");
		return headerLineNumber;
	}

	public int getHeaderKeywordMatches() {
		return headerKeywordMatches;
	}

	public void setHeaderKeywordMatches(int headerKeywordMatches) {
		this.headerKeywordMatches = headerKeywordMatches;
	}

	
	
}
