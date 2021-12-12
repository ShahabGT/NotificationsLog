package ir.shahabazimi.notification.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class NotificationModel(
    var key: String = "",
    var packageName: String = "",
    var appName: String = "",
    var title: String = "",
    var text: String = "",
    var date: Long = 0L,
    var dateString: String = ""
) : RealmObject()