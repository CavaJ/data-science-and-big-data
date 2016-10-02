package com.bigdata.hadoop.impl;

import java.io.Serializable;
import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.operation.Aggregator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.Scheme;
import cascading.scheme.hadoop.TextDelimited;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

//implementation a Cascading workflow UserSetCluster
//that first computes
//the usersetmatrix for each artname and then determines the
//closest cluster

//output would be: artname, clusterid
public class UserSetCluster implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1349331561124854976L;
	//delimiter will be comma
	private static final String DELIMITER = "\\t";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Flow getUserSetClusterFlow(String inputPath, 
			String outputPath, UserSetClusterModel userSetClusterModel)
	{
		System.out.println("\nUserSetCluster flow started....\n");

		// usernames are in format user_000001 .... user_001000
		// we have 1000 unique users and to convert those values to
		// matrix represnetation. I will String replce to replace user_ part
		// with empty
		// string and doing Integer conversion

		// define source and sink Taps.
		Fields sourceFields = new Fields("userid", "timestamp", 
				"artid", "artname", "traid", "traname");
		Scheme sourceScheme = new TextDelimited(sourceFields);
		Tap source = new Hfs(sourceScheme, inputPath);

		//next two lines guarantees the output as stated in slides
		//Scheme outputSehema = new TextDelimited(false, DELIMITER);
		//Tap sink = new Hfs(outputSehema, outputPath + "/usersetmatrix",
						//SinkMode.REPLACE);
		
		// create output tuples with field names "artname" and "cluster_id"
		Tap sink = new Hfs(new TextLine(new Fields("artname", "cluster_id")), 
				outputPath + "/artistcluster", SinkMode.REPLACE);

		Pipe pipe = new Pipe("listeningEvents");

		// Filter out other fields
		Fields targetFields = new Fields("userid", "artname");
		ProjectToFields projector = new ProjectToFields(targetFields);

		// apply projector to sourceFields to obtain targetFields
		pipe = new Each(pipe, targetFields, projector);

		// basically at the end we will end up with "artname" "userset"
		Fields groupByFields = new Fields("artname");

		// now group the tuples obtained for each field "artname"
		pipe = new GroupBy(pipe, groupByFields);

		// aggregation field will be named "usersetmatrix"
		// and will be obtained by aggregating "userid"s
		Fields aggregationFields = new Fields("userid");

		// define aggregator and aggregate users to the userSetMatrix
		Aggregator userSetMatrixAggregator = new UserSetMatrixAggregator(aggregationFields);
		pipe = new Every(pipe, userSetMatrixAggregator);
		
		//cluster the userset Matrix rows from the previous pipe
		//basically we want to project to "artname", "usersetmatrixrow"
		Fields fields = new Fields("userid");
		
		//now cluster out userSetMatrix rows
		ClustererFunction clusterer = new ClustererFunction(fields, userSetClusterModel);
		pipe = new Each(pipe, fields, clusterer);

		Properties properties = new Properties();
		AppProps.setApplicationJarClass(properties, UserSetCluster.class);

		FlowConnector flowConnector = new HadoopFlowConnector(properties);

		Flow flow = flowConnector.connect("UserSetCluster", source, sink, pipe);
		return flow;

	} // getUserSetFlow
	
} // UserCluster
