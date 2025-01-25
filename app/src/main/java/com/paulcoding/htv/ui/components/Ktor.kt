package com.paulcoding.htv.ui.components

import com.google.gson.Strictness
import com.paulcoding.htv.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson

val ktorClient
    get() = HttpClient(Android) {
        install(ContentNegotiation) {
            gson {
                setStrictness(Strictness.LENIENT)
            }
        }
    }
