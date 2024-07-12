package com.nokhyun.sensorexam

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nokhyun.sensorexam.ui.theme.SensorExamTheme
import kotlin.math.abs


class MainActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var isRunning = false
    private var prevStep = 0
    private var stepCount = 0

    private var _stepValue: MutableState<String> = mutableStateOf("0")
    var stepValue: State<String> = _stepValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1001)
            }
        }

        sensorManager = getSystemService(SensorManager::class.java)

        setContent {
            SensorExamTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    StepCounterUI()
                }
            }
        }
    }

    @Composable
    fun StepCounterUI() {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "만보기",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "걸음수 : 24",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { _stepValue.value = "0" },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EA))
                ) {
                    Text(text = "RESET", color = Color.White)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensor?.let {
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val isIncrementStep = abs(event.values[0].toInt() - prevStep)
            prevStep = event.values?.get(0)?.toInt()!!
            log("abs(event.values[0].toInt() - totalStep): $isIncrementStep :: ${isIncrementStep == 1}")

            if (isRunning && isIncrementStep == 1) {
                _stepValue.value = (++stepCount).toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun log(msg: String) {
        Log.e(this.javaClass.simpleName, msg)
    }
}