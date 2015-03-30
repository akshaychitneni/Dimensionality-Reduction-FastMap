package com.mwdb.phase2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task3f {
	
	public static HashMap<String, Integer> featureIndexMap = new HashMap<String,Integer>();
	public static HashMap<Double,String> fileIndexMap = new HashMap<Double, String>();
	private static final Charset charset = Charset.forName("ISO-8859-1");
//	private static String pathtofolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\epidemic_word_files";
	private static String pathtofolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\epidemic_word_files";
//	private static String inputQueryFolder = "E:\\MWDB\\sampledata_P1_F14\\sampledata_P1_F14\\Epidemic Simulation Datasets_2\\exec13\\input";
	private static String inputQueryFolder = "E:\\MWDB\\Anil_Kuncham_MWDB_Phase1\\output\\Epidemic Simulation Datasets_50\\input";
	
	private static String matlab_path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
	
	public static File[] file_list;
	public static MatlabProxyFactory factory = null;
	public static MatlabProxy proxy = null;
	public static File[] inputFileList = null;
	
	
	// Function to construct input query for File File similarity
	public void constructFileFileSimilarityQuery(int option) throws Exception{
		SimilarityWrapper sobj = new SimilarityWrapper();
//		SimilarityGenerator kobj = new SimilarityGenerator(Task3.location_file_path);
		PrintWriter swriter = new PrintWriter("/home/akshay/filefileSimilarityQueryInput.csv");
		double[][] filefilesimilarity = new double[file_list.length][file_list.length];
		StringBuilder input = new StringBuilder();
		for(int i=0;i<inputFileList.length;i++){
			for(int j=0;j<file_list.length;j++){
				filefilesimilarity[i][j] = sobj.getSimilarityForFiles(option,file_list[i].getAbsolutePath(),file_list[j].getAbsolutePath());
				input.append(filefilesimilarity[i][j]);
				input.append(",");
			}
			input.append("\n");
		}
		String output = input.toString().trim();
		swriter.write(output);
		swriter.close();
	}
	
	
	// Function to perform SVD search
	public void doFileFileSVDSearch(int k, int r) throws MatlabInvocationException
	{
		 proxy.eval(Task3.matlab_path);
			proxy.setVariable("r", r);
			proxy.setVariable("k", k);
		 proxy.eval("res = SVDSearch_file_file(r,k)");
		 double[] results= (double[]) proxy.getVariable("res");
		 for(int i=0;i<results.length/2;i++)
		 {
			 System.out.println(fileIndexMap.get(results[i+results.length/2])+"->"+results[i]);
		 }
	}
	
//	public static void main(String args[]) throws MatlabConnectionException, IOException, MatlabInvocationException
//	{
//		factory = new MatlabProxyFactory();
//		proxy = factory.getProxy();
//		File folder = new File(pathtofolder);
//		file_list = folder.listFiles();
//		for(int i=0;i<file_list.length;i++){
//			fileIndexMap.put(i+1, file_list[i].getName());
//		}
//		Iterator<Entry<Integer, String>> itr1 = fileIndexMap.entrySet().iterator();
//	    while (itr1.hasNext()) {
//	        Map.Entry pairs = (Map.Entry)itr1.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
//	    }
//		Task3d obj = new Task3d();
//		obj.constructFeatureVectorsQuerySVD();
//		doSVDSearch();
//	}
}
