package com.example.progressbarservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DownloadService: Service() {

    private val mLocalBinder = LocalBinder()
    private var download = 0
    private var isDownloading = false

    inner class LocalBinder: Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mLocalBinder
    }

    fun startDownloading() {
        isDownloading = true
        Thread {
            while (isDownloading && download < 100) {
                download++
                Thread.sleep(500)
            }
            isDownloading = false
        }.start()
    }

    fun getProgress(): Int {
        return download
    }

    override fun onDestroy() {
        super.onDestroy()
        isDownloading = false
    }
}