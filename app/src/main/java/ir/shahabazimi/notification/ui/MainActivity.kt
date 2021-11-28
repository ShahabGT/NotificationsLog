package ir.shahabazimi.notification.ui

import android.content.pm.PackageManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        init()
    }

    private fun init() {
        date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())

        db = Realm.getDefaultInstance()

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
//            notificationAdapter.updateData(
//                db.where<NotificationModel>().equalTo(
//                    "date",
//                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date(it))
//                )
//                    .equalTo("visible", true)
//                    .isNotEmpty("title")
//                    .sort("time", Sort.DESCENDING)
//                    .findAllAsync()
//            )
            date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it)

        }

        datePicker.show(supportFragmentManager, null)
    }



    private fun filter() {
        val data = db.copyFromRealm(db.where<NotificationModel>().distinct("packageName").findAll())
        val selected = mutableListOf<Boolean>()
        val appsPackage = mutableListOf<String>()
        val apps = mutableListOf<String>()
        data.forEach {
            selected.add(it.visible)
            appsPackage.add(it.packageName)
            apps.add(
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(
                        it.packageName,
                        PackageManager.GET_META_DATA
                    )
                ).toString()
            )
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Select Apps")
            .setMultiChoiceItems(
                apps.toTypedArray(),
                selected.toBooleanArray()
            ) { _, i, b ->
                db.executeTransactionAsync { realm ->
                    realm.where<NotificationModel>().equalTo("packageName", appsPackage[i])
                        .findAll()?.setBoolean("visible", b)
                }
            }
            .setPositiveButton(
                "done"
            ) { dialog, _ ->

                dialog.dismiss()
            }

        dialog.show()
    }


}