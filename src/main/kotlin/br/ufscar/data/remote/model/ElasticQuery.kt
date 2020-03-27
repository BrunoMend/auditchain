package br.ufscar.data.remote.model

import br.ufscar.utils.toElasticDateFormat
import br.ufscar.utils.toFileName
import java.io.Serializable


data class ElasticQuery(val indexPattern: String,
                        val rangeParameter: String,
                        val startAt: Long,
                        val finishIn: Long,
                        val size: Int = 10000): Serializable {

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