package com.example.minisumoaj.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minisumoaj.databinding.ActivityDeviceBinding
import com.example.minisumoaj.module.ModuleBluetooth2
import com.example.minisumoaj.recycleView.DeviceAdapter

class DeviceActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDeviceBinding
    private lateinit var moduleBluetooth: ModuleBluetooth2
    var devicesBluetooth = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        moduleBluetooth = ModuleBluetooth2(this)
        getDevice()
        binding.recycleDevice.layoutManager = LinearLayoutManager(this)
        binding.recycleDevice.adapter = DeviceAdapter(devicesBluetooth){onClickDevice(it)}

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun onClickDevice(device:String){
        Toast.makeText(this, "Dispositivo $device se a seleccionado",Toast.LENGTH_SHORT).show()
    }

    private fun getDevice(){
        //Empezar la busqueda de dispositivos
        if(!moduleBluetooth.stateBluetoooth()){
            moduleBluetooth.initializeBluetooth()
        }else{
            devicesBluetooth = moduleBluetooth.deviceBluetooth()
            if(devicesBluetooth.isEmpty()){
                Toast.makeText(this, "No tienes dispositivos vinculados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}