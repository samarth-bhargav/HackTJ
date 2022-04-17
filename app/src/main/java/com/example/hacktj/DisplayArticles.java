package com.example.hacktj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.UsernamePasswordCredentials;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.auth.BasicScheme;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.protocol.HTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayArticles extends AppCompatActivity implements View.OnClickListener{

    private String[] negativeWords = {"sell", "fall", "down", "hacking", "bad", "sued", "sue"};
    private String[] positiveWords = {"buy", "up", "rise", "well", "good", "win", "ahead", "investing", "invest"};
    private String companyName;
    private TextView displayArticlesLogo;
    private String APIKey = "6babdbaf841b46a38423ff697cbca978";
    private ListView articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_articles);

        companyName = getIntent().getStringExtra("Token");
        articleList = findViewById(R.id.articleListView);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        displayArticlesLogo = findViewById(R.id.displayArticleLogo);
        displayArticlesLogo.setOnClickListener(this);

        StringBuilder command = new StringBuilder("");
        command.append("https://newsapi.org/v2/everything?q=");
        command.append(companyName);
        command.append("&from=");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date result = cal.getTime();
        String dateMonthAgo = new SimpleDateFormat("yyyy-MM-dd").format(result);
        command.append(dateMonthAgo);
        command.append("&sortBy=popularity&searchIn=title&apiKey=");
        command.append(APIKey);

        JSONObject jsonObject;
        try {
            jsonObject = getRequest(command.toString());
            ArrayList<String> articleTitles = new ArrayList<>();
            JSONArray ar = jsonObject.getJSONArray("articles");
            for (int i = 0; i < Math.min(5, ar.length()); i++){
                JSONObject article = ar.getJSONObject(i);
                articleTitles.add(article.getString("title"));
                if (articleTitles.get(i).length() > 70){
                    String a = articleTitles.get(i);
                    a = a.substring(0,68);
                    a += "...";
                    articleTitles.set(i, a);
                }
            }
            Log.d("ArticleTitles", articleTitles.toString());
            ArticleViewAdapter adapter = new ArticleViewAdapter(getApplicationContext(), articleTitles);
            articleList.setAdapter(adapter);

            int negative = 0, positive = 0;
            for (int i = 0; i < articleTitles.size(); i++){
                for (String str : negativeWords){
                    if (articleTitles.get(i).toLowerCase(Locale.ROOT).contains(str)){
                        negative++;
                    }
                }
                for (String str : positiveWords){
                    if (articleTitles.get(i).toLowerCase(Locale.ROOT).contains(str)){
                        positive++;
                    }
                }
            }
            double score = (positive+1.0) / (positive + negative + 1.0);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.displayArticleLogo:
                startActivity(new Intent(DisplayArticles.this, HomePage.class));
                break;

        }
    }
    public JSONObject getRequest(String url) throws JSONException, IOException {
        final JSONObject[] jsonObject = new JSONObject[1];
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            jsonObject[0] = new JSONObject(response.body().string());
        }
        return jsonObject[0];
    }
}