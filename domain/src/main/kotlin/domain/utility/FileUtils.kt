package domain.utility

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
        .plus(".$fileExtension")