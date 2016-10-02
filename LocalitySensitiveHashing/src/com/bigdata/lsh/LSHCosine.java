package com.bigdata.lsh;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class LSHCosine {

	public static final void main(String... aArgs) throws IOException {
		Scanner scan = null;
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileWriter("LSH_Cosine_Group_10.txt"));
			// space dimension and Ball radius for target vectors
			log("Enter the dimension (int) of the space:");
			scan = new Scanner(System.in);
			int dim = scan.nextInt();
			log("Dimension: " + dim);
		//	log("Enter the max radius(int) for the vectors X and Y :");
		//	int radius = scan.nextInt();
		//	log("Max radius for vectors X and Y: " + radius);
		//	outputStream.println("/*-------Dim: " + dim
		//			+ "------Max Radius for X and Y: " + radius + "-------*/");
			
			// running k experiments
			for (int k = 0; k < 1; k++) {

				double[] X = new double[dim];
				double[] Y = new double[dim];
				double[] N = new double[dim];
				double angle = 0;
				//------------------------------------comment below out to run automatized Experiments
				System.out.println("Please enter X vector");
				for (int i = 0; i <dim; i++)
				{
				double temp = scan.nextInt();
				X[i]=temp;
				}
				System.out.println("Please enter Y vector");
				for (int i = 0; i <dim; i++)
				{
				double temp = scan.nextInt();
				Y[i]=temp;
				}
//-------------------UNcomment below out to run automatized Experiments---------------------//
				/*X = getVector(dim, radius);
				Y = getVector(dim, radius);
				 */
				logVector(dim, X, "X");
				logVector(dim, Y, "Y");
				angle = getAngle(dim, X, Y);
				
				/*----------FILE Logging-------------*/
				outputStream.println("Vector X: [");
				for (int i = 0; i < dim; ++i) {outputStream.print(X[i] + ", ");}
				outputStream.println("\n]");
				outputStream.println("Vector Y: [");
				for (int i = 0; i < dim; ++i) {outputStream.print(Y[i] + ", ");}
				outputStream.println("\n]");
				outputStream.println("\nAngle between X and Y: " + angle + " rad");
				outputStream.println("\n\nApproximations " + (k + 1) + ": \n");
				/*----------FILE Logging-------------*/
				
				System.out
						.println("\nAngle between X and Y: " + angle + " rad");
				System.out.println("\nApproximations " + (k + 1) + ": \n");

				// count the number of the hash-collisions
				int count = 0;

				// a single experiment BEGINN
				for (int iteration = 0; iteration <= 100000; iteration++) {

					// generating a norm vector for the hyperplane
					N = getUnitVector(dim);

					// track all the hash-collisions
					if (hashCosine(dim, X, N) == hashCosine(dim, Y, N))
						count++;
					// logging the precision of several estimations
					if (iteration == 10 || iteration == 50 || iteration == 100
							|| iteration == 200 || iteration == 500
							|| iteration == 1000 || iteration == 2000
							|| iteration == 5000 || iteration == 10000
							|| iteration == 20000 || iteration == 50000
							|| iteration == 80000 || iteration == 100000) {

						double angleAppr = 0;
						angleAppr = (1 - (double) count / (int) iteration)
								* Math.PI;
						/*----------FILE Logging-------------*/
						outputStream.println("Angle approximation at iteration "+iteration+" is: "+angleAppr+" rad");
						/*----------FILE Logging-------------*/
						System.out.println("Angle approximation at iteration "
								+ iteration + " is: " + angleAppr + " rad");
					}
				}
				// a single experiment END
			}
		}

		finally {
			if (scan != null)
				scan.close();

			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	/*--------------- logging functions ---------------*/

	private static void log(String aMessage) {
		System.out.println(aMessage);
	}

	private static void logVector(int dim, double[] vector, String name) {
		System.out.println();
		log("Vector " + name + ": [");
		for (int i = 0; i < dim; ++i) {
			System.out.print(vector[i] + ", ");
		}
		log("\n]");
	}

	/*--------------- logging functions ---------------*/

	/*
	 * ------generating a unit vector picked uniformly from the unit sphere
	 * ------
	 */
	public static double[] getUnitVector(int dim) {

		double[] vector = new double[dim];
		double norm = 0;
		Random randomGenerator = new Random();
		for (int i = 0; i < dim; ++i) {
			vector[i] = randomGenerator.nextGaussian();
			norm += vector[i] * vector[i];
		}
		norm = Math.sqrt(norm);
		for (int i = 0; i < dim; ++i) {
			vector[i] /= norm;
		}
		return vector;
	}

	/*------ generating a unit vector picked uniformly from the unit sphere ------*/

	/*------ generate a random d-dim vector inside the sphere of diameter  ------*/
	public static double[] getVector(int dim, int radius) {

		Random randomGenerator = new Random();
		double[] vector = new double[dim];
		for (int i = 0; i < dim; ++i) {
			vector[i] = randomGenerator.nextDouble() * 2 * radius - radius;
		}
		return vector;
	}

	/*------ generate a random d-dim vector inside the sphere of diameter ------*/

	/*------ calculating the angle between d-dim vectors X and Y ------*/

	public static double getAngle(int dim, double[] X, double[] Y) {

		double angle = 0, dot = 0, normX = 0, normY = 0;

		for (int i = 0; i < dim; ++i) {
			normX += X[i] * X[i];
			normY += Y[i] * Y[i];
			dot += X[i] * Y[i];
		}

		normX = Math.sqrt(normX);
		normY = Math.sqrt(normY);

		angle = Math.acos(dot / normX / normY);

		return angle;
	}

	/*------ calculating the angle between d-dim vectors X and Y ------*/

	/*-------------------hash calculation for Cosine Distance---------*/

	public static int hashCosine(int dim, double[] X, double[] N) {
		double dot = 0;
		for (int i = 0; i < dim; ++i) {
			dot += X[i] * N[i];
		}
		if (dot >= 0)
			return 1;
		else
			return 0;
	}
	/*-------------------hash calculation for Cosine Distance---------*/
} // class LSHCosine
