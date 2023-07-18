package com.gatikahome.dogimage.api;

import android.util.Log;
import android.widget.Spinner;

import com.gatikahome.dogimage.models.Breed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BreedApiClient {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
       private final OkHttpClient httpClient;
    private final String apiBaseUrl;

    public BreedApiClient() {
        this.httpClient = new OkHttpClient();
        this.apiBaseUrl = "https://dog.ceo/api/breeds/list/all";

    }
    public ArrayList<Breed> getAllBreeds() throws IOException{
        Request request = new Request.Builder()
                .url(apiBaseUrl)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("breedApiClient", responseBody);
                // Парсинг JSON-ответа и преобразование в список сущностей
                ArrayList<Breed> breeds = parseBreedsFromJson(responseBody);
                return breeds;
            } else {
                Log.d("breedApiClient", new IOException("Request failed with code: " + response.code()).getMessage());
                throw new IOException("Request failed with code: " + response.code());
            }
        }
    }

    private ArrayList<Breed> parseBreedsFromJson(String responseBody) {
        ArrayList<Breed> breeds = new ArrayList<>();
        String [] res = responseBody.split(":");
        int len = res.length;
        breeds.add(new Breed((res[1].substring(2,res[1].length()-1))));
        for (int i=1; i<len; i++){
            int pos =res[i].indexOf("],");
            if(pos>-1 && pos+3<len){
                breeds.add(new Breed(res[i].substring(pos+3,res[i].length()-1)));
            }
        }
        return breeds;
    }
}
