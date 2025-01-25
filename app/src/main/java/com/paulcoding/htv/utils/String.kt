package com.paulcoding.htv.utils

import java.io.ByteArrayInputStream

fun String.toInputStream(): ByteArrayInputStream {
    return ByteArrayInputStream(this.toByteArray())
}