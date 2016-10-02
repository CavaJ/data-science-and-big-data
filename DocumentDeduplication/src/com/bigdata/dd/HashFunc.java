package com.bigdata.dd;

//this function will calculate ((ax + b) mod p) mod r
public class HashFunc 
{
	//instance variables for a and b
	private int a, b;
	
	public HashFunc(int a, int b)
	{
		this.a = a;
		this.b = b;
	} // HashFunc
	
	//p is prime number > r, r is #rows
	public int hash(int x, int p, int r)
	{
		return ((a * x + b) % p) % r;
	} // hash
} // class HashFunc
