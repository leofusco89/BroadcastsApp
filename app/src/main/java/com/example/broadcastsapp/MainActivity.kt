package com.example.broadcastsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.broadcastsapp.receivers.BateriaBajaReceiver
import com.example.broadcastsapp.services.TemporizadorService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var unBateriaBajaReceiver = BateriaBajaReceiver()
    private var unBateriaBajaFilter = IntentFilter()
    private val unTemporizadorReceiver = TemporizadorReceiver()
    private val unTemporizadorFilter = IntentFilter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Asociamos los botones a TemporizadorService en foreground y background
        btn_foreground.setOnClickListener {
            val intent = Intent(this, TemporizadorService::class.java)
            //Indicamos un valor de referencia para saber que es la ejecución foreground
            intent.putExtra("Foreground", true)

            //Servicio en foreground
            //Verificamos que la versión sea mayor a Android Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                //Si no podemos, lo mandamos en background
                startService(intent)
            }
            //Deshabilitamos el botón
            btn_foreground.isEnabled = false
        }

        //Registramos evento batería baja para el receiver BateriaBajaReceiver (NOTA: Si
        //registramos en onCreate, desregistramos en onDestroy, podemos también hacerlo con
        //onStart y onClose. La diferencia radica en cuando queremos que nuestro receiver escuche
        //el evento o no)
        unBateriaBajaFilter.addAction(Intent.ACTION_BATTERY_LOW)
        registerReceiver(unBateriaBajaReceiver, unBateriaBajaFilter)

        //Registramos evento local ACTION_SERVICE_FINISH: Traemos una instancia de
        //LocalBroadcastnManager y de ella, registramos el broadcast receiver y el filger
        unTemporizadorFilter.addAction(TemporizadorService.ACTION_SERVICE_FINISH)
        LocalBroadcastManager.getInstance(this).registerReceiver(unTemporizadorReceiver, unTemporizadorFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        //Desasociamos el broadcast receiver de sus eventos
        unregisterReceiver(unBateriaBajaReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unTemporizadorReceiver)
    }

    //Creamos broadcast interno para el evento local ACTION_SERVICE_FINISH de TemporizadorService
    inner class TemporizadorReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Servicio finalizado", Toast.LENGTH_SHORT).show()
            //Habilitamos botón para volver a iniciar el servicio
            btn_foreground.isEnabled = true
        }
    }
}
