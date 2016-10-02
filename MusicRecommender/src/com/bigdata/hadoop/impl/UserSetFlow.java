package com.bigdata.hadoop.impl;

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
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

public class UserSetFlow 
{
	//delimiter will be comma
	private static final String DELIMITER = ", ";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Flow getUserSetFlow(String inputPath, String outputPath) 
	{
		System.out.println("UserSetFlow started....\n");
		
		// define source and sink Taps.
		Fields sourceFields = new Fields("userid", "timestamp", "artid",
				"artname", "traid", "traname");
		Scheme sourceScheme = new TextDelimited(sourceFields);
		Tap source = new Hfs(sourceScheme, inputPath);
		
		//next two lines guarantees the output as stated in slides
		Scheme outputSehema = new TextDelimited(false, DELIMITER);
		Tap sink = new Hfs(outputSehema, outputPath + "/userset",
				SinkMode.REPLACE);
		
		//create output tuples with field names "artname" and "userset"
		/*Tap sink = new Hfs(new TextLine(new Fields("artname", "userset")), 
						outputPath + "/userset", SinkMode.REPLACE);*/

		Pipe pipe = new Pipe("listeningEvents");

		// Filter out other fields
		Fields targetFields = new Fields("userid", "artname");
		ProjectToFields projector = new ProjectToFields(targetFields);
		
		//apply projector to sourceFields to obtain targetFields
		pipe = new Each(pipe, targetFields, projector);
		
		//basically at the end we will end up with "artname"	"userset"
		Fields groupByFields = new Fields("artname");
		
		//now group the tuples obtained for each field "artname"
		pipe = new GroupBy(pipe, groupByFields);
		
		//aggregation field will be named the "userset"
		//and will be obtained by aggregating "userid"s
		Fields aggregationFields = new Fields("userid");
		
		//define aggregator and aggregate users to the userSet
		Aggregator userSetAggregator = new UserSetAggregator(aggregationFields);
		pipe = new Every(pipe, userSetAggregator);

		Properties properties = new Properties();
		AppProps.setApplicationJarClass(properties,
				UserSetFlow.class);

		FlowConnector flowConnector = new HadoopFlowConnector(properties);

		Flow flow = flowConnector.connect("UserSetFlow", source, sink,
				pipe);
		return flow;

	} // getUserSetFlow
	
} // class UserSetFlow
