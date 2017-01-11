package com.example.user.intracka;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class LoggedInActivity extends AppCompatActivity {
    TextView welcomemsg;
    Button logoutbtn;
    DatabaseHandler db;
    SimpleLocation location;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        username = getIntent().getStringExtra("username");
        String status = getIntent().getStringExtra("access");

        welcomemsg= (TextView)findViewById(R.id.welcomemsg);
        logoutbtn = (Button)findViewById(R.id.logoutbtn);
        welcomemsg.setText("Welcome , " + username);

        db = new DatabaseHandler(this);

//        db.clearDb2();


        location = new SimpleLocation(this);

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
//            SimpleLocation.openSettings(this);
            Toast.makeText(getApplicationContext(),"Please enable your gps",Toast.LENGTH_LONG).show();
        }

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://10.0.2.2/intrack/getAllUser.php",

//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        10.0.2.2
//        192.168.1.75
        if(status.equals("online")){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://192.168.1.75/intrack/getAllUser.php",
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
                                Date dateobj = new Date();
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat t = new SimpleDateFormat("HH:mm:ss");


                                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

                                Gson gson = new Gson();
                                String aa;


                                AccessHistory ah = new AccessHistory(db.getUser(username).getUsr_id(),df.format(dateobj),t.format(dateobj),String.valueOf(location.getLatitude()) +"," + String.valueOf(location.getLongitude()),String.valueOf(telephonyManager.getDeviceId()));

                                db.addHist(ah);

                                aa = gson.toJson(db.getAllHist());
                                Log.d("aaaa",aa);

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoggedInActivity.this, "Please check your connection and try again", Toast.LENGTH_LONG).show();

                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else{
            Date dateobj = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat t = new SimpleDateFormat("HH:mm:ss");


            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            Gson gson = new Gson();
            String aa;


            AccessHistory ah = new AccessHistory(db.getUser(username).getUsr_id(),df.format(dateobj),t.format(dateobj),String.valueOf(location.getLatitude()) +"," + String.valueOf(location.getLongitude()),String.valueOf(telephonyManager.getDeviceId()));

            db.addHist(ah);

            aa = gson.toJson(db.getAllHist());
            Log.d("aaaa",aa);
        }

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateHist("nothing yet", db.getDuration());
                Gson gson = new Gson();
                String aa = gson.toJson(db.getAllHist());
                Log.d("aaaa",aa);
                final int[] count ={0};

                if(isNetworkAvailable()){

                   List<AccessHistory> alldata = db.getAllHist();

                    for(final AccessHistory ah : alldata){
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.75/intrack/add_record.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.equalsIgnoreCase("successful")){
                                            Log.d("countabc",String.valueOf(db.getHistoryCount()));

//                                            count[0]+=1;
//                                            Log.d("count1",String.valueOf(count[0]));

                                            db.deleteFirstRow();
                                            Log.d("countabc",String.valueOf(db.getHistoryCount()));

                                            if(db.getHistoryCount()==0){
                                                Toast.makeText(getApplicationContext(),"Successfully cleaned up",Toast.LENGTH_LONG).show();

                                                finish();
                                            }


                                        }
                                        else{
                                            Toast.makeText(LoggedInActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(LoggedInActivity.this, "An error has occured" + error.toString(), Toast.LENGTH_LONG).show();
                                        finish();

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                // params.put("site_id",siteidV);
                                params.put("usr_id",ah.getUsr_id() );
                                params.put("login_date",ah.getLogin_date() );
                                params.put("login_time",ah.getLogin_time() );
                                params.put("login_loc",ah.getLogin_loc() );
                                params.put("logout_date",ah.getLogout_date() );
                                params.put("logout_time",ah.getLogout_time() );
                                params.put("logout_loc",ah.getLogout_loc() );
                                params.put("device_imei_num",ah.getDevice_imei_num1() );
                                params.put("duration",ah.getDuration() );
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);


                    }

                }
                else{
                    finish();
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


}
