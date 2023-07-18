package com.gatikahome.dogimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gatikahome.dogimage.api.BreedApiClient;
import com.gatikahome.dogimage.models.Breed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_INTERNET = 1;
    private  static boolean USE_INTERNET_GRANTED = false;
    private BreedApiClient breedApiClient;
    private ImageView imageBreed;
    private Spinner breedSpinner;
    private ArrayAdapter<Breed> breedAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        breedSpinner = findViewById(R.id.spinner_breed);
        imageBreed = findViewById(R.id.image);
        breedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedSpinner.setAdapter(breedAdapter);
        breedSpinner.setOnItemSelectedListener(new BreedSpinerLisener(imageBreed, screenWidth(), screenHeight()));


        // Создаем Handler, привязанный к главному потоку (UI)
        handler = new Handler(Looper.getMainLooper());


    }
    @Override
    protected  void onResume() {

        super.onResume();
        // Проверяем наличие разрешения на доступ к Интернету
        if (checkInternetPermission()) {
            // Разрешение уже есть
            getListBreedForSpiner();
        } else {
            // Запрашиваем разрешение
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
        }

    }
    private boolean checkInternetPermission() {
                   int permissionResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
            return permissionResult == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                USE_INTERNET_GRANTED = true;
            }
        }
        if(USE_INTERNET_GRANTED){
            getListBreedForSpiner();
        }
        else{
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }
    private void getListBreedForSpiner(){

        // Создаем и запускаем поток для выполнения запроса к API и обновления Spinner
        Thread thread = new Thread(new BreedDataRunnable());
        thread.start();
    }

    private int screenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int screenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    private class BreedDataRunnable implements Runnable {
        @Override
        public void run() {
            try{
                BreedApiClient breedApi = new BreedApiClient();
                ArrayList<Breed> breeds = breedApi.getAllBreeds();
                // Отправляем данные в главный поток для обновления Spinner
                handler.post(new UpdateSpinnerRunnable(breeds));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class UpdateSpinnerRunnable implements Runnable {
        private ArrayList<Breed> breeds;
        public UpdateSpinnerRunnable(ArrayList<Breed> breeds) {
            this.breeds = breeds;
        }

        @Override
        public void run() {
            // Очищаем адаптер Spinner перед обновлением
            breedAdapter.clear();

            // Обновляем адаптер Spinner с полученными данными
            breedAdapter.addAll(breeds);
        }
    }
}