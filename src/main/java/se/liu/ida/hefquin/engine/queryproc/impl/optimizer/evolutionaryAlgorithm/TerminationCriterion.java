package se.liu.ida.hefquin.engine.queryproc.impl.optimizer.evolutionaryAlgorithm;

import java.util.List;

public interface TerminationCriterion {
    /**
     * Returns true if this termination criterion has been reached with the current generation.
     */
     boolean readyToTerminate( final int generationNumber,
                               final List<PhysicalPlanWithCost> currentGeneration,
                               final List<List<PhysicalPlanWithCost>> previousGenerations);

}