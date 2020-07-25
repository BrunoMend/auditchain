package exception

class DataNotMatchOriginalException(val originalData: ByteArray, val otsData: ByteArray) : Exception()