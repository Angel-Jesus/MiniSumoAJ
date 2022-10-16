package com.example.minisumoaj.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.minisumoaj.database.DatabaseDevice
import com.example.minisumoaj.databinding.ActivityMainBinding
import com.example.minisumoaj.module.ModuleBluetooth2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var moduleBluetooth: ModuleBluetooth2
    var device = ""
    var stateT = false

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        hideSystemBars()

        device = DatabaseDevice.preference.getDevice()
        moduleBluetooth = ModuleBluetooth2(this)
        moduleBluetooth.onBluetooth()

        if(device != ""){
            conexion()
            moduleBluetooth.state.observe(this,{
                if(it.name == "Pending"){
                    stateT = false
                    Toast.makeText(this,"Conectando...",Toast.LENGTH_SHORT).show()
                }
                if(it.name == "False"){
                    stateT = false
                    moduleBluetooth.closeConnection()
                    Toast.makeText(this,"No se pudo conectar al dispositivo",Toast.LENGTH_SHORT).show()
                }
                if(it.name == "Disconnect"){
                    stateT = false
                    Toast.makeText(this,"Desconectado",Toast.LENGTH_SHORT).show()
                }
                if(it.name == "True"){
                    stateT = true
                    Toast.makeText(this,"Conectado",Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch{
                        while(stateT){
                            withContext(Dispatchers.IO){
                                stateButton()
                                delay(100)
                            }
                        }
                    }
                }
            })
        }

        binding.conectedDeviceAgain.setOnClickListener {
            conexion()
        }

        binding.btnSearch.setOnClickListener {
            moduleBluetooth.closeConnection()
            startActivity(Intent(this,DeviceActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun stateButton(){

        binding.btnUp.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    moduleBluetooth.bluTx("5")
                    v.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                }
                MotionEvent.ACTION_DOWN -> {
                    moduleBluetooth.bluTx("1")
                    v.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        binding.btnDown.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    moduleBluetooth.bluTx("5")
                    v.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                }
                MotionEvent.ACTION_DOWN -> {
                    moduleBluetooth.bluTx("2")
                    v.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        binding.btnRight.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    moduleBluetooth.bluTx("5")
                    v.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                }
                MotionEvent.ACTION_DOWN -> {
                    moduleBluetooth.bluTx("3")
                    v.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        binding.btnLeft.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    moduleBluetooth.bluTx("5")
                    v.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                }
                MotionEvent.ACTION_DOWN -> {
                    moduleBluetooth.bluTx("4")
                    v.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    private fun conexion() {
        Log.d("mensaje", device)
        moduleBluetooth.connect(device)
    }

    //Solicitar los permisos del bluetooth en el caso no se ha aceptado
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(moduleBluetooth.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()
            moduleBluetooth.initializeBluetooth()
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                moduleBluetooth.initializeBluetooth()
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private val onBackPressedCallBack = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            finish()
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        device = DatabaseDevice.preference.getDevice()
        if(device == ""){
            Toast.makeText(this,"Aun no ha seleccionado el dispositivo a usar",Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

}