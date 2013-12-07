package com.example.lawyerapp;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoView extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.viewphoto);
        
        // Get the path and name of the photo being shown
        String path = this.getIntent().getExtras().getString("path");
        String name = this.getIntent().getExtras().getString("name");
        
        // Create a file at the specified path
        File imgFile = new File(path);
        
        if(imgFile.exists()){

        	// Get the bitmap of the file
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            // Set the name of the photo
            TextView myText = (TextView) findViewById(R.id.imgName);
            myText.setText(name);
            
            // Display the photo on the screen
            ImageView myImage = (ImageView) findViewById(R.id.imgView);
            myImage.setImageBitmap(myBitmap);

        }
	}

}
