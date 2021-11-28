package ir.shahabazimi.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.realm.Realm
import io.realm.kotlin.where
import java.text.SimpleDateFormat
import java.util.*

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            Realm.getDefaultInstance().executeTransactionAsync {
                val visible = it.where<NotificationModel>().equalTo("packageName",sbn.packageName).findFirst()?.visible ?: false

                it.insert(
                    NotificationModel(
                        sbn.key,
                        sbn.packageName,
                        sbn.notification.extras.getString("android.title", ""),
                        sbn.notification.extras.getString("android.text", ""),
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(sbn.postTime)),
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(sbn.postTime)),
                        visible
                    )
                )
            }
        }
    }


}