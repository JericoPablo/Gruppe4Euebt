package com.hsrt.euebt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Tugrul on 20.01.2017.
 */

public class ShowTrainingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int CAMERA_REQUEST = 1898;

    private ImageView imageShow;
    private Toolbar toolbar;
    private int result = RESULT_CANCELED;

    private TextView trainingNameTextView;
    private TextView trainingDescriptionTextView;
    private TrainingsDataSource datasource;
    private Bitmap photo;
    private Training showTraining;
    private TrainingExtra showTrainingExtraDescription;
    private TrainingExtra showTrainingExtraImage;
    private TrainingExtra descriptionExtra;
    private TextView trainingGeoDataTextView;
    private double lati;
    private double longi;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_training);
        trainingNameTextView = (TextView) findViewById(R.id.trainingNameTextView);
        trainingDescriptionTextView = (TextView) findViewById(R.id.trainingDescriptionTextView);
        trainingGeoDataTextView = (TextView) findViewById(R.id.trainingGeoDataTextView);
        imageShow =  (ImageView) findViewById(R.id.image_show);
        //hole Training Objekt aus dem Intent und die TrainingExtras
        showTraining = (Training) getIntent().getSerializableExtra("showTraining");
        showTrainingExtraDescription = (TrainingExtra) getIntent().getSerializableExtra("showTrainingDescription");
        showTrainingExtraImage = (TrainingExtra) getIntent().getSerializableExtra("imageToShow");
        lati = (double) getIntent().getDoubleExtra("Latitude", 0);
        longi = (double) getIntent().getDoubleExtra("Longitude", 0);
        // zeige Bild an wenn eins existiert
        if(showTrainingExtraImage!=null) {
            photo = ImageController.getInstance().loadImageFromStorage(showTrainingExtraImage.getContent());
            if(photo!=null){
                imageShow.setImageBitmap(photo);
            }
        }
        //Füge vom Training den Namen als Text ein
        trainingNameTextView.setText("Training Name: "+showTraining.getName());
        //wenn description existiert zeige an
        if(showTrainingExtraDescription!=null) {
            trainingDescriptionTextView.setText("Description: "+showTrainingExtraDescription.getContent());
        }
        if(trainingGeoDataTextView!=null) {
            trainingGeoDataTextView.setText("Ort: "+showTraining.getLocation());
        }

        initToolbar();
        datasource = new TrainingsDataSource(this);
        datasource.open();

        //google maps
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //momentan nichts
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    //wenn oben links auf abbrechen geklickt wird springt es wieeder in die vorherige Activity zurück
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Training> TrainingList = datasource.getAllTrainingsWithName(showTraining.getName());

//        for (Training t: TrainingList) {
  //          datasource.deleteAllTraining(showTraining);
    //    }

        datasource.deleteAllTraining(showTraining);
        datasource.close();
        result = RESULT_OK;
        finish();
        return true;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("deleteTraining", showTraining);
        setResult(result, intent);
        super.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati,longi),15));
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(lati,longi))
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
        //neustes Training
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lati,longi)).title(showTraining.getName()+ " "+new SimpleDateFormat("d.M.y - h:m a").format(new Date(showTraining.getTimestamp() * 1000))));
        //Historie Training
        List<Training> trainings = datasource.getAllTrainingsWithName(showTraining.getName());
        for (Training historieTraining: trainings) {

            googleMap.addMarker(new MarkerOptions().position(new LatLng(historieTraining.getLatitude(),historieTraining.getLongitude())).title(showTraining.getName()+ " "+new SimpleDateFormat("d.M.y - h:m a").format(new Date(historieTraining.getTimestamp() * 1000))));
        }


    }
}
