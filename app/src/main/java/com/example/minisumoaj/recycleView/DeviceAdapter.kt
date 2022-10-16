package com.example.minisumoaj.recycleView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minisumoaj.R

class DeviceAdapter(private val device:ArrayList<String>, private val onClickListener:(String) -> Unit): RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DeviceViewHolder(layoutInflater.inflate(R.layout.item_device,parent,false))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val item = device[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = device.size

}