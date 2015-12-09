package edu.unh.cs.ai.realtimesearch.domain.vacuumworld

import edu.unh.cs.ai.realtimesearch.domain.Domain
import edu.unh.cs.ai.realtimesearch.domain.State
import edu.unh.cs.ai.realtimesearch.domain.SuccessorBundle
import java.util.*

class VacuumWorld(val width: Int, val height: Int, val blockedCells: List<VacuumWorldState.Location>) : Domain {

    /**
     * @brief Domain interface
     */
    override fun successors(state: State): List<SuccessorBundle> {
        if (state is VacuumWorldState) {

            // to return
            val successors: MutableList<SuccessorBundle> = arrayListOf()

            VacuumWorldAction.values.forEach {
                val newLocation = state.agentLocation + it.getRelativeLocation()

                // add the legal movement actions
                if (it != VacuumWorldAction.VACUUM) {
                    if (isLegalLocation(newLocation)) {

                        successors.add(SuccessorBundle(
                                VacuumWorldState(newLocation, ArrayList(state.dirtyCells)),
                                it,
                                1.0 )) // all actions have cost of 1

                    }
                } else if (newLocation in state.dirtyCells) { // add legit vacuum action

                    successors.add(SuccessorBundle(
                            VacuumWorldState(newLocation, state.dirtyCells.filter { it == newLocation }),
                            it,
                            1.0 ))
                }
            }
            return successors
        }

        throw RuntimeException("Wrong state type provided to VacuumWorld")
    }

    /**
     * Returns whether location within boundaries and not a blocked cell.
     *
     * @param location the location to test
     * @return true if location is legal
     */
    fun isLegalLocation(location: VacuumWorldState.Location): Boolean {
        return (location.x < 0 || location.y < 0 || location.x >= width || location.y >= height) &&
                location !in blockedCells
    }

    /**
     * @TODO: document & implement
     */
    override fun heuristic(state: State): Double = .0

    /**
     * @TODO: document & implement
     */
    override fun distance(state: State): Double = .0

    /**
     * A state in vacuumworld is a goal state if there are no more dirty cells
     *
     * @param state: the state that is being checked on
     *
     * @return whether the state is a goal state
     */
    override fun isGoal(state: State): Boolean {
        if (state is VacuumWorldState) {
            return state.dirtyCells.isEmpty()
        }

        throw Throwable("VacuumWorld cannot handle any state other than actual VacuumWorldStates")
    }


}

