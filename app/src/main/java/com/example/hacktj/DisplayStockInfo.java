package com.example.hacktj;

import static java.net.Proxy.Type.HTTP;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.UsernamePasswordCredentials;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.auth.BasicScheme;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

public class DisplayStockInfo extends AppCompatActivity implements View.OnClickListener{

    private AlphaVantage alphaVantage;
    private String companyToken;
    private TextView displayCompanyName;
    private TextView displayCompanyDetails;
    private TextView logo;
    private CandleStickChart chart;
    private PriceList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock_info);

        companyToken = getIntent().getStringExtra("Token");
        displayCompanyName = findViewById(R.id.displayStockCompanyName);
        displayCompanyName.setText(companyToken);
        displayCompanyName.setOnClickListener(this);

        displayCompanyDetails = findViewById(R.id.displayStockCompanyDetails);
        logo = findViewById(R.id.displayLogo);
        logo.setOnClickListener(this);

        alphaVantage = new AlphaVantage("7RS2YJMXFI2LHA6L");
        JSONObject companyDetails = alphaVantage.query(companyToken);
        list = new PriceList(companyDetails);
        displayCompanyDetails.setText(list.priceList.get(0).toString());

        chart = findViewById(R.id.displayChart);

        chart.setHighlightPerDragEnabled(true);
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.LTGRAY);

        YAxis yAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        yAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(true);// disable x axis grid lines
        xAxis.setDrawLabels(true);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = chart.getLegend();
        l.setEnabled(true);

        ArrayList<CandleEntry> candles = new ArrayList<>();

        String[] dateIndex = new String[list.priceList.size()];
        try {
            for (int j = 0; j < list.priceList.size(); j++) {
                //System.out.println((float)index.getCompanyStockPrices().get(j).getDailyHigh());
                if(list.priceList.get(j).getDailyClose() != 0){
                    dateIndex[j] = String.valueOf(list.priceList.get(j).getDailyDate());
                    candles.add(new CandleEntry(
                            (float)j * 1f,
                            (float)list.priceList.get(j).getDailyHigh() * 1f,
                            (float)list.priceList.get(j).getDailyLow() * 1f,
                            (float)list.priceList.get(j).getDailyOpen() * 1f,
                            (float)list.priceList.get(j).getDailyClose() * 1f));
                }
            }
        }catch (Exception ex){ex.printStackTrace();}

//        Collections.reverse(candles);
        IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter(dateIndex);
        xAxis.setValueFormatter(indexAxisValueFormatter);
        xAxis.setLabelCount(4);

        CandleDataSet set1 = new CandleDataSet(candles, "Stock Prices");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.GRAY);
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.GREEN);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);

        Description description = new Description();
        description.setText("");

        CandleData data = new CandleData(set1);
        chart.setDescription(description);
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.displayLogo:
                startActivity(new Intent(DisplayStockInfo.this, HomePage.class));
                break;
            case R.id.displayStockCompanyName:
                Intent intent = new Intent(DisplayStockInfo.this, DisplayArticles.class);
                intent.putExtra("Token", companyToken);
                startActivity(intent);
                break;
        }
    }

}