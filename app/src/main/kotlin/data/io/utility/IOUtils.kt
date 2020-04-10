package data.io.utility

fun String.toFileName(fileExtension: String = ""): String =
    this.trim()
        .replace("\\", "")
        .replace("/", "")
        .replace(":", "")
        .replace("*", "")
        .replace("?", "")
        .replace("\"", "")
        .replace("<", "")
        .replace(">", "")
        .replace("|", "")
        .plus(if (fileExtension.isNotEmpty()) ".$fileExtension" else "")