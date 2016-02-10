package edu.unh.cs.ai.realtimesearch.experiment.configuration

object EmptyConfiguration : ExperimentConfiguration {
    override fun getDomainName(): String {
        throw UnsupportedOperationException()
    }

    override fun getRawDomain(): String {
        throw UnsupportedOperationException()
    }

    override fun getAlgorithmName(): String {
        throw UnsupportedOperationException()
    }

    override fun getNumberOfRuns(): Int {
        throw UnsupportedOperationException()
    }

    override fun getTerminationCheckerType(): String {
        throw UnsupportedOperationException()
    }

    override fun getTerminationCheckerParameter(): Int {
        throw UnsupportedOperationException()
    }
}