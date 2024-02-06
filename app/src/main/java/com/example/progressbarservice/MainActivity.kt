package com.example.progressbarservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var mDownloadService: DownloadService
    private var isBound = false

    private lateinit var tvProgress : TextView
    private lateinit var progressBar : ProgressBar

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as DownloadService.LocalBinder
            mDownloadService = binder.getService()
            isBound = true
            mDownloadService.startDownloading()
            updateProgressBar()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart: Button = findViewById(R.id.btnStart)
        val btnStop: Button = findViewById(R.id.btnStop)
        tvProgress = findViewById(R.id.textViewProgress)
        progressBar = findViewById(R.id.progressBar)

        btnStart.setOnClickListener {
            Intent(this, DownloadService::class.java).also {
                bindService(it, connection, Context.BIND_AUTO_CREATE)
            }
        }

        btnStop.setOnClickListener {
            unbindService(connection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    fun updateProgressBar() {
        Thread {
            while (isBound && mDownloadService.getProgress() <= 100){
                val progress = mDownloadService.getProgress()
                runOnUiThread {
                    progressBar.progress = progress
                    tvProgress.text = "Progress: $progress %"
                }
                Thread.sleep(500)
            }
        }.start()
    }
}