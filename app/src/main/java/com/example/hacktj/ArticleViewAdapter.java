package com.example.hacktj;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ArticleViewAdapter extends ArrayAdapter<String> {
    ArrayList<String> list;
    ArrayList<Double> scoreList;
    Context context;

    public ArticleViewAdapter(Context context, ArrayList<String> items, ArrayList<Double> scores) {
        super(context, R.layout.list_row, items);
        this.context = context;
        list = items;
        scoreList = scores;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.article_row, null);

            TextView name = convertView.findViewById(R.id.name);
            name.setText(list.get(position));

            TextView scored = convertView.findViewById(R.id.scored);
            scored.setText(roundTwoDecimals(scoreList.get(position)) + "");
        }
        return convertView;
    }
    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
