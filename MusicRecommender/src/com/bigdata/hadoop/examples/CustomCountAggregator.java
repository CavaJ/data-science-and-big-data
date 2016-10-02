package com.bigdata.hadoop.examples;

import cascading.flow.FlowProcess;
import cascading.operation.Aggregator;
import cascading.operation.AggregatorCall;
import cascading.operation.BaseOperation;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;

public class CustomCountAggregator extends BaseOperation<CustomCountAggregator.Context>
implements Aggregator<CustomCountAggregator.Context>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3874103634009633912L;

	//custom context to be used
	public class Context 
	{  
		private int count;
		
	} // class Context
	
	public CustomCountAggregator(Fields fields) {
		super(1, fields); // one input field and output fields named as in constructor call
	} // CustomCountAggregator

	//called on each tuple of the group
	@SuppressWarnings("rawtypes")
	@Override
	public void aggregate(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		//TupleEntry arguments = aggregatorCall.getArguments();
		Context context = aggregatorCall.getContext();
		context.count ++; // increment the count for each member of the group
	} // aggregate

	//called after all tuples of the group have been processed,
	//can generate one or more output tuples
	@SuppressWarnings("rawtypes")
	@Override
	public void complete(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		Context context = aggregatorCall.getContext();
		Tuple result = new Tuple(); // create new tuple
		result.add(context.count); // add the aggregation count the tuple and output collect it
		aggregatorCall.getOutputCollector().add(result);
	} // complete

	//called before any tuples comes in, count of the context woudl be 0 initially
	@SuppressWarnings("rawtypes")
	@Override
	public void start(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		Context context = new Context();
		context.count = 0;
		aggregatorCall.setContext(context); // set context to be retrieved in aggregate and
											// complete methods
	} // start
	
} // class CustomCountAggregator
