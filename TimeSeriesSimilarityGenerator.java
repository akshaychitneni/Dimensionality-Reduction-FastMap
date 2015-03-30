package com.mwdb.phase2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
/**
 * Similarity generator to find out similarity between two given files
 * @author karthikchandrasekar
 *
 */
class SimilarityGenerator
{
    HashMap<String, List<String>> adjacencyHashMap;
    List<String> statesList = new ArrayList<String>();
    
    public SimilarityGenerator(String inputLocationFile) throws Exception
    {
        adjacencyHashMap = formAdjacencyHashMap(inputLocationFile);
    }

    
    public void collectWords(String fileName, List<List<String>> wordList) throws FileNotFoundException
    {
        //Collect the words from the given input files and form word list 
        
        List<String> tempList;

        //Data Format - 1.csv,US-AK,2012-01-01 12:00:00,0.28816285436749933,0.28816285436749933,0.28816285436749933,0.28816285436749933,0.28816285436749933
        File inputFile = new File(fileName);
        Scanner input = new Scanner(inputFile);
        
        while(input.hasNextLine())
        {
            tempList = Arrays.asList(input.nextLine().split(","));
            if (!wordList.contains(tempList))
            {
                wordList.add(tempList);
            }
        }
    }
    
    
    public List<Double> getWindowMatch(List<String> rowWord, List<String> colWord)
    {
        /** Find the common matching matching characters with same index in given window of the words
         *  Return the similarity value based on the number of matches
         *  Return 0 when the match is less than half the length of the max of two words
         */
        double matchCount = 0; 
        double totalLen = 0;
        double similarity = 0;
        int windowMatchBoost = 5;
        int exactMatchBoost = 10;
        double exactMatchFlag = 0.0;
        double matchThreshold = 0;
        int iterationCount = 0;
        List<Double> resultList = new ArrayList<Double>();
        
        //System.err.println("roweord " +rowWord);
        //System.err.println("colmnwrd "+ colWord);
        iterationCount = Math.min(rowWord.size(), colWord.size());
        for(int i=0; i < iterationCount ;i++)
        {
            if(i < 3)
            {
                continue;
            }
            if(rowWord.get(i).equals(colWord.get(i)))
            {
                matchCount ++;
            }
        }
        
        totalLen = Math.max(rowWord.size(), colWord.size())-3;
        
        matchThreshold = totalLen*0.8;
        //System.out.println("matchTres "+matchThreshold);
        //System.out.println("matchcount "+matchCount);
      
        if(matchCount == totalLen)
        {
        	
            similarity = ((double)1/(double)totalLen*2) * matchCount;
            exactMatchFlag = 1.0;
        }
        else if (matchCount > matchThreshold)
        {
        	//System.out.println("Partial match");
            similarity = ((double)1/(double)totalLen*2) * matchCount;
        	
        }
        else
        {
            similarity = 0;
        }
        //System.out.println("wondowmatch "+similarity);
        
        resultList.add(exactMatchFlag);
        resultList.add(similarity);
        return resultList;      
    }
    
    
    public HashMap<String, List<String>> formAdjacencyHashMap(String inputFilePath) throws Exception
    {
        /**Parse adjacency matrix and form a state, adjacent states hash map
         * Also form a list of state to identify state index position
         */

        HashMap<String, List<String>> adjacencyHashMap = new HashMap<String, List<String>>();
        List<String> headerList;
        List<String> valueList;
        int count;
        String header="";
        Scanner scannerObj = new Scanner(new File(inputFilePath));

        headerList = Arrays.asList(scannerObj.nextLine().split(","));
        while(scannerObj.hasNextLine())
        {
            count = 0;
            valueList = new ArrayList<String>();
            for(String value : scannerObj.nextLine().split(","))
            {
                if(count == 0)
                {
                    header = value;
                    count++;
                    continue;
                }
                else if (Integer.parseInt(value)==1)
                {
                    valueList.add(headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put(header, valueList);
            statesList.add(header);
        }    
        return adjacencyHashMap;
    }
    
    public boolean isNeighbor(String stateOne, String stateTwo)
    {
        // Check if the given two states are neighbors
        
        if (stateOne==null || stateTwo==null)return false;
        
        return adjacencyHashMap.get(stateOne).contains(stateTwo);
    }
    
    public double getStateMatch(List<String> listOne, List<String> listTwo)
    {
        /*** Check if the state is equal between two given lists. State is located in index position 1 of the given lists. 
             If the state is same give similarity as 0.3
             If the states are neighbors assign similarity as 0.15
        ***/
        
        double similarity = 0.0;
        String stateOne = statesList.get(Integer.parseInt(listOne.get(1))-1);
        String stateTwo = statesList.get(Integer.parseInt(listTwo.get(1))-1);
        
        
        if(stateOne.equals(stateTwo))
        {
            similarity = 1;
        }
        else if(isNeighbor(stateOne, stateTwo))
        {
            similarity = 0.5;
        }
        return similarity;
    }
        
    
    double getTimeMatch(List<String> listOne, List<String> listTwo)
    {
        /**Check time match between two words and add the boost if they are same. 
         * Time is present in the index position 2 of the string list
         */
        
        double similarity = 0.0;
        
        if(listOne.get(2).equals(listTwo.get(2)))
            similarity = 0.2;
        return similarity;      
    }
    
    int exactMatchCount=0;
    int partialMatchCount=0;
    int misMatchCount=0;
    public double getWordSimilarity(List<String> rowWord, List<String> colWord)
    {   
        //Generate a word-word similarity for the given two words 
        
        double similarity = 0.0;
        double windowMatch = 0.0;
        double stateMatch = 0.0;
        double timeMatch = 0.0;
        double exactWordBoost = 10000;
        List<Double> windowResults = new ArrayList<Double>();
        double exactMatchFlag;
        
        windowResults = getWindowMatch(rowWord, colWord);
        exactMatchFlag = windowResults.get(0);
        similarity += windowResults.get(1);
        if(similarity>0){
        stateMatch = getStateMatch(rowWord, colWord);
        similarity += stateMatch;
        timeMatch = getTimeMatch(rowWord, colWord);
       // System.out.println("Timematch" + timeMatch + "  StateMatch" + stateMatch + "  ExactMatchFlag" +  exactMatchFlag);
        if (timeMatch > 0 && exactMatchFlag > 0 && stateMatch > 0.5)
        {
        	//System.out.println("Inside exact match boost");
        	similarity = similarity * exactWordBoost ;
        	exactMatchCount++;
        }
        else if (similarity > 0)
        {
        	similarity = similarity * 0.001;
        	partialMatchCount++;
        }
        else 
        {
        	misMatchCount++;
        }
        similarity += timeMatch;
        }        
        return similarity;
    }
    

    public double constructAMatrix(List<List<String>> fileOneWordList, List<List<String>> fileTwoWordList)
    {
        int rowSize = fileOneWordList.size();
        int colSize =  fileTwoWordList.size();
        List<String> rowWord, colWord;
        double fileSimilarity = 0 ;
        
        for(int i=0; i<rowSize; i++)
        {
            rowWord = fileOneWordList.get(i);
            for(int j=0; j<colSize; j++)
            {
                colWord = fileTwoWordList.get(j);
                fileSimilarity += getWordSimilarity(rowWord, colWord);
            }
        }
        
        return fileSimilarity;
    }
    
    double matrixMultiply(List<Integer> binaryVectorOne, double[][] AMatrix, List<Integer> binaryVectorTwo)
    {
        double fileSimilarity = 0;
        double [][] tempMatrix = new double[1][binaryVectorTwo.size()];
        double temp=0;
                
        //Multiply binaryVectorOne and AMatrix
        for(int i=0;i<binaryVectorTwo.size();i++)
        {
            temp = 0;
            for(int j=0;j<binaryVectorOne.size();j++)
            {
                temp += binaryVectorOne.get(j) * AMatrix[j][i];
            }
            tempMatrix[0][i] = temp;
        }
        
        //Multiply tempMatrix and binaryVectorTwo
        for(int i=0; i<binaryVectorTwo.size();i++)
        {
            temp = 0;
            temp += tempMatrix[0][i] * binaryVectorTwo.get(i);
        }
        fileSimilarity = temp;
        
        return fileSimilarity; 
    }
    
    public double getFileSimilarity(String fileNameOne, String fileNameTwo) throws Exception
    {
        //Generate the similarity for the given two files
        
        List<List<String>> fileOneWordList = new ArrayList<List<String>>();
        List<List<String>> fileTwoWordList = new ArrayList<List<String>>();
        double fileSimilarity;
        
        collectWords(fileNameOne, fileOneWordList);
        collectWords(fileNameTwo, fileTwoWordList); 
        fileSimilarity = constructAMatrix(fileOneWordList, fileTwoWordList);    
        //System.out.println("Exact match count " + exactMatchCount + " Partial match count " + partialMatchCount + " MistmatchCount " + misMatchCount);
        //System.out.println("File similarity before normalization " + fileSimilarity);
        fileSimilarity = fileSimilarity / ((fileOneWordList.size() * fileTwoWordList.size()));
        //System.out.println("File similarity after normalization" + fileSimilarity);
        //System.out.println("First file word size " + fileOneWordList.size() + "Second File word size" + fileTwoWordList.size());
        return fileSimilarity;
    }


    public static void main(String args[]) throws Exception
    {        
         String locationFile = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
         SimilarityGenerator simObj = new SimilarityGenerator(locationFile);
         String fileNameOne = "/Users/karthikchandrasekar/Downloads/epidemic_word_files/avgn2.csv";
         String fileNameTwo = "/Users/karthikchandrasekar/Downloads/epidemic_word_files/avgn1.csv";
         simObj.getFileSimilarity(fileNameOne, fileNameTwo);
    }
}

