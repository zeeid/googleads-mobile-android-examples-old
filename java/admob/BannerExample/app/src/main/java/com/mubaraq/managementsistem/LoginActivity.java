package com.mubaraq.managementsistem;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "https://zeeid.net/api/mobile/login";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    login(email, password);
                }
            }
        });
    }

    private void login(String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing login...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create the JSON request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Hide the progress dialog
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            String name = response.getString("name");
                            String userEmail = response.getString("email");
                            int jmlBaner = response.getInt("jml_baner");
                            int isVPNProtection = response.getInt("isVPNProtection");

                            Log.e("API_Response", "Response received: " + response.toString());

                            // Handle categorized ads safely
                            // Menggunakan optJSONArray untuk memeriksa dan mendapatkan array
                            JSONArray layarPembukaAplikasi = response.optJSONArray("Iklan_Layar_Pembuka_Aplikasi");
                            if (layarPembukaAplikasi != null) {
                                for (int i = 0; i < layarPembukaAplikasi.length(); i++) {
                                    String adCode = layarPembukaAplikasi.getString(i);

                                }
                            }

                            JSONArray bannerAdaptif = response.optJSONArray("Iklan_Banner_Adaptif");
                            if (bannerAdaptif != null) {
                                for (int i = 0; i < bannerAdaptif.length(); i++) {
                                    String adCode = bannerAdaptif.getString(i);
                                    Log.d("Iklan_Banner_Adaptif", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray bannerUkuranTetap = response.optJSONArray("Iklan_Banner_Ukuran_Tetap");
                            if (bannerUkuranTetap != null) {
                                for (int i = 0; i < bannerUkuranTetap.length(); i++) {
                                    String adCode = bannerUkuranTetap.getString(i);
                                    Log.d("Iklan_Banner_Ukuran_Tetap", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray interstisial = response.optJSONArray("Iklan_Interstisial");
                            if (interstisial != null) {
                                for (int i = 0; i < interstisial.length(); i++) {
                                    String adCode = interstisial.getString(i);
                                    Log.d("Iklan_Interstisial", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray iklanReward = response.optJSONArray("Iklan_Iklan_Reward");
                            if (iklanReward != null) {
                                for (int i = 0; i < iklanReward.length(); i++) {
                                    String adCode = iklanReward.getString(i);
                                    Log.d("Iklan_Iklan_Reward", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray interstisialReward = response.optJSONArray("Iklan_Interstisial_Reward");
                            if (interstisialReward != null) {
                                for (int i = 0; i < interstisialReward.length(); i++) {
                                    String adCode = interstisialReward.getString(i);
                                    Log.d("Iklan_Interstisial_Reward", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray nativeAds = response.optJSONArray("Iklan_Native");
                            if (nativeAds != null) {
                                for (int i = 0; i < nativeAds.length(); i++) {
                                    String adCode = nativeAds.getString(i);
                                    Log.d("Iklan_Native", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            JSONArray videoNative = response.optJSONArray("Iklan_Video_Native");
                            if (videoNative != null) {
                                for (int i = 0; i < videoNative.length(); i++) {
                                    String adCode = videoNative.getString(i);
                                    Log.d("Iklan_Video_Native", "Ad Code " + (i + 1) + ": " + adCode);
                                }
                            }

                            // Display success
                            Toast.makeText(LoginActivity.this, "Login Successful: " + name, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Parsing error, please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hide the progress dialog
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        // Handle the error response
                        Toast.makeText(LoginActivity.this, "Login Failed: Invalid credentials", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}