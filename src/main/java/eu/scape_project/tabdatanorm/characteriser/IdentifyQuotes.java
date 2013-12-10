package eu.scape_project.tabdatanorm.characteriser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.scape_project.tabdatanorm.characteriser.IdentifyCharacters;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class IdentifyQuotes {

	private Character[] defaultQuoteCharacters = Constants.DEFAULT_QUOTE_CHARACTERS;
	private ArrayList<Character> quoteCharacters = new ArrayList<Character>();
	
	public IdentifyQuotes () {	
		for (int i=0; i<defaultQuoteCharacters.length; i++) {
			this.quoteCharacters.add(defaultQuoteCharacters[i]);
		}
	}
	
	public IdentifyQuotes ( ArrayList<Character> quoteCharacters ) {	
		for (int i=0; i<defaultQuoteCharacters.length; i++) {
			this.quoteCharacters = quoteCharacters;
		}
	}
	
	public boolean isContentQuoted2(String text, String delimiter) {
		boolean quoted = true;
		Character quoteChar = identifyQuoteCharacter(text);

		return isContentQuoted(text, delimiter, quoteChar);
	} 
	
	public boolean isContentQuoted(String text, String delimiter) {
		boolean quoted = true;
		Character quoteChar = identifyQuoteCharacter(text);

		return isContentQuoted(text, delimiter, quoteChar);
	} 
	
	public boolean isContentQuoted(String text, String delimiter, Character quoteCharacter) {
		boolean quoted = true;
		if (quoteCharacter != null) {
			String[] dataElements = text.split(delimiter);
			for (int i=0; i<dataElements.length; i++) {
				if ((dataElements[i].charAt(0) != quoteCharacter) || 
					(dataElements[i].charAt(dataElements[i].length()-1) != quoteCharacter)) {
					quoted = false;
					break;
				}
			}
		}
			
		return quoted;
	}
	
	public int getQuoteCharacterCount(String text, Character quoteChar) {
		
		IdentifyCharacters identifyCharacters = new IdentifyCharacters();
		ArrayList<Character> quoteCharacter = new ArrayList<Character>();
		quoteCharacter.add(quoteChar);
		Map<Character, Integer> charCounts = identifyCharacters.getCharacters(text, quoteCharacter);
		
		return charCounts.get(quoteChar);
	}
	
	public Map<Character, Integer> getQuoteCharacterCount(String text) {
		
		IdentifyCharacters identifyCharacters = new IdentifyCharacters();
		Map<Character, Integer> charCounts = identifyCharacters.getCharacters(text, quoteCharacters);
		return charCounts;
	}
	
	public Map<Character, Integer> getQuoteCharacterCount(List<String> text) {
		
		IdentifyCharacters identifyCharacters = new IdentifyCharacters();
		Map<Character, Integer> charCounts = identifyCharacters.getCharacters(text, quoteCharacters);
		return charCounts;
	}
	
	public Character identifyQuoteCharacter(String text) {
		Character quoteCharacter = null;
		Map<Character, Integer> quoteChars = getQuoteCharacterCount(text);
		Iterator quotes = quoteChars.entrySet().iterator();
		int quoteMaxCount = 0;
		while (quotes.hasNext()) {
			Map.Entry pairs = (Map.Entry)quotes.next();
			int quoteCount = 0;
			System.out.println("Quote char " + pairs.getKey() + " used " + pairs.getValue() + " times");
			if (pairs.getValue() != null && (Integer)pairs.getValue() > quoteMaxCount) {
				quoteMaxCount = (Integer)pairs.getValue();
				quoteCharacter = (Character)pairs.getKey();
			}
		}
		System.out.println("Quote character = " + quoteCharacter);
		return quoteCharacter;
	}

	public Character identifyQuoteCharacter(List<String> text) {
		Character quoteCharacter = null;
		Map<Character, Integer> quoteChars = getQuoteCharacterCount(text);
		Iterator quotes = quoteChars.entrySet().iterator();
		int quoteMaxCount = 0;
		while (quotes.hasNext()) {
			Map.Entry pairs = (Map.Entry)quotes.next();
			int quoteCount = 0;
			System.out.println("Quote char " + pairs.getKey() + " used " + pairs.getValue() + " times");
			if (pairs.getValue() != null && (Integer)pairs.getValue() > quoteMaxCount) {
				quoteMaxCount = (Integer)pairs.getValue();
				quoteCharacter = (Character)pairs.getKey();
			}
		}
		System.out.println("Quote character = " + quoteCharacter);
		return quoteCharacter;
	}
	
	public ArrayList<Character> getQuoteCharacters() {
		return quoteCharacters;
	}

	public void setQuoteCharacters(ArrayList<Character> quoteCharacters) {
		this.quoteCharacters = quoteCharacters;
	}	
	
}
