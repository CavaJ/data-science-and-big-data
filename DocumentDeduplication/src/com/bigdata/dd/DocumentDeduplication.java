package com.bigdata.dd;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

public class DocumentDeduplication
{
	//each of these characters take up 1 byte in memory. They are english characters
	private static final String CHARACTERS 
		= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	//number of documents
	private static final int NO_OF_DOCS = 100;
	
	//main method
	public static void main(String[] args) 
	{
		//Document array of 100 documents
		String[] D = new String[NO_OF_DOCS];
		
		//System.out.println(generateRandomString());
		/*for(int i = 0; i < 100; i ++)
		System.out.println((char) getRandomValue());*/
		
		D[0] = generateRandomString(); // D1 is the baseline for other documents
		
		//K array for replacing k_i random characters in D1 by random values between 0 ... 255
		int[] K = new int[NO_OF_DOCS];
		//lets make our baseline for k to be 5 character replacements
		K[0] = 1; // default
		//K[0] = 5
		
		//generate other elements starting from K[0]
		for (int index = 1; index < K.length; index ++)
		{
			K[index] = K[index - 1] + 1;
		} // for
		
		//L array to swap l_i random character pairs in D1
		int[] L = new int[NO_OF_DOCS];
		//baseline value for L is 5
		L[0] = 2;
		//L[0] = 5; default
		
		//generate other elements starting from L[0]
		for (int index = 1; index < L.length; index ++)
		{
			L[index] = L[index - 1] + 1;
		} // for
		
		//now generate other documents by replacing characters and by swapping 
		//character pairs on doc D1
		for (int index = 1; index < D.length; index ++)
		{
			String tempDoc = replaceRandomCharaters(D[0], K[index]);
			D[index] = swapCharacterPairs(tempDoc, L[index]);
		} // for
		
		//TODO calculate rabin fingerprints of all shingles in the document
		//and store them, then compare the fingerprints to this storage to detect
		//distinct fingerprints
		
		/*String s = "A função, Ãugent";
		try {
			byte[] bytes = s.getBytes("UTF-8");
			for(byte b : bytes)
			{
				System.out.println("c:" + (char) b + "-> "
	                    + Integer.toBinaryString(b));
				//System.out.println("c0xFF:" + (char) b + "-> " 
	                    //+ Integer.toBinaryString(b & 0xFF).replace(' ', '0'));
			} // for
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} // catch
		*/
		
		//aggregate all q-shingles in a tree set, duplicate elements will be removed
		TreeSet<String> qShingles = new TreeSet<String>();
		
		PrintWriter M_writer = null;
		PrintWriter M_s_writer = null;
		
		try {

			M_writer = new PrintWriter(new File(
					"C:\\Users\\Rufat Babayev\\Desktop\\Jaccard_M.txt"));
			M_s_writer = new PrintWriter(new File(
					"C:\\Users\\Rufat Babayev\\Desktop\\Jaccard_M_s.txt"));
			
			M_writer.println("--------- Jaccard Similarities based on matrix M -------------\n");
			System.out.println("--------- Jaccard Similarities based on matrix M -------------\n");

			// for q = 1 ... 10, different shingle sizes
			
			int[] Q = new int[] {1, 5, 10, 20};
			
			//for (int q = 1; q <= 10; q ++) default
			for(int q : Q)
			{

				// go through all the documents and generate shingles and store
				// them
				for (int index = 0; index < D.length; index++) {
					String[] shingles = shingle(D[index], q); // q-single the
																// doc
					for (String shingle : shingles) {
						qShingles.add(shingle);
					} // for
				} // for

				//convert shingles to string array
				String[] shingles = qShingles.toArray(new String[] {});

				// create matrix M and fill it
				int[][] matrix_M = new int[shingles.length][D.length];

				for (int row = 0; row < shingles.length; row++) {
					for (int col = 0; col < D.length; col++) {
						if (D[col].contains(shingles[row])) {
							matrix_M[row][col] = 1;
						} // if

					} // for
				} // for

				// printMatRep(matrix_M);

				for (int col = 1; col < D.length; col++) 
				{
					int intersectionAB = 0;
					int A = 0, B = 0;
					int unionAB = 0;

					for (int row = 0; row < shingles.length; row++) {
						// computer number of intersections
						if (matrix_M[row][0] == 1 && matrix_M[row][col] == 1) {
							intersectionAB++;
						} // if

						// computer number of shingles in A
						if (matrix_M[row][0] == 1) {
							A++;
						} // if

						// computer number of shingles in B
						if (matrix_M[row][col] == 1) {
							B++;
						} // if

						// now find the union which is A + B - intersectionAB
						unionAB = A + B - intersectionAB;
					} // for

					// now compute jaccard similarities, double division has to
					// be used
					double similarity = (double) intersectionAB
							/ (double) unionAB;

					System.out.println("q = " + q + ":sim(doc" + 1 + ", doc"
							+ (col + 1) + ") = " + similarity);
					M_writer.println("q = " + q + ":sim(doc" + 1 + ", doc"
							+ (col + 1) + ") = " + similarity);

				} // for (docs)
				
				System.out.println("\n");
				M_writer.println("\n");
				
				//------ Now deal with M_s matrix -------
				int kL = 100; // 100 minhash functions
				//starting values of a and b, a and be will be incremented by 1 in
				//each hash function creation
				int a = 1;
				int b = 0;
				
				//p has to be greater than number of rows in Matrix M
				//which is r. p has to be a prime number 
				int r = shingles.length;
				int p = getPrimeNumberBiggerThan(r);
				
				//get kL hashFunctions
				HashFunc[] hashFuncs = new HashFunc[kL];
				
				//generate 100 minHash functions
				for (int l = 0; l < kL; l ++)
				{
					//constructor accepts a and b
					hashFuncs[l] = new HashFunc(a, b);
					
					//increment a and b for the next hash function
					a ++;
					b ++;
				} // for
				
				//create Ms matrix of size kL x #docs
				double[][] matrix_M_s = new double[kL][D.length];
				//set all elements to infinity
				
				for(int row = 0; row < kL; row ++)
				{
					for(int col = 0; col < D.length; col ++)
					{
						matrix_M_s[row][col] = Double.POSITIVE_INFINITY;
					} // for
				} // for
				
				System.out.println("--------- Jaccard Similarities based on matrix M_s -------------\n");
				M_s_writer.println("--------- Jaccard Similarities based on matrix M_s -------------\n");
				
				//construct M_s from matrix M
				for (int row = 0; row < shingles.length; row ++)
				{
					for (int col = 0; col < D.length; col ++)
					{
						if(matrix_M[row][col] == 1)
						{
							for (int l = 0; l < kL; l ++)
							{
								//if h_l[i] < M_s[l][j], then M_s[l][j] = h_l[i]
								if(hashFuncs[l].hash(row + 1, p, r) < matrix_M_s[l][col])
								{
									matrix_M_s[l][col] = hashFuncs[l].hash(row + 1, p, r);
								} // if
								
							} // for (l)
							
						} // if
						
					} // for (col)
					
				} // for (row)
				
				//printMatRep(matrix_M_s);
				
				for (int col = 1; col < D.length; col++) 
				{
					int intersectionAB = 0;

					for (int row = 0; row < kL; row++) {
						// computer number of intersections
						if (matrix_M_s[row][0] == matrix_M_s[row][col]) 
						{
							intersectionAB++;
						} // if
					} // for

					// now compute jaccard similarities, double division has to
					// be used, intersections divided by number of rows
					double similarity = (double) intersectionAB
							/ (double) kL;

					System.out.println("q = " + q + ":sim_m_s(doc" + 1 + ", doc"
							+ (col + 1) + ") = " + similarity);
					M_s_writer.println("q = " + q + ":sim_m_s(doc" + 1 + ", doc"
							+ (col + 1) + ") = " + similarity);

				} // for (docs)
				
				System.out.println("\n");
				M_s_writer.println("\n");

			} // for (q)

		} // try
		catch (Exception e) 
		{
			e.printStackTrace();
		} // catch
		finally 
		{
			if (M_writer != null)
				M_writer.close();
			
			if(M_s_writer != null)
				M_s_writer.close();
		} // finally
		
		System.out.println("Please check out C:\\Users\\Rufat Babayev\\Desktop\\Jaccard_M.txt \n"
				+ "and C:\\Users\\Rufat Babayev\\Desktop\\Jaccard_M_s.txt for results!");
		System.out.println("If you get an error please change file paths in the program "
				+ "to match with your file system.");
		
	} // main
	
	//irreducable polynomial
	//(0)*X^31+(1)*X^16+(1)*X^15+(1)*X^9+(1)*X^7+(1)*X^6+(1)*X^5+(1)*X^4+(1)*X^3+(1)*X^2+(1)*X+(1)
	
	//In Java, Strings are made of characters, 
	//but the number of bytes required to represent 
	//a character can be 1 or 2 (or sometimes more) 
	//depending on the character and the encoding. 
	//eg characters encoded in UTF8 that are over ascii 127 
	//need 2 bytes, but those under 
	//- like english letters and numbers, take only 1. 
	
	//generate random string of 1000 bytes
	private static String generateRandomString(int length)
	{
		Random random  = new Random();
		
		StringBuilder sb = new StringBuilder(length);
	    for (int i = 0; i < length; i++) {
	        sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
	    }
	    return sb.toString();
	} // generateRandomString
	
	//by default it will generate string of 1000 bytes
	private static String generateRandomString()
	{
		return generateRandomString(1000);
	} // generateRandomString
	
	//method to getting random number between 0 and 255
	private static int getRandomValue()
	{
		Random rand = new Random();

		return rand.nextInt(256);
	} // getRandomValue
	
	//helper method to replace k random characters with random values between 0 ... 255
	private static String replaceRandomCharaters(String s, int k)
	{
	   char[] characters = s.toCharArray();
	   
	   //ArrayList to hold generated indices for the replacement in doc D
	   //we will make sure that, the same index won't be replaced twice
	   ArrayList<Integer> replacementIndices = new ArrayList<Integer>();
	   
	   for (int replacementCount = 0; replacementCount < k; replacementCount ++)
	   {
		   int randIndex = (int)(Math.random() * s.length());
		   
		   //if it the same index generated randomly, check and generate a new index
		   while(replacementIndices.contains(randIndex))
		   {
			   randIndex = (int)(Math.random() * s.length());
		   } // while
		   
		   replacementIndices.add(randIndex);
		   
		   //replace the random index with random value between 0 ... 255
		   characters[randIndex] = (char) getRandomValue();
	   } // for
	   
	   return new String(characters);
	} // replaceRandomCharacters
	
	//helper method to swap l random character pairs in a given string
	private static String swapCharacterPairs(String s, int l)
	{
		char[] c = s.toCharArray();
		
		//ArrayList to hold generated indices for the swap in doc D
		//we will make sure that, the same index pairs won't be swapped twice
		ArrayList<Integer> swapIndices = new ArrayList<Integer>();
		
		// l character pairs will be swapped
		for (int swaps = 0; swaps < l; swaps++) 
		{

			// generate two random indices for swap
			int randIndex1 = (int) (Math.random() * s.length());
			int randIndex2 = (int) (Math.random() * s.length());

			// if it the same index generated randomly, check and generate a new
			// index
			while (swapIndices.contains(randIndex1)) {
				randIndex1 = (int) (Math.random() * s.length());
			} // while

			// do it also for randIndex2
			while (swapIndices.contains(randIndex2)) 
			{
				randIndex2 = (int) (Math.random() * s.length());
			} // while

			swapIndices.add(randIndex1);
			swapIndices.add(randIndex2);

			// Replace with a "swap" function, if desired:
			/*
			 * char temp = c[0]; c[0] = c[1]; c[1] = temp;
			 */
			swap(c, randIndex1, randIndex2);
		} // for

		String swappedString = new String(c);
		
		return swappedString;
	} // swapCharacterPairs
	
	//for swapping characters from firstIndex to secondIndex
	private static void swap(char[] c, int firstIndex, int secondIndex)
	{
		char temp = c[firstIndex];
		c[firstIndex] = c[secondIndex];
		c[secondIndex] = temp;
	} // swap
	
	//method for shingling
	private static String[] shingle(String string, int size) {
	    String[] shingles = new String[string.length() - size + 1];
	    
	    for (int i = 0; i < string.length() - size + 1; i ++) 
	    {
	        shingles[i] = string.substring(i, i + size);
	    } // for
	    
	    return shingles;
	} // shingle
	
	//printing matrix rep for matrix_M or matrix_Ms
	private static void printMatRep(int[][] matrix)
	{
		for (int row = 0; row < matrix.length; row ++)
		{
			for (int col = 0; col < matrix[row].length; col ++)
			{
				System.out.print(matrix[row][col] + " ");
			} // for
			
			System.out.println();
		} // for
	} // printMatRep
	
	//printing matrix rep for matrix_M or matrix_Ms for double array
	private static void printMatRep(double[][] matrix)
	{
		for (int row = 0; row < matrix.length; row ++)
		{
			for (int col = 0; col < matrix[row].length; col ++)
			{
				System.out.print(matrix[row][col] + " ");
			} // for
			
			System.out.println();
		} // for
	} // printMatRep
	
	//this method returns prime number bigger than n
	private static int getPrimeNumberBiggerThan(int n)
	{
		int i = 0;
	    int num = 0;
	    
	    ArrayList<Integer> primes = new ArrayList<Integer>();

		for (i = n; i <= n + 100; i++) 
		{
			int counter = 0;
			
			for (num = i; num >= 1; num--) 
			{
				if (i % num == 0) {
					counter = counter + 1;
				} // if
			}
			
			if (counter == 2) {
				primes.add(i);
			}
		} // for
		
		return primes.get(0);
	} // getPrimeNumberBiggerThan

} // class DocumentDeduplication