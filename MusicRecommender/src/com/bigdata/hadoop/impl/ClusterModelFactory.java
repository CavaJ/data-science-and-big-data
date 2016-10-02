package com.bigdata.hadoop.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClusterModelFactory {

	private static final boolean skipHeader = true;
	//private static final double eps = 0.0000001d;

	public static ClusterModel readFromCsvResource(String file) {
		ClusterModel retval = new ClusterModel();
		BufferedReader br = null; 
		
		  try
		  {
			br = new BufferedReader(new FileReader(file));   
			String line;
			
			if (skipHeader) {
				br.readLine();
			}
			
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(",");
				String key = vals[0];
				StringBuilder sb = new StringBuilder();
				
				for (int i = 1; i < vals.length - 1; ++i) 
				{
					int dval = (int) Double.parseDouble(vals[i]);
					sb.append(dval);
					sb.append(" ");
				} // for
				
				int dval = (int) Double.parseDouble(vals[vals.length - 1]);
				sb.append(dval);

				retval.put(key, sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retval;

	} // readFromCsvResource
	
} // class ClusterModelFactory
