package data.file.model

//TODO save logs from operations
data class LogFM(val tag: String, val message: String) {
    override fun toString(): String = "$tag: $message"
}