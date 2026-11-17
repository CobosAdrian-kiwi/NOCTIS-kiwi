package com.example.noctis_kiwi  // usa tu paquete

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdaptacionOcularActivity : AppCompatActivity() {

    private val TAG = "NOCTIS"
    private lateinit var tvTiempo: TextView
    private lateinit var etMinutos: EditText

    private var timer: CountDownTimer? = null
    private var corriendo = false
    private var finMs = 0L        // momento (elapsedRealtime) en que acaba
    private var restanteMs = 0L   // tiempo restante cuando está en pausa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptacion_ocular)
        Log.d(TAG, "onCreate")

        tvTiempo = findViewById(R.id.tvTiempo)
        etMinutos = findViewById(R.id.etMinutos)

        if (savedInstanceState != null) {
            corriendo = savedInstanceState.getBoolean("corriendo")
            finMs = savedInstanceState.getLong("finMs")
            restanteMs = savedInstanceState.getLong("restanteMs")
            if (corriendo) {
                arrancar(finMs - SystemClock.elapsedRealtime())
            } else if (restanteMs > 0) {
                mostrar(restanteMs)
            }
        }

        findViewById<Button>(R.id.btnIniciar).setOnClickListener {
            if (!corriendo) {
                val ms = if (restanteMs > 0) restanteMs
                else (etMinutos.text.toString().toLongOrNull() ?: 20L) * 60_000L
                arrancar(ms)
            }
        }
        findViewById<Button>(R.id.btnPausar).setOnClickListener {
            if (corriendo) {
                restanteMs = finMs - SystemClock.elapsedRealtime()
                timer?.cancel()
                corriendo = false
            }
        }
    }

    private fun arrancar(ms: Long) {
        timer?.cancel()
        finMs = SystemClock.elapsedRealtime() + ms
        corriendo = true
        timer = object : CountDownTimer(ms, 1000) {
            override fun onTick(restante: Long) = mostrar(restante)
            override fun onFinish() {
                corriendo = false
                restanteMs = 0
                mostrar(0)
            }
        }.start()
    }

    private fun mostrar(ms: Long) {
        val s = (ms / 1000).coerceAtLeast(0)
        tvTiempo.text = String.format("%02d:%02d", s / 60, s % 60)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("corriendo", corriendo)
        outState.putLong("finMs", finMs)
        outState.putLong("restanteMs", restanteMs)
    }

    override fun onStart()   { super.onStart();   Log.d(TAG, "onStart") }
    override fun onResume()  { super.onResume();  Log.d(TAG, "onResume") }
    override fun onPause()   { super.onPause();   Log.d(TAG, "onPause") }
    override fun onStop()    { super.onStop();    Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        Log.d(TAG, "onDestroy")
    }
}