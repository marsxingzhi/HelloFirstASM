package com.mars.asm.time.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mars.asm.time.library.TimeInject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    @TimeInject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener {
            clickLogin()
        }
    }

    private fun clickLogin() {
        Log.e("gy", "clickLogin")
        doAction()
    }

    @TimeInject
    override fun onResume() {
        super.onResume()
        Thread.sleep(2000)
    }

    @TimeInject
    private fun doAction() {
        Thread.sleep(1000)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}