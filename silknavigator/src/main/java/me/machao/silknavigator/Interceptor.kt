package me.machao.silknavigator

import android.app.Activity

/**
 * Date  2018/12/7
 * @author charliema
 */
interface Interceptor {
    fun onIntercept(source: Activity, destination: String): Boolean
}