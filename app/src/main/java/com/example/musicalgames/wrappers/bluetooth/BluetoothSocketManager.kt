package com.example.musicalgames.wrappers.bluetooth

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.jvm.Throws

class BluetoothSocketManager {
    @Throws
    fun startListening(bluetoothSocket: BluetoothSocket, onMessage: (Int)->Unit, onError: (e:Exception)->Unit) {
        val inputStream = bluetoothSocket.inputStream
        val buffer = ByteArray(4)
        var bytes: Int
        val thread = Thread {
            try {
                while (true) {
                    val bytes = inputStream.read(buffer)
                    if (bytes != -1) {
                        onMessage(readMessage(buffer))
                    }
                }
            } catch (e: IOException) {
                onError(e)
            }
        }
        thread.start()
    }
    @Throws
    fun sendMessage(bluetoothSocket: BluetoothSocket, message: Int) {
        val outputStream = bluetoothSocket.outputStream
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(message).array()
        outputStream.write(bytes)
    }
    fun readMessage(buffer: ByteArray): Int {
        val byteBuffer = ByteBuffer.wrap(buffer)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return byteBuffer.int
    }
}