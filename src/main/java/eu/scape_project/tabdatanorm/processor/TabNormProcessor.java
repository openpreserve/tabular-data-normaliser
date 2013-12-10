package eu.scape_project.tabdatanorm.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import eu.scape_project.tabdatanorm.characteriser.BLFileCharacteriser;
import eu.scape_project.tabdatanorm.characteriser.BLPDFFileCharacteriser;
import eu.scape_project.tabdatanorm.characteriser.FileCharacteriser;
import eu.scape_project.tabdatanorm.characteriser.GovDataFileCharacteriser;
import eu.scape_project.tabdatanorm.dto.DataFileDetailsDto;
import eu.scape_project.tabdatanorm.identifier.FileIdentifier;
import eu.scape_project.tabdatanorm.identifier.FileListGenerator;
import eu.scape_project.tabdatanorm.identifier.FileProcessor;
import eu.scape_project.tabdatanorm.normalisation.NormalisedDataItem;
import eu.scape_project.tabdatanorm.normalisation.NormalisedFormat;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class TabNormProcessor {

	private String propertiesFile;
	private String outputDirectory;
	private String auditDirectory;
	private ArrayList<String> inputFiles;
	private ArrayList<String> outputDataItems;
	
	
	public TabNormProcessor (String propertiesFile, ArrayList<String> inputFiles) {
		this.propertiesFile = propertiesFile;
		this.inputFiles = inputFiles;
	}

	public void processRegister() throws Exception {
		
		// Validate propertiesFile, outputDirectory and inputfILES
		validateInput();
		
		// Get normalisation properties
		NormalisedFormat normalisedFormat = getNormalisationProperties();
		
		// Generate list of register files
		ArrayList<String> fileList = generateListOfFiles();
			
		// Identify the type of each file
		Hashtable<String, DataFileDetailsDto> registerDTOs = identifyFileTypes(fileList);
		
		// Characterise and process each file in turn
		Enumeration<String> dataFilesEnum = registerDTOs.keys();
		while(dataFilesEnum.hasMoreElements()) {
			String key = dataFilesEnum.nextElement();
			DataFileDetailsDto inputDataFile = registerDTOs.get(key);
			characteriseRegisterFile(inputDataFile, normalisedFormat);
			processRegisterFile(inputDataFile, normalisedFormat);	
		}
		
		listInputFiles(registerDTOs);
		createAuditLog(registerDTOs);
	
	}
	
	/**
	 * Carries out validation on the input parameters
	 * <p>
	 * Checks that properties file exists
	 * <p>
	 * Checks that output directory exists
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	private void validateInput () throws IOException, Exception {
		
		// Check that the properties file exists
		File propFile = new File(propertiesFile);
		if (!propFile.exists()) { 
			throw new IOException("Unable to open properties file " + propertiesFile);	
		}
		
		// Check that the list of register files is not empty
		if (inputFiles == null || inputFiles.isEmpty()) {
			throw new Exception("No files to process");
		}
	}
	
	private NormalisedFormat getNormalisationProperties() throws IOException, NumberFormatException {
		
		NormalisedFormat normalisedFormat = new NormalisedFormat();
		Properties prop = new Properties();
		outputDataItems = new ArrayList<String>();
		 
        //load a properties file
		prop.load(new FileInputStream(propertiesFile));
		
		// Get the output directory
		outputDirectory = prop.getProperty(Constants.PROPERTY_OUTPUT_DIRECTORY);
		// Check that the output directory exists
		File outDir = new File(outputDirectory);
		if (!outDir.exists() || !outDir.isDirectory()) {
			throw new IOException("Output directory " + outputDirectory + " does not exist");		
		}
		
		// Get the audit directory
		auditDirectory = prop.getProperty(Constants.PROPERTY_AUDIT_DIRECTORY);
		// Check that the output directory exists
		File auditDir = new File(auditDirectory);
		if (!auditDir.exists() || !auditDir.isDirectory()) {
			throw new IOException("Audit directory " + auditDirectory + " does not exist");		
		}
    		
		// Get the delimiter that will be used in the output file
		normalisedFormat.setNormalisedOutputDelimiter(prop.getProperty(Constants.PROPERTY_OUTPUT_FILE_DELIMITER));
		
		// Get the newline character(s) that will be used in the output file
		normalisedFormat.setNormalisedOutputNewLine(prop.getProperty(Constants.PROPERTY_OUTPUT_FILE_NEWLINE));
		
		// Get the quote character
		normalisedFormat.setNormalisedOutputQuotes(prop.getProperty(Constants.PROPERTY_OUTPUT_FILE_QUOTES));
		
		// Get the output file suffix
		normalisedFormat.setOutputFileType(prop.getProperty(Constants.PROPERTY_OUTPUT_FILE_TYPE));
		
		// Get the list of input header keywords
		String inputHeaderKeywordsProp = prop.getProperty(Constants.PROPERTY_HEADER_KEYWORDS);
		StringTokenizer tk = new StringTokenizer(inputHeaderKeywordsProp, ",");
        while (tk.hasMoreTokens()) {
        	normalisedFormat.addInputHeaderKeyword(tk.nextToken());
        }
        
		// Get the list of data items for the normalised output 
		String normalisedDataItemsProp = prop.getProperty(Constants.PROPERTY_NORMALISED_DATA_ITEMS);
		tk = new StringTokenizer(normalisedDataItemsProp, ",");
        while (tk.hasMoreTokens()) {
        	outputDataItems.add(tk.nextToken());	
        }
        
        // For each data item get the properties that will be used to generate the mapping
        // between the input data and the normalised output data
        // For each data item the properties are :
        // 		<dataItem>_MANDATORY - the data item must exist
        //		<dataItem>_VALIDATION_REQUIRED - carry out validation against the item
        //		<dataItem>_MIN_LENGTH - min length of data item , only used if data is mandatory
        //		<dataItem>_MAX_LENGTH - max length of data item , only used if data is mandatory
        //		<dataItem>_DEFAULT_VALUE - default value for the data item
        //		<dataItem>_KEYWORDS - list of keywords in the form of regular expression to identify 
        //                            the input column header(s) which maps to the output column.
        for (String propPrefix : outputDataItems) {
        	NormalisedDataItem normalisedDataItem = new NormalisedDataItem(propPrefix);    
            normalisedDataItem.setMandatory((String) prop.get(propPrefix + "_" + Constants.PROPERTY_MANDATORY));
            normalisedDataItem.setValidationRequired((String) prop.get(propPrefix + "_" + Constants.PROPERTY_VALIDATION_REQUIRED));
            normalisedDataItem.setMinLength((String) prop.get(propPrefix + "_" + Constants.PROPERTY_MIN_LENGTH));
            normalisedDataItem.setMaxLength((String) prop.get(propPrefix + "_" + Constants.PROPERTY_MAX_LENGTH));
            normalisedDataItem.setDefaultValue((String) prop.get(propPrefix + "_" + Constants.PROPERTY_DEFAULT_VALUE));
            normalisedDataItem.setValidationRegex((String) prop.get(propPrefix + "_" + Constants.PROPERTY_VALIDATION_REGEX));
            String dataItemKeywordProp = (String) prop.get(propPrefix + "_" + Constants.PROPERTY_KEYWORDS);
    		tk = new StringTokenizer(dataItemKeywordProp, ",");
            while (tk.hasMoreTokens()) {
            	normalisedDataItem.addDataItemHeaderKeywords(tk.nextToken());	
            }         
            String dataColumnIdentificationValuesProp = (String) prop.get(propPrefix + "_" + Constants.PROPERTY_VALUES);
            System.out.println("Column value identifiers for " + propPrefix + " = " + dataColumnIdentificationValuesProp);
            if (dataColumnIdentificationValuesProp != null && !dataColumnIdentificationValuesProp.isEmpty()) {
            	tk = new StringTokenizer(dataColumnIdentificationValuesProp, ",");
            	while (tk.hasMoreTokens()) {
            		normalisedDataItem.addColumnIdentificationValue(tk.nextToken());	
            	}
            }
            normalisedDataItem.setColumnIdentificationRegex((String) prop.get(propPrefix + "_" + Constants.PROPERTY_REGEX));
            System.out.println("Column regex identifier for " + propPrefix + " = " + normalisedDataItem.getColumnIdentificationRegex());  
            
            normalisedDataItem.setReplaceNonPrintableCharacters((String) prop.get(propPrefix + "_" + Constants.PROPERTY_REPLACE_NONPRINTABLE_CHARACTERS));
            normalisedDataItem.setReplaceNonPrintableCharactersWith((String) prop.get(propPrefix + "_" + Constants.PROPERTY_REPLACE_NONPRINTABLE_CHARACTERS_WITH));
            
            normalisedFormat.addDataItem(normalisedDataItem);
        }
        
        System.out.println(normalisedFormat.toStringFull());
		return normalisedFormat;
	}
	
	
	private ArrayList<String> generateListOfFiles() {		
		System.out.println("Generating file list");
		FileListGenerator listGenerator = new FileListGenerator(inputFiles);
		ArrayList<String> fileList = listGenerator.getListOfFiles();
		System.out.println("Generated file list");
		return fileList;
	}
	
	private Hashtable<String, DataFileDetailsDto> identifyFileTypes(ArrayList<String> fileList) {
		
		System.out.println("Identify file types");
		FileIdentifier identifier = new FileIdentifier(fileList, outputDirectory, auditDirectory);
		Hashtable<String, DataFileDetailsDto> registerDTOs = identifier.identify();
		System.out.println("Completed identification of file types\n");
		return registerDTOs;
		
	}
	
	private void characteriseRegisterFile (DataFileDetailsDto inputDataFile,
			NormalisedFormat normalisedFormat) 
			throws IOException, Exception {

		System.out.println("\n\nStarting to characterise register file" + inputDataFile.getInputFileName());
		ArrayList<NormalisedDataItem> normalisedDataItems = normalisedFormat.getDataItems();

		// Get data file details
		if (Constants.FILE_MIME_TYPE_PLAIN_TEXT.equals(inputDataFile.getInputFileType()) ||
			Constants.FILE_MIME_TYPE_CSV_TEXT.equals(inputDataFile.getInputFileType())) {
			// Get file characteristics
			Map<String, ArrayList<String>> headerColumnKeywords = new HashMap<String, ArrayList<String>>();
			Map<String, ArrayList<String>> columnIdentificationValues = new HashMap<String, ArrayList<String>>();
			Map<String, String> columnIdentificationRegex = new HashMap<String, String>();
			for (NormalisedDataItem normalisedDataItem : normalisedDataItems) {
				headerColumnKeywords.put(normalisedDataItem.getDataItemName(), normalisedDataItem.getDataItemHeaderKeywords());
				if (normalisedDataItem.getColumnIdentificationValues() != null && !normalisedDataItem.getColumnIdentificationValues().isEmpty()) {
					columnIdentificationValues.put(normalisedDataItem.getDataItemName(), normalisedDataItem.getColumnIdentificationValues());
				}
				if (normalisedDataItem.getColumnIdentificationRegex() != null && !normalisedDataItem.getColumnIdentificationRegex().isEmpty()) {
					columnIdentificationRegex.put(normalisedDataItem.getDataItemName(), normalisedDataItem.getColumnIdentificationRegex());
				}
			}

			BLFileCharacteriser characteriser = 
					new BLFileCharacteriser(inputDataFile.getInputFilePath(), headerColumnKeywords);
			characteriser.setColumnIdentificationValues(columnIdentificationValues);
			characteriser.setColumnIdentificationRegex(columnIdentificationRegex);
			characteriser.process();
			inputDataFile.setDelimiter(characteriser.getDelimiter());
			inputDataFile.setHeaderLine(characteriser.getHeaderLine());
			inputDataFile.setErrorMessages(characteriser.getMessages());
			inputDataFile.setInputLineCount(characteriser.getLinesInFile());
			if (characteriser.isQuotedStrings()) {
				inputDataFile.setQuoteCharacter(characteriser.getQuoteCharacter());
			}

			Map<String, ArrayList<Integer>> headerColumnMappings = characteriser.getHeaderColumnMapping();
			Iterator mappings = headerColumnMappings.entrySet().iterator();
			while (mappings.hasNext()) {
				Map.Entry pairs = (Map.Entry)mappings.next();
				for (NormalisedDataItem normalisedDataItem: normalisedDataItems) {
					if (normalisedDataItem.getDataItemName().equals(pairs.getKey())) {
						normalisedDataItem.setInputDataColumnIndex((ArrayList<Integer>)pairs.getValue());
						System.out.println("Column mapping " + pairs.getKey() + "=" + pairs.getValue());
					}
				}
			}

		} else if (Constants.FILE_MIME_TYPE_PDF.equals(inputDataFile.getInputFileType())) {
			System.out.println("File identified as PDF");
			BLPDFFileCharacteriser characteriser = new BLPDFFileCharacteriser(inputDataFile.getInputFilePath());
			characteriser.process();
			inputDataFile.setDelimiter(characteriser.getDelimiter());
			inputDataFile.setHeaderLine(characteriser.getHeaderLine());
			inputDataFile.setErrorMessages(characteriser.getMessages());
			inputDataFile.setInputLineCount(characteriser.getLinesInFile());
			if (characteriser.isQuotedStrings()) {
				inputDataFile.setQuoteCharacter(characteriser.getQuoteCharacter());
			}

			Map<String, ArrayList<Integer>> headerColumnMappings = characteriser.getHeaderColumnMapping();
			System.out.println("Characteriser column mapping " + characteriser.getHeaderColumnMapping().toString());
			Iterator mappings = headerColumnMappings.entrySet().iterator();
			while (mappings.hasNext()) {
				Map.Entry pairs = (Map.Entry)mappings.next();
				for (NormalisedDataItem normalisedDataItem: normalisedDataItems) {
					if (normalisedDataItem.getDataItemName().equals(pairs.getKey())) {
						normalisedDataItem.setInputDataColumnIndex((ArrayList<Integer>)pairs.getValue());
						//inputDataFile.setInputDataColumnIndex((ArrayList<Integer>)pairs.getValue());
						System.out.println("Column mapping " + pairs.getKey() + "=" + pairs.getValue());
					}
				}
			}
		}			

		System.out.println("Completed characterisation of register file" + inputDataFile.getInputFileName());	
	}	
	
		
	private void processRegisterFiles (Hashtable<String, DataFileDetailsDto> registerDTOs,
			NormalisedFormat normalisedFormat) 
			throws IOException, Exception {
		System.out.println("\n\nStarting to process register files");
		Enumeration<String> dataFilesEnum = registerDTOs.keys();
		while(dataFilesEnum.hasMoreElements()) {
			// Get data file details
			String key = dataFilesEnum.nextElement();
			DataFileDetailsDto inputDataFileDetails = registerDTOs.get(key);
			FileProcessor processor = new FileProcessor(inputDataFileDetails, normalisedFormat);
			processor.process();
		}
		System.out.println("Completed processing of register files");	
	}
	
	
	private void processRegisterFile (DataFileDetailsDto inputDataFileDetails,
			NormalisedFormat normalisedFormat) 
			throws IOException, Exception {
		System.out.println("\n\nStarting to process register file " + inputDataFileDetails.getInputFileName());
		FileProcessor processor = new FileProcessor(inputDataFileDetails, normalisedFormat);
		processor.process();
		System.out.println("Completed processing of register file" + inputDataFileDetails.getInputFileName());
	}
		
	
	private void outputToCSV(Hashtable<String, DataFileDetailsDto> registerDTOs) throws IOException {	
		
		File fileListFile = new File(auditDirectory + "//" + "RegisterFileListing.csv");
		Writer fileListBuffer = new BufferedWriter(new FileWriter(fileListFile));
		fileListBuffer.write("Input File, File Type, Input Line Count, Processed Record Count, Error Count, Encrypted, Password, Header Line, Delimiter, Output File, Output Record Count, Messages/Warnings\r\n");
		fileListBuffer.flush();
		
		Enumeration<String> dataFilesEnum = registerDTOs.keys();
		while(dataFilesEnum.hasMoreElements()) {
			String key = dataFilesEnum.nextElement();
			DataFileDetailsDto dataFile = registerDTOs.get(key);	
			String out = dataFile.getInputFilePath() + "," + dataFile.getInputFileType() + "," + 
						 dataFile.getInputLineCount() + "," + dataFile.getInputRecordCount() + "," +
						 dataFile.getRecordsFailedProcessing() + "," + dataFile.isEncrypted() + "," + 
						 dataFile.getPassword() + ",";
			if (dataFile.getHeaderLine() != -1) {
				out = out + dataFile.getHeaderLine() + ",";
			} else {
				out = out + "NO HEADER,";
			}
			if ("\t".equals(dataFile.getDelimiter())) {
				out = out + "TAB,";
			} else {
				out = out + "\"" + dataFile.getDelimiter() + "\",";
			}
			out = out + dataFile.getOutputFilePath() + "," + 
					dataFile.getOutputRecordCount() + "," + dataFile.getErrorMessages();
			fileListBuffer.write(out + "\r\n");
		}
		fileListBuffer.flush();
		fileListBuffer.close();
	}
	
	private void createAuditLog(Hashtable<String, DataFileDetailsDto> registerDTOs) throws IOException {	
		
		File auditFile = new File(auditDirectory + "RegisterAudit.csv");
		String auditHeader = null;
		if (!auditFile.exists()) {
			auditHeader = "Date/Time,Input File,File Type,Input Line Count,Processed Record Count,Error Count,Encrypted,Password,Header Line,Delimiter,Output File,Output Record Count,Messages/Warnings" + Constants.DEFAULT_OUTPUT_FILE_NEWLINE;
		}
		Writer auditBuffer = new BufferedWriter(new FileWriter(auditFile, true));
		if (auditHeader != null) {
			auditBuffer.write(auditHeader);
		}
		
		Enumeration<String> dataFilesEnum = registerDTOs.keys();
		while(dataFilesEnum.hasMoreElements()) {
			String key = dataFilesEnum.nextElement();
			DataFileDetailsDto dataFile = registerDTOs.get(key);	
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			String out = dateFormat.format(date) + "," + dataFile.getInputFilePath() + "," + 
						dataFile.getInputFileType() + "," + 
						dataFile.getInputLineCount() + "," + dataFile.getInputRecordCount() + "," +
						dataFile.getRecordsFailedProcessing() + "," + dataFile.isEncrypted() + "," + 
						dataFile.getPassword() + ",";
			if (dataFile.getHeaderLine() != -1) {
				out = out + dataFile.getHeaderLine() + ",";
			} else {
				out = out + "NO HEADER,";
			}
			if ("\t".equals(dataFile.getDelimiter())) {
				out = out + "TAB,";
			} else {
				out = out + "\"" + dataFile.getDelimiter() + "\",";
			}
			out = out + dataFile.getOutputFilePath() + "," + 
					dataFile.getOutputRecordCount() + "," + dataFile.getErrorMessages();
			auditBuffer.write(out + Constants.DEFAULT_OUTPUT_FILE_NEWLINE);
		}
		auditBuffer.flush();
		auditBuffer.close();
	}
	
	
	private void listInputFiles(Hashtable<String, DataFileDetailsDto> registerDTOs) {	
		Enumeration<String> dataFilesEnum = registerDTOs.keys();
		while(dataFilesEnum.hasMoreElements()) {
			String key = dataFilesEnum.nextElement();
			DataFileDetailsDto dataFile = registerDTOs.get(key);
			
			System.out.println("\tInput File : " + dataFile.getInputFilePath());
			System.out.println("\t\tFile Type      : " + dataFile.getInputFileType());
			System.out.println("\t\tRecord count   : " + dataFile.getInputLineCount());
			System.out.println("\t\tEncrypted      : " + dataFile.isEncrypted());
			System.out.println("\t\tPassword       : " + dataFile.getPassword());
			if (dataFile.getHeaderLine() != -1) {
				System.out.println("\t\tHeader line    : " + dataFile.getHeaderLine());
			} else {
				System.out.println("\t\tHeader line    : NO HEADER");
			}
			System.out.println("\t\tDelimiter      : " + dataFile.getDelimiter());
			System.out.println("\t\tOutput File        : " + dataFile.getOutputFilePath());			
			System.out.println("\t\tError messages : " + dataFile.getErrorMessages());
		}
	}
			
}
