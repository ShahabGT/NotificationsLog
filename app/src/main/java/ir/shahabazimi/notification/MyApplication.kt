package ir.shahabazimi.notification

import android.app.Application
import com.google.android.material.color.DynamicColors
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("notification.realm")
            .schemaVersion(6)
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)
    }
}
