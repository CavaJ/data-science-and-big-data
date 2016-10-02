package com.bigdata.hadoop.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class WordCount 
{
	public static void main(String[] args)
	{
		//code is identical in running on cluster or in development "local mode"
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("wordcount");
		
		//define output key and output value classes
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		//set mapper and reducer classes
		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		//hard-coded path variables, which points to directories
		String inputPath = "/home/deep-learning/input";
		String outputPath = "/home/deep-learning/output";
		
		FileInputFormat.setInputPaths(conf, new Path(inputPath));
		FileOutputFormat.setOutputPath(conf, new Path(outputPath));
		
		//run the job
		try 
		{
			JobClient.runJob(conf);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} // catch
		
		System.out.println("\nDone, check ~/output folder");
		
	} //main
	
	//Mapper class for mapping each line of input.txt to
	//to word, wordCount  key-value pair
	//initial key-value pair is lineNumber, line.
	private static class Map extends MapReduceBase implements 
	Mapper<LongWritable, Text, Text, IntWritable> 
	//inputkeytype, inputvaluetype, outputkeytype, outputvaluetype
	{
		//output value
		private final static IntWritable one = new IntWritable(1);
		
		//output key
		private Text word = new Text();
		
		@Override
		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException 
		{
			//get the string value of input value
			String line = value.toString();
			
			//tokenize line to words
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			//for each token, add key value pair word, 1
			while(tokenizer.hasMoreTokens())
			{
				word.set(tokenizer.nextToken());
				output.collect(word, one); // output key-value pair word, 1
			} // while
			
		} // map
		
	} // class Map
	
	//Reduce class to combine the occurences of the same word together,
	//collection will happen in key-value pair where the values of the same keys
	//will be aggregated.
	private static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> 
	{

		@Override
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
				Reporter reportter) throws IOException 
		{
			int sum = 0;
			
			//go through the iterator and sum all values of the same key
			while(values.hasNext())
			{
				sum += values.next().get();
			} // while
			
			output.collect(key, new IntWritable(sum));
			
		} // reduce
		
	} // class Reduce
	
} // class WordCount
