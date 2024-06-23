package com.nasyithm.dicostoryv2.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import coil.request.ImageRequest
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.local.room.StoryDao
import com.nasyithm.dicostoryv2.data.local.room.StoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var dao: StoryDao
    private val mWidgetItems = ArrayList<Bitmap>()
    private val mAppWidgetName = ArrayList<String>()

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext).storyDao()
    }

    override fun onDataSetChanged() {
        val tokenIdentifier = Binder.clearCallingIdentity()
        runBlocking(Dispatchers.IO) {
            try {
                dao.getStories().take(10).forEach {
                    val bitmap = getImage(it.photoUrl)
                    mWidgetItems.add(bitmap)
                    mAppWidgetName.add(it.name)
                }
            } catch (_: Exception) {
                mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,
                    R.drawable.no_image
                ))
            }
        }
        Binder.restoreCallingIdentity(tokenIdentifier)
    }

    private suspend fun getImage(url: String) : Bitmap {
        val imageLoader = coil.ImageLoader(mContext)
        val request = ImageRequest.Builder(mContext)
            .data(url)
            .build()
        val drawable = imageLoader.execute(request).drawable
        return (drawable as BitmapDrawable).bitmap
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            DicoStoryWidget.EXTRA_ITEM to mAppWidgetName[position]
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}