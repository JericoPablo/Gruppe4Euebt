package com.hsrt.euebt;

/**
 * Created by Johannes on 11.12.2016.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

/**
 * This class offers an interface for the app's database.
 */
public class TrainingsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] trainingColumns = { MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE };
    private String[] extraDataColumns = { MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_EXTRA_TYPE, MySQLiteHelper.COLUMN_EXTRA_CONTENT};

    public TrainingsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Writes any training unit to the database.
     * @param training The training unit you want to write to the database.
     */
    public void addTraining(Training training) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, training.getName());
        values.put(MySQLiteHelper.COLUMN_TIMESTAMP, training.getTimestamp());
        values.put(MySQLiteHelper.COLUMN_LOCATION, training.getLocation());
        values.put(MySQLiteHelper.COLUMN_LATITUDE, training.getLatitude());
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, training.getLongitude());
        database.insert(MySQLiteHelper.TABLE_TRAININGS, null, values);
    }

    /**
     * Adds a new training unit that was just done by the user to the database.
     * For this, a new training unit with the current timestamp will be created.
     * @param name The name of the training unit that shall be stored in the database.
     * @param location The location where the training unit was done.
     * @return The resulting training unit that was generated and stored in the database.
     */
    public Training addTraining(String name, String location, double latitude, double longitude) {
        Training result = new Training(name, location, latitude, longitude);
        this.addTraining(result);
        return result;
    }

    /**
     * Writes any training extra to the database.
     * @param extra The training extra you want to write to the database.
     */
    public void addTrainingExtra(TrainingExtra extra) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_EXTRA_NAME, extra.getTrainingName());
        values.put(MySQLiteHelper.COLUMN_EXTRA_TYPE, extra.getType().toString());
        values.put(MySQLiteHelper.COLUMN_EXTRA_CONTENT, extra.getContent());
        database.insert(MySQLiteHelper.TABLE_TRAINING_EXTRAS, null, values);
    }

    /**
     * Adds a new training extra data unit to the database.
     * @param trainingName The name of the training unit this extra data unit belongs to.
     * @param type The type this extra data unit is of.
     * @param content The content (corresponding the type) of this extra data unit.
     * @return The resulting extra data unit that was generated and stored in the database.
     */
    public TrainingExtra addTrainingExtra(String trainingName, TrainingExtra.ExtraType type, String content) {
        TrainingExtra result = new TrainingExtra(trainingName, type, content);
        this.addTrainingExtra(result);
        return result;
    }

    /**
     * Deletes a specific training unit from the database.
     * @param training The training unit that shall be deleted from the database.
     */
    public void deleteTraining(Training training) {
        long timestamp = training.getTimestamp();
        database.delete(MySQLiteHelper.TABLE_TRAININGS, MySQLiteHelper.COLUMN_TIMESTAMP + " = " + timestamp, null);
    }

    public void deleteAllTraining(Training training) {

        database.delete(MySQLiteHelper.TABLE_TRAININGS,null, null);
    }


    /**
     * Returns all names of training units stored in the database.
     * @return A list of all names of training units stored in the database. Each name is contained once.
     */
    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = database.query(true, MySQLiteHelper.TABLE_TRAININGS, new String[] { trainingColumns[0] }, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return names;
    }

    /**
     * Retrieves a list of all training units belonging to a specific training unit pool.
     * The training units will be ordered descending by timestamp! Therefore, the most recent unit is the first item.
     * @param name The name of the training units that should be retrieved.
     * @return All training units from the database that have the given name.
     */
    public List<Training> getAllTrainingsWithName(String name) {
        List<Training> trainings = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRAININGS, trainingColumns, MySQLiteHelper.COLUMN_NAME + " = '" + name + "'", null, MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_TIMESTAMP + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Training training = cursorToTraining(cursor);
            trainings.add(training);
            cursor.moveToNext();
        }
        cursor.close();
        return trainings;
    }

    /**
     * Retrieves a list of all training extra data units belonging to a specific training.
     * @param trainingName The name of the training you want to get the extra data from.
     * @return A list of all extra data units that belong to the given training.
     */
    public List<TrainingExtra> getAllExtraDataForTraining(String trainingName) {
        List<TrainingExtra> extras = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRAINING_EXTRAS, extraDataColumns, MySQLiteHelper.COLUMN_EXTRA_NAME + " = '" + trainingName + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TrainingExtra extra = cursorToTrainingExtra(cursor);
            extras.add(extra);
            cursor.moveToNext();
        }
        cursor.close();
        return extras;
    }

    private Training cursorToTraining(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        System.out.println("============ CURSOR 3"+cursor.getDouble(3));

        System.out.println("============ CURSOR 4"+cursor.getDouble(4));
        return new Training(cursor.getString(0), cursor.getLong(1), cursor.getString(2), cursor.getDouble(3),cursor.getDouble(4));
    }

    private TrainingExtra cursorToTrainingExtra(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        return new TrainingExtra(cursor.getString(0), TrainingExtra.ExtraType.valueOf(cursor.getString(1)), cursor.getString(2));
    }
}