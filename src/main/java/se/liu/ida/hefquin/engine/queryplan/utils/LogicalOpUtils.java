package se.liu.ida.hefquin.engine.queryplan.utils;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import se.liu.ida.hefquin.engine.federation.FederationMember;
import se.liu.ida.hefquin.engine.federation.access.BGPRequest;
import se.liu.ida.hefquin.engine.federation.access.DataRetrievalRequest;
import se.liu.ida.hefquin.engine.federation.access.SPARQLRequest;
import se.liu.ida.hefquin.engine.federation.access.TriplePatternRequest;
import se.liu.ida.hefquin.engine.query.BGP;
import se.liu.ida.hefquin.engine.query.SPARQLGraphPattern;
import se.liu.ida.hefquin.engine.query.SPARQLQuery;
import se.liu.ida.hefquin.engine.query.TriplePattern;
import se.liu.ida.hefquin.engine.query.impl.BGPImpl;
import se.liu.ida.hefquin.engine.query.impl.SPARQLGraphPatternImpl;
import se.liu.ida.hefquin.engine.queryplan.LogicalOperator;
import se.liu.ida.hefquin.engine.queryplan.PhysicalOperator;
import se.liu.ida.hefquin.engine.queryplan.logical.UnaryLogicalOp;
import se.liu.ida.hefquin.engine.queryplan.logical.impl.LogicalOpBGPAdd;
import se.liu.ida.hefquin.engine.queryplan.logical.impl.LogicalOpRequest;
import se.liu.ida.hefquin.engine.queryplan.logical.impl.LogicalOpTPAdd;
import se.liu.ida.hefquin.engine.queryplan.physical.PhysicalOperatorForLogicalOperator;

import java.util.HashSet;
import java.util.Set;

public class LogicalOpUtils {

    /**
     * Creates a BGP by merging two sets of triple patterns,
     * which are extracted from two given Requests.
     */
    public static BGP createNewBGP( final LogicalOpRequest<?, ?> lop1, final LogicalOpRequest<?, ?> lop2 ) {
        final Set<TriplePattern> tps = getTriplePatternsOfReq(lop1);
        tps.addAll( getTriplePatternsOfReq(lop2) );

        return new BGPImpl(tps);
    }

    /**
     * Creates a BGP by adding a triple pattern to a set of triple patterns,
     * where the triple pattern is extracted from a given tpAdd operator,
     * and the set of triple patterns are extracted from the given Request.
     */
    public static BGP createNewBGP( final LogicalOpTPAdd lopTPAdd, final LogicalOpRequest<?, ?> lopReq ) {
        final TriplePattern tp = lopTPAdd.getTP();

        final Set<TriplePattern> tps = getTriplePatternsOfReq(lopReq);
        tps.add(tp);

        return new BGPImpl(tps);
    }

    /**
     * Creates a BGP by merging two sets of triple patterns,
     * where one of them is extracted from a given bgpAdd operator,
     * and another one is extracted from a given Request.
     */
    public static BGP createNewBGP( final LogicalOpBGPAdd lopBGPAdd, final LogicalOpRequest<?, ?> lopReq ) {
        final Set<TriplePattern> tps = new HashSet<>( lopBGPAdd.getBGP().getTriplePatterns() );

        tps.addAll( getTriplePatternsOfReq(lopReq) );

        return new BGPImpl(tps);
    }

    /**
     * Creates a BGP by merging two sets of triple patterns,
     * which are extracted from two given bgpAdd operators.
     */
    public static BGP createNewBGP( final LogicalOpBGPAdd lopBGPAdd1, final LogicalOpBGPAdd lopBGPAdd2 ) {
        final Set<TriplePattern> tps = new HashSet<>( lopBGPAdd1.getBGP().getTriplePatterns() );

        tps.addAll( lopBGPAdd2.getBGP().getTriplePatterns() );

        return new BGPImpl(tps);
    }

    /**
     * Creates a BGP by adding a triple pattern to a set of triple patterns,
     * where the triple pattern is extracted from a given tpAdd operator,
     * and the set of triple patterns are extracted from a given bgpAdd operator.
     */
    public static BGP createNewBGP( final LogicalOpTPAdd lopTPAdd, final LogicalOpBGPAdd lopBGPAdd ) {
        final TriplePattern tp = lopTPAdd.getTP();

        final Set<TriplePattern> tps = new HashSet<>(lopBGPAdd.getBGP().getTriplePatterns());
        tps.add(tp);

        return new BGPImpl(tps);
    }

    /**
     * Creates a new graph pattern by adding a triple pattern to the graph pattern of a given SPARQLRequest,
     * where the triple pattern is extracted from a given tpAdd operator.
     */
    public static SPARQLGraphPattern createNewGraphPatternWithAND(final LogicalOpTPAdd lopTPAdd, final LogicalOpRequest<?, ?> lopReq ) {
        final Triple tp = lopTPAdd.getTP().asJenaTriple();

        final SPARQLQuery graphPattern = ((SPARQLRequest) lopReq.getRequest()).getQuery();
        final Element element = graphPattern.asJenaQuery().getQueryPattern();

        ((ElementGroup) element).addTriplePattern(tp);

        return new SPARQLGraphPatternImpl(element);
    }

    /**
     * Creates a new graph pattern by adding a BGP to the graph pattern of a given SPARQLRequest,
     * where the BGP is extracted from a given bgpAdd operator.
     */
    public static SPARQLGraphPattern createNewGraphPatternWithAND( final LogicalOpBGPAdd lopBGPAdd, final LogicalOpRequest<?,?> lopReq ) {
        final BGP bgpOfBGPAdd = lopBGPAdd.getBGP();
        final BasicPattern bgp = ( (OpBGP)bgpOfBGPAdd.asJenaOp() ).getPattern();
        final ElementTriplesBlock elementBGP = new ElementTriplesBlock( bgp );

        final SPARQLQuery queryOfReq = ((SPARQLRequest) lopReq.getRequest()).getQuery();
        final Element elementPattern = queryOfReq.asJenaQuery().getQueryPattern();

        ((ElementGroup) elementPattern).addElement(elementBGP);

        return new SPARQLGraphPatternImpl(elementPattern);
    }

    /**
     * Creates a new graph pattern using a conjunction of two graph patterns,
     * which are extracted from two given SPARQLRequests.
     */
    public static SPARQLGraphPattern createNewGraphPatternWithAND( final LogicalOpRequest<?, ?> lopReq1, final LogicalOpRequest<?, ?> lopReq2 ) {
        final SPARQLQuery graphPattern1 = ((SPARQLRequest) lopReq1.getRequest()).getQuery();
        final Element element1 = graphPattern1.asJenaQuery().getQueryPattern();

        final SPARQLQuery graphPattern2 = ((SPARQLRequest) lopReq2.getRequest()).getQuery();
        final Element element2 = graphPattern2.asJenaQuery().getQueryPattern();

        ((ElementGroup) element1).addElement(element2);

        return new SPARQLGraphPatternImpl(element1);
    }

    /**
     * Creates a new graph pattern using a union of two graph patterns,
     * which are extracted from two given SPARQLRequests.
     */
    public static SPARQLGraphPattern createNewGraphPatternWithUnion( final LogicalOpRequest<?, ?> lopReq1, final LogicalOpRequest<?, ?> lopReq2 ) {
        final SPARQLQuery graphPattern1 = ((SPARQLRequest) lopReq1.getRequest()).getQuery();
        final Element element1 = graphPattern1.asJenaQuery().getQueryPattern();

        final SPARQLQuery graphPattern2 = ((SPARQLRequest) lopReq2.getRequest()).getQuery();
        final Element element2 = graphPattern2.asJenaQuery().getQueryPattern();

        final ElementUnion elementUnion = new ElementUnion(element1);
        elementUnion.addElement(element2);

        return new SPARQLGraphPatternImpl(elementUnion);
    }

    /**
     * Return a set of triple patterns, which are extracted from a given Request (support TriplePatternRequest and BGPRequest)
     */
    public static Set<TriplePattern> getTriplePatternsOfReq( final LogicalOpRequest<?, ?> lop ) {
        final DataRetrievalRequest req = lop.getRequest();

        if ( req instanceof TriplePatternRequest) {
            final TriplePatternRequest tpReq = (TriplePatternRequest) lop.getRequest();
            final Set<TriplePattern> tps = new HashSet<>();
            tps.add( tpReq.getQueryPattern() );

            return tps;
        }
        else if ( req instanceof BGPRequest) {
            final BGPRequest bgpReq = (BGPRequest) lop.getRequest();
            final BGP bgp = bgpReq.getQueryPattern();

            if ( bgp.getTriplePatterns().size() == 0 ) {
                throw new IllegalArgumentException( "the BGP is empty" );
            }
            else {
                return new HashSet<>( bgp.getTriplePatterns() );
            }
        }
        else  {
            throw new IllegalArgumentException( "Cannot get triple patterns of the given request operator (type: " + req.getClass().getName() + ")." );
        }
    }

    public static UnaryLogicalOp createUnaryLopFromReq( final PhysicalOperator op ) {
        final LogicalOperator lop = ((PhysicalOperatorForLogicalOperator) op).getLogicalOperator();
        if ( lop instanceof BGPRequest) {
            return createBGPAddLopFromReq((BGPRequest) lop);
        }
        else if( lop instanceof TriplePatternRequest ) {
            return createTPAddLopFromReq( (TriplePatternRequest) lop);
        }
        else {
            throw new IllegalArgumentException( "unsupported type of request: " + op.getClass().getName() );
        }
    }

    /**
     * Create a bgpAdd logical operator by extracting the BGP and fm from a given BGPRequest.
     */
    public static LogicalOpBGPAdd createBGPAddLopFromReq( final BGPRequest lop ) {
        final BGPRequest bgpReq = (BGPRequest) ((LogicalOpRequest<?, ?>) lop).getRequest();
        final BGP bgp = bgpReq.getQueryPattern();

        final FederationMember fm = ((LogicalOpRequest<?, ?>) lop).getFederationMember();

        return new LogicalOpBGPAdd( fm, bgp );
    }

    /**
     * Create a tpAdd logical operator by extracting the triple pattern and fm from a given TriplePatternRequest.
     */
    public static LogicalOpTPAdd createTPAddLopFromReq( final TriplePatternRequest lop ) {
        final TriplePatternRequest tpReq = (TriplePatternRequest) ((LogicalOpRequest<?, ?>) lop).getRequest();
        final TriplePattern tp = tpReq.getQueryPattern();

        final FederationMember fm = ((LogicalOpRequest<?, ?>) lop).getFederationMember();

        return new LogicalOpTPAdd( fm, tp );
    }

}