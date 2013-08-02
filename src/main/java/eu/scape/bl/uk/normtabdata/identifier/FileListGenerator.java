package eu.scape.bl.uk.normtabdata.identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class FileListGenerator {

	private ArrayList<String> inputList = new ArrayList<String>() ;
	private int counter = 0;

    public FileListGenerator(ArrayList<String> inputList) {     
    	this.inputList = inputList;   	
    }
    
    public ArrayList<String> getListOfFiles() {
    	
    	ArrayList<String> inputFileList = getListOfInputFiles(); 
		System.out.println("  Found - " +  inputFileList.size() +  " files ");
    	return inputFileList;
    	
    }
    

    private ArrayList<String> getListOfInputFiles() { 
    	
    	ArrayList<String> inputFileNameList = new ArrayList<String>();
		Iterator<String> fileIterator = inputList.iterator();
		while( fileIterator.hasNext() ) {
			File inputFile = new File (fileIterator.next());
			if (inputFile.isFile()) {
				counter++;
				System.out.println("  File " + inputFile.getPath() + " added to list [" + counter + "]");
				inputFileNameList.add(inputFile.getPath());
			} else if (inputFile.isDirectory()) {
				System.out.println("  File " + inputFile.getPath() + " is a directory, getting file list");
				inputFileNameList.addAll(getFilesInFolder(inputFile));
			} else {
				System.out.println("  Value " + inputFile.getPath() + " is not a file or directory and will be ignored");
			}	
		}
        return inputFileNameList;
    }
    
    private ArrayList<String> getFilesInFolder(File folder) {
    	
		File[] filesInFolder = folder.listFiles();
		ArrayList<String> fileNameList = new ArrayList<String>();
		
		for (int i = 0; i < filesInFolder.length; i++) {
			if (filesInFolder[i].isFile()) {
				counter++;
				System.out.println("  File " + filesInFolder[i].getPath() + " added to list  [" + counter + "]");
				fileNameList.add(filesInFolder[i].getPath());
			} else if (filesInFolder[i].isDirectory()) {
				System.out.println("  File " + filesInFolder[i].getPath() + " is a directory, getting files");
				fileNameList.addAll(getFilesInFolder(filesInFolder[i]));
			} else {
				System.out.println("  Value " + filesInFolder[i].getPath() + " is not a file or directory and will be ignored");
			}
		}   	
    	return fileNameList;
    }

	
}
