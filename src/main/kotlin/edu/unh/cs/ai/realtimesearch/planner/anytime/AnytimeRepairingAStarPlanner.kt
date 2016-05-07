package edu.unh.cs.ai.realtimesearch.planner.anytime

import edu.unh.cs.ai.realtimesearch.environment.Action
import edu.unh.cs.ai.realtimesearch.environment.Domain
import edu.unh.cs.ai.realtimesearch.environment.State
import edu.unh.cs.ai.realtimesearch.planner.AnytimePlanner
import edu.unh.cs.ai.realtimesearch.util.resize
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.comparisons.compareBy

class AnytimeRepairingAStarPlanner<StateType : State<StateType>>(domain: Domain<StateType>) : AnytimePlanner<StateType>(domain) {

    private val logger = LoggerFactory.getLogger(AnytimeRepairingAStarPlanner::class.java)

    private var inflationFactor = 3.0
    private val openList: Queue<Node<StateType>> = PriorityQueue(compareBy<Node<StateType>> { inflatedFValue(it) })
    private val closedList: MutableMap<StateType, Node<StateType>> = hashMapOf()
    private val inconsistentStates: MutableList<Node<StateType>> = arrayListOf()
    private var goal: StateType? = null
    private var targetGoal: StateType? = null
    private var goalNode: Node<StateType>? = null
    private val allNodes: HashMap<StateType, Node<StateType>> = HashMap<StateType, Node<StateType>>(100000000, 1F).resize()
    private var iterationCount = 0

    data class Node<State>(var parent: Node<State>? = null, val state: State, var action: Action? = null, var cost: Double = 0.0, var iteration: Int)

    private fun improvePath() {
        // This is analogue to Likhachev's CLOSED list
        val localClosedList: MutableSet<StateType> = hashSetOf()

        while (goalCost() > inflatedFValue(openList.peek())) {
            val currentNode = openList.poll() ?: return // Return if the frontier is empty
            val currentState = currentNode.state

            localClosedList.add(currentState)
            expandedNodeCount++

            domain.predecessors(currentState).forEach {
                val predecessorNode = closedList[it.state]

                if (predecessorNode == null || predecessorNode.cost > currentNode.cost + it.actionCost) {
                    generatedNodeCount++
                    var updatedSuccessorNode = allNodes[it.state]
                    if (updatedSuccessorNode == null) {
                        updatedSuccessorNode = Node(currentNode, it.state, it.action, Double.POSITIVE_INFINITY, iterationCount)
                        allNodes[it.state] = updatedSuccessorNode
                    } else if (updatedSuccessorNode.iteration == iterationCount
                            || (updatedSuccessorNode.iteration < iterationCount && updatedSuccessorNode.cost > currentNode.cost + it.actionCost)) {
                        updatedSuccessorNode.action = it.action
                        updatedSuccessorNode.parent = currentNode
                        updatedSuccessorNode.cost = currentNode.cost + it.actionCost
                        updatedSuccessorNode.iteration = iterationCount
                        allNodes[it.state] = updatedSuccessorNode
                    }

                    closedList[it.state] = updatedSuccessorNode

                    if (targetGoal!!.equals(it.state)) {
                        goal = it.state
                        goalNode = updatedSuccessorNode
                    }

                    if (localClosedList.contains(it.state)) {
                        inconsistentStates.add(updatedSuccessorNode)
                    } else {
                        openList.add(updatedSuccessorNode)
                    }
                }
            }
        }
    }

    fun solve(startState: StateType, goalStates: List<StateType>): List<Action?> {
        //Solving backwards, so flip start and goal states
        targetGoal = startState

        for (goalState in goalStates) {
            var tempNode = allNodes[goalState]

            if (tempNode == null) {
                tempNode = Node(state = goalState, iteration = iterationCount)
                allNodes[goalState] = tempNode
            } else {
                tempNode.parent = null
                tempNode.action = null
                tempNode.cost = 0.0
                tempNode.iteration = iterationCount
                allNodes[goalState] = tempNode
            }

            closedList[goalState] = tempNode
            openList.add(closedList[goalState])
        }

        improvePath()

        iterationCount++
        val result: MutableList<Action?> = mutableListOf()
        var cur = goalNode
        while (cur != null) {
            result.add(cur.action)
            cur = cur.parent
        }

        return result
    }

    fun update(): Double {
        inflationFactor *= 100
        inflationFactor -= 2
        inflationFactor /= 100

        if (inflationFactor <= 1)
            return inflationFactor
        // Re-sort open list with new inflation factor by removing and re-adding all
        // TODO implement heap instead of using priority queue for more efficient re-sorting
        val tempOpen = openList.toMutableList()
        openList.clear()
        openList.addAll(tempOpen)
        openList.addAll(inconsistentStates)
        inconsistentStates.clear()
        closedList.clear()
        goal = null
        return inflationFactor
    }

    private fun fValue(state: StateType): Double {
        val node = closedList[state]!!
        return node.cost + domain.heuristic(node.state, targetGoal!!)
    }

    private fun inflatedFValue(node: Node<StateType>?): Double {
        if (node == null)
            return -1.0
        return node.cost + inflationFactor * domain.heuristic(node.state, targetGoal!!)
    }

    private fun goalCost(): Double {
        goal ?: return Double.POSITIVE_INFINITY
        return closedList[goal!!]!!.cost
    }
}