package com.example.minisumoaj.module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread

class ModuleBluetooth2(private val context:Context){

    val state = MutableLiveData<Connected>()

    companion object {
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 9999
    }
    enum class Connected {
        False, Pending, True,Disconnect
    }

    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val btAdapter =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private var requiredPermissions = listOf<String>()
    private var permisosAdmitidos = false

    private var msOuStream: OutputStream? = null
    private var btSocket: BluetoothSocket? = null

    fun onBluetooth() {
        if (!permisosAdmitidos) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                verifyPermission()
            } else {
                initializeBluetooth()
            }
        } else {
            initializeBluetooth()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun verifyPermission() {
        requiredPermissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        }

        val missingPermissions = requiredPermissions.filter { permission ->
            context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            initializeBluetooth()
        } else {
            (context as Activity).requestPermissions(
                missingPermissions.toTypedArray(),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun initializeBluetooth() {
        (context as Activity).startActivityForResult(
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            100
        )
    }

    fun stateBluetoooth() = btAdapter.isEnabled

    fun checkPermissions(requestCode: Int, grantResults: IntArray): Boolean {
        return when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.none { it != PackageManager.PERMISSION_GRANTED }) {
                    permisosAdmitidos = true
                    // all permissions are granted
                    initializeBluetooth()
                    return true
                } else {
                    permisosAdmitidos = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        (context as Activity).requestPermissions(
                            requiredPermissions.toTypedArray(),
                            BLUETOOTH_PERMISSION_REQUEST_CODE
                        )
                    }
                    return false
                }
            }
            else -> false
        }

    }

    fun deviceBluetooth(): ArrayList<String> {
        val pairedDevices = btAdapter.bondedDevices
        val arrayListDevice = ArrayList<String>()
        for (i in pairedDevices) {
            arrayListDevice.add(i.name + "\n" + i.address)
        }
        return arrayListDevice
    }

    fun connect(address: String){
        val dirAddres = address.subSequence(address.length - 17, address.length).toString()
        println("dirAddres: $dirAddres")
        val device = btAdapter.getRemoteDevice(dirAddres)
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
        //cancela el proceso de deteccion de dispositvos actual
        btAdapter.cancelDiscovery()
        thread(start = true){
            state.postValue(Connected.Pending)
            try {
                btSocket!!.connect()
                connectThread()
                state.postValue(Connected.True)
            }catch(e:Exception){
                state.postValue(Connected.False)
            }
        }

    }

    private fun connectThread() {
        var datosOut: OutputStream? = null
        try {
            datosOut = btSocket!!.outputStream
        } catch (var6: IOException) {
        }
        msOuStream = datosOut
    }

    fun bluTx(message: String): Boolean {
        return try {
            btSocket!!.outputStream.write(message.toByteArray())
            true
        } catch (e: Exception) {
            btSocket!!.close()
            false
        }
    }

    fun closeConnection() {
        if (btSocket != null) {
            try {
                btSocket!!.close()
                state.postValue(Connected.Disconnect)
            }
            catch (e: Exception) {}
            btSocket = null
        }
    }
}