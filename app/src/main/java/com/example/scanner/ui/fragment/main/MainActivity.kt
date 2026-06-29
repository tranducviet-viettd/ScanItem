package com.example.scanner.ui.fragment.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.scanner.R
import com.example.scanner.data.db.repository.CloudinaryConfig

class MainActivity : AppCompatActivity() {

    private lateinit var mainProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainProgressBar=findViewById(R.id.main_progressBar)

        CloudinaryConfig.init(applicationContext)
        val navController = findNavController(R.id.nav_host_fragment)

    }
    fun showGlobalProgressBar(show: Boolean) {
        if (show) mainProgressBar.visibility = View.VISIBLE
        else mainProgressBar.visibility = View.GONE
    }
}