package com.paulcoding.htv

const val DIR = "scripts"


data class Config(
    val version: Int = 1,
    val blackList: String = "",
    val sites: List<Site> = emptyList()
)

data class Site(
    val baseUrl: String,
    val cssBlocks: List<String>,
    val intercepts: List<List<String>>,
    val name: String,
    val startupScripts: List<String>
) {
    val interceptMap: Map<String, String>
        get() = intercepts.associate {
            it[0] to it[1]
        }
}
