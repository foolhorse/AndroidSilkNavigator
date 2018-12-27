package me.machao.silknavigator.sample

import android.app.Application
import android.content.Context
import android.widget.Toast
import me.machao.silknavigator.Navigator
import android.preference.PreferenceManager

/**
 * Date  2018/12/6
 * @author charliema
 */
class App : Application() {

    companion object {
        fun isLogin(c: Context): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(c)
            return preferences.getBoolean("isLogin", false)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Navigator.init(listOf("app","user"))
        addNavigatorInterceptors()
    }

    private fun addNavigatorInterceptors() {
        Navigator.getInstance().addInterceptor { source, destination ->
            if (!isLogin(this@App) && "user/edit" == destination) {
                Toast.makeText(source, "need login", Toast.LENGTH_SHORT).show()
                Navigator.getInstance().from(source).to("login").go()
                true
            } else {
                false
            }
        }


    }


}