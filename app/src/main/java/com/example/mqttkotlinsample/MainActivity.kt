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
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random


class MainActivity : AppCompatActivity(),SensorEventListener{

    private val TAG = "Sensori:";

    private lateinit var mqttClient : MQTTClient




    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))




        val manager = getSystemService(SENSOR_SERVICE) as SensorManager
        val linearAcc = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        val giroscopio = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        val temperatura = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        manager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(this, temperatura, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Acceleratore lineare:  " + linearAcc.toString());
        Log.d(TAG, "Giroscopio:  " + giroscopio.toString());
        Log.d(TAG, "Temperatura:  " + temperatura.toString());





        mqttClient = MQTTClient(this,"tcp://broker.hivemq.com:1883","")
        mqttClient.connect("","")



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

    private fun heartRate(): Int{
        var heartRate =  Random.nextInt(97, 100)
        Log.d("MSG","Battito: " +  heartRate)
        return heartRate
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
                valori[0] = "%,.2f".format(Locale.ENGLISH, event.values[0]).toDouble()
                valori[1] = "%,.2f".format(Locale.ENGLISH, event.values[1]).toDouble()
                valori[2] = "%,.2f".format(Locale.ENGLISH, event.values[2]).toDouble()
                // Log.d(TAG, "onCreate: Battito: "+heartSensor());
                Log.d(
                    TAG,
                    "onSensorChanged: X: " + valori[0] + " Y: " + valori[1] + " Z: " + valori[2]
                )
                val message =
                    "{\"tmstp\":\"\",\"e\":[" +
                            "{\"n\":5702,\"v\":" + valori[0] + "}," +
                            "{\"n\":5703,\"v\":" + valori[1] + "}," +
                            "{\"n\":5704,\"v\":" + valori[2] + "}" +
                            "]} "
                mqttClient.publish("tele/CIOTR/3313/0", message)
                val message2 =
                    "{\"tmstp\":\"\",\"e\":[" +
                            "{\"n\":5500,\"v\":" + heartRate().toString() + "}" +
                            "]} "


                    mqttClient.publish("tele/CIOTR/3200/0", message2)



            }
        }
        //_________________________
        if (event!!.sensor.type === Sensor.TYPE_GYROSCOPE) {
            if (event!!.values[0] >= 1 || event.values[1] >= 1 || event.values[2] >= 1) {
                Log.d(
                    TAG,
                    "Giroscopio X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]
                )
                val message3="{\"tmstp\":\"\",\"e\":[{\"n\":5702,\"v\":" + valori[0] + "},{\"n\":5703,\"v\":" + valori[1] + "},{\"n\":5704,\"v\":" + valori[2] + "}]} "
                mqttClient.publish("tele/CIOTR/3334/0", message3)
            }
        }
        //__________________________
        if (event!!.sensor.type === Sensor.TYPE_AMBIENT_TEMPERATURE) {
            if (event!!.values[0] >= 1) {
                Log.d(TAG, "Temperatura: " + event.values[0])
                //mqttClient.publish("tele/CIOTR/3303/0", message4)
            }
        }
    }

    private val defaultCbPublish = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d(this.javaClass.name, "Message published to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            TODO("Not yet implemented")
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //Log.d(TAG,"Accuracy changed")
    }
}