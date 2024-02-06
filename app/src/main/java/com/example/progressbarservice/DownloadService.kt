package com.example.progressbarservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder

class DownloadService: Service() {

    private val mLocalBinder = LocalBinder()
    private var download = 0
    private var isDownloading = false
    private lateinit var sharedPreferences: SharedPreferences

    inner class LocalBinder: Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("DownloadProgress", Context.MODE_PRIVATE)
        download = sharedPreferences.getInt("progress", 0)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mLocalBinder
    }

    fun startDownloading() {
        isDownloading = true
        Thread {
            while (isDownloading && download <= 100) {
                download++
                saveProgress(download)
                Thread.sleep(500)
            }
            isDownloading = false
        }.start()
    }

    private fun saveProgress(progress: Int) {
        sharedPreferences.edit().putInt("progress", progress).apply()
    }

    fun getProgress(): Int {
        return download
    }

    override fun onDestroy() {
        super.onDestroy()
        isDownloading = false
    }
}