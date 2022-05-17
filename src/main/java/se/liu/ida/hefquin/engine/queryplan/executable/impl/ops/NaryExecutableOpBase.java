package se.liu.ida.hefquin.engine.queryplan.executable.impl.ops;

import se.liu.ida.hefquin.engine.queryplan.ExecutableOperatorStats;
import se.liu.ida.hefquin.engine.queryplan.executable.impl.ExecutableOperatorStatsImpl;

/**
 * Top-level base class for all implementations of {@link NaryExecutableOp}.
 */
public abstract class NaryExecutableOpBase implements NaryExecutableOp
{

	@Override
	public void resetStats() {
	}

	@Override
	public final ExecutableOperatorStats getStats() {
		return createStats();
	}

	protected ExecutableOperatorStatsImpl createStats() {
		final ExecutableOperatorStatsImpl s = new ExecutableOperatorStatsImpl(this);
		return s;
	}
}