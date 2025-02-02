package com.paulcoding.htv

const val DIR = "scripts"


data class Config(
    val version: Int = 1,
    val blackList: String = "",
    val sites: List<Site> = emptyList()
)

data class Site(
    val baseUrl: String,
    val cssBlocks: List<String>? = emptyList(),
    val intercepts: List<List<String>>? = emptyList(),
    val name: String,
    val startupScripts: List<String>? = emptyList()
) {
    val interceptMap: Map<String, String>
        get() = if (intercepts.isNullOrEmpty()) emptyMap() else intercepts.associate {
            it[0] to it[1]
        }
}
