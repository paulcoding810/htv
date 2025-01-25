package com.paulcoding.htv.utils

fun log(
    message: Any?,
    tag: String? = "HTV",
) {
    val border = "*".repeat(150)
    println("\n")
    println(border)
    print("\t")
    println("$tag:")
    print("\t")
    println(message)
    println(border)
    println("\n")
}


fun <T> T.alsoLog(tag: String? = null): T {
    log(this, tag)
    return this
}
