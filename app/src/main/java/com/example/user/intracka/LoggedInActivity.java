package com.example.user.intracka;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoggedInActivity extends AppCompatActivity {
    TextView welcomemsg;
    Button logoutbtn;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        String username = getIntent().getStringExtra("username");
        String status = getIntent().getStringExtra("access");
        db = new DatabaseHandler(this);

        welcomemsg= (TextView)findViewById(R.id.welcomemsg);
        logoutbtn = (Button)findViewById(R.id.logoutbtn);

        welcomemsg.setText("Welcome , " + username);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2/intrack/getAllUser.php",

        if(status.equals("online")){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.79/intrack/getAllUser.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            db.clearDb1();
                            try{
                                JSONObject obj = new JSONObject(response);
                                JSONArray arr = obj.getJSONArray("users");

                                for(int i=0;i<arr.length();i++){
                                    JSONObject obj1 = arr.getJSONObject(i);

//                                Log.d("user id",obj1.getString("usr_id"));
                                    User u = new User(obj1.getString("usr_id"),obj1.getString("site_id"),
                                            obj1.getString("username"),obj1.getString("password"));

                                    db.addRecord(u);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(LoggedInActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                            Toast.makeText(LoggedInActivity.this, "Please check your connection and try again", Toast.LENGTH_LONG).show();

                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dateobj = new Date();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        DateFormat t = new SimpleDateFormat("HH:mm:ss");
//        AccessHistory ah = new AccessHistory(username,df.format(dateobj),t.format(dateobj),,String.valueOf(telephonyManager.getDeviceId()));


    }
}
