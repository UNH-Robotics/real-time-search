package edu.unh.cs.ai.realtimesearch.experiment

import edu.unh.cs.ai.realtimesearch.environment.Action
import edu.unh.cs.ai.realtimesearch.experiment.configuration.ExperimentConfiguration
import edu.unh.cs.ai.realtimesearch.experiment.configuration.ManualConfiguration
import groovy.json.JsonSlurper
import java.io.InputStream
import java.math.BigDecimal

data class ExperimentResult(val experimentConfiguration: ExperimentConfiguration?,
                            val expandedNodes: Int = 0,
                            val generatedNodes: Int = 0, val timeInMillis: Long = 0,
                            val actions: List<Action> = emptyList(),
                            val pathLength: Double? = null,
                            val errorMessage: String? = null
) {
    companion object {
        fun fromStream(stream: InputStream): ExperimentResult = fromMap(JsonSlurper().parse(stream) as Map<*,*>)
        fun fromString(string: String): ExperimentResult = fromMap(JsonSlurper().parseText(string) as Map<*,*>)

        fun fromMap(map: Map<*,*>): ExperimentResult {
            val experimentConfiguration = map["experimentConfiguration"]

//            val actions = map["actions"] as List<*>// TODO Can't currently convert strings to actions
//            val actionList: MutableList<String> = mutableListOf()
//            for (action in actions) {
//                actionList.add(action as String)
//            }

            return ExperimentResult(
                    if (experimentConfiguration == null) null else ManualConfiguration.fromMap(experimentConfiguration as Map<*,*>), // TODO depends on ManualConfiguration class
                    map["expandedNodes"] as Int,
                    map["generatedNodes"] as Int,
                    (map["timeInMillis"] as Int).toLong(),
                    /*actionList,*/
                    pathLength = (map["pathLength"] as BigDecimal?)?.toDouble(),
                    errorMessage = map["errorMessage"] as String?)
        }
    }
}