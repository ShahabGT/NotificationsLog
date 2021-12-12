package ir.shahabazimi.notification.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.shahabazimi.notification.NotificationModel
import ir.shahabazimi.notification.R
import ir.shahabazimi.notification.SharedViewModel
import ir.shahabazimi.notification.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var db: Realm
    private var date by Delegates.observable("") { _, _, newValue ->
      b.topAppBar.title = getString(R.string.main_date, newValue)
    }
    private val viewModel: SharedViewModel by viewModels()
    private val apps= mutableListOf<Pair<String,Boolean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        init()
    }

    private fun init() {
        date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())

        db = Realm.getDefaultInstance()

        apps.clear()

        db.copyFromRealm(db.where<NotificationModel>().distinct("packageName").sort("appName",Sort.DESCENDING).findAll()).forEach {
            apps.add(Pair(it.appName,true))
        }

        onClicks()

    }

    private fun onClicks(){
        b.mainFab.setOnClickListener { selectDate() }

        b.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appbar_menu_filter -> {
                    filter()
                    true
                }

                R.id.appbar_menu_date->{
                    selectDate()
                    true
                }
                else -> false
            }


        }
    }

    private fun selectDate() {

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()


        datePicker.addOnPositiveButtonClickListener {
            viewModel.selectDate(it)
            date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it)
        }

        datePicker.show(supportFragmentManager, null)
    }



    private fun filter() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle("Select Apps")
            .setMultiChoiceItems(
                apps.map { it.first }.toTypedArray(),
                apps.map { it.second }.toBooleanArray()
            ) { _, index, selected ->
                apps[index]=apps[index].copy(second = selected)
            }
            .setPositiveButton(
                "done"
            ) { dialog, _ ->
                val visible = mutableListOf<String>()
                apps.forEach {
                    if(it.second)
                        visible.add(it.first)
                }
                viewModel.filterData(visible)
                dialog.dismiss()
            }

        dialog.show()
    }


}