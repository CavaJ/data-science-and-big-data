package com.bigdata.hadoop.impl;

import cascading.flow.FlowProcess;
import cascading.operation.Aggregator;
import cascading.operation.AggregatorCall;
import cascading.operation.BaseOperation;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class UserSetMatrixAggregator extends BaseOperation<UserSetMatrixAggregator.Context>
implements Aggregator<UserSetMatrixAggregator.Context>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4399261420015890792L;
	private final Fields fields;
	
	//custom context to be used
	//matrix will have a length of 1000
	public class Context 
	{  
		private Matrix matrix;
	} // class Context
	
	public UserSetMatrixAggregator(Fields fields) {
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
		//which corresponds to String object
		String userId;
		
		for (int i = 0; i < this.fields.size(); i++) {
			
			//get the field name "userid"
			Comparable fieldName = fields.get(i);
			
			//get the String e.g. "user_000345" in that field
			//and update the corresponding entry in our matrix
			userId = arguments.getString(fieldName);
			
			//now preprocess the userId to have it in the format e.g. "000365"
			String integerRepresentableUserId = userId.replace("user_", "");
			
			//get the index that will be set 1 in our matrix
			//userid are 1-based we will do -1 operation to do it 0-based
			int index = Integer.parseInt(integerRepresentableUserId) - 1;
			
			//obtain the matrix and update the corresponding entry with 1
			context.matrix.set(index, 1);
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
		
		result.add(context.matrix); // add the aggregated matrix to the tuple 
								   //and output collect it
		
		aggregatorCall.getOutputCollector().add(result);
	} // complete

	//called before any tuples comes in
	@SuppressWarnings("rawtypes")
	@Override
	public void start(FlowProcess flowProcess, AggregatorCall<Context> aggregatorCall) 
	{
		Context context = new Context();
		context.matrix = new Matrix(1000); // assign empty matrix with all zeros to the context
										   // matrix has a dimension of 1000, after this call
									       // it will have 1000 zero elements
		aggregatorCall.setContext(context); // set context to be retrieved in aggregate and
											// complete methods
	} // start
	
} // class UserSetMatrixAggregator
