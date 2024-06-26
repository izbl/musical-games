package com.example.musicalgames.games.chase.connection

import android.bluetooth.BluetoothDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R

class DeviceAdapter(private val devices: List<BluetoothDevice>, private val onItemClick: (BluetoothDevice)->Unit) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.deviceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        try {
            holder.deviceName.text = device.name ?: "Unknown Device"
            holder.itemView.setOnClickListener { onItemClick(device) }
        }catch (e: SecurityException) {
            Log.e("Bluetooth","Device Adapter Exception: $e")
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }
}
