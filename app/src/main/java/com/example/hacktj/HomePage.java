package com.example.hacktj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    static ListView listView;
    static ArrayList<String> items;
    static ListViewAdapter adapter;

    EditText input;
    ImageView enter;
    ImageView exit;
    ImageView gone;
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

        listView = findViewById(R.id.listview);
        input = findViewById(R.id.input);
        enter = findViewById(R.id.add);
        exit = findViewById(R.id.minus);
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
                if (text == null || text.length() == 0) {
                    makeToast("Enter an item.");
                }
                else{
                    addItem(text);
                    input.setText("");
                    makeToast("Added: " + text);
                }
            }
        });


    }

    public void addItem(String item) {
        item = item.trim();
        items.add(item);
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
                }
            }
        });
        listView.setAdapter(adapter);
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
}