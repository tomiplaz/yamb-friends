package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_TAKE_A_PICTURE = 13;
    public static final int MEDIA_TYPE_IMAGE = 72;

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File storageDirectory =  new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "yamb-friends");

            if (!storageDirectory.exists()){
                if (! storageDirectory.mkdirs()){
                    Log.d("ProfileActivity", ": Failed creating storage directory.");
                    return null;
                }
            }

            if (type == MEDIA_TYPE_IMAGE){
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                return new File(storageDirectory.getPath() + File.separator + "IMG_"+ timestamp + ".jpg");
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
        if (requestCode == RC_TAKE_A_PICTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_SHORT).show();
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Action failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void takeAPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, RC_TAKE_A_PICTURE);
    }

    public void pickAPicture() {
        // ...
    }

}
