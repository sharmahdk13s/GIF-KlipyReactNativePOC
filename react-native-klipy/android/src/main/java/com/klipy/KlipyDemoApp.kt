package com.klipy

import android.app.Application
import com.klipy.data.di.dataModule
import com.klipy.data.di.networkModule
import com.klipy.presentation.di.appModule
import com.klipy.di.initKoin
import org.koin.core.context.startKoin

class KlipyDemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}