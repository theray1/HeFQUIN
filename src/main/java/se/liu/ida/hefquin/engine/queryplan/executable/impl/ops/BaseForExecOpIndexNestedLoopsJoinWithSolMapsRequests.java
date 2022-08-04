package se.liu.ida.hefquin.engine.queryplan.executable.impl.ops;

import se.liu.ida.hefquin.engine.data.SolutionMapping;
import se.liu.ida.hefquin.engine.federation.FederationMember;
import se.liu.ida.hefquin.engine.federation.access.DataRetrievalRequest;
import se.liu.ida.hefquin.engine.federation.access.SolMapsResponse;
import se.liu.ida.hefquin.engine.query.Query;
import se.liu.ida.hefquin.engine.queryplan.executable.IntermediateResultElementSink;

public abstract class BaseForExecOpIndexNestedLoopsJoinWithSolMapsRequests<QueryType extends Query,
                                                                           MemberType extends FederationMember,
                                                                           ReqType extends DataRetrievalRequest>
     extends BaseForExecOpIndexNestedLoopsJoinWithRequests<QueryType,MemberType,ReqType,SolMapsResponse>
{
	public BaseForExecOpIndexNestedLoopsJoinWithSolMapsRequests( final QueryType query, final MemberType fm ) {
		super( query, fm );
	}

	@Override
	protected MyResponseProcessor createResponseProcessor( final SolutionMapping sm, final IntermediateResultElementSink sink )
	{
		return new MyResponseProcessor(sm, sink) {
			@Override
			protected Iterable<SolutionMapping> extractSolMaps(final SolMapsResponse response) {
				return response.getSolutionMappings();
			}
		};
	}

}