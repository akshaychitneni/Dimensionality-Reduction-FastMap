package com.mwdb.phase2;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.sql.rowset.spi.SyncFactoryException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

public class Task3 
{
	
	public static String matlab_path = "cd(\'/home/akshay')";
	public static String location_file_path = "/home/akshay/Desktop/phase2/LocationMatrix.csv";
	
	public static File[] file_list;
	private static MatlabProxyFactory factory = null;
	private static MatlabProxy proxy = null;


	public static void main(String args[]) throws Exception
	{
		//Initialize matlab factory class
		
		System.out.println("Launching Matlab and Initializing workspace");
		/*factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
		*/
		
		// Code block for Interactive console input and invoke corresponding class objects
		
		String input = "";
		String path = "";
		while(!input.equals("exit"))
		{
			input = "";
			System.out.print("Enter the task number : ");
			Scanner in = new Scanner(System.in);
			input = in.nextLine();
				//Task 3a
				if(input.equals("3a"))
				{
					path = "";
					int r = 0;
					System.out.print("Enter the input files folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					System.out.print("Provide number of latent semantics : ");
					p = new Scanner(System.in);
					r = Integer.parseInt(p.nextLine());
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3a.file_list = file_list;
					Task3a.factory = factory;
					Task3a.proxy = proxy;
					Task3a obj1 = new Task3a();
					obj1.constructFeatureSpace();
					obj1.constructFeatureVectorsSVD();
					obj1.calculateSVD(r);
				}
				// Task 3b
				if(input.equals("3b"))
				{
					path = "";
					int k=0;
					System.out.print("Enter the input files folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					System.out.print("Provide number of Topics : ");
					Scanner q = new Scanner(System.in);
					k = Integer.parseInt(q.nextLine());
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3b.factory = factory;
					Task3b.proxy = proxy;
					Task3b.file_list = file_list;
					Task3b obj2 = new Task3b();
					obj2.constructFeatureSpace();
					obj2.constructLDAInput();
					obj2.calculateLDA(k);
				}
//				//Task 3c
				if(input.equals("3c")){
					path = "";
					int r,option = 0;
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
					System.out.print("Enter the input files folder path : ");
					p = new Scanner(System.in);
					path = p.nextLine();
					System.out.print("Provide number of Latent Semantics : ");
					p = new Scanner(System.in);
					r = Integer.parseInt(p.nextLine());
					
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3c.file_list = file_list;
					Task3c.factory = factory;
					Task3c.proxy = proxy;
					Task3c obj = new Task3c();
					Task3f.inputFileList = file_list;
					obj.constructSimilaritySimilarityMatrix(option);
					obj.doFileFileSVD(r);
				}
				//Task 3d
				if(input.equals("3d")){
					path = "";
					int r,k = 0;
					System.out.print("Enter the input query file folder path : ");
					Scanner p = new Scanner(System.in);
					path = p.nextLine();
					System.out.print("Enter number of latent semantics : ");
					p = new Scanner(System.in);
					r = Integer.parseInt(p.nextLine());
					System.out.print("Enter number of top documents to be retrieved : ");
					p = new Scanner(System.in);
					k = Integer.parseInt(p.nextLine());
					Task3d.factory = factory;
					Task3d.proxy = proxy;
					File folder = new File(path);
					file_list = folder.listFiles();
					Task3d.featureIndexMap = Task3a.featureIndexMap;
					Task3d.file_list = file_list;
					Task3d.fileIndexMap = Task3a.fileIndexMap;
					Task3d obj = new Task3d();
//					obj.constructFeatureSpace();
					obj.constructFeatureVectorsQuerySVD();
					obj.doSVDSearch(r,k);
				}
				//Task 3e
				if(input.equals("3e")){
				int k=0;
				path = "";
				System.out.print("Enter the input query file folder path : ");
				Scanner p = new Scanner(System.in);
				path = p.nextLine();
				System.out.print("Enter number of top documents to be retrieved : ");
				p = new Scanner(System.in);
				k = Integer.parseInt(p.nextLine());
				Task3e.factory = factory;
				Task3e.proxy = proxy;
				File folder = new File(path);
				file_list = folder.listFiles();
				Task3e.featureIndexMap = Task3b.featureIndexMap;
				Task3e.file_list = file_list;
				Task3e.fileIndexMap = Task3b.fileIndexMap;
				Task3e obj = new Task3e();
//				obj.constructFeatureSpace();
				obj.constructLDAInputQuery();
				obj.doLDASearch(k);
			}
			// Task 3f	
			if(input.equals("3f")){
				path = "";
				int r,k,option=0;
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
				System.out.print("Enter the input query file folder path : ");
				p = new Scanner(System.in);
				path = p.nextLine();
				System.out.print("Enter number of latent semantics : ");
				p = new Scanner(System.in);
				r = Integer.parseInt(p.nextLine());
				System.out.print("Enter number of top documents to be retrieved : ");
				p = new Scanner(System.in);
				k = Integer.parseInt(p.nextLine());
				Task3f.factory = factory;
				Task3f.proxy = proxy;
				File folder = new File(path);
				file_list = folder.listFiles();
				if(Task3c.file_list.length > 0)
					Task3f.file_list = Task3c.file_list;
				else
					System.out.println("Error in setting file list");
				Task3f.inputFileList = file_list;
				Task3f.fileIndexMap = Task3c.fileIndexMap;
				Task3f obj = new Task3f();
				obj.constructFileFileSimilarityQuery(option);
				obj.doFileFileSVDSearch(k,r);
			}
			}
		proxy.disconnect();
	}
	
}
