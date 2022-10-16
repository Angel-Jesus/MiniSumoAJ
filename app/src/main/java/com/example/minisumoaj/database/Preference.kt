package com.example.minisumoaj.database

import android.content.Context

class Preference(context: Context) {
    private val KEY_DEVICE = "keyDevice"
    private val DEVICE = "deviceSelected"

    private val settingDevice = context.getSharedPreferences(KEY_DEVICE,0)

    fun saveDevice(device:String){
        settingDevice.edit().putString(DEVICE,device).apply()
    }

    fun getDevice():String{
        return settingDevice.getString(DEVICE,"").toString()
    }
}