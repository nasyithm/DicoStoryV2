package com.nasyithm.dicostoryv2.data

import android.content.Intent
import android.widget.RemoteViewsService
import com.nasyithm.dicostoryv2.widget.StackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext)
}