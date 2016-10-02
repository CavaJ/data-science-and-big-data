package com.bigdata.hadoop.impl;

import java.io.Serializable;
import java.util.HashSet;

//class to represent a userset of the artist
//it is basically a abstract wrapper for HashSet
//we will have 1000 unique users in our dataset
public class UserSet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5188145591355301867L;
	
	//each user is represented as a string in our dataset
	//for example, user_000639. We can use TreeSet too, but here I have used HashSet
	private HashSet<String> users = new HashSet<String>();
	//set is a java data structure that removes duplicates when the new elements are inserted
	
	
	//add: helper method to add a user to our useset
	public void add(String username)
	{
		users.add(username);
	} // add
	
	//distanceTo will calculate the Jaccard Similarity between two userSets
	public double distanceTo(UserSet other)
	{
		//variable to hold the number of identical elements in both sets
		int intersectionAB = 0;
		
		//number of elements in this userset
		int A = users.size();
		
		//number of elements in another userset
		int B = other.getUsers().size();
		
		
		//intersection will be calculated in the folowing way
		//if specific element of this userSet is contained in other userSet
		//intersectionAB will be incremented
		for (String user : users)
		{
			if(other.getUsers().contains(user))
			{
				intersectionAB ++;
			} // if
		} // for
		
		//jaccard similarity will be calculated A intercetion B / A union B
		//where the union is A + B - intersectionAB
		double jaccardSimilarity = 
				(double) intersectionAB / (double) (A + B - intersectionAB);
		
		//distance is 1 - similarity
		return 1 - jaccardSimilarity;
	} // distanceTo

	public HashSet<String> getUsers() {
		return users;
	} // getUsers
	
	//override toString method to have the this kind of output in UserSetFlow:
	//[user_000231, user_000123, ...]
	@Override
	public String toString()
	{
		return users.toString();
	} // toString
	
} // main
