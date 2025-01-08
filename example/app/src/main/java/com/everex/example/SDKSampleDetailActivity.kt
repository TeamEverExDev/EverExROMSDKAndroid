package com.everex.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.everex.example.viewModel.SDKSampleDetailViewModel
import com.prestonk.aos.api.data.dto.ErrorStatus
import com.prestonk.aos.base.data.ElbowMeasure
import com.prestonk.aos.base.data.HipJointMeasure
import com.prestonk.aos.base.data.KneeMeasure
import com.prestonk.aos.base.data.Measurable
import com.prestonk.aos.base.data.MeasurePart
import com.prestonk.aos.base.data.NeckMeasure
import com.prestonk.aos.base.data.ROMTestResultWrapper
import com.prestonk.aos.base.data.ShoulderMeasure
import com.prestonk.aos.base.data.SpineMeasure
import com.prestonk.aos.base.data.StaticMeasure
import com.prestonk.aos.base.data.StaticTestResultWrapper
import com.prestonk.aos.base.data.UserInfo
import com.prestonk.aos.base.enterEverExROMSDK
import java.util.UUID

class SDKSampleDetailActivity : ComponentActivity() {

    private val TAG = "SDKSampleDetailActivity"

    private val viewModel : SDKSampleDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedRomName = intent.getStringExtra("romSelect")
        val selectRom = selectedRomName?.let { MeasurePart.valueOf(selectedRomName) }
        setContent {
            DetailView(rom = selectRom ?: MeasurePart.SHOULDER)
        }
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    fun selectDetailROM(rom : MeasurePart) : List<Measurable> {
        return when(rom) {
            MeasurePart.STATIC -> StaticMeasure.entries
            MeasurePart.SPINE -> SpineMeasure.entries
            MeasurePart.SHOULDER -> ShoulderMeasure.entries
            MeasurePart.KNEE -> KneeMeasure.entries
            MeasurePart.ELBOW -> ElbowMeasure.entries
            MeasurePart.NECK -> NeckMeasure.entries
            MeasurePart.HIP_JOINT -> HipJointMeasure.entries
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetailView(rom : MeasurePart) {

        viewModel.setDataSet(selectDetailROM(rom))

        val visibleIndices = remember { mutableStateListOf<Boolean>().apply { repeat(viewModel.dataSource.size) { add(false) } } }

        Column {
            TopAppBar(title = { Text(rom.exerciseName) })
            LazyColumn {
                items(viewModel.dataSource.indices.toList()) { index ->
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                // 클릭할 때 해당 인덱스의 가시성 상태를 토글
                                visibleIndices[index] = !visibleIndices[index]
                                viewModel.toggleSelection(viewModel.dataSource[index])
                            },
                        // ListItem에 필요한 요소들을 정의
                        headlineContent = {
                            ListItem(viewModel.dataSource[index]) // romDetail의 이름을 표시
                        }
                    )

                    // 해당 인덱스가 클릭되었을 때만 인덱스를 보여줌
                    if (visibleIndices[index]) {
                        val selectedValue = viewModel.selectedItem[viewModel.dataSource[index]]
                        Text(text = "Index: ${selectedValue ?: 0}", modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }

            Row {
                Button(onClick = { finish() }) {
                    Text("Back")
                }
                Button(onClick = {

                    val list = viewModel.selectedItem.entries.sortedBy { it.value }

                    val measureableList = mutableListOf<Measurable>()
                    for (entry in list) {
                        measureableList.add(entry.value - 1, entry.key)
                    }

                    val userInfo = UserInfo(
                        uid = UUID.randomUUID(),
                    )

                    // SDK에 최종적으로 값을 보냄
                    enterEverExROMSDK(this@SDKSampleDetailActivity, measureableList, userInfo) { resultList, error ->
                        // 측정 성공
                        if(resultList != null) {

                            val resultWrapperList = if(resultList[0] is ROMTestResultWrapper) {
                                resultList.filterIsInstance<ROMTestResultWrapper>()
                            } else {
                                resultList.filterIsInstance<StaticTestResultWrapper>()
                            }

                            val resultIntent = Intent(this@SDKSampleDetailActivity, SDKSampleResultActivity :: class.java)
                            resultIntent.putParcelableArrayListExtra("resultWrapper", ArrayList(resultWrapperList))
                            startActivity(resultIntent)
                            finish()
                        }

                        // 에러
                        if(error != null) {
                            when(error) {
                                ErrorStatus.UNAUTHORIZED_CLIENT -> {
                                    ErrorStatus.UNAUTHORIZED_CLIENT
                                }
                                ErrorStatus.INVALID_PARAMETER -> {
                                    ErrorStatus.INVALID_PARAMETER
                                }
                                ErrorStatus.NOT_FOUND_MEASURES -> {
                                    ErrorStatus.NOT_FOUND_MEASURES
                                }
                                ErrorStatus.SERVER_ERROR -> {
                                    ErrorStatus.SERVER_ERROR
                                }
                                ErrorStatus.UNKNOWN_ERROR -> {
                                    ErrorStatus.UNKNOWN_ERROR
                                }
                            }
                        }
                    }

                }) {
                    Text("Start")
                }
            }
        }
    }

    @Composable
    fun ListItem(item: Measurable) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BasicText(text = item.exerciseName)
        }
    }
}