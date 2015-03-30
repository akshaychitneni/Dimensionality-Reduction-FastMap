package com.mwdb.phase2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class SimilarityWrapper {

	private SimilarityGenerator kobj;
	private Task1_c_d_e aobj;
	
	
	public SimilarityWrapper() throws Exception{
		kobj = new SimilarityGenerator(Task3.location_file_path);
		aobj = new Task1_c_d_e();
	}
	
	public double getSimilarityForFiles(int option, String f1, String f2) throws Exception{
		double similarity = 0;
		if(option == 1 ){
			//task 1a
			//System.out.println("task 1a");
			String query = "python /home/akshay/Desktop/phase2/python/eucled.py "+f1+" "+f2;
			//System.out.println(query);
			Process p = Runtime.getRuntime().exec(query);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = in.readLine();
		    p.waitFor();
		    p.destroy();
		   // System.out.println(s);
			similarity = Double.parseDouble(s);
		}
		
		if(option == 2){
			//task 1b
			//System.out.println("task 1a");
			String query = "python /home/akshay/Desktop/phase2/python/dtwdynamic.py "+f1+" "+f2;
			//System.out.println(query);
			Process p = Runtime.getRuntime().exec(query);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = in.readLine();
		    p.waitFor();
		    p.destroy();
			similarity = Double.parseDouble(s);
		}

		else if(option == 3 || option == 4 || option == 5){
			similarity = aobj.compareFiles(f1, f2);
		}
		
		else if(option == 6 || option == 7 || option == 8){
			
			similarity = kobj.getFileSimilarity(f1,f2); 
		}
		return similarity;		
	}
	
	public static void main(String args[]) throws Exception{
		SimilarityWrapper sw = new SimilarityWrapper();
		int option = 0;
		System.out.print("Select a similarity measure : "
				+"1 - Task 1a\t"
				+"2 - Task 1b\t"
				+"3 - Task 1c\t"
				+"4 - Task 1d\t"
				+"5 - Task 1e\t"
				+"6 - Task 1f\t"
				+"7 - Task 1g\t"
				+"8 - Task 1h\t");
		Scanner p = new Scanner(System.in);
		option = Integer.parseInt(p.nextLine());
		String f1,f2 = "";
		System.out.print("Enter File-1 : ");
		p = new Scanner(System.in);
		f1 = p.nextLine();
		System.out.print("Enter File-2 : ");
		p = new Scanner(System.in);
		f2 = p.nextLine();
		System.out.println(sw.getSimilarityForFiles(option,f1,f2 ));
	}
}
