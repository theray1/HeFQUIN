package se.liu.ida.hefquin.federation.access.impl;

import se.liu.ida.hefquin.federation.BRTPFServer;
import se.liu.ida.hefquin.federation.FederationAccessManager;
import se.liu.ida.hefquin.federation.SPARQLEndpoint;
import se.liu.ida.hefquin.federation.TPFServer;
import se.liu.ida.hefquin.federation.access.BindingsRestrictedTriplePatternRequest;
import se.liu.ida.hefquin.federation.access.SPARQLRequest;
import se.liu.ida.hefquin.federation.access.SolMapsResponse;
import se.liu.ida.hefquin.federation.access.TriplePatternRequest;
import se.liu.ida.hefquin.federation.access.TriplesResponse;
import se.liu.ida.hefquin.federation.access.impl.reqproc.BRTPFRequestProcessor;
import se.liu.ida.hefquin.federation.access.impl.reqproc.SPARQLRequestProcessor;
import se.liu.ida.hefquin.federation.access.impl.reqproc.TPFRequestProcessor;

/**
 * A very simple {@link FederationAccessManager}
 * that simply blocks for each request.
 */
public class BlockingFederationAccessManagerImpl extends FederationAccessManagerBase
{
	public BlockingFederationAccessManagerImpl(
			final SPARQLRequestProcessor reqProcSPARQL,
			final TPFRequestProcessor reqProcTPF,
			final BRTPFRequestProcessor reqProcBRTPF ) {
		super(reqProcSPARQL, reqProcTPF, reqProcBRTPF);
	}

	@Override
	public SolMapsResponse performRequest( final SPARQLRequest req, final SPARQLEndpoint fm ) {
		return reqProcSPARQL.performRequest( req, fm );
	}

	@Override
	public TriplesResponse performRequest( final TriplePatternRequest req, final TPFServer fm ) {
		return reqProcTPF.performRequest( req, fm );
	}

	@Override
	public TriplesResponse performRequest( final TriplePatternRequest req, final BRTPFServer fm ) {
		return reqProcTPF.performRequest( req, fm );
	}

	@Override
	public TriplesResponse performRequest( final BindingsRestrictedTriplePatternRequest req, final BRTPFServer fm) {
		return reqProcBRTPF.performRequest( req, fm );
	}

}