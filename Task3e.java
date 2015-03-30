package com.mwdb.phase2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task3e {
	
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

	
	// Function to calculate input LDA 
	
	public void constructLDAInputQuery() throws IOException{
		PrintWriter swriter = new PrintWriter("/home/akshay/ldaqueryinput.csv");
		for(File file:file_list){
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
				if(featureIndexMap.containsKey(tword)){
					int localindex = featureIndexMap.get(tword);
					if(!fvectorMap.containsKey(localindex)){
						fvectorMap.put(localindex, 1);
					}
					else{
						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
					}
				}
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
			output = output.substring(0, output.length()-1);
			swriter.write(output);
			swriter.write("\n");
		}
		swriter.close();
	}
	
	
	// Function to invoke matlab function to do LDA search
	public void doLDASearch(Integer k) throws MatlabInvocationException
	{
		 proxy.eval(Task3.matlab_path);
		 proxy.setVariable("k", k);
		 proxy.eval("res = LDASearch(k)");
		 double[] results= (double[]) proxy.getVariable("res");
		 for(int i=0;i<results.length/2;i++)
		 {
			 System.out.println(fileIndexMap.get(results[i+results.length/2])+"->"+results[i]);
		 }
	}
}
