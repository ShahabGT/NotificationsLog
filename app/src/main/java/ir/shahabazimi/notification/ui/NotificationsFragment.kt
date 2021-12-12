package ir.shahabazimi.notification.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import ir.shahabazimi.notification.classes.NotificationAdapter
import ir.shahabazimi.notification.models.NotificationModel
import ir.shahabazimi.notification.models.SharedViewModel
import ir.shahabazimi.notification.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment() {

    private lateinit var b: FragmentNotificationsBinding
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
        notificationAdapter = NotificationAdapter(
            requireContext(),  Realm.getDefaultInstance().where<NotificationModel>()
                .equalTo(
                    "dateString",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(
                        Date()
                    )
                )
                .isNotEmpty("title")
                .sort("date", Sort.DESCENDING)
                .findAll()
        )
        b.notificationsRecycler.adapter = notificationAdapter

        viewModel.selectedDate.observe(viewLifecycleOwner) {
            notificationAdapter.updateData(
                Realm.getDefaultInstance().where<NotificationModel>().equalTo(
                    "dateString",
                    SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date(it))
                )
                    .isNotEmpty("title")
                    .sort("date", Sort.DESCENDING)
                    .findAll()


            )
            emptyDetector()
        }
        viewModel.filteredData.observe(viewLifecycleOwner){ visible->
            notificationAdapter.updateData( Realm.getDefaultInstance().where<NotificationModel>().`in`("appName",visible.toTypedArray()).findAll())
            emptyDetector()

        }
        emptyDetector()

    }

    private fun emptyDetector(){
        if(notificationAdapter.itemCount==0)
            b.notificationEmpty.visibility=View.VISIBLE
        else
            b.notificationEmpty.visibility=View.INVISIBLE
    }

}