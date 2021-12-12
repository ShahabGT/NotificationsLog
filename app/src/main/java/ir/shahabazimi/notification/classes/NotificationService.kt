package ir.shahabazimi.notification.classes

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.realm.Realm
import ir.shahabazimi.notification.models.NotificationModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            Realm.getDefaultInstance().executeTransaction {
                it.insert(
                    NotificationModel(
                        sbn.key,
                        sbn.packageName,
                        packageManager.getApplicationLabel(
                            packageManager.getApplicationInfo(
                                sbn.packageName,
                                PackageManager.GET_META_DATA
                            )
                        ).toString(),
                        sbn.notification.extras.getString("android.title", ""),
                        sbn.notification.extras.getString("android.text", ""),
                        sbn.postTime,
                        SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(sbn.postTime)

                    )
                )
            }
        }
    }


}