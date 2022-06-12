package com.example.mqttkotlinsample

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class Sensori: AppCompatActivity(), SensorEventListener {
    private val TAG = "Sensori:";

    var Movimento = "";

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        val linearAcc = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //val stepCounter=manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        manager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Acceleratore lineare:  " + linearAcc.toString());
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val valori = DoubleArray(3)
        if (event!!.sensor.type === Sensor.TYPE_LINEAR_ACCELERATION) {
            if (event!!.values[0] >= 1 || event.values[1] >= 1 || event.values[2] >= 1) {
                valori[0] = event.values[0].toDouble().roundToInt() / 100.0
                valori[1] = event.values[1].toDouble().roundToInt() / 100.0
                valori[2] = event.values[2].toDouble().roundToInt() / 100.0
                // Log.d(TAG, "onCreate: Battito: "+heartSensor());
                Log.d(
                    TAG,
                    "onSensorChanged: X: " + valori[0] + " Y: " + valori[1] + " Z: " + valori[2]
                )
            }

            Movimento= " X: " + valori[0] + " Y: " + valori[1] + " Z: " + valori[2]

        }
    }

    fun sensorValue(): String{
        return Movimento;
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}