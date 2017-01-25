package com.hsrt.euebt;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


public class TrainingListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 12;
    public static final int REQUEST_CODESHOW = 13;
    private TrainingAdapter wdAdapter;
    private TrainingsDataSource datasource;
    private ListView lvProduct;
    private ArrayList<Training> trainingList = new ArrayList<>();
    private TrainingExtra trainingDescriptionExtra;

    private Toolbar toolbar;
    private EditText trainingNameEditText;
    private EditText trainingDescriptionEditText;
    private RecyclerView recyclerView;

    private LocationManager locMan;
    private locListener locLis;
    private Training clickedTraining;
    private Geocoder geoCoder;
    List<Address> adresses;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebungen);
        initToolbar();
        //lvProduct = (ListView)findViewById(R.id.listview_product);

        //Trainings aus DB saugen und in die Liste einfügen
        datasource = new TrainingsDataSource(this);
        datasource.open();
        List<String> values = datasource.getAllNames();
        for(String trainingName: values){
            List<Training> trainingListWithNames = datasource.getAllTrainingsWithName(trainingName);
            trainingList.add(trainingListWithNames.get(0));
        }
        sortListData();

         /* Zusätzlich wird, weil unser Projekt scheinbar eine relativ alte Java-API benutzt,
         * folgende Annotation vor dieser Funktion (onCreate) benötigt, um keine Warnung zu bekommen!
         * @RequiresApi(api = Build.VERSION_CODES.N)
         */

        setupRecyclerView();
        /*for(String trainingFromDB:values){
            Training newTrainingFromDB = new Training(trainingFromDB);
            trainingList.add(newTrainingFromDB);
        }*/

        locLis = new locListener();
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        geoCoder = new Geocoder(this, Locale.getDefault());
        checkForLocationPermission();

    }

    //die Toolbar wird initialisiert
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void sortListData(){


        /* [Johannes] Bisher wird die Liste noch nicht sortiert, das sollte hier passieren:
         */
        //noinspection Since15
        trainingList.sort(new Comparator<Training>() {
            @Override
            public int compare(Training o1, Training o2) {
                // Sort the list according to the timestamps...
                return o1.getTimestamp() > o2.getTimestamp() ? 1 : -1;
            }
        });

    }

    private void checkForLocationPermission (){

        // Permission überprüfen
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_product);

        wdAdapter = new TrainingAdapter(this, trainingList, new OnObjectClickListener() {
            @Override
            public void onObjectClick(final Training training) {
               // Item in der Liste wurde angeklickt, alert wird aufgerufen

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TrainingListActivity.this);

                // set title
                alertDialogBuilder.setTitle("Schon gEÜBT?");

                // set dialog message
                //Anzeigen-> Zeigt Training an
                // Aktualisieren -> >Training wird als erneut geübt aktualisiert
                alertDialogBuilder
                        .setMessage("Aktualisieren?")
                        .setCancelable(false)
                        .setPositiveButton("Details",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Intent um das AnzeigeActivity zu öffnen
                                Intent showTrainingIntent = new Intent(getBaseContext(),ShowTrainingActivity.class);

                                //Aus Datenbank das Training auslesen mit den Extras
                                List<Training> trainings  = datasource.getAllTrainingsWithName(training.getName());
                                List<TrainingExtra> trainingExtras = datasource.getAllExtraDataForTraining(training.getName());
                                //das aktuellste Training aus der Liste nehmen (hoffentlich ist die letzte Training einheit in der Liste
                                //die aktuellste)
                                Training trainingToShow = trainings.get(trainings.size()-1);
                                clickedTraining = training;
                                //Training wird dem Intent mit gegeben
                                showTrainingIntent.putExtra("showTraining",clickedTraining);
                                checkForLocationPermission();
                                Location tmpLoc =locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                showTrainingIntent.putExtra("Latitude", tmpLoc.getLatitude());
                                showTrainingIntent.putExtra("Longitude", tmpLoc.getLongitude());
                                //durch die zugehörigen Extras des Trainings gehen schauen ob description oder Image und das dem Inten zufügen
                                for (TrainingExtra trainingExtraToShow :trainingExtras) {
                                    if(trainingExtraToShow.getType().equals(TrainingExtra.ExtraType.Description)){
                                        showTrainingIntent.putExtra("showTrainingDescription",trainingExtraToShow);

                                    }
                                    if(trainingExtraToShow.getType().equals(TrainingExtra.ExtraType.Image))
                                        showTrainingIntent.putExtra("imageToShow",trainingExtraToShow);

                                }

                                startActivityForResult(showTrainingIntent, REQUEST_CODESHOW);
                            }
                        })
                        .setNegativeButton("JA",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                Location tmpLoc = null;

                                try {

                                    checkForLocationPermission();
                                    tmpLoc =locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    adresses = geoCoder.getFromLocation(tmpLoc.getLatitude(),tmpLoc.getLongitude(),1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Training trainedTraining = datasource.addTraining(training.getName(), "" + adresses.get(0).getAddressLine(0), tmpLoc.getLatitude(), tmpLoc.getLongitude());
                                trainingList.remove(training);
                                trainingList.add(trainedTraining);
                                wdAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        recyclerView.setAdapter(wdAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void clearTextField(EditText toClear){
        toClear.setText("");
    }

    //Wenn ein neues Training hinzugefügt werden soll, wird eine neue Activity aufgerufen mittels einem Intent
    public void addTraining(View view) {

        // Permissions überprüfen
        checkForLocationPermission();
        Location tmpLoc =locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Intent addTrainingIntent = new Intent(this, addNewTrainingActivity.class);
        try {
            adresses = geoCoder.getFromLocation(tmpLoc.getLatitude(),tmpLoc.getLongitude(),1);

        } catch (IOException e) {
           e.printStackTrace();
        }

        //addTrainingIntent.putExtra("Location",String.valueOf(tmpLoc.getLatitude()) + " # " +String.valueOf(tmpLoc.getLongitude()) );
        addTrainingIntent.putExtra("Location","" + adresses.get(0).getAddressLine(0));
        addTrainingIntent.putExtra("Latitude", tmpLoc.getLatitude());
        addTrainingIntent.putExtra("Longitude", tmpLoc.getLongitude());

        adresses.clear();
        startActivityForResult(addTrainingIntent, REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    //Springt in die Methode wenn von er von dieser Activity in eine andere springt und wieder zurück kommt.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // kehrt von der Hinzufügen Activity zurück und fügt das Objekt in die Liste, Liste wird aktualisiert
            Training addNewTraining = (Training) data.getSerializableExtra("newTraining");
            trainingList.add(addNewTraining);
            wdAdapter.notifyDataSetChanged();
            sortListData();
        }
        if(requestCode == REQUEST_CODESHOW && resultCode == RESULT_OK) {
            trainingList.remove(clickedTraining);
            wdAdapter.notifyDataSetChanged();
            sortListData();



        }
    }

}