package manoj.appcomp.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityNameEditText;
    TextView weatherResult;

    public class DownloadTask extends AsyncTask<String, Void, String >{

        @Override
        protected String doInBackground(String... urls) {
            String res = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while(data != -1)
                {
                    res += (char) data;
                    data = reader.read();
                }

                return res;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                // weather is the key in API
                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for(int i = 0; i<arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals(""))
                    {
                        message += main + ": " + description + "\r\n";
                    }
                }

                if(!message.equals(""))
                {
                    weatherResult.setText(message);
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                Log.i("Weather", weatherInfo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityNameEditText = findViewById(R.id.cityNameEditText);
        weatherResult = findViewById(R.id.textViewWeatherResult);


    }

    public void getWeather(View view)
    {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityNameEditText.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName  + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }
            });

        }

        // to move down the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(weatherResult.getWindowToken(),0);
    }
}