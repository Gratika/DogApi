package com.gatikahome.dogimage.api;

import android.util.Log;

import com.gatikahome.dogimage.models.Breed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BreedImageApi {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;
    private final String apiBaseUrl;
    private  final String breed;

    public BreedImageApi(String breed) {
        this.breed = breed;
        this.httpClient = new OkHttpClient();
        this.apiBaseUrl = "https://dog.ceo/api/breed/"+breed+"/images/random";
        Log.d("breedImageApi", this.apiBaseUrl);

    }
    public  Optional<String> getRandomBreedImage() throws IOException {
        Request request = new Request.Builder()
                .url(apiBaseUrl)
                .build();
        Log.d("breedImageApi", "request build");
        Optional<String> imageURL=null;
        try (Response response = httpClient.newCall(request).execute()) {

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("breedImageApi", responseBody);
                // Парсинг JSON-ответа и преобразование в список сущностей
                //Optional<String>
                        imageURL = parseBreedImageFromJson(responseBody);
               // return imageURL;
            } else {
                Log.d("breedImageApi", new IOException("Request failed with code: " + response.code()).getMessage());
                throw new IOException("Request failed with code: " + response.code());
            }
        }catch (Exception e){
            Log.d("breedImageApi","Error in BreedImageApi :"+e.getMessage());
        }
        return imageURL;
    }

    private Optional<String> parseBreedImageFromJson(String responseBody) {
        String [] res = responseBody.split(",");
        Optional<String> imageURL = Arrays.stream(res[0].split("\"")).filter(r->r.contains("https")).findFirst();


        return imageURL;
    }
}
