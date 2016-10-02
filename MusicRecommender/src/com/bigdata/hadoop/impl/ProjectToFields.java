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
public class ProjectToFields extends BaseOperation implements Function {

	private static final long serialVersionUID = 1L;
	private final Fields fields;

	public ProjectToFields(Fields fieldDeclaration) {
		super(1, fieldDeclaration);
		this.fields = fieldDeclaration;
	} // ProjectToFields

	@Override
	public void operate(FlowProcess flowProcess, FunctionCall functionCall) 
	{

		TupleEntry arguments = functionCall.getArguments();

		// create a Tuple to hold our result values
		Tuple result = new Tuple();

		Object object;
		
		for (int i = 0; i < this.fields.size(); i++) {
			Comparable name = fields.get(i);
			object = arguments.getObject(name);
			result.add(object);
		} // for

		// return the result Tuple
		functionCall.getOutputCollector().add(result);
	} // operate
	
} // class ProjectToFields

