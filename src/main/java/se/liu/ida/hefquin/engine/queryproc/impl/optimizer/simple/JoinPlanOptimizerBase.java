package se.liu.ida.hefquin.engine.queryproc.impl.optimizer.simple;

import java.util.List;

import se.liu.ida.hefquin.engine.queryplan.PhysicalPlan;
import se.liu.ida.hefquin.engine.queryplan.logical.impl.LogicalOpJoin;
import se.liu.ida.hefquin.engine.queryplan.physical.BinaryPhysicalOp;
import se.liu.ida.hefquin.engine.queryplan.utils.LogicalToPhysicalOpConverter;
import se.liu.ida.hefquin.engine.queryproc.QueryOptimizationException;

/**
 * An abstract base class for implementations of {@link JoinPlanOptimizer}.
 */
public abstract class JoinPlanOptimizerBase implements JoinPlanOptimizer
{
	@Override
	public final PhysicalPlan determineJoinPlan( final List<PhysicalPlan> subplans )
			throws QueryOptimizationException
	{
		// no need to use the enumeration algorithm if there is only one subplan
		if ( subplans.size() == 1 ) {
			return subplans.get(0);
		}

		final EnumerationAlgorithm algo = initializeEnumerationAlgorithm(subplans);
		return algo.getResultingPlan();
	}

	protected abstract EnumerationAlgorithm initializeEnumerationAlgorithm( final List<PhysicalPlan> subplans );

	protected interface EnumerationAlgorithm
	{
		PhysicalPlan getResultingPlan() throws QueryOptimizationException;

		static BinaryPhysicalOp createNewJoinOperator() {
			return LogicalToPhysicalOpConverter.convert( LogicalOpJoin.getInstance() );
		}
	}

}