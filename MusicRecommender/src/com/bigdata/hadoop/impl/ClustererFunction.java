package com.bigdata.hadoop.impl;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

//The class that represents function to achieve projection of 6 fields to 2 fields 
@SuppressWarnings("rawtypes")
public class ClustererFunction extends BaseOperation implements Function {

	private static final long serialVersionUID = 1L;
	private final Fields fields;
	private final UserSetClusterModel userSetClusterModel;

	public ClustererFunction(Fields fieldDeclaration, 
			UserSetClusterModel userSetClusterModel) 
	{
		super(1, fieldDeclaration);
		this.fields = fieldDeclaration;
		
		//assign deep copy of this cluster model
		this.userSetClusterModel = new UserSetClusterModel(userSetClusterModel);
	} // ProjectToFields

	@Override
	public void operate(FlowProcess flowProcess, FunctionCall functionCall) 
	{

		TupleEntry arguments = functionCall.getArguments();

		// create a Tuple to hold our result values
		Tuple result = new Tuple();

		Matrix userSetMatrixRow;
		
		for (int index = 0; index < this.fields.size(); index ++) {
			Comparable name = fields.get(index);
			
			if(name.equals("userid"))
			{
				//get Matrix object from the arguments
				userSetMatrixRow = (Matrix) arguments.getObject(name);
				
				//get string representation that will be in the format
				//0,1,1,0,1,0,1,1
				String stringRepOfRow = userSetMatrixRow.toString();
				
				//cluster that userSetMatrixRow
				String clusterId = userSetClusterModel.findClosestCluster(stringRepOfRow);
				
				result.add(clusterId);
			} // if
			
		} // for

		// return the result Tuple
		functionCall.getOutputCollector().add(result);
	} // operate
	
} // class ClustererFunction
