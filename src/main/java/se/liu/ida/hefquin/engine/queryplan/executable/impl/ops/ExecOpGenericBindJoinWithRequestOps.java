package se.liu.ida.hefquin.engine.queryplan.executable.impl.ops;

import se.liu.ida.hefquin.engine.data.SolutionMapping;
import se.liu.ida.hefquin.engine.data.utils.SolutionMappingUtils;
import se.liu.ida.hefquin.engine.federation.FederationMember;
import se.liu.ida.hefquin.engine.query.Query;
import se.liu.ida.hefquin.engine.queryplan.executable.ExecOpExecutionException;
import se.liu.ida.hefquin.engine.queryplan.executable.IntermediateResultBlock;
import se.liu.ida.hefquin.engine.queryplan.executable.IntermediateResultElementSink;
import se.liu.ida.hefquin.engine.queryproc.ExecutionContext;

/**
 * Abstract base class to implement bind joins by using request operators.
 *
 * Note that executing the request operator is a blocking operation within
 * the algorithm implemented by this class. However, it does not matter
 * because this bind join algorithm uses only one request for any given
 * {@link IntermediateResultBlock}. Issuing the request directly (and then
 * using a response processor) would also be blocking because we would have
 * to wait for the response processor. Attention: things may look different
 * if we have to do multiple requests per {@link IntermediateResultBlock},
 * which may be the case if the block size is greater than what the
 * server can/wants to handle.
 */
public abstract class ExecOpGenericBindJoinWithRequestOps<QueryType extends Query,
                                                          MemberType extends FederationMember>
           extends ExecOpGenericBindJoinBase<QueryType,MemberType>
{
	public ExecOpGenericBindJoinWithRequestOps( final QueryType query, final MemberType fm ) {
		super(query, fm);
	}

	@Override
	public void process( final IntermediateResultBlock input,
	                     final IntermediateResultElementSink sink,
	                     final ExecutionContext execCxt)
			throws ExecOpExecutionException
	{
		final NullaryExecutableOp reqOp = createExecutableRequestOperator( input.getSolutionMappings() );
		if ( reqOp != null ) {
			final IntermediateResultElementSink mySink = new MyIntermediateResultElementSink(sink, input);
			try {
				reqOp.execute(mySink, execCxt);
			}
			catch ( final ExecOpExecutionException e ) {
				throw new ExecOpExecutionException("Executing a request operator used by this bind join caused an exception.", e, this);
			}
		}
	}

	protected abstract NullaryExecutableOp createExecutableRequestOperator( Iterable<SolutionMapping> solMaps );


	// ------- helper classes ------

	protected static class MyIntermediateResultElementSink implements IntermediateResultElementSink
	{
		protected final IntermediateResultElementSink outputSink;
		protected final Iterable<SolutionMapping> inputSolutionMappings;

		public MyIntermediateResultElementSink( final IntermediateResultElementSink outputSink,
		                                        final IntermediateResultBlock input ) {
			this.outputSink = outputSink;
			this.inputSolutionMappings = input.getSolutionMappings();
		}

		@Override
		public void send( final SolutionMapping smFromRequest ) {
			// TODO: this implementation is very inefficient
			// We need an implementation of IntermediateResultBlock that can
			// be used like an index.
			// See: https://github.com/LiUSemWeb/HeFQUIN/issues/3
			for ( final SolutionMapping smFromInput : inputSolutionMappings ) {
				if ( SolutionMappingUtils.compatible(smFromInput, smFromRequest) ) {
					outputSink.send( SolutionMappingUtils.merge(smFromInput,smFromRequest) );
				}
			}
		}
    } // end of helper class MyIntermediateResultElementSink

}