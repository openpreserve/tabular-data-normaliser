package eu.scape_project.tabdatanorm.characteriser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IdentifyDelimiter {

	private ArrayList<String> delimiterCharacters = new ArrayList<String>();
	private String text;
	private String[] defaultDelimiters = {"\t", ",", ":", "|"};
	
	public IdentifyDelimiter () {
		for (int i=0; i<defaultDelimiters.length; i++) {
			this.delimiterCharacters.add(defaultDelimiters[i]); 
		}
	}
	
	public IdentifyDelimiter (String text) {
		this();
		this.text = text;
	}
	
	public IdentifyDelimiter (String text, ArrayList<String> delimiterCharacters) {
		this.delimiterCharacters = delimiterCharacters;
		this.text = text;
	}
	
	public IdentifyDelimiter (ArrayList<String> delimiterCharacters) {
		this.delimiterCharacters = delimiterCharacters;
	}
	
	public Map<String, Integer> getDelimiters(String text) {
		// TODO
		// Should ignore delimiter characters that are enclosed in quotes, 
		// i.e. part of the data rather than delimiters
		Map<String, Integer> delimiters = new HashMap<String, Integer>();
		for ( String delimiterCharacter : delimiterCharacters ) {
			int delimiterCount = 0;
			if (text != null) {
				for (int i=0; i<text.length(); i++) {
					if (text.indexOf(delimiterCharacter, i) > -1) {
						delimiterCount++;
					}
				}
			}
			delimiters.put(delimiterCharacter, delimiterCount);
		}
		return delimiters;	
	}
	
	public Map<String, Integer> getDelimiters() {
		return getDelimiters(this.text);	
	}
		
	
}
