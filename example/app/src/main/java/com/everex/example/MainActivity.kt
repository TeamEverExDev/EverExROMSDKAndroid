package com.everex.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prestonk.aos.base.data.MeasurePart

// 실질적으로 동작하지 않는 앱입니다.
class MainActivity  : ComponentActivity() {

    private val TAG = "MainActivity"

    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // PowerManager 초기화
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        // WakeLock 초기화 (PARTIAL_WAKE_LOCK을 사용)
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                MeasurePartList { selectedPart ->
                    Log.d(TAG, "onCreate: $selectedPart")
                    val intent =  Intent(this@MainActivity, SDKSampleDetailActivity :: class.java)
                    intent.putExtra("romSelect", selectedPart.name)

                    startActivity(intent)

                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        preventSleep() // Activity가 보이는 동안 절전 방지
    }

    override fun onPause() {
        super.onPause()
        allowSleep() // Activity가 사라지면 절전 허용
    }

    // 자동 절전 방지 시작
    private fun preventSleep() {
        if (!wakeLock.isHeld) {
            wakeLock.acquire() // WakeLock 획득
        }
    }

    // 자동 절전 방지 해제
    private fun allowSleep() {
        if (wakeLock.isHeld) {
            wakeLock.release() // WakeLock 해제
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurePartList(onMeasurePartSelected: (MeasurePart) -> Unit) {
    val measureParts = remember { MeasurePart.entries.toTypedArray() }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("EverEx AI SDK Sample(aar test)") },
            // Large title과 같은 속성은 MaterialTheme를 사용하여 설정할 수 있습니다.
        )

        LazyColumn {
            items(measureParts) { measurePart ->
                MeasurePartItem(measurePart, onMeasurePartSelected)
            }
        }
    }
}

@Composable
fun MeasurePartItem(measurePart: MeasurePart, onMeasurePartSelected: (MeasurePart) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMeasurePartSelected(measurePart) }
            .padding(16.dp)
    ) {
        BasicText(text = measurePart.exerciseName)
    }
}