package org.techtown.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.0.8:8080";
    private static String TAG = "phptest";

    private EditText et_pn;
    private EditText et_ID;
    private EditText et_password;
    private EditText et_name;
    private EditText et_birth;
    private EditText et_email;
    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_ID = (EditText)findViewById(R.id.et_ID);
        et_password = (EditText)findViewById(R.id.et_password);
        et_name = (EditText)findViewById(R.id.et_name);
        et_birth = (EditText)findViewById(R.id.et_birth);
        et_pn = (EditText)findViewById(R.id.et_pn);
        et_email = (EditText)findViewById(R.id.et_email);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        Button btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = et_ID.getText().toString();
                String pwd = et_password.getText().toString();
                String name = et_name.getText().toString();
                String birth = et_birth.getText().toString();
                String num = et_pn.getText().toString();
                String e_mail = et_email.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insert.php", id,pwd,name,birth,num,e_mail);


                et_ID.setText("");
                et_password.setText("");
                et_name.setText("");
                et_birth.setText("");
                et_pn.setText("");
                et_email.setText("");
            }
        });
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];
            String pwd = (String)params[2];
            String name = (String)params[3];
            String  birth = (String)params[4];
            String num = (String)params[5];
            String e_mail = (String)params[6];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id + "&pwd=" + pwd + "&name=" + name
                    + "&birth=" + birth + "&num=" + num + "&e_mail=" + e_mail;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}
