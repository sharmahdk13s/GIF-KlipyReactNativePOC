package com.klipy.di

import android.app.Application
import com.klipy.data.di.dataModule
import com.klipy.data.di.networkModule
import com.klipy.presentation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

/**
 * Initializes Koin for the Klipy Android SDK.
 *
 * Host apps (like your React Native app) should call this once from their Application.onCreate().
 */
fun initKoin(app: Application) {
  // Avoid re-starting Koin if it's already running
  if (GlobalContext.getOrNull() != null) return

  startKoin {
    androidLogger()
    androidContext(app)
    modules(appModule, networkModule, dataModule)
  }
}
