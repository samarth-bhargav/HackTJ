package com.example.hacktj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    static ListView listView;
    static ArrayList<String> items;
    static ListViewAdapter adapter;

    EditText input;
    ImageView enter;
    String APIKey = "m_33oiHKum6CiUpAPuwCljTwPFuN4CVL";

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private static CollectionReference stocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        stocks = db.collection("users")
                .document(auth.getUid())
                .collection("stocks");

        listView = findViewById(R.id.tickerList);
        input = findViewById(R.id.input);
        enter = findViewById(R.id.add);
        items = new ArrayList<>();

        stocks.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot qs = task.getResult();
//                Iterator<QueryDocumentSnapshot> i = qs.iterator();
//                Log.d("d", qs.getDocuments().toString());
                for (DocumentSnapshot ds : qs.getDocuments()){
                    items.add(ds.get("Token").toString());
                }
                adapter = new ListViewAdapter(getApplicationContext(), items);
                listView.setAdapter(adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HomePage.this, DisplayStockInfo.class);
                intent.putExtra("Token", items.get(i));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                makeToast("Long Press: " + items.get(i));
                return false;
            }
        });



        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = input.getText().toString();
                JSONObject jsnFile = query(text);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (jsnFile == null) {
                    makeToast("Please Enter a Valid Stock");
                    return;
                }
                String valid = null;
                try {
                    valid = jsnFile.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (valid.equals("NOT_FOUND") || valid.equals("ERROR")) {
                    makeToast("Too many Polygon Queries");
                    return;
                }
                else{
                    addItem(text);
                    input.setText("");
                    makeToast("Added: " + text);
                    return;
                }
            }
        });


    }

    public void addItem(String item) {
        item = item.trim();
        String finalItem = item;
        stocks.document(item).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    makeToast("You're already tracking this stock");
                    input.setText("");
                }
                else{
                    Map<Object, Object> map = new HashMap<>();
                    map.put("Token", finalItem);
                    stocks.document(finalItem).set(map);
                    items.add(finalItem);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
    public static void removeItem(int index) {
        stocks.document(items.get(index)).delete();
        items.remove(index);
        listView.setAdapter(adapter);
    }

    Toast t;

    private void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    public JSONObject query(final String companyCode) {
        final JSONObject[] queryResult = new JSONObject[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String inputLine;
                String result;
                    try {
                        String url = "https://api.polygon.io/v1/open-close/"
                                + companyCode +
                                "/2020-10-14?adjusted=true&apiKey=" + APIKey;
                        System.out.println(url);
                        URL api = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
                        connection.connect();

                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((inputLine = reader.readLine()) != null) {
                            stringBuilder.append(inputLine);
                        }

                        reader.close();
                        streamReader.close();

                        result = stringBuilder.toString();

                        queryResult[0] = new JSONObject(result);
                    } catch (Exception ex) {
                        queryResult[0] = null;
                    }
                }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            return null;
        }
        return queryResult[0];
    }
}