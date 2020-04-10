package data.io.model

//TODO adicioanr logs em um arquivo validando operações realizadas
data class Log(val tag: String, val message: String) {
    override fun toString(): String = "$tag: $message"
}