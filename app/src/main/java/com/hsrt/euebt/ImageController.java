package com.hsrt.euebt;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Tugrul Duyar on 07.06.2016.
 */
public class ImageController {

    public static ImageController instance;

    private ImageController(){}

    public static ImageController getInstance(){
        if(instance == null){
            instance = new ImageController();
        }
        return instance;
    }

    /**
     * Save image to storage.
     * @param bm Image as Bitmap.
     * @param cw Context wrapper.
     * @return Path to the image.
     */
    public String saveImageToStorage(Bitmap bm, ContextWrapper cw){
        String path = null;
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(dir, generateRandomName());
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(mypath);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            path = mypath.getAbsolutePath();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException ioException) {
                Log.e("Sreamclosure", ioException.getMessage());
            }
        }
        return path;
    }

    public Bitmap loadImageFromStorage(String path){
        Bitmap bm = null;
        try{
            File f = new File(path);
            bm = BitmapFactory.decodeStream(new FileInputStream(f));
        }catch(Exception e){
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * Load image From storage.
     * @param path Imagepath.
     * @param context Activitycontext.
     * @return Image as Bitmap.
     */
    public Bitmap loadImageFromStorage(String path, Context context) {
        if (context == null) {
            return loadImageFromStorage(path);
        } else {
            if (!path.contains("/")) {
                try {
                    AssetManager manager = context.getAssets();
                    InputStream open = manager.open(path);
                    return BitmapFactory.decodeStream(open);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return loadImageFromStorage(path);
            }
        }
        return null;
    }

    private String generateRandomName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        return "eubt" + currentDateAndTime + "_" + new Random().nextInt(10000) + ".jpg"; //Just to be sure ...
    }
}
