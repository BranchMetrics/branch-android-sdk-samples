package com.example.branchandroidtestsampleapp

import android.app.Application
import android.content.Context
import android.util.Log
import io.branch.referral.Branch

class CustomApplicationClass : Application () {
    override fun onCreate() {
        super.onCreate()
        // Delay session initialization
        Branch.expectDelayedSessionInitialization(true)

        // Branch object initialization
        Branch.getAutoInstance(this)

        val sharedPrefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val firstOpen = sharedPrefs.getBoolean("firstOpen", true)
        if (firstOpen) {
            Branch.getInstance().disableTracking(true);
            Log.i("CustomApplicationClass","Tracking disabled")
        }

        Branch.enableLogging()

    }
}