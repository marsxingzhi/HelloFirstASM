package com.mars.asm.time.library

import android.os.Looper
import android.util.Log

/**
 * Created by geyan on 2021/5/9
 */
object TimeManager {

    private const val TAG = "TimeManager"
    private const val DEFAULT_THRESHOLD = 10L

    fun timeMethod(method: String, cost: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()
            && cost >= threshold()
        ) {
            realTimeMethod(method, cost)
        }
    }

    fun timeMethod(className: String, method: String, cost: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()
            && cost >= threshold()
        ) {
            Log.e(TAG, "------ start >>> ------")
            Log.e(TAG, "the class's name: $className")
            Log.e(TAG, "the method's name: $method")
            Log.e(TAG, "the cost time: $cost ms")
            Log.e(TAG, "------ <<< end ------")
        }
    }

    private fun realTimeMethod(method: String, cost: Long) {
        Log.e(TAG, "$method cost $cost ms")
    }

    // 最小阈值
    private fun threshold(): Long {
        return DEFAULT_THRESHOLD
    }
}