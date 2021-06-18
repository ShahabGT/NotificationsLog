package ir.shahabazimi.notification

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import io.realm.Realm
import io.realm.kotlin.where
import ir.shahabazimi.notification.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var adapter: NotificationAdapter
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
        date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date())
        b.mainRecycler.layoutManager = LinearLayoutManager(this)
        val dialog = DetailsFragment()
        adapter = NotificationAdapter(
            applicationContext, Realm.getDefaultInstance().where<NotificationModel>().equalTo(
                "date",
                SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(
                    Date()
                )
            )
                .distinct("key")
                .findAll(),
                {
                dialog.show(supportFragmentManager,"")
                },{dialog.dismiss()})
        b.mainRecycler.adapter = adapter

        b.mainRecycler.addOnScrollListener(object :RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy > 0){
                    b.mainDateCard.visibility= View.INVISIBLE
                    b.mainFab.hide()
                } else{
                    b.mainDateCard.visibility= View.VISIBLE
                    b.mainFab.show()
                }
            }
        })


        b.mainFab.setOnClickListener {
            selectDate()
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
                Realm.getDefaultInstance().where<NotificationModel>().equalTo(
                    "date",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(
                        Date(it)
                    )
                ).distinct("key").findAll()
            )
            date = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(it)

        }

        datePicker.show(supportFragmentManager, null)
    }


}