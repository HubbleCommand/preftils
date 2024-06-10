package com.hubble.preftilsapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hubble.preftils.Preference
import com.hubble.preftils.get
import com.hubble.preftils.put


class MainActivity : AppCompatActivity() {

    object Preferences {
        val SAMPLE = Preference("sample", 10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val preferences = this.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        findViewById<Button>(R.id.button_set).setOnClickListener {
            try {
                val value = findViewById<EditText>(R.id.et_value).text.toString().toInt()
                with (preferences.edit()) {
                    put(Preferences.SAMPLE, value)
                    apply()
                }
            } catch (_: Exception) { }
        }

        findViewById<Button>(R.id.button_get).setOnClickListener {
            val set = preferences.get(Preferences.SAMPLE)
            findViewById<TextView>(R.id.tv_value).text = set.toString()
        }
    }
}
