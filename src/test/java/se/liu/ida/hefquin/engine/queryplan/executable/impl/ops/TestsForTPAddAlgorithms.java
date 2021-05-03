package se.liu.ida.hefquin.engine.queryplan.executable.impl.ops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.graph.GraphFactory;

import se.liu.ida.hefquin.engine.data.SolutionMapping;
import se.liu.ida.hefquin.engine.data.impl.SolutionMappingUtils;
import se.liu.ida.hefquin.engine.federation.FederationAccessManager;
import se.liu.ida.hefquin.engine.federation.FederationMember;
import se.liu.ida.hefquin.engine.query.TriplePattern;
import se.liu.ida.hefquin.engine.query.impl.TriplePatternImpl;
import se.liu.ida.hefquin.engine.queryplan.executable.IntermediateResultBlock;
import se.liu.ida.hefquin.engine.queryplan.executable.impl.GenericIntermediateResultBlockImpl;
import se.liu.ida.hefquin.engine.queryplan.executable.impl.MaterializingIntermediateResultElementSink;
import se.liu.ida.hefquin.engine.queryproc.ExecutionContext;

/**
 * This is an abstract class with tests for any algorithm that is
 * meant to be used as an implementation for the tpAdd operator.
 */
public abstract class TestsForTPAddAlgorithms<MemberType extends FederationMember> extends ExecOpTestBase
{
	protected void _tpWithJoinOnObject() {
		final Var var1 = Var.alloc("v1");
		final Var var2 = Var.alloc("v2");
		final Var var3 = Var.alloc("v3");

		final Node p = NodeFactory.createURI("http://example.org/p");
		final Node x1 = NodeFactory.createURI("http://example.org/x1");
		final Node x2 = NodeFactory.createURI("http://example.org/x2");
		final Node y1 = NodeFactory.createURI("http://example.org/y1");
		final Node y2 = NodeFactory.createURI("http://example.org/y2");
		final Node z1 = NodeFactory.createURI("http://example.org/z1");
		final Node z2 = NodeFactory.createURI("http://example.org/z2");
		final Node z3 = NodeFactory.createURI("http://example.org/z3");

		final GenericIntermediateResultBlockImpl input = new GenericIntermediateResultBlockImpl();
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, x1,
				var2, y1) );
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, x2,
				var2, y2) );

		final TriplePattern tp = new TriplePatternImpl(var2,p,var3);

		final Graph dataForMember = GraphFactory.createGraphMem();
		dataForMember.add( Triple.create(y1,p,z1) );
		dataForMember.add( Triple.create(y1,p,z2) );
		dataForMember.add( Triple.create(y2,p,z3) );

		final Iterator<SolutionMapping> it = runTest(input, dataForMember, tp);

		// checking
		final Set<Binding> result = new HashSet<>();

		assertTrue( it.hasNext() );
		result.add( it.next().asJenaBinding() );

		assertTrue( it.hasNext() );
		result.add( it.next().asJenaBinding() );

		assertTrue( it.hasNext() );
		result.add( it.next().asJenaBinding() );

		assertFalse( it.hasNext() );

		boolean b1Found = false;
		boolean b2Found = false;
		boolean b3Found = false;
		for ( final Binding b : result ) {
			assertEquals( 3, b.size() );

			if ( b.get(var1).getURI().equals("http://example.org/x1") ) {
				assertEquals( "http://example.org/y1", b.get(var2).getURI() );
				if ( b.get(var3).getURI().equals("http://example.org/z1") ) {
					b1Found = true;
				}
				else if ( b.get(var3).getURI().equals("http://example.org/z2") ) {
					b2Found = true;
				}
				else {
					fail( "Unexpected URI for ?v3: " + b.get(var3).getURI() );
				}
			}
			else if ( b.get(var1).getURI().equals("http://example.org/x2") ) {
				assertEquals( "http://example.org/y2", b.get(var2).getURI() );
				assertEquals( "http://example.org/z3", b.get(var3).getURI() );
				b3Found = true;
			}
			else {
				fail( "Unexpected URI for ?v1: " + b.get(var1).getURI() );
			}
		}

		assertTrue(b1Found);
		assertTrue(b2Found);
		assertTrue(b3Found);

	}

	protected void _tpWithJoinOnSubjectAndObject() {
		final Var var1 = Var.alloc("v1");
		final Var var2 = Var.alloc("v2");
		final Var var3 = Var.alloc("v3");

		final Node x1 = NodeFactory.createURI("http://example.org/x1");
		final Node x2 = NodeFactory.createURI("http://example.org/x2");
		final Node y1 = NodeFactory.createURI("http://example.org/y1");
		final Node y2 = NodeFactory.createURI("http://example.org/y2");
		final Node z1 = NodeFactory.createURI("http://example.org/z1");
		final Node z2 = NodeFactory.createURI("http://example.org/z2");

		final GenericIntermediateResultBlockImpl input = new GenericIntermediateResultBlockImpl();
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, x1,
				var2, y1) );
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, x2,
				var2, y2) );

		final TriplePattern tp = new TriplePatternImpl(var1,var2,var3);

		final Graph dataForMember = GraphFactory.createGraphMem();
		dataForMember.add( Triple.create(x1,y1,z1) );
		dataForMember.add( Triple.create(x2,y2,z2) );

		final Iterator<SolutionMapping> it = runTest(input, dataForMember, tp);

		// checking
		final Set<Binding> result = new HashSet<>();

		assertTrue( it.hasNext() );
		result.add( it.next().asJenaBinding() );

		assertTrue( it.hasNext() );
		result.add( it.next().asJenaBinding() );

		assertFalse( it.hasNext() );

		boolean b1Found = false;
		boolean b2Found = false;
		for ( final Binding b : result ) {
			assertEquals( 3, b.size() );

			if ( b.get(var1).getURI().equals("http://example.org/x1") ) {
				assertEquals( "http://example.org/y1", b.get(var2).getURI() );
				assertEquals( "http://example.org/z1", b.get(var3).getURI() );
				b1Found = true;
			}
			else if ( b.get(var1).getURI().equals("http://example.org/x2") ) {
				assertEquals( "http://example.org/y2", b.get(var2).getURI() );
				assertEquals( "http://example.org/z2", b.get(var3).getURI() );
				b2Found = true;
			}
			else {
				fail( "Unexpected URI for ?v1: " + b.get(var1).getURI() );
			}
		}

		assertTrue(b1Found);
		assertTrue(b2Found);
	}

	protected void _tpWithoutJoinVariable() {
		final Var var1 = Var.alloc("v1");
		final Var var2 = Var.alloc("v2");
		final Var var3 = Var.alloc("v3");

		final Node p = NodeFactory.createURI("http://example.org/p");
		final Node x1 = NodeFactory.createURI("http://example.org/x1");
		final Node x2 = NodeFactory.createURI("http://example.org/x2");
		final Node y1 = NodeFactory.createURI("http://example.org/y1");
		final Node y2 = NodeFactory.createURI("http://example.org/y2");
		final Node z1 = NodeFactory.createURI("http://example.org/z1");
		final Node z2 = NodeFactory.createURI("http://example.org/z2");

		final GenericIntermediateResultBlockImpl input = new GenericIntermediateResultBlockImpl();
		input.add( SolutionMappingUtils.createSolutionMapping(var1, x1) );
		input.add( SolutionMappingUtils.createSolutionMapping(var1, x2) );

		final TriplePattern tp = new TriplePatternImpl(var2,p,var3);

		final Graph dataForMember = GraphFactory.createGraphMem();
		dataForMember.add( Triple.create(y1,p,z1) );
		dataForMember.add( Triple.create(y2,p,z2) );

		final Iterator<SolutionMapping> it = runTest(input, dataForMember, tp);

		assertTrue( it.hasNext() );
		final Binding b1 = it.next().asJenaBinding();
		assertEquals( 3, b1.size() );

		assertTrue( it.hasNext() );
		final Binding b2 = it.next().asJenaBinding();
		assertEquals( 3, b2.size() );

		assertTrue( it.hasNext() );
		final Binding b3 = it.next().asJenaBinding();
		assertEquals( 3, b3.size() );

		assertTrue( it.hasNext() );
		final Binding b4 = it.next().asJenaBinding();
		assertEquals( 3, b4.size() );

		assertFalse( it.hasNext() );
	}

	protected void _tpWithEmptyInput() {
		final Var var2 = Var.alloc("v2");
		final Var var3 = Var.alloc("v3");

		final Node p = NodeFactory.createURI("http://example.org/p");
		final Node y1 = NodeFactory.createURI("http://example.org/y1");
		final Node z1 = NodeFactory.createURI("http://example.org/z1");

		final GenericIntermediateResultBlockImpl input = new GenericIntermediateResultBlockImpl();

		final TriplePattern tp = new TriplePatternImpl(var2,p,var3);

		final Graph dataForMember = GraphFactory.createGraphMem();
		dataForMember.add( Triple.create(y1,p,z1) );

		final Iterator<SolutionMapping> it = runTest(input, dataForMember, tp);

		assertFalse( it.hasNext() );
	}

	protected void _tpWithEmptyResponses() {
		final Var var1 = Var.alloc("v1");
		final Var var2 = Var.alloc("v2");

		final GenericIntermediateResultBlockImpl input = new GenericIntermediateResultBlockImpl();
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, NodeFactory.createURI("http://example.org/x1")) );
		input.add( SolutionMappingUtils.createSolutionMapping(
				var1, NodeFactory.createURI("http://example.org/x2")) );

		final Node p = NodeFactory.createURI("http://example.org/p");
		final TriplePattern tp = new TriplePatternImpl(var1,p,var2);

		final Graph dataForMember = GraphFactory.createGraphMem();

		final Iterator<SolutionMapping> it = runTest(input, dataForMember, tp);

		assertFalse( it.hasNext() );
	}



	protected Iterator<SolutionMapping> runTest(
			final IntermediateResultBlock input,
			final Graph dataForMember,
			final TriplePattern tp )
	{
		final FederationAccessManager fedAccessMgr = new FederationAccessManagerForTest();
		final ExecutionContext execCxt = new ExecutionContext(fedAccessMgr);
		final MaterializingIntermediateResultElementSink sink = new MaterializingIntermediateResultElementSink();

		final MemberType fm = createFedMemberForTest(dataForMember);

		final UnaryExecutableOp op = createExecOpForTest(tp, fm);
		op.process(input, sink, execCxt);
		op.concludeExecution(sink, execCxt);

		return sink.getMaterializedIntermediateResult().iterator();
	}

	protected abstract MemberType createFedMemberForTest( Graph dataForMember );

	protected abstract UnaryExecutableOp createExecOpForTest( TriplePattern tp, MemberType fm );
}