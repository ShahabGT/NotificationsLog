package ir.shahabazimi.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.OrderedRealmCollection

class SharedViewModel : ViewModel() {

    private val mutableSelectedDate = MutableLiveData<Long>()
    val selectedDate: LiveData<Long> get() = mutableSelectedDate

    fun selectDate(date: Long?) {
        if (date != null)
            mutableSelectedDate.value = date
    }


    private val mutableFilterData = MutableLiveData<List<String>>()
    val filteredData: LiveData<List<String>> get() = mutableFilterData

    fun filterData(data: List<String>?) {
        if (data != null)
            mutableFilterData.value = data
    }
}