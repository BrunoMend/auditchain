package br.ufscar.auditchain.data.io.infrastructure

import java.io.*

class ObjectStorage {

    fun <T : Serializable> writeObject(filePathName: String, obj: T) {
        val fileOutputStream = FileOutputStream(filePathName)
        val objectOut = ObjectOutputStream(fileOutputStream)
        objectOut.writeObject(obj)
        objectOut.close()
        fileOutputStream.close()
    }

    fun <T : Serializable> readObject(filePathName: String): T {
        val fileInputStream = FileInputStream(filePathName)
        val objectIn = ObjectInputStream(fileInputStream)
        val result: T = objectIn.readObject() as? T ?: throw NullPointerException()
        objectIn.close()
        fileInputStream.close()
        return result
    }

}