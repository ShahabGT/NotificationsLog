package ir.shahabazimi.notification

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("notification.realm")
            .schemaVersion(6)
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .migration { realm, oldVersion, newVersion ->
                if(newVersion>oldVersion)
                    realm.schema.get("NotificationModel")!!.addField("visible",Boolean::class.java)
            }
            .build()
        Realm.setDefaultConfiguration(config)
    }
}
