package com.example.minisumoaj.recycleView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.minisumoaj.database.DatabaseDevice
import com.example.minisumoaj.databinding.ItemDeviceBinding

class DeviceViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemDeviceBinding.bind(view)
    private val database = DatabaseDevice.preference
    fun render(device:String, onClickListener: (String) -> Unit){
        binding.itemDevice.text = device

        binding.itemDevice.setOnClickListener {
            database.saveDevice(device)
            onClickListener(device)
        }
    }
}