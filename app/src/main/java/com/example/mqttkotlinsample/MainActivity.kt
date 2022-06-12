package com.example.mqttkotlinsample

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt
import kotlin.random.Random


class MainActivity : AppCompatActivity(),SensorEventListener{

    private val TAG = "Sensori:";


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        var heartRate =  Random.nextInt(97, 100)
        Log.d("MSG","Battito: " +  heartRate)

        val manager = getSystemService(SENSOR_SERVICE) as SensorManager
        val linearAcc = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //val stepCounter=manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        manager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Acceleratore lineare:  " + linearAcc.toString());
       // Log.d(TAG, "Contapassi: " + stepCounter.toString());

        /* Check if Internet connection is available */
        if (!isConnected()) {
            Log.d(this.javaClass.name, "Internet connection NOT available")

            Toast.makeText(
                applicationContext,
                "Internet connection NOT available",
                Toast.LENGTH_LONG
            ).show()
        }
    }






    private fun isConnected(): Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                result = when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                result = when (activeNetwork.type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return result
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
        }
       /* if (event!!.sensor.type === Sensor.TYPE_STEP_COUNTER) {
            var step = 0f
            if (event!!.values[0] >= 1 || event.values[1] >= 1 || event.values[2] >= 1) {
                step = event.values[0]
                Log.d(TAG, "onSensorChanged: X: $step")
            }
        }*/
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}