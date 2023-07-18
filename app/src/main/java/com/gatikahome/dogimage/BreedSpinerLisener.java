package com.gatikahome.dogimage;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gatikahome.dogimage.api.BreedImageApi;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Optional;


public class BreedSpinerLisener  extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final ImageView imageBreed;
    final int screenWidth;
    final int screenHeight;
    //final

    public BreedSpinerLisener(ImageView imageBreed, int screenWidth, int screenHeight) {
        this.imageBreed = imageBreed;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Optional<String> imageUrl = new BreedImageApi((adapterView.getItemAtPosition(i)).toString())
                                .getRandomBreedImage();
                        getImageData(imageUrl);
                    } catch (Exception e) {
                        Log.d("breedImageApi", "Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            thread.start();



    }
    private void getImageData(Optional<String> imgUrl){
        if (imgUrl.isPresent()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadImage(imgUrl.get());
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void loadImage(String imageUrl){
        Picasso.get()
                .load(imageUrl)
                .resize(screenWidth, screenHeight)
                .centerInside()
                .into(imageBreed, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Картинка успешно загружена и отображена
                        Log.d("image", "Success");
                    }

                    @Override
                    public void onError(Exception e) {
                        // Обработка ошибки загрузки или отображения изображения
                        Log.d("image", "error: "+e.getMessage());
                    }
                });


    }

}
