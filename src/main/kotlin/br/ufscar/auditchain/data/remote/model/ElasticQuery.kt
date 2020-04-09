package br.ufscar.auditchain.data.remote.model

import br.ufscar.auditchain.common.Config
import br.ufscar.auditchain.common.utility.toElasticDateFormat
import br.ufscar.auditchain.data.io.utility.toFileName
import java.io.Serializable


data class ElasticQuery(val startAt: Long,
                        val finishIn: Long,
                        val indexPattern: String = Config.indexPattern,
                        val rangeParameter: String = Config.rangeParameter,
                        val size: Int = Config.resultMaxSize): Serializable {

    val query: String
        get() = "$rangeParameter:[${startAt.toElasticDateFormat()} TO ${finishIn.toElasticDateFormat()}]"

    override fun toString(): String = "$indexPattern($rangeParameter[${startAt}TO${finishIn}])".toFileName()
    
    companion object {
        fun getRequestContent(responseBody: String?): String? {
            return if(responseBody.isNullOrEmpty()) null
            else {
                val initLogs = "\"hits\":["
                responseBody.substring((responseBody.indexOf(initLogs) + initLogs.length - 1), responseBody.lastIndexOf("]") + 1)
            }
        }
    }

}