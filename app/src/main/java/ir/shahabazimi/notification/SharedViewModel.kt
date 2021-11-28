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
}