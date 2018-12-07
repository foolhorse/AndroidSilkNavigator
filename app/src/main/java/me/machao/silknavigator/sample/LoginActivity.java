package me.machao.silknavigator.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import me.machao.silknavigator.Navigator;
import me.machao.silknavigator.anno.Route;


@Route("login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Switch swc = findViewById(R.id.swc);
        swc.setChecked(App.Companion.isLogin());
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.Companion.setLogin(isChecked);
            }
        });

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.Companion.getInstance().from(LoginActivity.this).to("user/edit").go();
            }
        });
    }
}
