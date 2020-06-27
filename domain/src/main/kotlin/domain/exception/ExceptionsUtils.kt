package domain.exception

val Throwable.className: String
    get() = this::class.qualifiedName ?: "java.rmi.UnexpectedException"