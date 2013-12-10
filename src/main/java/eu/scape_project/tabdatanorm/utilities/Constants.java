package eu.scape_project.tabdatanorm.utilities;


public final class Constants {

	// File mime types
	public static final String FILE_MIME_TYPE_PLAIN_TEXT = "text/plain";  		// Plain text file type
	public static final String FILE_MIME_TYPE_CSV_TEXT = "text/csv";  			// Comma delimited text file type
	public static final String FILE_MIME_TYPE_PDF = "application/pdf"; 			// PDF format
	public static final String FILE_MIME_TYPE_MSWORD = "application/msword"; 	// MSWord format
	public static final String FILE_MIME_TYPE_ZIP = "application/zip";			// Zip file
	public static final String FILE_MIME_TYPE_MSEXCEL = "application/vnd.ms-excel";	 // Zip file
	
	//Properties
	public static final String PROPERTY_OUTPUT_FILE_DELIMITER="OUTPUT_FILE_DELIMITER";
	public static final String PROPERTY_OUTPUT_FILE_NEWLINE="OUTPUT_FILE_NEWLINE";
	public static final String PROPERTY_OUTPUT_FILE_QUOTES="OUTPUT_FILE_QUOTES";
	public static final String PROPERTY_NORMALISED_DATA_ITEMS="NORMALISED_DATA_ITEMS";
	public static final String PROPERTY_HEADER_KEYWORDS="HEADER_KEYWORDS";
	public static final String PROPERTY_OUTPUT_FILE_TYPE="OUTPUT_FILE_TYPE";
	public static final String PROPERTY_MANDATORY="MANDATORY";
	public static final String PROPERTY_VALIDATION_REQUIRED="VALIDATION_REQUIRED";
	public static final String PROPERTY_MIN_LENGTH="MIN_LENGTH";
	public static final String PROPERTY_MAX_LENGTH="MAX_LENGTH";
	public static final String PROPERTY_DEFAULT_VALUE="DEFAULT_VALUE";
	public static final String PROPERTY_KEYWORDS="KEYWORDS";
	public static final String PROPERTY_VALUES="VALUES";
	public static final String PROPERTY_REGEX="REGEX";
	public static final String PROPERTY_VALIDATION_REGEX="VALIDATION_REGEX";
	public static final String PROPERTY_OUTPUT_DIRECTORY="OUTPUT_DIRECTORY";
	public static final String PROPERTY_AUDIT_DIRECTORY="AUDIT_DIRECTORY";
	public static final String PROPERTY_REPLACE_NONPRINTABLE_CHARACTERS="REPLACE_NONPRINTABLE_CHARACTERS";
	public static final String PROPERTY_REPLACE_NONPRINTABLE_CHARACTERS_WITH="REPLACE_NONPRINTABLE_CHARACTERS_WITH";

	
	// Default properties
	public static final String DEFAULT_OUTPUT_FILE_TYPE = "csv";
	public static final String DEFAULT_OUTPUT_FILE_DELIMITER = ",";
	public static final String DEFAULT_AUDIT_FILE_TYPE = "audit";
	public static final String DEFAULT_OUTPUT_FILE_NEWLINE = "\n";
	public static final Character[] DEFAULT_QUOTE_CHARACTERS = {'\"', '\''};
	
	// Hadoop constants
	public static final String HADOOP_ER_ROOT = "/HADOOP_ER_ROOT_NOT_SET";
	public static final String HADOOP_ER_INPUTFILES_DIR = "InputFiles/";
	public static final String HADOOP_ER_INPUT_FILE_LIST = "FileList.txt";
	public static final String HADOOP_ER_PROCESSED_DIR = "Processed/";
	public static final String HADOOP_ER_PROCESSED_NAME = "RegisterFull.csv";
	public static final String HADOOP_LOCAL_INPUT = "ERInput";
	public static final String HADOOP_LOCAL_OUTPUT = "EROutput";
	public static final String HADOOP_LOCAL_OUTPUT_FILE_PREFIX = "part-";	
	
	public static final String BUILDING_ADDRESS = "BUILDING_ADDRESS";
	public static final String BUILDING_ADDRESS_REGEX = "(.*COTTAGES|.*COTTAGE|.*FARM|.*HOUSE|.*CROFT|.*INN|.*LODGE|.*FLAT|.*HOME|.*SCHOOL|THE.*)";

	public static final String STREET_ADDRESS = "STREET_ADDRESS";
	public static final String STREET_ADDRESS_REGEX = "(.*ROAD|.*STREET|.*RISE|.*DRIVE|.*LANE|.*VIEW|.*ROW|.*GROVE|.*CLOSE|.*GATE|.*HILL|.*GREEN)";
	
	public static final String DATE_DOB_REGEX = "^([0-3][0-9]/[0-1][0-9]/[0-9]{2}).*";
	public static final String DATE_DDMMYY_REGEX = "([0-3][0-9]/[0-1][0-9]/[0-9]{2})";
	public static final String POSTCODE_PART1_REGEX = "([A-PR-UWYZ0-9][A-HK-Y0-9][AEHMNPRTVXY0-9]?[ABEHMNPRVWXY0-9]?)";
	public static final String POSTCODE_PART2_REGEX = "({1,2}[0-9][ABD-HJLN-UW-Z]{2}|GIR 0AA)";

	public static final String NUMBER_REGEX = "[0-9]{1,}";
	public static final String SURNAME_REGEX ="^[A-Z`'-\\[\\]]{3,}";
	public static final String SURNAME_FIRSTNAME_REGEX ="^([A-Z`'-]{1,},\\s[A-Z]{1}[a-z]{1,}).*";
	public static final String FIRSTNAME_REGEX ="^([A-Z]{1}[a-z]{1,})";
	public static final String SINGLE_INITIAL_REGEX = "[A-Z]{1}";
	public static final String INITIALS_REGEX = "[A-Z]{1}.{0,3}";
	public static final String DETAILS_NO_INITIAL_REGEX = "";	
	public static final String DETAILS_WITH_DOB = "";
}
