package com.bigdata.hadoop.impl;

import cascading.flow.FlowProcess;
import cascading.operation.Aggregator;
import cascading.operation.AggregatorCall;
import cascading.operation.BaseOperation;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class UserSetAggregator extends BaseOperation<UserSetAggregator.Context>
implements Aggregator<UserSetAggregator.Context>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3874103634009633912L;
	private final Fields fields;

	//custom context to be used
	public class Context 
	{  
		private UserSet userSet;
		
	} // class Context
	
	public UserSetAggregator(Fields fields) {
		super(1, fields); // one input field and output fields named as in constructor call
		this.fields = fields;
	} // CustomCountAggregator

	//called on each tuple of the group
	@SuppressWarnings("rawtypes")
	@Override
	public void aggregate(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		TupleEntry arguments = aggregatorCall.getArguments();
		Context context = aggregatorCall.getContext();
		
		//we will get the object in each field of the tuple
		//Here out tuple will have one just one field "userid"
		Object object;
		
		for (int i = 0; i < this.fields.size(); i++) {
			
			//get the field name "userid"
			Comparable name = fields.get(i);
			
			//get the object in that field
			//and add it to the UserSet
			object = arguments.getObject(name);
			
			//obtain UserSet and add
			//make cast to string
			context.userSet.add((String) object);
		} // for
		
	} // aggregate

	//called after all tuples of the group have been processed,
	@SuppressWarnings("rawtypes")
	//can generate one or more output tuples
	@Override
	public void complete(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		Context context = aggregatorCall.getContext();
		Tuple result = new Tuple(); // create new tuple
		
		result.add(context.userSet); // add the aggregated userset to the tuple 
								   //and output collect it
		
		aggregatorCall.getOutputCollector().add(result);
	} // complete

	//called before any tuples comes in
	@SuppressWarnings("rawtypes")
	@Override
	public void start(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		Context context = new Context();
		context.userSet = new UserSet(); // assign empty UserSet to the context
		aggregatorCall.setContext(context); // set context to be retrieved in aggregate and
											// complete methods
	} // start
	
} // class UserSetAggregator
