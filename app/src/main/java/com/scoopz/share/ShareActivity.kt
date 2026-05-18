package com.scoopz.share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import java.io.File

class ShareActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check if the toggle is ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Toggle is OFF - Send user to settings
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                Toast.makeText(this, "Enable 'All Files' to use Scoopz", Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }

        // 2. If toggle is ON, write the link
        val url = intent?.getStringExtra(Intent.EXTRA_TEXT)
        if (!url.isNullOrEmpty() && "tiktok.com" in url) {
            try {
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloads, "links_inbox.txt")
                file.appendText("${url.trim()}\n")
                Toast.makeText(this, "✅ Queued", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        finish()
    }
}