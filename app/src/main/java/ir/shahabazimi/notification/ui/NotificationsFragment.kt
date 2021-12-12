package ir.shahabazimi.notification.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.shahabazimi.notification.NotificationAdapter
import ir.shahabazimi.notification.NotificationModel
import ir.shahabazimi.notification.SharedViewModel
import ir.shahabazimi.notification.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment() {

    private lateinit var b: FragmentNotificationsBinding
    private lateinit var db: Realm
    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentNotificationsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        db = Realm.getDefaultInstance()
        notificationAdapter = NotificationAdapter(
            requireContext(), db.where<NotificationModel>()
                .equalTo(
                    "date",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(
                        Date()
                    )
                )
                .isNotEmpty("title")
                .sort("time", Sort.DESCENDING)
                .findAll()
        )
        b.notificationsRecycler.apply {
            adapter = notificationAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        //      b.mainFab.hide()
                    } else {
                        //    b.mainFab.show()
                    }
                }
            })
        }

        b.notificationsRecycler.adapter

        viewModel.selectedDate.observe(viewLifecycleOwner) {
            notificationAdapter.updateData(
                db.where<NotificationModel>().equalTo(
                    "date",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date(it))
                )
                    .isNotEmpty("title")
                    .sort("time", Sort.DESCENDING)
                    .findAll()


            )
            emptyDetector()
        }
        viewModel.filteredData.observe(viewLifecycleOwner){ visible->
            notificationAdapter.updateData(db.where<NotificationModel>().`in`("appName",visible.toTypedArray()).findAll())
            emptyDetector()

        }
        emptyDetector()

        onClicks()

    }

    private fun emptyDetector(){
        if(notificationAdapter.itemCount==0)
            b.notificationEmpty.visibility=View.VISIBLE
        else
            b.notificationEmpty.visibility=View.INVISIBLE
    }

    private fun onClicks() {

    }


}