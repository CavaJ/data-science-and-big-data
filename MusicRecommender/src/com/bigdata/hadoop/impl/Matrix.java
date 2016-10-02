package com.bigdata.hadoop.impl;

import java.io.Serializable;

//Class for holding matrix representation of userSet
public class Matrix implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7390291844228246955L;
	//instance variable to hold matrix representation
	private int[] matrix;
	
	public Matrix(int size)
	{
		matrix = new int[size];
	} // Matrix
	
	/*
	 * helper method for setting the value at specific index
	 * newly set values will be zeros or ones
	 * indices are zero-based */
	public void set(int index, int value)
	{
		matrix[index] = value;
	} // set
	
	//string representation of the Matrix
	@Override
	public String toString()
	{
		/*return Arrays.toString(matrix);*/
		
		return getCommaDelimitedString();
	} // toString
	
	//helper method to achieve output of 0,1,0,1,1,1....
	private String getCommaDelimitedString()
	{
		StringBuilder result = new StringBuilder("");
		
		for (int index = 0; index < matrix.length; index ++)
		{
			//for the last entry we would not add any comma
			if (index == matrix.length - 1)
			{
				result.append(matrix[index]);
			} // if
			else
			{
				result.append(matrix[index] + ",");
			} // else
		} // for
		
		return result.toString();
	} // getCommaDelimitedString

	//getter method to retrieve the matrix array 
	public int[] getMatrix() {
		return matrix;
	} // getMatrix
	
	
} // class Matrix
