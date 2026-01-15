package com.inavisys.navisdk.androiddemo

import android.app.Application
import com.inavisys.navisdk.core.NaviController

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        NaviController.applicationInitialize(applicationContext)
    }
}