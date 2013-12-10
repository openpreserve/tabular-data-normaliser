package eu.scape_project.tabdatanorm.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Reducer;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import eu.scape_project.tabdatanorm.identifier.FileListGenerator;
import eu.scape_project.tabdatanorm.normalisation.NormalisedDataItem;
import eu.scape_project.tabdatanorm.utilities.Constants;

public class TabNormHadoopMain extends Configured implements Tool {

	public static TabNormHadoopProcessor processor = new TabNormHadoopProcessor();
	
	public static class ERMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
			
			System.out.println("ERMAPPER : map() - Key=" + key.toString() + ", Value=" + value.toString());
			ArrayList<String> inputFileList = new ArrayList<String>();
			inputFileList.add(value.toString());
					
			// Pass the list of input files to the register processor
			processor.setInputFiles(inputFileList);
			String outputFileName = "";
			try {
				// Process the input files
				outputFileName = processor.processRegister();
			} catch (Exception e) {
				throw new IOException("Failed to process input file");
			}
			
			System.out.println("ERMAPPER : Map() - Processed " + outputFileName);
						
			// Write contents of file to output for processing by the reducer
			BufferedReader reader = new BufferedReader(new FileReader(new File (outputFileName)));
			String text = null;
			boolean firstLine = true;
			String newline = processor.getNormalisedFormat().getNormalisedOutputNewLine();
			while ((text = reader.readLine()) != null) {
				if (!firstLine) {
					output.collect(new Text(key.toString()), new Text(text+newline));
				}
				firstLine = false;
			}
			System.out.println("ERMapper completed");
		}
	}
	
	public static class ERReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		private static int fileCounter=0; 
		
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,Text> output, Reporter reporter) throws IOException {
			System.out.println("ERREDUCER : reduce() - Key=" + key.toString());
			//reporter.setStatus("Starting ERREDUCER");
			ERReducer.fileCounter++;
			// Get the header
			String outputValues = "";
			if (fileCounter==1) {
				String headerDelimiter = processor.getNormalisedFormat().getNormalisedOutputDelimiter();
				ArrayList<String> columnHeaders = processor.getOutputDataItems();
				for (String columnHeader : columnHeaders) {
					outputValues = outputValues + columnHeader + headerDelimiter;
				}
				outputValues = outputValues.substring(0, outputValues.lastIndexOf(headerDelimiter));
				outputValues = outputValues + processor.getNormalisedFormat().getNormalisedOutputNewLine();
				System.out.println("ERREDUCER : Output header=" + outputValues);
			}
			while (values.hasNext()) {
				//if (textValues.length() > 0) textValues = textValues + " ";
				outputValues = outputValues + values.next();
			}
			System.out.println("ERREDUCER : reduce() - outputValues=" + outputValues);
			output.collect(new Text(""), new Text(outputValues));
			System.out.println("ERReducer : reduce() - Completed");
		}
	}
	
	public int run(String[] args) throws Exception {
		
		JobConf jobconf = new JobConf(TabNormHadoopMain.class);
		
		FileInputFormat.setInputPaths(jobconf, new Path(args[0]));
		FileOutputFormat.setOutputPath(jobconf, new Path(args[1]));

		jobconf.setJobName("Register");	
		jobconf.setJarByClass(TabNormHadoopMain.class);
		jobconf.setMapperClass(ERMapper.class);
		jobconf.setCombinerClass(ERReducer.class);
		jobconf.setReducerClass(ERReducer.class);
		jobconf.setOutputKeyClass(Text.class);
		jobconf.setOutputValueClass(Text.class);
		jobconf.setInputFormat(TextInputFormat.class);
		jobconf.setOutputFormat(TextOutputFormat.class);	
		jobconf.setMapOutputKeyClass(Text.class);
		jobconf.setOutputValueClass(Text.class);
		
		try {
			JobClient.runJob(jobconf);
		} catch (Exception e) {
			System.out.println("Job did not complete successfully - " + e.getMessage() + "");
			return 1;
		}
		System.out.println("Job completed successfully");
		return 0;
	}	
	
	/**
	 * Main method for the Hadoop version of the Register App
	 * 
	 * @param argsList	List of arguments, properties file and files to process
	 * @throws Exception
	 */
	public static void main(String[] argsList) throws Exception {
		
		Log log = LogFactory.getLog(TabNormHadoopMain.class);
		Logger logger = Logger.getLogger(TabNormHadoopMain.class);
		log.debug("LOGGER - RegisterHadoopMain - checking number of args, should be at least 2");
		logger.debug("LOGGER2 - RegisterHadoopMain - checking number of args, should be at least 2");
		System.out.println("RegisterHadoopMain - checking number of args, should be at least 2");
		// Expects one or more file or directory names
		if ( argsList.length < 2) {
			System.out.println("Usage: ");
			System.out.println("   Properties file - file containing values that will direct the processing of the input/output files");
			System.out.println("   Input file/directory name - the path of the raw register file, or directory containing the raw register files");
			System.exit(1);
		} 

		// Validate input arguments 
		// First argument is the properties file
		String propertiesFileName = argsList[0];
		System.out.println("  Properties File = " + propertiesFileName);
		File propFile = new File(propertiesFileName);
		if (!propFile.exists()) { 
			throw new IOException("Unable to open properties file " + propertiesFileName);	
		}	
		processor.setPropertiesFile(propertiesFileName);
	
		// Remaining arguments are list of input files and/or directories
		ArrayList<String> inputFiles = new ArrayList<String>();
		for (int i=1; i<argsList.length; i++) {
			inputFiles.add(argsList[i]);	
			System.out.println("\tInput file/dir " + (i-1) + " = " + argsList[i]);
		}
		
		if (inputFiles == null || inputFiles.isEmpty()) {
			throw new Exception("No files to process");
		}

		// Generate the list of files - this is required because the input argument(s) may be 
		// the name of one or more directories so we need a list of files in these directories
		System.out.println("Generating file list");
		FileListGenerator listGenerator = new FileListGenerator(inputFiles);
		ArrayList<String> inputFileList = listGenerator.getListOfFiles();
		System.out.println("Generated file list :-");
		for (String inputFileName: inputFileList) {
			System.out.println("\t" + inputFileName);
		}

		// Create the file containing the list of files to process
		File inputFileListFile = new File(Constants.HADOOP_ER_ROOT + Constants.HADOOP_ER_INPUTFILES_DIR + Constants.HADOOP_ER_INPUT_FILE_LIST);
		Writer fileListBuffer = new BufferedWriter(new FileWriter(inputFileListFile));
		for (String fileName: inputFileList) {
			fileListBuffer.write(fileName + "\n");
		}
		fileListBuffer.flush();
		fileListBuffer.close();
		
		// Set up the local Hadoop file system
		
		
		FileSystem localfs = FileSystem.getLocal(new Configuration());
		Path hadoopInputDir = new Path(Constants.HADOOP_LOCAL_INPUT);
		if (!localfs.exists(hadoopInputDir)) localfs.create(hadoopInputDir);
		Path hadoopOutputDir = new Path(Constants.HADOOP_LOCAL_OUTPUT);
		if (localfs.exists(hadoopOutputDir)) localfs.delete(hadoopOutputDir, true);
		Path inputFileListDir = new Path(inputFileListFile.getPath());
		localfs.copyFromLocalFile(false, true, inputFileListDir, hadoopInputDir);
		FileStatus[] hadoopInputFiles = localfs.listStatus(hadoopInputDir);
		for (int i=0; i<hadoopInputFiles.length; i++) {
			System.out.println("Hadoop input files " + hadoopInputFiles[i].getPath().getName());
		}		
				
		// Run the Hadoop map/reduce process
		Configuration conf = new Configuration();
		String[] hadoopArgsList = {Constants.HADOOP_LOCAL_INPUT, Constants.HADOOP_LOCAL_OUTPUT};
		int res = ToolRunner.run(conf, new TabNormHadoopMain(), hadoopArgsList);
		
		System.out.println("RegisterHadoopMain completed - result = " + res);
		
		// Postprocessing
		// Get the collated output from Hadoop, move the file and add the header
		FileStatus[] hadoopOutputFiles = localfs.listStatus(hadoopOutputDir);
		for (int i=0; i<hadoopOutputFiles.length; i++) {
			String hadoopOutputFileName = hadoopOutputFiles[i].getPath().getName();
			System.out.println("Hadoop output files " + hadoopOutputFileName);
			if (hadoopOutputFileName.startsWith(Constants.HADOOP_LOCAL_OUTPUT_FILE_PREFIX)) {
				Path outputFile = new Path(Constants.HADOOP_ER_ROOT + Constants.HADOOP_ER_PROCESSED_DIR + Constants.HADOOP_ER_PROCESSED_NAME);
				System.out.println("Copying " + hadoopOutputFiles[i].getPath() + " to " + outputFile);				
				localfs.copyToLocalFile(hadoopOutputFiles[i].getPath(), outputFile);
				break;
			}
		}		
		
		// TODO
		// Postprocessing
		// Add header line
		// Create the file containing the list of files to process
		
		/*
		String header = "";
		String headerDelimiter = processor.getNormalisedFormat().getNormalisedOutputDelimiter();
		ArrayList<String> columnHeaders = processor.getOutputDataItems();
		for (String columnHeader : columnHeaders) {
			header = header + columnHeader + headerDelimiter;
		}
		header = header.substring(0, header.lastIndexOf(headerDelimiter));
		header = header + processor.getNormalisedFormat().getNormalisedOutputNewLine();

		File registerFile = new File(Constants.HADOOP_ER_ROOT + Constants.HADOOP_ER_PROCESSED_DIR + Constants.HADOOP_ER_PROCESSED_NAME);
		registerFile.createNewFile();
        BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(registerFile.getName(),true));
        bufferWriter.write(header);
        bufferWriter.close();
		
		Writer registerBuffer = new BufferedWriter(new FileWriter(inputFileListFile));
		for (String fileName: inputFileList) {
			fileListBuffer.write(header);
		}
		
		
		
		
		fileListBuffer.close();
		*/
		
		System.exit(res);

	}

}
