package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_CAPTURE_AN_IMAGE = 13;
    public static final int RC_CHOOSE_AN_IMAGE = 27;
    public static final int MEDIA_TYPE_IMAGE = 72;

    private SharedPreferences settings;
    ImageView ivProfileImage;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        ivProfileImage = (ImageView) findViewById(R.id.profile_image);

        findViewById(R.id.capture_an_image_button).setOnClickListener(this);
        findViewById(R.id.choose_an_image_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set image if possible
        fileUri = null;
        setImage(fileUri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_an_image_button:
                captureAnImage();
                break;
            case R.id.choose_an_image_button:
                chooseAnImage();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CAPTURE_AN_IMAGE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show();
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), R.string.image_capture_failed, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_CHOOSE_AN_IMAGE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.chosen_successfully, Toast.LENGTH_SHORT).show();
                fileUri = data.getData();
            } else {
                Toast.makeText(getApplicationContext(), R.string.image_selection_failed, Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_OK) setImage(fileUri);
    }

    private Uri getFileUri(int type){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File storageDirectory =  new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Yamb Friends");

            if (!storageDirectory.exists()){
                Log.d("ProfileActivity", "storageDirectory doesn't exist");
                if (!storageDirectory.mkdirs()){
                    Log.d("ProfileActivity", "failed creating storageDirectory");
                    return null;
                }
            }

            if (type == MEDIA_TYPE_IMAGE){
                File imageFile = new File(storageDirectory.getPath() + File.separator + settings.getString("username", null) + ".jpg");
                return Uri.fromFile(imageFile);
            } else return null;
        } else return null;
    }

    private void captureAnImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, RC_CAPTURE_AN_IMAGE);
    }

    private void chooseAnImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");

        startActivityForResult(intent, RC_CHOOSE_AN_IMAGE);
    }

    private void setImage(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream.available() != 0) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivProfileImage.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getApplicationContext(), R.string.could_not_read_image_file, Toast.LENGTH_SHORT).show();
            }
            inputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
