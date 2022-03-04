package com.example.broadcastsapp.classes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.broadcastsapp.R
import java.util.concurrent.atomic.AtomicInteger

class NotificationID private constructor(){
    //Clase singleton para crear un ID incremental para las notificaciones que vamos mostrando, así vemos
    //como se van encolando los servicios (vemos una notificacion cada 4 segundos) o si se
    //ejecutan en paralelo (vemos más de 1 notificación en simultáneo)

    //Una variable tipo AtomicInteger tiene un método para incrementar en 1 su valor y devolver ese
    //nuevo número
    private val unAtomicInt: AtomicInteger = AtomicInteger(0)

    companion object{ //Companion indica atributos y métodos estáticos
        private var unNotifID: NotificationID? = null

        //Método estático que inicializa instancia o devuelva la instanciada anteriormente
        fun getInstance(): NotificationID{
            //Si unNotifID no es null, instanciarlo
            unNotifID = unNotifID?: NotificationID()
            return unNotifID!!
        }

        fun getNotification(context: Context, textContent: String?): Notification {
            //Creamos notificación
            val notification = NotificationCompat.Builder(context, "canal_id_001")
                .setContentTitle("Notificación")
                .setContentText(textContent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setProgress(
                    0,
                    0,
                    true
                ) //Esta configuración es para mostrar una barra que actúa como la ruedita de carga, que se vea que está corriendo algo pero sin mostrar el progreso
            return notification.build()
        }

        fun createMyNotifChannel(unNotificationManager: NotificationManager){
            //Creamos canal de notificación si es android oreo o superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Crear canal si no fue creado previamente y si es versión superior a Android 8.0 (Oreo)
                val unNotificationChannel = NotificationChannel(
                    "canal_id_001",
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

    fun getID(): Int {
        //Sube en 1 la variable unAtomicInt y devuelve ese valor
        return unAtomicInt.incrementAndGet()
    }
}
