package ir.shahabazimi.notification.classes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import ir.shahabazimi.notification.BuildConfig
import ir.shahabazimi.notification.databinding.RowNotificationBinding
import ir.shahabazimi.notification.models.NotificationModel
import java.io.File
import java.io.FileOutputStream
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

            b.root.setOnLongClickListener {
                if(it!=null)
                    shareImageUri(saveImage(screenShot(it)))
                true
            }

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

    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveImage(image: Bitmap): Uri {
        val imagesFolder = File(ctx.cacheDir, "images")
        return try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "${Date().time}.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(
                ctx, "ir.shahabazimi.notification.fileprovider", file
            )
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    private fun shareImageUri(uri: Uri) =
        ctx.startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/png"
        })



}