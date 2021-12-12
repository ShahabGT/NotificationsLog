package ir.shahabazimi.notification.classes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import ir.shahabazimi.notification.databinding.RowNotificationBinding
import ir.shahabazimi.notification.models.NotificationModel
import java.text.SimpleDateFormat
import java.util.*


class NotificationAdapter(
    private val ctx: Context,
    data: OrderedRealmCollection<NotificationModel>
) :
    RealmRecyclerViewAdapter<NotificationModel, NotificationAdapter.ViewHolder>(data, true) {


    inner class ViewHolder(private val b: RowNotificationBinding) :
        RecyclerView.ViewHolder(b.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: NotificationModel) {
            b.rowNotificationAppname.text = model.appName

            if (model.title.isEmpty())
                b.rowNotificationTitle.visibility=View.GONE
            else
                b.rowNotificationTitle.text = model.title

            if (model.text.isEmpty())
                b.rowNotificationText.visibility=View.GONE
            else
                b.rowNotificationText.text = model.text

            if (model.packageName.isNotEmpty())
                b.rowNotificationIcon.setImageDrawable(ctx.packageManager.getApplicationIcon(model.packageName))

            b.rowNotificationDate.text = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH).format(model.date)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null)
            h.bind(model)
    }
}