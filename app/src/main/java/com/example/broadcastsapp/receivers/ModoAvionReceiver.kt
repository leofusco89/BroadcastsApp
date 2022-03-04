package com.example.broadcastsapp.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.broadcastsapp.R

class ModoAvionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Esta clase puede llegar a ser utilizada para varios eventos (si es que fue registrado en
        //ellos previamente), así que chequeamos que el evento que llamó a esta instancia sea el del
        //modo avión para poder seguir:
        if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            //El evento que gatilló este receiver es el modo avión, pero se gatilla tanto en la
            //activación como desactivación, por eso, ahora chequeamos a través del intent si el
            //modo avión se activó o se desactivó para así mostrar un mensaje distinto para cada
            //caso. El nombre del dato que trae esta información se llama "state", lo sabemos
            //googleando la documentación (también pasamos defaultValue: false, por si no existe
            //el dato state, aunque sabemos que para evento modo avión, siempre existe):
            val modoAvionActivado = intent?.getBooleanExtra("state", false)
            var texto: String = "a"
            if (modoAvionActivado)
                texto = "Modo avión activado"
            else
                texto = "Modo avión desactivado"

            //Crear notificación
            val unNotifManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //Crear canal para Android 8.0 o superior
            createMyNotifChannel(unNotifManager)

            val unNotifBuilder = NotificationCompat.Builder(context, "Canal Modo Avión")
                .setContentTitle("Modo avión cambió")
                .setContentText(texto)
                .setSmallIcon(R.mipmap.ic_launcher_round)

            unNotifManager.notify(1, unNotifBuilder.build())
        }
    }

    fun createMyNotifChannel(unNotificationManager: NotificationManager) {
        //Creamos canal de notificación si es android oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Crear canal si no fue creado previamente y si es versión superior a Android 8.0 (Oreo)
            val unNotificationChannel = NotificationChannel(
                "Canal Modo Avión",
                "Canal para nuestra notificación",
                NotificationManager.IMPORTANCE_HIGH
            )
            unNotificationChannel.description = "Descripción del canal"
            unNotificationChannel.lockscreenVisibility =
                NotificationCompat.VISIBILITY_PRIVATE //Para este canal, en pantalla de bloqueo, solo muestra título e ícono
            unNotificationManager.createNotificationChannel(unNotificationChannel)
        }
    }
}
