package me.machao.silknavigator.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import me.machao.silknavigator.Navigator
import me.machao.silknavigator.anno.Route

@Route("main")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn0.setOnClickListener {
            Navigator.getInstance().from(this).to("login").go()
        }

        btn1.setOnClickListener{
            Navigator.getInstance().from(this).to("user/edit").go()
        }

    }

}
