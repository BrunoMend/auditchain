package br.ufscar.auditchain.data.ots

//import br.ufscar.auditchain.data.io.readObjectFromFile
//import br.ufscar.auditchain.data.io.writeObjectToFile
//import com.eternitywall.ots.DetachedTimestampFile
//import com.eternitywall.ots.OpenTimestamps
//import com.eternitywall.ots.op.OpSHA256
//import java.io.File
//import java.util.*
//
//
//fun stamp(filePath: String) {
//    val file = File(filePath)
//    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), file)
//    OpenTimestamps.stamp(detachedFile)
//    writeObjectToFile(detachedFile.serialize(), "$filePath.ots")
//    println(OpenTimestamps.info(detachedFile))
//}
//
//fun getInfo(filePath: String) {
//    val detachedFile = DetachedTimestampFile.deserialize(
//        readObjectFromFile(
//            filePath
//        ) as ByteArray)
//    println(OpenTimestamps.info(detachedFile))
//}
//
//fun verifyFile(originalFilePath: String, otsFilePath: String) {
//    val originalFile = File(originalFilePath)
//    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
//
//    val detachedFile: DetachedTimestampFile = DetachedTimestampFile.from(OpSHA256(), originalFile)
//    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
//
//    verifyStamp(detachedFile, detachedOts)
//}
//
//fun verifyStamp(detachedFile: DetachedTimestampFile, detachedOts: DetachedTimestampFile) {
//
//    if(OpenTimestamps.upgrade(detachedOts)) println("Timestamp upgraded")
//
////    println(OpenTimestamps.info(detachedOts))
//
//    val result = OpenTimestamps.verify(detachedOts,detachedFile)
//    if (result == null || result.isEmpty()) {
//        println("Pending or Bad attestation")
//    } else {
//        result.forEach{(k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}")}
//    }
//}
//
//fun verifyStamp(otsFilePath: String) {
//    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
//    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
//
//    if(OpenTimestamps.upgrade(detachedOts)) println("Timestamp upgraded")
//
////    println(OpenTimestamps.info(detachedOts))
//
//    val result = OpenTimestamps.verify(detachedOts.timestamp)
//    if (result == null || result.isEmpty()) {
//        println("Pending or Bad attestation")
//    } else {
//        result.forEach{(k, v) -> println("Success! $k attests data existed as of ${Date(v.timestamp * 1000)}")}
//    }
//}
//
//fun upgrade(otsFilePath: String){
//    val otsFileObj = readObjectFromFile(otsFilePath) as ByteArray
//    val detachedOts: DetachedTimestampFile = DetachedTimestampFile.deserialize(otsFileObj)
//    val changed = OpenTimestamps.upgrade(detachedOts)
//    if (!changed) {
//        println("Timestamp not upgraded")
//    } else {
//        writeObjectToFile(detachedOts.serialize(), otsFilePath)
//        println("Timestamp upgraded")
//    }
//}
