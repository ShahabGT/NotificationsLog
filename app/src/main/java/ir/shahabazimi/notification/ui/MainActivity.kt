package ir.shahabazimi.notification.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.shahabazimi.notification.models.NotificationModel
import ir.shahabazimi.notification.R
import ir.shahabazimi.notification.models.SharedViewModel
import ir.shahabazimi.notification.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private var selectedDate by Delegates.observable(0L) { _, _, newValue ->
        b.topAppBar.title =  SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(newValue)
    }
    private val viewModel: SharedViewModel by viewModels()
    private val apps = mutableListOf<Pair<String, Boolean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        init()
    }

    private fun init() {
        selectedDate= System.currentTimeMillis()
        visibleApps()
        onClicks()

    }

    private fun visibleApps(){
        apps.clear()

        Realm.getDefaultInstance().where<NotificationModel>().distinct("packageName")
            .equalTo("dateString",SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(selectedDate))
            .sort("appName", Sort.DESCENDING)
            .findAll().forEach {
                apps.add(Pair(it.appName, true))
            }
    }

    override fun onResume() {
        super.onResume()
        checkForPermission()
    }

    private fun onClicks() {
        b.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appbar_menu_filter -> {
                    filter()
                    true
                }

                R.id.appbar_menu_date -> {
                    selectDate()
                    true
                }
                else -> false
            }
        }
    }

    private fun selectDate() {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(
                    DateValidatorPointBackward.now())
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.appbar_menu_date)
                .setSelection(selectedDate)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()


        datePicker.addOnPositiveButtonClickListener {
            viewModel.selectDate(it)
            selectedDate=it
            visibleApps()
        }

        datePicker.show(supportFragmentManager, null)
    }


    private fun filter() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle(R.string.app_select_dialog_title)
            .setMultiChoiceItems(
                apps.map { it.first }.toTypedArray(),
                apps.map { it.second }.toBooleanArray()
            ) { _, index, selected ->
                apps[index] = apps[index].copy(second = selected)
            }
            .setPositiveButton(
                R.string.dialog_done
            ) { dialog, _ ->
                val visible = mutableListOf<String>()
                apps.forEach {
                    if (it.second)
                        visible.add(it.first)
                }
                viewModel.filterData(visible)
                dialog.dismiss()
            }
        if (!apps.isNullOrEmpty())
            dialog.show()
        else
            Toast.makeText(this, R.string.notification_empty_title, Toast.LENGTH_SHORT).show()
    }


    private fun checkForPermission() {
        val res = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        if (!res)
            notificationDialog()
    }

    private fun notificationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.notification_dialog_title)
            .setCancelable(false)
            .setMessage(R.string.notification_dialog_message)
            .setPositiveButton(resources.getString(R.string.dialog_allow)) { _, _ ->
                askForPermission()
            }
            .show()
    }

    private fun askForPermission() {
        startActivity(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            else
                Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        )

    }


}