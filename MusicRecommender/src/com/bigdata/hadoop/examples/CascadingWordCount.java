package com.bigdata.hadoop.examples;

import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.operation.Aggregator;
import cascading.operation.Function;
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexGenerator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

public class CascadingWordCount {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) 
	{
		//hard-coded path variables, which points to directories
		String inputPath = "/home/deep-learning/input";
		String outputPath = "/home/deep-learning/output";
		
		//create tuples with the field name "line" form inputPath 
		Tap source = new Hfs(new TextLine(new Fields("line")), inputPath);
		
		//create output tuples with field names "word" and "count"
		Tap sink = new Hfs(new TextLine(new Fields("word", "count")), 
				outputPath, SinkMode.REPLACE);
		
		//create an assembly pipeline wordcount
		Pipe pipe = new Pipe("wordcount");
		
		//word tokenizer regex
		String regex = "(?<!\\pL)(?=\\pL)[^ ]*(?<=\\pL)(?!\\pL)";
		
		//for each tuple with field name "line", create a new tuples 
		//from them with the field name "word"
		Function function = new RegexGenerator(new Fields("word"), regex);
		
		//for each tuple, apply the function and obtain new pipe
		pipe = new Each(pipe, new Fields("line"), function);
		
		//now group the tuples obtained for each field "word"
		pipe = new GroupBy(pipe, new Fields("word"));
		
		//define aggregator to aggregate counts of each word
		Aggregator count = new Count(new Fields("count"));
		pipe = new Every(pipe, count);
		
		// initialize app properties, tell Hadoop which jar file to use
		Properties properties = new Properties();
		AppProps.setApplicationJarClass(properties, CascadingWordCount.class);
		
		// plan a new Flow from the pipe using the source and sink Taps
		// with the above properties
		FlowConnector flowConnector = new HadoopFlowConnector(properties);
		Flow flow = flowConnector.connect("word-count", source, sink, pipe);
		
		flow.complete();
		
		System.out.println("\nWARNings does not matter, job has run successfully");
		System.out.println("Done, check ~/output folder");
		System.out.println("cat part0000 file there");
	} // main

} // class CascadingWordCount
