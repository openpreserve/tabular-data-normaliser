package eu.scape_project.tabdatanorm.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;
import eu.scape_project.tabdatanorm.normalisation.NormalisedDataItem;
import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class PDFFileDataParser extends DataFileParser {

	private String postCode = "";
	private String streetName = "";
	private boolean detailsComplete = true;
	//private int details = 0;
	//ArrayList<String> elementList = new ArrayList<String>();
		
	public PDFFileDataParser( DataFileDetailsDto inputDataFile, 
			NormalisedFormat normalisedFormat) 
							throws IOException {
		super(inputDataFile, normalisedFormat);	
	}

	//public void addElement(String inputElement) {
	//	elementList.add(inputElement.trim());
	//}

	public void endOfLine() {
		
		System.out.println("PDFFileDataParser - end of line " + inputRecordCounter + ", " + elementList.size() + " elements");		
		inputRecordCounter++;
		if (elementList.size() >= 2) {
			// Check for a postcode line
			if ((elementList.get(0).matches(Constants.POSTCODE_PART1_REGEX)) && 
				(elementList.get(1).matches(Constants.POSTCODE_PART2_REGEX))) {
				System.out.println("  Line " + inputRecordCounter + " Found postcode [" + elementList.get(0) + " " + elementList.get(0) +"]");
				postCode=elementList.get(0) + " " + elementList.get(1);
				//detailsComplete = true;
			}
			// Check for DOB, remove
			if (elementList.get(1).matches(Constants.DATE_DOB_REGEX)) {
				System.out.println("  Line " + inputRecordCounter + " contains DOB [" + elementList.get(1) + "]");
				elementList.set(1,(elementList.get(1).replaceFirst(Constants.DATE_DDMMYY_REGEX, "")).trim());
				System.out.println("  Removed DOB [" + elementList.get(1) + "]");	
			}
			// Check for single uppercase G after number
			if ((elementList.get(0).matches(Constants.NUMBER_REGEX)) &&
				(elementList.get(1).matches("[G]"))) {
				elementList.remove(1);
				System.out.println("  Removed G prefix");
			}
			// Separate surname and first name
			if (elementList.get(1).matches(Constants.SURNAME_FIRSTNAME_REGEX)) {
				String[] names = elementList.get(1).split(",");
				elementList.set(1, names[0].trim());
				elementList.add(2, names[1].trim());
				System.out.println("  Separating surname and first name [" + elementList.size() + "]");
			}
		}

		// Expect 4 elements, number, surname, first name and first part of address
		if (elementList.size() >= 2 && 
			((elementList.get(0).matches(Constants.NUMBER_REGEX)) &&
			 (elementList.get(1).matches(Constants.SURNAME_REGEX)))) {
			System.out.println("  Line " + inputRecordCounter + " Found details [" + 
					elementList.get(0) + " " + elementList.get(1) + "]");

			if (elementList.size() >= 4) {
				// Remove trailing comma from surname
				if (elementList.get(0).endsWith(",")) {
					elementList.set(0, elementList.get(0).substring(0, elementList.get(0).length()-1));
				}
				try {
					parse();
					elementList.clear();
					elementList.clear();
					normalisedFormat.clear();	
					detailsComplete = true;
				} catch (IOException ioe) {
						
				}
			} else {
				// At this point we have a partial record, the rest is on the next line
				// mark details as incomplete
				detailsComplete = false;
			}
		} else {
			// Fewer elements than expected, possibly a continuation of previous line
			if (detailsComplete) {
				elementList.clear();
			}
		}
	}		
}
