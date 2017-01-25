package com.hsrt.euebt;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tugrul on 10.01.2017.
 */

public class addNewTrainingActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 1898;

    private ImageView image;
    private Toolbar toolbar;
    private int result = RESULT_CANCELED;

    private EditText trainingNameEditText;
    private EditText trainingDescriptionEditText;
    private TrainingsDataSource datasource;
    private Bitmap photo;
    private Training newTraining;
    private TrainingExtra descriptionExtra;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_training);
        image = (ImageView) findViewById(R.id.image);
        trainingNameEditText = (EditText) findViewById(R.id.trainingNameEditText);
        trainingDescriptionEditText = (EditText) findViewById(R.id.trainingDescriptionEditText);


        initToolbar();

        datasource = new TrainingsDataSource(this);
        datasource.open();
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

    public void addItems(View v) {
    }
    public void clearTextField(EditText toClear){
        toClear.setText("");
    }

    //Inten für die Camera wird ausgeführt
    public void takePicture(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    //sobald die Camera Activity fertig ist wird wieder die Activity aufgerufen und das Bild erstellt.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
        }

    }

    //wenn oben links auf abbrechen geklickt wird springt es wieeder in die vorherige Activity zurück
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_toolbar, menu);
        return true;
    }

    //wenn oben rechts auf akzeptieren geklickt wird wird das Bild in die Storage gespeichert und der Pfad ausgelesen und in die DB geschrieben
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSave) {
            newTraining =  datasource.addTraining(trainingNameEditText.getText().toString(),
                    getIntent().getStringExtra("Location"), getIntent().getDoubleExtra("Latitude",0),
                    getIntent().getDoubleExtra("Longitude",0));
                if(photo!=null){
                    String imagePath = ImageController.getInstance().saveImageToStorage(photo, this);
                        if(imagePath!=null && imagePath.length()>=0) {
                            datasource.addTrainingExtra(trainingNameEditText.getText().toString(), TrainingExtra.ExtraType.Image, imagePath);
                            System.out.print(imagePath);
                        }
                }
                if(trainingDescriptionEditText.getText()!=null && trainingDescriptionEditText.getText().length()>=0){
                    descriptionExtra = datasource.addTrainingExtra(trainingNameEditText.getText().toString(),TrainingExtra.ExtraType.Description,trainingDescriptionEditText.getText().toString());
                }

            result = RESULT_OK;
            finish();

        }
        return true;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        System.out.println(newTraining);
        intent.putExtra("newTraining", newTraining);
        setResult(result, intent);
        super.finish();
    }
}
