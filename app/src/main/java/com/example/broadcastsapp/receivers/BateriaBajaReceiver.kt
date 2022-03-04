package com.example.broadcastsapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BateriaBajaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Texto de BateríaBajaReceiver", Toast.LENGTH_SHORT).show()
    }
}