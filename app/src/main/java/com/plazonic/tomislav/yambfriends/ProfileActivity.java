package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_CAPTURE_AN_IMAGE = 13;
    private static final int RC_CHOOSE_AN_IMAGE = 27;
    private static final int MEDIA_TYPE_IMAGE = 72;
    private File imageFile;

    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File storageDirectory =  new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Yamb Friends");

            if (!storageDirectory.exists()){
                Log.d("ProfileActivity", "storageDirectory doesn't exist");
                if (! storageDirectory.mkdirs()){
                    Log.d("ProfileActivity", "failed creating storageDirectory");
                    return null;
                }
            }

            if (type == MEDIA_TYPE_IMAGE){
                //String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFile = new File(storageDirectory.getPath() + File.separator + "yolo.jpg"); // Use user's name for file name
                return imageFile;
            } else return null;
        } else return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CAPTURE_AN_IMAGE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_SHORT).show();
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Image capture failed.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_CHOOSE_AN_IMAGE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Chosen successfully.", Toast.LENGTH_SHORT).show();
                imageFile = new File(data.getData().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Image selection failed.", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ImageView ivProfileImage = (ImageView) findViewById(R.id.profile_image);
                ivProfileImage.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getApplicationContext(), "Could not find image file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void captureAnImage(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, RC_CAPTURE_AN_IMAGE);
    }

    public void chooseAnImage(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");

        startActivityForResult(intent, RC_CHOOSE_AN_IMAGE);
    }

}
