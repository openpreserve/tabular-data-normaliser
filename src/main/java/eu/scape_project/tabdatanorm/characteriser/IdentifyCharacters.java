package eu.scape_project.tabdatanorm.characteriser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IdentifyCharacters {

	public Map<Character, Integer> getCharacters(String text, ArrayList<Character> charsToFind) {
		
		Map<Character, Integer> charCounts = new HashMap<Character, Integer>();
		
		for ( Character charToFind : charsToFind ) {
			//System.out.println("Looking for character " + charToFind);
			int charCount = 0;
			int charLocation = 0;
			if (text != null && !text.isEmpty()) {
				while (charLocation != -1 && charLocation<=text.length()) {
					charLocation = text.indexOf(charToFind, charLocation);
				    if (charLocation != -1) {
				    	charCount++;
				    	charLocation++;
				    }
				}
			}
			charCounts.put(charToFind, charCount);
		}
		return charCounts;	
	}

	public Map<Character, Integer> getCharacters(List<String> textArray, ArrayList<Character> charsToFind) {
		
		Map<Character, Integer> charCounts = new HashMap<Character, Integer>();
		for (Character charToFind: charsToFind) {
			charCounts.put(charToFind, 0);
		}
		
		for (String text: textArray) {
			Map<Character, Integer> charCount = getCharacters(text, charsToFind);
			Iterator charCounter = charCount.entrySet().iterator();
			while (charCounter.hasNext()) {
				Map.Entry charCountPairs = (Map.Entry)charCounter.next();
				int charCountValue = (Integer) charCounts.get(charCountPairs.getKey()) +
						(Integer) charCountPairs.getValue();
				charCounts.put((Character)charCountPairs.getKey(), charCountValue);
			}
		}
		
		return charCounts;	
	}
		
	
}
