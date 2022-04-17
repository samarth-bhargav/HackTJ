package com.example.hacktj;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AlphaVantage {
    private String APIKey;
    public AlphaVantage(String APIKey){
        this.APIKey = APIKey;
    }
    public JSONObject query(final String companyCode) {
        final JSONObject[] queryResult = new JSONObject[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String inputLine;
                String result;

                boolean successful = false;
                while (!successful) {
                    try {
                        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
                                + companyCode +
                                "&apikey=" + APIKey + "&outputsize=compact";
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
                        successful = true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return queryResult[0];
    }
}
