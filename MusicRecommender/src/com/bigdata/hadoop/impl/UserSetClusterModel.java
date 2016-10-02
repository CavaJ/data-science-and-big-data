package com.bigdata.hadoop.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class UserSetClusterModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//its predefined cluster model
	private ClusterModel clusterModel;
	
	//constructor accepting sampleClusters file path
	public UserSetClusterModel(String path)
	{
		this.clusterModel = ClusterModelFactory.readFromCsvResource(path);
	} // UserSetClusterModel
	
	//generates a deep copy of usersetclustermodel
	public UserSetClusterModel(UserSetClusterModel model)
	{
		ClusterModel tempModel = model.getClusterModel();
		
		HashMap<String, String> newMap = new HashMap<>();
		
		ClusterModel newModel = new ClusterModel();
		
		for(String key : tempModel.getKeys())
		{
			newMap.put(new String(key), new String(tempModel.getValue(key)));
		} // for
		
		newModel.setMap(newMap);
		
		this.clusterModel = newModel;
		
	} // UserSetClusterModel
	
	//giving argument "Userset userset" creates inconsistency
	//basically argument has to usersetmatrix row
	public String findClosestCluster(String usersetMatrixRow) {
		
		
		Set<String> keys =  this.clusterModel.getKeys();
		java.util.Iterator<String> it = keys.iterator();
		
		double minDist= Double.POSITIVE_INFINITY;
		String minKey="";
		
		//replace "," with empty string
		usersetMatrixRow = usersetMatrixRow.replace(",", "");
		
		while (it.hasNext()){
			String key = it.next();//storing the key for the minDist cluster
			int dist=0;//Hamming distance would do the job
			
			
			String candidate = clusterModel.getValue(key.toString());
			
			//replace " " with empty string
			candidate = candidate.replace(" ", "");
			
			//System.out.println("Candidate: " + candidate);
			//System.out.println("Row: " + usersetMatrixRow);
			//System.out.println("Candidate length: " + candidate.length());
			//System.out.println("Row length: " + usersetMatrixRow.length());
			
			for (int i = 0; i < candidate.length(); i++) {
			    if (candidate.charAt(i) != usersetMatrixRow.charAt(i)) 
			    {
			        dist++;
			    }
			    
			   }//storing the minimum distance
			
			if(dist<minDist){
				   minDist=(double)dist;
				   minKey=key;
			}
			
		}
		return minKey;
		
	} // findClosestCluster

	public ClusterModel getClusterModel() 
	{
		return clusterModel;
	} // getModel
	
	
	
} // class UserSetClusterModel
