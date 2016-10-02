package com.bigdata.hadoop.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class ClusterModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<String, String> map = new HashMap<String, String>();

	public void put(String clusterId, String metroid) {
		map.put(clusterId, metroid);
	}

	//Returns the string of 0,1,0,1 .. ,0 representing the userset
	//of the metroid of the cluster
	public String getValue(String clusterid) 
	{
		return map.get(clusterid);
	}
	
	//return keyset of the hashmap
	public Set<String> getKeys()
	{
		return map.keySet();
	} // getKeys
	
	public HashMap<String, String> getMap() {
		return map;
	} // getMap
	
	public void setMap(HashMap<String, String> newMap)
	{
		map = newMap;
	} // setMap

} // ClusterModel
