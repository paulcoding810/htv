package com.paulcoding.htv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.paulcoding.androidtools.AndroidTools
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class HTVApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        AndroidTools.initialize(this)

        startKoin {
            androidContext(this@HTVApp)
            modules(appModules)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}

val appModules = module {
    single {
        androidContext().applicationContext
    }
}