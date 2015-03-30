package com.mwdb.phase2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
/**
 * 
 * @author Aneesh
 * The class contains the methods to compare two files and returns a similarity measure
 *
 */

public class Task1_c_d_e {
	
	private String f1,f2;
	
	
	 /**
	  * 
	  * @param file1 Input File 1
	  * @param file2 Input File 2
	  * @return
	  * 
	  * Accepts two files names as arguments. Calls the readFile(String f, ArrayList<String> bag) method 
	  * for File 1 to read the csv file and collect the words into the arraylist bag1
	  * Adds all elements from bag1 to the Hashset hs to remove the duplicates from bag1
	  * Clear bag1 and add all elements from hs to bag1. Now bag1 will contain only distinct words. Clear the hash set hs
	  * call method readFile using bag2 for File 2 and repeat the steps
	  * bag1 and bag 2 will have distinct elements from File 1 and File 2
	  * We need the distinct words from both the files, so we add all the elements from bag1 and bag2 into the hash set hs
	  * A binary vector for each corresponding to each element in the hash set is formed.
	  * If the element in the hash set exists in the file, the vector feature is given the value 1, else 0.
	  * The similarity score is calculated by ratio of number of features with the value 1 to the total number of features in the binary vector. 
	  * A double values similarity measure is returned by the method
	  * 
	  */

	public Double compareFiles(String file1, String file2) {
		
		f1 = file1;
		f2 = file2;
		
		ArrayList<String> bag = new ArrayList<>();
		ArrayList<String> bag1 = new ArrayList<>();
		ArrayList<String> bag2 = new ArrayList<>();
		ArrayList<String> bagFile1 = new ArrayList<>();
		ArrayList<String> bagFile2 = new ArrayList<>();
		HashSet<String> hs = new HashSet<>();
			  
		 int simScore = 0; 
		
		 readFile(f1,bag); // read file 1 and insert the words into the bag arraylist
		
		bagFile1.addAll(bag);
		hs.addAll(bag); // to remove duplicates by adding all words into a hash set
		bag.clear();
		bag.addAll(hs);
		 
		hs.clear();
		readFile(f2,bag1);
		
		bagFile2.addAll(bag1);
		hs.addAll(bag1);
		bag1.clear();
		bag1.addAll(hs);
		
		hs.clear();
		
		hs.addAll(bag);
		hs.addAll(bag1);
		bag2.addAll(hs);
		
		
		int[][] binary = new int[2][bag2.size()];
		int[] result = new int[bag2.size()];
		
			
		int fcount1 = 0;
		int fcount2 = 0;
		for( int i = 0 ; i < bag2.size() ; i++)
		{
						
			if (bagFile1.contains(bag2.get(i)))
			{
				binary[0][i] = 1;
				fcount1++;
			}
			
			if (bagFile2.contains(bag2.get(i)))
			{
					binary[1][i] = 1;
					fcount2++;
			}
			
		}
		
			
		for( int i = 0 ; i< bag2.size() ; i++ )
		{
			if(binary[0][i] == binary[1][i]){
				result[i] = 1;
				simScore++;
			}
			else{
				result[i] = 0;
			}

		}
		double res = (((double)simScore / (double)bag2.size()));
		return res;
		
	}
	
	/**
	 * 
	 * @param f The file name
	 * @param bag The array lost used to collect the words from file
	 * Accepts the file name as a string and an arraylist to collect the words. Scans through the file
	 * and extract the words Window vector) line by line and and add them into the arraylist bag.
	 * 
	 */

	private static void readFile(String f, ArrayList<String> bag) {
		String csvFile = f;
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		int  count = 0;
		
		try {
	 
			
				br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] words = line.split(csvSplitBy);
				String temp = null;
				for( int i = 3 ; i < words.length ; i++){
						words[i] = words[i].trim();

				if(temp == null)
					temp = words[i] + ",";
				else
					temp = temp + words[i] + ",";
				
				}
				temp = temp.substring(0, temp.length()-1);
				bag.add(temp);
				count++;
				
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	  }
}
