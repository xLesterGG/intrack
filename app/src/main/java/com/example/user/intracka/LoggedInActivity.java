package com.example.user.intracka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoggedInActivity extends AppCompatActivity {
    TextView welcomemsg;
    Button logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        String username = getIntent().getStringExtra("username");

        welcomemsg= (TextView)findViewById(R.id.welcomemsg);
        logoutbtn = (Button)findViewById(R.id.logoutbtn);

        welcomemsg.setText("Welcome , " + username);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
