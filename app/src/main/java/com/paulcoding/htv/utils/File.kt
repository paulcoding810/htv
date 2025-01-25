package com.paulcoding.htv.utils

import android.content.Context
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun Context.readFile(filePath: String): String {
    val file = if (filePath.startsWith("/")) {
        File(filePath)
    } else {
        File(filesDir, filePath)
    }
    file.inputStream().use { inputStream ->
        val sb = StringBuilder()
        BufferedReader(InputStreamReader(inputStream)).use { br ->
            var sCurrentLine: String?
            while ((br.readLine().also { sCurrentLine = it }) != null) {
                sb.append(sCurrentLine).append(System.lineSeparator())
            }
            return sb.toString()
        }
    }
}

inline fun <reified T> Context.readJSONFile(filePath: String): T {
    val data = readFile(filePath)
    return Gson().fromJson(data, T::class.java)
}