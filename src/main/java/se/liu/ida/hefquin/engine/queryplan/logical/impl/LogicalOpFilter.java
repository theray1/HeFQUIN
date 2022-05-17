package se.liu.ida.hefquin.engine.queryplan.logical.impl;

import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;

import se.liu.ida.hefquin.engine.queryplan.logical.LogicalPlanVisitor;
import se.liu.ida.hefquin.engine.queryplan.logical.UnaryLogicalOp;

public class LogicalOpFilter implements UnaryLogicalOp
{
	protected final ExprList filterExpressions;

	public LogicalOpFilter( final ExprList filterExpressions ) {
		assert filterExpressions != null;
		assert ! filterExpressions.isEmpty();

		this.filterExpressions = filterExpressions;
	}

	public LogicalOpFilter( final Expr filterExpression ) {
		assert filterExpression != null;

		this.filterExpressions = new ExprList(filterExpression);
	}

	@Override
	public boolean equals( final Object o ) {
		if ( o == this ) return true;
		if ( ! (o instanceof LogicalOpFilter) ) return false;

		final LogicalOpFilter oo = (LogicalOpFilter) o;
		return oo.filterExpressions.equals(filterExpressions); 
	}

	public ExprList getFilterExpressions() {
		return filterExpressions;
	}

	@Override
	public void visit( final LogicalPlanVisitor visitor ) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "> filter ( " + filterExpressions.toString() + " )";
	}
}