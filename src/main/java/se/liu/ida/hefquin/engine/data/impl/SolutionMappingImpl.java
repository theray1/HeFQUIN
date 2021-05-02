package se.liu.ida.hefquin.engine.data.impl;

import org.apache.jena.sparql.engine.binding.Binding;

import se.liu.ida.hefquin.engine.data.SolutionMapping;

public class SolutionMappingImpl implements SolutionMapping
{
	protected final Binding jenaObj;

	public SolutionMappingImpl( final Binding jenaObject ) {
		assert jenaObject != null;
		this.jenaObj = jenaObject;
	}

	@Override
	public Binding asJenaBinding() {
		return jenaObj;
	}
}