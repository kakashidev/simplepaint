package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawingView mDrawingView;
    private ImageButton currPaint, newButton, saveButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingView = (DrawingView)findViewById(R.id.drawing);
        // Getting the initial paint color.
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        // 0th child is skin color, so selecting first child to give black as initial color.
        currPaint = (ImageButton)paintLayout.getChildAt(1);
        newButton = (ImageButton) findViewById(R.id.btn_clear);
        newButton.setOnClickListener(this);
        saveButton = (ImageButton) findViewById(R.id.btn_save);
        saveButton.setOnClickListener(this);
    }

    public void paintClicked(View view){
            // Update the color
            ImageButton imageButton = (ImageButton) view;
            String colorTag = imageButton.getTag().toString();
            mDrawingView.setColor(colorTag);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_clear:
                // Show new painting alert dialog
                mDrawingView.startNew();
                Log.i("clear", "y");
                break;
            case R.id.btn_save:
                // Show save painting confirmation dialog.
                savePainting();
                break;
        }
    }

    private void savePainting(){
        Bitmap bmp = mDrawingView.save();
        Context context = getApplicationContext();
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // Make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.myapp.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
}