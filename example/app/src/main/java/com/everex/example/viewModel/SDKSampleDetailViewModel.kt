package com.everex.example.viewModel

import androidx.lifecycle.ViewModel
import com.prestonk.aos.base.data.Measurable

class SDKSampleDetailViewModel : ViewModel() {

    var dataSource : MutableList<Measurable> = mutableListOf()

    private val _selectedItems = mutableMapOf<Measurable, Int>()
    val selectedItem : Map<Measurable, Int>
        get() = _selectedItems

    fun toggleSelection(measure: Measurable) {
        if (_selectedItems.contains(measure)) {
            _selectedItems.filter { it.value > _selectedItems[measure]!! }.forEach {
                _selectedItems[it.key] = it.value - 1
            }
            _selectedItems.remove(measure)
        } else {
            _selectedItems[measure] = _selectedItems.size + 1
        }
    }

    fun setDataSet(dataSource : List<Measurable>) {
        this.dataSource = dataSource.toMutableList()
    }
}