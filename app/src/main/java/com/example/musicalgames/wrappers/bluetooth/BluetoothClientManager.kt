package com.example.musicalgames.wrappers.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.core.content.ContextCompat.getSystemService
import com.example.musicalgames.R
import java.io.IOException
import java.util.UUID
import kotlin.jvm.Throws

class BluetoothClientManager(context: Context, activityResultRegistry: ActivityResultRegistry) :
    BluetoothConnectionManager(context, activityResultRegistry) {
    private var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket?=null
    private var bluetoothListener: BluetoothEventListener? = null
    private var socketManager = BluetoothSocketManager()
    private var receiverRegistered = false
    override fun bluetoothUnsubscribe() {
        bluetoothListener=null
    }

    override fun releaseResources() {
        try {
            if(bluetoothSocket!!.isConnected)
                bluetoothSocket?.close()
            else
                Log.e("Bluetooth", "socket not connected, cannot disconnect")
        } catch (e: IOException) {
            // Handle the exception, if necessary
        }
    }

    override fun connected(): Boolean {
        return (bluetoothSocket!=null && bluetoothSocket!!.isConnected)
    }
    override fun bluetoothSubscribe(listener: BluetoothEventListener) {
        this.bluetoothListener=listener
    }

    init {
        val bluetoothManager = getSystemService(context,BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
    }

    override fun sendMessage(i: Int) {
        if(connected())
            socketManager.sendMessage(bluetoothSocket!!, i)
        else
            Log.e("Bluetooth", "cannot send message, socket not connected")
    }
    private fun disconnect() {
        if(connected())
            bluetoothSocket?.close()
        else
            Log.e("Bluetooth", "cannot disconnect, not connected")
    }
    fun destroy() {
        disconnect()
        if(receiverRegistered)
            context.unregisterReceiver(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    @Throws
    fun discover() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        }
        //TODO: register the receiver elsewhere
        context.registerReceiver(bluetoothReceiver, filter)
        receiverRegistered=true

        // Start Bluetooth device discovery
        var started = bluetoothAdapter.startDiscovery()
        if(!started)
            Toast.makeText(context, "try manually turning on location", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    @Throws
    fun pair(device: BluetoothDevice) {
        if(connected())
            disconnect()
        if(isDevicePaired(device))
            return
        if (device.createBond()) {
        } else {
        }
    }
    @SuppressLint("MissingPermission")
    @Throws
    fun connectToServer(serverDevice: BluetoothDevice) {
        val connectThread = Thread {
            try {
                //should be a resource
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                bluetoothSocket = serverDevice.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket!!.connect()
                bluetoothListener?.onConnected()
                // Send a signal to the server to keep the connection alive
                socketManager.sendMessage(bluetoothSocket!!, R.integer.CONNECTED)

                socketManager.startListening(bluetoothSocket!!, { message ->
                    bluetoothListener?.onMessageReceived(message)
                }, {e->bluetoothListener?.onDisconnected(e)})
            } catch (e: IOException) {
                e.printStackTrace()
                bluetoothListener?.onDisconnected(e)
            }
        }
        connectThread.start()
    }

    @SuppressLint("MissingPermission")
    @Throws
    private fun isDevicePaired(deviceToCheck: BluetoothDevice): Boolean {
        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            if (device.address == deviceToCheck.address) {
                // Device is already paired
                return true
            }
        }
        // Device is not paired
        return false
    }

    // BroadcastReceiver for Bluetooth events
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    bluetoothListener?.onDeviceFound(device)

                }
            }
        }
    }
}