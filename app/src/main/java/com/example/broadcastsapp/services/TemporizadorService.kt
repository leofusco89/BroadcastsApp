package com.example.broadcastsapp.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.broadcastsapp.classes.NotificationID

class TemporizadorService : Service() {

    companion object {
        private const val TAG = "TemporizadorService" //La usamos para escribir en el log y así
        //poder ver los momentos en el que se llaman los callbacks (métodos abajo)

        //Creamos esta constante para llamar así al evento que vamos a crear
        const val ACTION_SERVICE_FINISH = "com.example.broadcastsapp.ACTION_SERVICE_FINISH"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ON CREATE")
    }

    override fun onBind(p0: Intent?): IBinder? {
        //No se va a llamar nunca porque estamos creando servicio started, retornamos null entonces
        //En Bound Service, lo asociamos a la activity (intent) que lo invoca
        Log.d(TAG, "ON BIND")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Acá se indica el servicio que iniciamos.
        //startId es el ID que representa a la instancia que se va a correr, es incremental
        //automáticamente. Sirve para luego, por ejemplo, finalizar esta instancia en particular.
        Log.d(TAG, "ON START COMMAND")

        //Obtiene la referencia del servicio del sistema que maneja las notificaciones
        val unNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Creamos canal si es necesario y no fue creado anteriormente
        NotificationID.createMyNotifChannel(unNotificationManager)

        //Recuperamos ID unívoco para la siguiente notificación
        val notifID: Int = NotificationID.getInstance().getID()

        //Es un servicio foreground, por lo tanto, llamamos al startForeground
        var textContent: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            textContent = "Servicio foreground Android > 8.0 N°$notifID"
        else
            textContent = "Servicio foreground Android < 8.0 N°$notifID"

        //El servicio primero se inicia en background desde el main con startForegroundService o
        //startService(), y apenas entra acá, se pasa a foreground con startForeground(), donde
        //efectivamente pasamos el servicio a primer plano
        startForeground(notifID, NotificationID.getNotification(this, textContent))

        //Creamos con clase anónima de Runnable, un thread que va a correr en el main thread
        Thread(
            Runnable {
                Log.d(TAG, "TAREA INICIADA")
                Thread.sleep(4000)
                Log.d(TAG, "TAREA FINALIZADA")

                //Eliminamos notificación
                unNotificationManager.cancel(notifID)

                //Como ya finalizó el trabajo del servicio, gatillamos evento ACTION_SERVICE_FINISH
                val unIntent = Intent(ACTION_SERVICE_FINISH)
                LocalBroadcastManager.getInstance(this).sendBroadcast(unIntent)

                stopSelf(startId)
            }
        ).start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "ON DESTROY")
        super.onDestroy()
    }
}
