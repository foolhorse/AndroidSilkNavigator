package me.machao.silknavigator.sample

import android.app.Application
import android.widget.Toast
import me.machao.silknavigator.Navigator

/**
 * Date  2018/12/6
 * @author charliema
 */
class App : Application() {

    companion object {
        var isLogin: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        addNavigatorInterceptors()
    }

    private fun addNavigatorInterceptors() {
        Navigator.getInstance().addInterceptor { source, destination ->
            if (!isLogin && "user/edit" == destination) {
                Toast.makeText(source, "need login", Toast.LENGTH_SHORT).show()
                Navigator.getInstance().from(source).to("login").go()
                true
            } else {
                false
            }
        }


    }


}