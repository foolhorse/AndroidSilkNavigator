package me.machao.silknavigator.sample.user.edit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import me.machao.silknavigator.anno.Route
import me.machao.silknavigator.sample.user.R

@Route("user/edit")
class UserEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit)
    }
}
