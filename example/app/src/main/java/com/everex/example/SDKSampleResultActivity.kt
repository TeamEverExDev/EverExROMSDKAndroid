package com.everex.example

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prestonk.aos.base.data.ROMTestResultWrapper
import com.prestonk.aos.base.data.ResultWrapper
import com.prestonk.aos.base.data.StaticTestResultWrapper

class SDKSampleResultActivity : ComponentActivity() {

    private val TAG = "SDKSampleResultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인텐트로부터 데이터 가져오기
        val itemsArray = intent.getParcelableArrayListExtra<Parcelable>("resultWrapper")
            ?.filterIsInstance<ResultWrapper>()
        setContent {
            if (itemsArray != null) {
                ResultList(itemsArray.toList())
            }
        }
    }

    private fun recordTypeString(result: ROMTestResultWrapper): String {
        return when (result.valueType) {
            ROMTestResultWrapper.ResultType.MAX -> "Maximum Angle"
            ROMTestResultWrapper.ResultType.MIN -> "Minimum Angle"
        }
    }

    private fun angleString(angle: Float?): String {
        return angle?.let { "$it 도" } ?: "Measure Failed"
    }

    @Composable
    fun ResultList(items: List<ResultWrapper>) {
        MaterialTheme {
            // 전체 화면에 맞게 설정
            Column {
                Text(text = "측정 결과", modifier = Modifier.padding(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items) { item ->
                        ListCell(item = item)
                    }
                }
            }
        }
    }

    @Composable
    fun ListCell(item: ResultWrapper) {

        if (item is ROMTestResultWrapper) {
            Column {
                Text(
                    text = item.name,
                    modifier = Modifier.padding(16.dp)
                )

                Row {

                    Text(
                        text = recordTypeString(item),
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    Text(
                        text = angleString(item.angleInDegree),
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    )
                }

                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),  // 전체 너비로 구분선이 적용됨
                    color = Color.Gray,  // 색상 설정
                    thickness = 1.dp     // 두께 설정
                )
            }
        } else if (item is StaticTestResultWrapper) {
            for ((key, value) in item.angleInDegree) {

                Column {
                    Row {
                        Text(
                            text = key,
                            modifier = Modifier.padding(16.dp)
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Text(
                            text = value.toString(),
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),  // 전체 너비로 구분선이 적용됨
                color = Color.Gray,  // 색상 설정
                thickness = 1.dp     // 두께 설정
            )

        }
    }
}
