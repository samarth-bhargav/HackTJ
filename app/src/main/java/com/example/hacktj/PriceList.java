package com.example.hacktj;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PriceList {
    public ArrayList<Price> priceList;

    public PriceList(JSONObject jsonFile){
        priceList = new ArrayList<Price>();
        try {
            JSONObject jsonPrices = jsonFile.getJSONObject("Time Series (Daily)");
            Iterator<String> dates = jsonPrices.keys();

            while (dates.hasNext()) {
                String date = dates.next();
                JSONObject jsonPrice = jsonPrices.getJSONObject(date);
                Price dailyPrice = new Price(
                        date,
                        jsonPrice.getDouble("1. open"),
                        jsonPrice.getDouble("2. high"),
                        jsonPrice.getDouble("3. low"),
                        jsonPrice.getDouble("4. close"),
                        jsonPrice.getInt("5. volume"));
                priceList.add(dailyPrice);
            }
            Collections.reverse(priceList);
            String symbol = jsonFile
                    .getJSONObject("Meta Data")
                    .getString("2. Symbol");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
