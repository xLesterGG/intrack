package com.example.user.intracka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText siteid,username,password;
    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        siteid = (EditText)findViewById(R.id.siteid);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

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

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/intrack/login.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    String arr[] = response.split(",");

                                    if(arr[0].equalsIgnoreCase("password correct"))
                                    {
                                        Toast.makeText(LoginActivity.this, arr[0], Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(),LoggedInActivity.class);
                                        intent.putExtra("username",arr[1]);
                                        startActivity(intent);


                                    }else{
                                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                           // params.put("site_id",siteidV);
                            params.put("username",usernameV);
                            params.put("password",passwordV);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }

            }
        });
    }
}
