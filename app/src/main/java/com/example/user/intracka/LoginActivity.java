package com.example.user.intracka;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import im.delight.android.location.SimpleLocation;

public class LoginActivity extends AppCompatActivity {

    EditText siteid,username,password;
    Button loginbtn;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        siteid = (EditText)findViewById(R.id.siteid);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        db = new DatabaseHandler(this);


//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Date dateobj = new Date();
//
//        DateFormat t = new SimpleDateFormat("HH:mm:ss");
//
//        System.out.println(df.format(dateobj)+ "aaa") ;
//        System.out.println(t.format(dateobj)+ "bbb") ;


        loginbtn = (Button)findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String siteidV = siteid.getText().toString();
                final String usernameV = username.getText().toString();
                final String passwordV = password.getText().toString();


                if(siteidV.equals("")|| usernameV.equals("")|| passwordV.equals("")){
                    Toast.makeText(getApplicationContext(),"Please fill inn all required details",Toast.LENGTH_LONG).show();
                }
                else{
                    int method =0;

                    if(isNetworkAvailable())
                       method=1;
                    else
                        method =2;

                    Log.d("method",String.valueOf(method));

                    if(method==1) {
//                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/intrack/login.php",

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.79/intrack/login.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                String arr[] = response.split(",");

                                                if (arr[0].equalsIgnoreCase("password correct")) {
                                                    // Toast.makeText(LoginActivity.this, arr[0], Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getBaseContext(), LoggedInActivity.class);
                                                    intent.putExtra("username", arr[1]);
                                                    intent.putExtra("access","online");
                                                    startActivity(intent);


                                                } else {
                                                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
//                                                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                                Toast.makeText(LoginActivity.this, "Please check your connection" + error.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                // params.put("site_id",siteidV);
                                params.put("username", usernameV);
                                params.put("password", passwordV);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                    else if(method==2){
                        if(db.getUser(usernameV)!=null){
                            User u = db.getUser(usernameV);

                            String pw = u.getPassword();
                            MCrypt mcrypt = new MCrypt();
                            String dpw="";

                            //Log.d("count",String.valueOf(db.getHistoryCount()));

                            try{
                                dpw = new String(mcrypt.decrypt(pw));

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            dpw = dpw.replaceAll("[^A-Za-z0-9]","");
                            dpw = dpw.replaceAll("\\s","");

                            Log.d("password",dpw);
                            Log.d("password",String.valueOf(dpw.length()) +"  "+ passwordV);

                            if(dpw.equalsIgnoreCase(passwordV) ){
                                Toast.makeText(getApplicationContext(),"Password correct",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getBaseContext(), LoggedInActivity.class);
                                intent.putExtra("username", u.getUsername());
                                intent.putExtra("access","offline");
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Password wrong",Toast.LENGTH_LONG).show();
                            }


                        }
                        else{
                            Toast.makeText(getApplicationContext(),"No such username",Toast.LENGTH_LONG).show();
                        }
                    }

                }

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // make the device update its location
//        location.beginUpdates();
//
//        // ...
//    }
}


