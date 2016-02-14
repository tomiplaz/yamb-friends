package com.plazonic.tomislav.yambfriends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_CAPTURE_AN_IMAGE = 13;
    public static final int RC_CHOOSE_AN_IMAGE = 27;

    private SharedPreferences settings;
    private String username;
    private ImageView ivProfileImage;
    private TextView tvProfileImageInfo;
    private Uri fileUri;
    private ProgressDialog progressDialog;
    private RestApi restApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        username = settings.getString("username", null);

        ivProfileImage = (ImageView) findViewById(R.id.profile_image);
        tvProfileImageInfo = (TextView) findViewById(R.id.profile_image_info);

        findViewById(R.id.capture_an_image_button).setOnClickListener(this);
        findViewById(R.id.choose_an_image_button).setOnClickListener(this);
        findViewById(R.id.my_stats_button).setOnClickListener(this);

        restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);

        refreshImageView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_stats_button:
                openMyStats();
                break;
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
            if (resultCode != RESULT_OK && resultCode != RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), R.string.image_capture_failed, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RC_CHOOSE_AN_IMAGE) {
            if (resultCode == RESULT_OK) {
                fileUri = data.getData();
            } else {
                Toast.makeText(getApplicationContext(), R.string.image_selection_failed, Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = uriToBitmap(fileUri);
            uploadImage(bitmapToString(bitmap));
        }
    }

    private Uri getFileUri(){
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

            File imageFile = new File(storageDirectory.getPath() + File.separator + settings.getString("username", null) + ".jpg");
            return Uri.fromFile(imageFile);
        } else return null;
    }

    private void openMyStats() {
        startActivity(new Intent().setClass(getBaseContext(), MyStatsDialogActivity.class));
    }

    private void captureAnImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RC_CAPTURE_AN_IMAGE);
    }

    private void chooseAnImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, RC_CHOOSE_AN_IMAGE);
    }

    private void uploadImage(String image) {
        showProgressDialog("Uploading image...");
        restApi.uploadImage(username, image, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                hideProgressDialog();
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String responseString = bufferedReader.readLine();

                    if (responseString.contains("PHP Error")) {
                        Toast.makeText(getApplicationContext(), R.string.php_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                        refreshImageView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap uriToBitmap(Uri fileUri) {
        Bitmap bitmap = null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } else {
                Toast.makeText(getApplicationContext(), R.string.could_not_read_image_file, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Toast.makeText(getApplicationContext(), R.string.file_not_found, Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
        }

        return bitmap;
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void refreshImageView() {
        showProgressDialog("Loading image...");
        restApi.getUserId(username, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String responseString = bufferedReader.readLine();

                    if (responseString.contains("PHP Error")) {
                        Toast.makeText(getApplicationContext(), R.string.php_error, Toast.LENGTH_SHORT).show();
                    } else if (responseString.equals("User not found.")) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    } else {
                        new LoadImage().execute(responseString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_SHORT).show();
                updateImageUI(null);
            }
        });

    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String userId = params[0];
            String url = "http://ugodnomjesto.net84.net/yambfriends/images/" + userId + ".jpeg";
            Bitmap image = null;

            if (urlConnectionOkay(url)) {
                try {
                    InputStream inputStream = new java.net.URL(url).openStream();
                    image = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            hideProgressDialog();
            updateImageUI(image);
        }
    }

    private void updateImageUI(Bitmap image) {
        if (image != null) {
            ivProfileImage.setImageBitmap(image);
            ivProfileImage.setVisibility(View.VISIBLE);
            tvProfileImageInfo.setVisibility(View.GONE);
        } else {
            ivProfileImage.setVisibility(View.GONE);
            tvProfileImageInfo.setVisibility(View.VISIBLE);
        }
    }

    private boolean urlConnectionOkay(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
