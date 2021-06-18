package ir.shahabazimi.notification

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter


class NotificationAdapter(
    private val ctx: Context,
    data: OrderedRealmCollection<NotificationModel>,
    private val actionDown: (String) -> Unit,
    private val actionUp: () -> Unit
) :
    RealmRecyclerViewAdapter<NotificationModel, NotificationAdapter.ViewHolder>(data, true) {


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.row_notification_icon)
        val title: MaterialTextView = v.findViewById(R.id.row_notification_title)
        val text: MaterialTextView = v.findViewById(R.id.row_notification_text)
        val date: MaterialTextView = v.findViewById(R.id.row_notification_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_notification, parent, false)
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null && model.title.isNotEmpty()) {
            h.title.text = model.title
            h.text.text = model.text
            h.text.visibility = if (model.text.isEmpty())
                View.GONE
            else
                View.VISIBLE
            if (model.packageName.isNotEmpty())
                h.icon.setImageDrawable(ctx.packageManager.getApplicationIcon(model.packageName))
            h.date.text = "${model.date} ${model.time}"


            h.itemView.setOnTouchListener { v, event ->
                if (event.action === MotionEvent.ACTION_DOWN) {
                    actionDown(model.key)
                } else {
                    actionUp()
                }
                true
            }
        }

    }
}