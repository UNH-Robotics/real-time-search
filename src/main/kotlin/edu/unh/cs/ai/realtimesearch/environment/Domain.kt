package edu.unh.cs.ai.realtimesearch.environment

/**
 * @author Bence Cserna (bence@cserna.net)
 *
 * A domain is a problem setting that defines, given a state, all possible successor states with their
 * accompanied action and cost
 */
interface Domain {
    public fun successors(state: State): List<SuccessorBundle>
    public fun predecessors(state: State): List<SuccessorBundle>
    public fun heuristic(state: State): Double
    public fun distance(state: State): Double
    public fun isGoal(state: State): Boolean
    public fun print(state: State): String
    public fun randomState(): State
}

data class SuccessorBundle(val state: State, val action: Action?, val actionCost: Double)

