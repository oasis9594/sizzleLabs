package com.example.dell.testappkotlin

import android.app.Application

import com.kontakt.sdk.android.common.KontaktSDK

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        KontaktSDK.initialize(this)
    }
}