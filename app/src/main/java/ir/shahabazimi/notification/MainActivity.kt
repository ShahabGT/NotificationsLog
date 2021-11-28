package ir.shahabazimi.notification

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.shahabazimi.notification.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var db: Realm
    private var date by Delegates.observable("") { _, _, newValue ->
        b.mainDate.text = getString(R.string.main_date, newValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        init()
    }

    private fun init() {
        db = Realm.getDefaultInstance()
        date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        b.mainRecycler.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(
            this, db.where<NotificationModel>()
                .equalTo("visible", true)

                .equalTo(
                    "date",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(
                        Date()
                    )
                )
                .isNotEmpty("title")
                .sort("time",Sort.DESCENDING)
                .findAllAsync()
        )
        b.mainRecycler.adapter = adapter

        b.mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    b.mainFab.hide()
                } else {
                    b.mainFab.show()
                }
            }
        })

        onClicks()

    }

    private fun onClicks() {
        b.mainFab.setOnClickListener {
            selectDate()
        }

        b.mainMenu.setOnClickListener {
            val data = db.where<NotificationModel>().distinct("packageName").findAll()
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
                                                .findAll()?.setBoolean("visible",b)
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

    private fun selectDate() {

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()


        datePicker.addOnPositiveButtonClickListener {
            adapter.updateData(
                db.where<NotificationModel>().equalTo(
                    "date",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date(it))
                )
                    .equalTo("visible", true)
                    .isNotEmpty("title")
                    .sort("time",Sort.DESCENDING)
                    .findAllAsync()
            )
            date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(it)

        }

        datePicker.show(supportFragmentManager, null)
    }


}