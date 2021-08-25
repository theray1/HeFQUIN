package se.liu.ida.hefquin.engine.queryproc.impl.optimizer.rewriting.rules;

import se.liu.ida.hefquin.engine.queryplan.PhysicalOperator;
import se.liu.ida.hefquin.engine.queryplan.logical.impl.LogicalOpTPAdd;
import se.liu.ida.hefquin.engine.queryplan.physical.PhysicalOperatorForLogicalOperator;
import se.liu.ida.hefquin.engine.queryplan.physical.impl.*;

/**
 * This class contains methods that identifying the root physical operator of a physical plan.
 * It helps to check whether the root physical operator a physical plan can be applied to a rule.
 */
public class IdentifyPhysicalOpUsedForTPAdd {

    public static boolean isIndexNLJ( final PhysicalOperator pop ) {
        return matchTPAdd( pop ) && ( pop instanceof PhysicalOpIndexNestedLoopsJoin );
    }

    public static boolean isBindJoinFILTER( final PhysicalOperator pop ) {
        return matchTPAdd( pop ) && ( pop instanceof PhysicalOpBindJoinWithFILTER);
    }

    public static boolean isBindJoinUNION( final PhysicalOperator pop ) {
        return matchTPAdd( pop ) && ( pop instanceof PhysicalOpBindJoinWithUNION);
    }

    public static boolean isBindJoinVALUES( final PhysicalOperator pop ) {
        return matchTPAdd( pop ) && ( pop instanceof PhysicalOpBindJoinWithVALUES);
    }

    public static boolean isBindJoin( final PhysicalOperator pop ) {
        return matchTPAdd( pop ) && ( pop instanceof PhysicalOpBindJoin);
    }

    protected static boolean matchTPAdd( final PhysicalOperator pop ) {
        final PhysicalOperatorForLogicalOperator popLop = (PhysicalOperatorForLogicalOperator) pop;
        return popLop.getLogicalOperator() instanceof LogicalOpTPAdd;
    }

}