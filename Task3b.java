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
import java.util.Map.Entry;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class Task3b 
{
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
	
	
	// Function to construct input for LDA matlab toolkit
	public void constructLDAInput() throws IOException
	{
		System.out.println("Constructing LDA input..");
		PrintWriter swriter = new PrintWriter("/home/akshay/ldainput.csv");
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
//		for(File file:file_list){
		for(int l=0;l<file_list.length;l++){
//			List<String> rows = Files.readAllLines(file.toPath(), charset);
			List<String> rows = Files.readAllLines(file_list[l].toPath(), charset);
			HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
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
					fvectorwriter.append((l+1)+" ");
					fvectorwriter.append((k)+" ");
					fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append("\n");
				}
			}
			String output = fvectorwriter.toString();
			swriter.write(output);
//			swriter.write("\n");
		}
		swriter.close();
		System.out.println("Done");
	}
	
	
	
	// Function to create feature space and initiate featureIndexMap
	public void constructFeatureSpace() throws IOException
	{	
		System.out.println("Constructing feature space..");
		for(int s=0;s<file_list.length;s++){
			fileIndexMap.put((double) (s+1), file_list[s].getName());
		}
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
	    System.out.println("Done");
	}
	
	
	// Function to invoke Matlab function to calculate LDA
	public void calculateLDA(Integer k) throws MatlabConnectionException, MatlabInvocationException, FileNotFoundException
	{
		 System.out.println("Executing LDA");
		 PrintWriter swriter = new PrintWriter("/home/akshay/ldaoutput.csv");	
		 //set matlab path
//		 String path = "cd(\'C:\\Users\\ANIL\\Documents\\MATLAB\\\')";
		 proxy.eval(Task3.matlab_path);
		 proxy.setVariable("k", k);
		 MatlabTypeConverter obj = new MatlabTypeConverter(proxy);
		 proxy.eval("final_result = calcldanew(k)");
		 double[][] temp = obj.getNumericArray("final_result").getRealArray2D();
		 for(int l=0;l<k;l++)
		 {
			System.out.println("Topic - "+l); 
			for(int i=0;i<temp[l].length/2;i++)
			{
				System.out.println(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
				swriter.write(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
			}
		 }
	}
	
//	public static void Main() throws MatlabConnectionException, IOException{
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
//		Task3b obj = new Task3b();
//		obj.constructLDAInput();
//		
////		obj.constructLDAInputQuery();
//	}
	
}
