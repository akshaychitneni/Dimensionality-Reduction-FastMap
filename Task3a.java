package com.mwdb.phase2;

import java.awt.BufferCapabilities.FlipContents;
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
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class Task3a {

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
	
	
	// Function to construct feature space
	public void constructFeatureSpace() throws IOException
	{	
		System.out.println("constructing feature space..");
		for(int s=0;s<file_list.length;s++){
			fileIndexMap.put((double) (s+1), file_list[s].getName());
		}
		System.out.println("FileIndexMap size"+fileIndexMap.size());
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
		int index=1;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			for(int i=0;i<rows.size();i++){
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				StringBuilder timeword = new StringBuilder();
				StringBuilder stateword = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				
				stateword.append(rvalues[1]+"_");
				stateword.append(vword);
				String sword = stateword.toString();
				
				if(!featureIndexMap.containsKey(vword)){
					featureIndexMap.put(vword, index);
					index++;
				}
//				if(!featureIndexMap.containsKey(tword)){
//					featureIndexMap.put(tword, index);
//					index++;
//				}
			}
		}
		//display the contents of map
		Iterator<Entry<String, Integer>> itr = featureIndexMap.entrySet().iterator();
	    while (itr.hasNext()) {
	        Map.Entry pairs = (Map.Entry)itr.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	    }
	    System.out.println("Size of Feature space "+featureIndexMap.size());
	    System.out.println("done..");
	}
	
	public int constructFeatureSpaceLSH() throws IOException
	{	
		System.out.println("constructing feature space..");
		for(int s=0;s<file_list.length;s++){
			fileIndexMap.put((double) (s+1), file_list[s].getName());
		}
		System.out.println("FileIndexMap size"+fileIndexMap.size());
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
		int index=1;
		for(File file:file_list){
			List<String> rows = Files.readAllLines(file.toPath(), charset);
			for(int i=0;i<rows.size();i++){
				String[] rvalues = rows.get(i).split(",");
				StringBuilder word = new StringBuilder();
				StringBuilder timeword = new StringBuilder();
				StringBuilder stateword = new StringBuilder();
				for(int j=3;j<rvalues.length;j++){
					word.append(rvalues[j]);
					word.append(",");
				}
				String vword = word.toString();
				/*timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();
				*/
				stateword.append(rvalues[1]+"_");
				stateword.append(vword);
				String sword = stateword.toString();
				
				/*if(!featureIndexMap.containsKey(vword)){
					featureIndexMap.put(vword, index);
					index++;
				}*/
				if(!featureIndexMap.containsKey(sword)){
					featureIndexMap.put(sword, index);
					index++;
				}
				
				/*if(!featureIndexMap.containsKey(vword)){
					featureIndexMap.put(vword, index);
					index++;
				}*/
//				if(!featureIndexMap.containsKey(tword)){
//					featureIndexMap.put(tword, index);
//					index++;
//				}
			}
		}
		//display the contents of map
		Iterator<Entry<String, Integer>> itr = featureIndexMap.entrySet().iterator();
	    while (itr.hasNext()) {
	        Map.Entry pairs = (Map.Entry)itr.next();
//	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	    }
	    System.out.println("Size of Feature space "+featureIndexMap.size());
	    return featureIndexMap.size();
	}
	
	//Function to construct feature vectors to be passed as input to SVD calc function
	public void constructFeatureVectorsSVD() throws IOException{ 
		System.out.println("Constructing Obj-Feature Matrix");
		PrintWriter swriter = new PrintWriter("/home/akshay/svdinput.csv");
		double[][] coordinates = new double[file_list.length][featureIndexMap.size()];
		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
		for(int n=0;n<file_list.length;n++){
			File file = file_list[n];
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
				/*StringBuilder timeword = new StringBuilder();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();*/
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
					
					//coordinates[n][k-1] = fvectorMap.get(k);
					fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append(",");
				}
				else{
					//coordinates[n][k-1] = 0;
					fvectorwriter.append("0");
					fvectorwriter.append(",");
				}
			}
			String output = fvectorwriter.toString();
			output = output.substring(0,output.length()-1);
			swriter.write(output);
			swriter.write("\n");
			fvectorMap.clear();
		}
		//swriter.close();
		System.out.println("Done");
	}
	
	
	public double[][] constructFeatureVectors() throws IOException{ 
		System.out.println("Constructing Obj-Feature Matrix");
		//PrintWriter swriter = new PrintWriter("/home/akshay/svdinput.csv");
		double[][] coordinates = new double[file_list.length][featureIndexMap.size()];
		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
		for(int n=0;n<file_list.length;n++){
			File file = file_list[n];
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
				/*StringBuilder timeword = new StringBuilder();
				timeword.append(rvalues[2]+"_");
				timeword.append(vword);
				String tword = timeword.toString();*/
				StringBuilder stateword = new StringBuilder();
				stateword.append(rvalues[1]+"_");
				stateword.append(vword);
				String sword = stateword.toString();
				
				
				if(featureIndexMap.containsKey(sword)){
					int localindex = featureIndexMap.get(sword);
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
			//StringBuilder fvectorwriter = new StringBuilder();
			int featurespaceSize = featureIndexMap.size();
			for(int k=1;k<=featurespaceSize;k++){
				if(fvectorMap.containsKey(k)){
					
					coordinates[n][k-1] = fvectorMap.get(k);
					/*fvectorwriter.append(fvectorMap.get(k));
					fvectorwriter.append(",");*/
				}
				else{
					coordinates[n][k-1] = 0;
					/*fvectorwriter.append("0");
					fvectorwriter.append(",");*/
				}
			}
			/*String output = fvectorwriter.toString();
			output = output.substring(0,output.length()-1);
			swriter.write(output);
			swriter.write("\n")*/;
			fvectorMap.clear();
		}
		//swriter.close();
		System.out.println("Done");
		return coordinates;
	}
	
	public double[] getQueryCoordinates(File queryFile) throws IOException{
		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
		List<String> rows = Files.readAllLines(queryFile.toPath(), charset);
		double[] coordinates = new double[featureIndexMap.size()];
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
			/*StringBuilder timeword = new StringBuilder();
			timeword.append(rvalues[2]+"_");
			timeword.append(vword);
			String tword = timeword.toString();*/
			
			StringBuilder stateword = new StringBuilder();
			stateword.append(rvalues[1]+"_");
			stateword.append(vword);
			String sword = stateword.toString();
			
			
			if(featureIndexMap.containsKey(sword)){
				int localindex = featureIndexMap.get(sword);
				if(!fvectorMap.containsKey(localindex)){
					fvectorMap.put(localindex, 1);
				}
				else{
					fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
				}
			}
//			if(featureIndexMap.containsKey(tword)){
//				int localindex = featureIndexMap.get(tword);
//				if(!fvectorMap.containsKey(localindex)){
//					fvectorMap.put(localindex, 1);
//				}
//				else{
//					fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//				}
//			}
		}
		//StringBuilder fvectorwriter = new StringBuilder();
		int featurespaceSize = featureIndexMap.size();
		for(int k=1;k<=featurespaceSize;k++){
			if(fvectorMap.containsKey(k)){
				
				coordinates[k-1] = fvectorMap.get(k);
				/*fvectorwriter.append(fvectorMap.get(k));
				fvectorwriter.append(",");*/
			}
			else{
				coordinates[k-1] = 0;
				/*fvectorwriter.append("0");
				fvectorwriter.append(",");*/
			}
		}
		/*String output = fvectorwriter.toString();
		output = output.substring(0,output.length()-1);
		swriter.write(output);
		swriter.write("\n")*/;
		fvectorMap.clear();
        return coordinates;
	}
	// Function to invoke SVD calculation function in Matlab
	public void calculateSVD(Integer r) throws MatlabConnectionException, MatlabInvocationException, FileNotFoundException
	{
		 PrintWriter swriter = new PrintWriter("/home/akshay/svdoutput.csv");
		 System.out.println("Executing SVD function in Matlab");	
		 //set matlab path
		 proxy.eval(Task3.matlab_path);
		 proxy.setVariable("r", r);
		 MatlabTypeConverter obj = new MatlabTypeConverter(proxy);
		 proxy.eval("final_result = svdcalc(r)");
		 double[][] temp = obj.getNumericArray("final_result").getRealArray2D();
		 for(int l=0;l<r;l++)
		 {
			System.out.println("Laten symantic - "+l);
			swriter.write("Laten symantic - "+l+"\n");
		 for(int i=0;i<temp[l].length/2;i++)
			{
				System.out.println(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]);
				swriter.write(fileIndexMap.get(temp[l][i+temp[l].length/2])+" --> "+temp[l][i]+"\n");
			}
		 }
		 swriter.close();
	}
	
//	public static void doSVDSearch() throws MatlabInvocationException, FileNotFoundException{
//		
//		String input = "";
//		do{
//		input = "";
//		System.out.print("Enter input file path and filename : ");
//		Scanner in = new Scanner(System.in);
//		input = in.nextLine();
//		inputQueryFolder = input;
//		proxy.eval(matlab_path);
//		proxy.eval("res = SVDSearch()");
//		double[] results= (double[]) proxy.getVariable("res");
//		for(int i=0;i<results.length/2;i++)
//		{
//			System.out.println(fileIndexMap.get((int)(results[i+results.length/2]))+"->"+results[i]);
//			
//		}
//		}while(input.equals("none"));
//	}
//	
//	
////	public static void doLDASearch() throws MatlabInvocationException, FileNotFoundException{
////		
////		String input = "";
////		do{
////		input = "";
////		System.out.print("Enter input file path and filename : ");
////		Scanner in = new Scanner(System.in);
////		input = in.nextLine();
////		inputQueryFolder = input;
////		 proxy.eval(matlab_path);
////		 proxy.eval("res = LDASearch()");
////		 double[] results= (double[]) proxy.getVariable("res");
////		 for(int i=0;i<results.length/2;i++)
////		 {
////			 System.out.println(fileIndexMap.get((int)(results[i+results.length/2]))+"->"+results[i]);
////			 
////		 }
////		}while(input.equals("none"));
////	}
////	
//	
//	public void constructFeatureVectorsQuerySVD() throws IOException{ 
//		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\svdqueryinput.csv");
//		File folder = new File(inputQueryFolder);
//		File[] file_list = folder.listFiles();
//		for(File file:file_list){
//			HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
//			List<String> rows = Files.readAllLines(file.toPath(), charset);
//			// Loop over each row and find number of occurrances in the file
//			for(int i=0;i<rows.size();i++)
//			{
//				String[] rvalues = rows.get(i).split(",");
//				StringBuilder word = new StringBuilder();
//				for(int j=3;j<rvalues.length;j++){
//					word.append(rvalues[j]);
//					word.append(",");
//				}
//				String vword = word.toString();
//				// Additional feature - <time,word>
//				StringBuilder timeword = new StringBuilder();
//				timeword.append(rvalues[2]+"_");
//				timeword.append(vword);
//				String tword = timeword.toString();
//				if(featureIndexMap.containsKey(vword)){
//					int localindex = featureIndexMap.get(vword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//				if(featureIndexMap.containsKey(tword)){
//					int localindex = featureIndexMap.get(tword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//			}
//			StringBuilder fvectorwriter = new StringBuilder();
//			int featurespaceSize = featureIndexMap.size();
//			for(int k=1;k<=featurespaceSize;k++){
//				if(fvectorMap.containsKey(k)){
//					fvectorwriter.append(fvectorMap.get(k));
//					fvectorwriter.append(",");
//				}
//				else{
//					fvectorwriter.append("0");
//					fvectorwriter.append(",");
//				}
//			}
//			fvectorwriter.toString();
//			swriter.write(fvectorwriter.toString());
//			swriter.write("\n");
//		}
//		swriter.close();
//	}
//	
//	
//	
//	public void constructLDAInput_not_inuse() throws IOException{
//		HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>(); 
//		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\ldainput.csv");
//		File folder = new File(pathtofolder);
//		File[] file_list = folder.listFiles();
//		int index=0;
//		for(File file:file_list){
//			List<String> rows = Files.readAllLines(file.toPath(), charset);
//			// Loop over each row and find number of occurrances in the file
//			for(int i=0;i<rows.size();i++)
//			{
//				String[] rvalues = rows.get(i).split(",");
//				StringBuilder word = new StringBuilder();
//				for(int j=3;j<rvalues.length;j++){
//					word.append(rvalues[j]);
//					word.append(",");
//				}
//				String vword = word.toString();
//				// Additional feature - <time,word>
//				StringBuilder timeword = new StringBuilder();
//				timeword.append(rvalues[2]+"_");
//				timeword.append(vword);
//				String tword = timeword.toString();
//				if(featureIndexMap.containsKey(vword)){
//					int localindex = featureIndexMap.get(vword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//				if(featureIndexMap.containsKey(tword)){
//					int localindex = featureIndexMap.get(tword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//			}
//			StringBuilder fvectorwriter = new StringBuilder();
//			int featurespaceSize = featureIndexMap.size();
//			for(int k=0;k<featurespaceSize;k++){
//				if(fvectorMap.containsKey(k)){
//					fvectorwriter.append((k+1)+":");
//					fvectorwriter.append(fvectorMap.get(k));
//					fvectorwriter.append(" ");
//				}
//			}
//			String output = fvectorwriter.toString().trim();
//			swriter.write(output);
//			swriter.write("\n");
//		}
//		swriter.close();
//	}
//	
//	
//	
//	private void constructLDAInput() throws IOException{
//		 
//		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\ldainput.csv");
////		File folder = new File(pathtofolder);
////		File[] file_list = folder.listFiles();
////		for(File file:file_list){
//		for(int l=0;l<file_list.length;l++){
////			List<String> rows = Files.readAllLines(file.toPath(), charset);
//			List<String> rows = Files.readAllLines(file_list[l].toPath(), charset);
//			HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
//			// Loop over each row and find number of occurrances in the file
//			for(int i=0;i<rows.size();i++)
//			{
//				String[] rvalues = rows.get(i).split(",");
//				StringBuilder word = new StringBuilder();
//				for(int j=3;j<rvalues.length;j++){
//					word.append(rvalues[j]);
//					word.append(",");
//				}
//				String vword = word.toString();
//				// Additional feature - <time,word>
//				StringBuilder timeword = new StringBuilder();
//				timeword.append(rvalues[2]+"_");
//				timeword.append(vword);
//				String tword = timeword.toString();
//				if(featureIndexMap.containsKey(vword)){
//					int localindex = featureIndexMap.get(vword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//				if(featureIndexMap.containsKey(tword)){
//					int localindex = featureIndexMap.get(tword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//			}
//			StringBuilder fvectorwriter = new StringBuilder();
//			int featurespaceSize = featureIndexMap.size();
//			for(int k=1;k<=featurespaceSize;k++){
//				if(fvectorMap.containsKey(k)){
//					fvectorwriter.append((l+1)+" ");
//					fvectorwriter.append((k)+" ");
//					fvectorwriter.append(fvectorMap.get(k));
//					fvectorwriter.append("\n");
//				}
//			}
//			String output = fvectorwriter.toString();
//			swriter.write(output);
////			swriter.write("\n");
//		}
//		swriter.close();
//	}
//	
//	public void constructLDAInputQuery() throws IOException{
//		PrintWriter swriter = new PrintWriter("C:\\Users\\ANIL\\Documents\\MATLAB\\ldaqueryinput.csv");
//		File folder = new File(inputQueryFolder);
//		File[] file_list = folder.listFiles();
//		for(File file:file_list){
//			HashMap<Integer, Integer> fvectorMap = new HashMap<Integer,Integer>();
//			List<String> rows = Files.readAllLines(file.toPath(), charset);
//			// Loop over each row and find number of occurrances in the file
//			for(int i=0;i<rows.size();i++)
//			{
//				String[] rvalues = rows.get(i).split(",");
//				StringBuilder word = new StringBuilder();
//				for(int j=3;j<rvalues.length;j++){
//					word.append(rvalues[j]);
//					word.append(",");
//				}
//				String vword = word.toString();
//				// Additional feature - <time,word>
//				StringBuilder timeword = new StringBuilder();
//				timeword.append(rvalues[2]+"_");
//				timeword.append(vword);
//				String tword = timeword.toString();
//				if(featureIndexMap.containsKey(vword)){
//					int localindex = featureIndexMap.get(vword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//				if(featureIndexMap.containsKey(tword)){
//					int localindex = featureIndexMap.get(tword);
//					if(!fvectorMap.containsKey(localindex)){
//						fvectorMap.put(localindex, 1);
//					}
//					else{
//						fvectorMap.put(localindex,fvectorMap.get(localindex)+1);
//					}
//				}
//			}
//			StringBuilder fvectorwriter = new StringBuilder();
//			int featurespaceSize = featureIndexMap.size();
//			for(int k=1;k<=featurespaceSize;k++){
//				if(fvectorMap.containsKey(k)){
//					fvectorwriter.append(fvectorMap.get(k));
//					fvectorwriter.append(",");
//				}
//				else{
//					fvectorwriter.append("0");
//					fvectorwriter.append(",");
//				}
//			}
//			String output = fvectorwriter.toString();
//			output = output.substring(0, output.length()-1);
//			System.out.println(output);
//			swriter.write(output);
//			swriter.write("\n");
//		}
//		swriter.close();
//	}

	
//	public static void main(String args[]) throws IOException, MatlabConnectionException, MatlabInvocationException{
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
//		Task3a obj = new Task3a();
//		obj.constructFeatureSpace();
//		obj.constructFeatureVectorsSVD();
//		obj.constructFeatureVectorsQuerySVD();
//		calculateSVD();
////		obj.constructLDAInput_not_inuse();
//		obj.constructLDAInput();
//		obj.constructLDAInputQuery();
////		obj.constructSimilaritySimilarityMatrix();
//		obj.constructSimilaritySimilarityMatrixQuery();
//		calculateLDA();
//	}
	
}
