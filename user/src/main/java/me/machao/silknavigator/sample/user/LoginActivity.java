package me.machao.silknavigator.sample.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        swc.setChecked(isLogin(this));
        swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLogin(LoginActivity.this, isChecked);
            }
        });

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.Companion.getInstance().from(LoginActivity.this).to("user/edit").go();
            }
        });
    }

    private boolean isLogin(Context c) {
        Log.e("machao.me", "c.getPackageName():" + c.getPackageName());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        return preferences.getBoolean("isLogin", false);
    }

    private void setLogin(Context c, boolean isLogin) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        preferences.edit().putBoolean("isLogin", isLogin).apply();
    }
}
