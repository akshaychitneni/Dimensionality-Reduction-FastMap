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
import matlabcontrol.extensions.MatlabTypeConverter;

public class Task3d {
	
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
	
	
	
	// Function to construct feature vector for the query
	
	public void constructFeatureVectorsQuerySVD() throws IOException{
		PrintWriter swriter = new PrintWriter("/home/akshay/svdqueryinput.csv");
//		File folder = new File(inputQueryFolder);
//		File[] file_list = folder.listFiles();
		for(File file:Task3.file_list)
		{
			HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			// Loop over each row and find number of occurrances in the file
			for(int i=0;i<rows.size();i++)
			{
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				// Additional feature - <time,word>
				StringBuilder timeword = new StringBuilder();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				if(featureIndexMap.containsKey(vword)){
					int localindex = featureIndexMap.get(vword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
//				if(featureIndexMap.containsKey(tword)){
//					int localindex = featureIndexMap.get(tword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
			}
			StringBuilder fvectorwriter = new StringBuilder();
			int featurespaceSize = featureIndexMap.size();
			for(int k=1;k<=featurespaceSize;k++){
				if(fvectorMap.containsKey(k)){
					fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append(",");
				}
				else{
					fvectorwriter.append("0");
					fvectorwriter.append(",");
				}
			}
			String output = fvectorwriter.toString();
			output = output.substring(0,output.length()-1);
			swriter.write(output);
			swriter.write("\n");
		}
		swriter.close();
	}
	
	
	// Function to create feature space for the query
	
	public void constructFeatureSpace() throws IOException
	{	
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
		int index=1;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			for(int i=0;i<rows.size();i++){
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				StringBuilder timeword = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				if(!featureIndexMap.containsKey(vword)){
					featureIndexMap.put(vword, index);
					index++;
				}
				if(!featureIndexMap.containsKey(tword)){
					featureIndexMap.put(tword, index);
					index++;
				}
			}
		}
		//display the contents of map
		Iterator<Entry<String, Integer>> itr = featureIndexMap.entrySet().iterator();
	    while (itr.hasNext()) {
	        Map.Entry pairs = (Map.Entry)itr.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	    }
	    System.out.println("Size of Feature space "+featureIndexMap.size());
	}
	
	
	
	// Function to invoke matlab function to perform SVD search 
	
	public static void doSVDSearch(Integer r, Integer k) throws MatlabInvocationException{
		//MatlabTypeConverter obj = new MatlabTypeConverter(proxy);
		proxy.eval(Task3.matlab_path);
		proxy.setVariable("r", r);
		proxy.setVariable("k", k);
		proxy.eval("res = SVDSearch(r,k)");
		double[] results= (double[]) proxy.getVariable("res");
		System.out.println("task 3a fileindex size"+fileIndexMap.size());
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
