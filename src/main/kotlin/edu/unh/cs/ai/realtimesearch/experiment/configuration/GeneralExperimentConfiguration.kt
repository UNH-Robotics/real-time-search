package edu.unh.cs.ai.realtimesearch.experiment.configuration

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.json.JsonSlurper

@JsonSerialize(`as` = ExperimentData::class)
open class GeneralExperimentConfiguration(values: MutableMap<String, Any?> = hashMapOf<String, Any?>()) : ExperimentData(values) {
    constructor(domainName: String,
                rawDomain: String,
                algorithmName: String,
                terminationCheckerType: String,
                terminationCheckerParameter: Int) : this() {
        this.domainName = domainName
        this.rawDomain = rawDomain
        this.algorithmName = algorithmName
        this.terminationCheckerType = terminationCheckerType
        this.terminationCheckerParameter = terminationCheckerParameter
    }

    var domainName: String by valueStore
    var rawDomain: String by valueStore
    var algorithmName: String by valueStore
    var terminationCheckerType: String by valueStore
    var terminationCheckerParameter: Int by valueStore

    override fun contains(key: String) = valueStore.containsKey(key)
}