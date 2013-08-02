package eu.scape.bl.uk.normtabdata.parser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import eu.scape.bl.uk.normtabdata.utilities.Constants;

public class PDFContentHandler implements ContentHandler {
	
	private ArrayList<String> contentLines = new ArrayList<String>();
	private PDFFileDataParser parser;
	String SURNAME_FIRSTNAME_REGEX ="^([A-Z`'-\\[\\]]{1,},\\s[A-Z]{1}[a-z]{1,})";
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String content = "";
		for (int i=0; i<ch.length; i++) {
			content = content + ch[i];
		}
		if (content.charAt(0) == '\n' || content.charAt(0) == '\r') {	
			parser.endOfLine();
			System.out.println("    PDFContentHandler - newline");
		} else {
			/*
			if (content.matches(Constants.DATE_DOB_REGEX)) {
				System.out.println("  Line contains DOB [" + content + "]");
				content = content.replaceFirst(Constants.DATE_DDMMYY_REGEX, "").trim();
				System.out.println("  Removed DOB [" + content + "]");
			}
			if (content.matches(SURNAME_FIRSTNAME_REGEX)) {
				String names[] = content.split(",");
				for (int i=0; i<names.length; i++) {
					parser.addElement(names[i].trim());
				}
			} */
			parser.addElement(content);
			System.out.println("    PDFContentHandler - content [" + content +"]");
		}
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		//parser.endOfLine();
		parser.endOfFile();
		System.out.println("    PDFContentHandler - end of file");

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("    PDFContentHandler - end of element [" + localName + " " + qName +"]");
		
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("    PDFContentHandler - skipped entity [" + name + "]");

		
	}

	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		parser.startOfFile();
		System.out.println("    PDFContentHandler - start of file");

	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("    PDFContentHandler - start of element [" + localName + " " + qName + "]");
		if ("div".equals(qName)) {
			parser.endOfLine();
		}
		
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<String> getContentLines() {
		return contentLines;
	}

	public void setContentLines(ArrayList<String> contentLines) {
		this.contentLines = contentLines;
	}

	public PDFFileDataParser getParser() {
		return parser;
	}

	public void setParser(PDFFileDataParser parser) {
		this.parser = parser;
	}
	
	public String toHex(String in) {
		return String.format("%x", new BigInteger(1, in.getBytes(Charset.defaultCharset())));
	}
}
