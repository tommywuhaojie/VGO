package v_go.version10.ActivityClasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import v_go.version10.ApiClasses.User;
import v_go.version10.HelperClasses.Global;
import v_go.version10.R;

public class SignUp3 extends AppCompatActivity {

    private ImageView imgView;
    private static final int GALLERY_RESULT = 100;
    private static final int CAMERA_RESULT = 200;
    private Uri target_uri;
    private Uri uriFromCamera;
    private boolean isPicturePicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_3);

        imgView = (ImageView) findViewById(R.id.avator);
    }

    public void onGalleryClicked(View view){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
            startActivityForResult(intent, GALLERY_RESULT);
        } catch (ActivityNotFoundException e) {

        }
    }

    public void onCameraClicked(View view){
        try {
            uriFromCamera = Uri.fromFile(getOutputMediaFile());
            System.out.println("uri" + uriFromCamera.toString());
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFromCamera);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_RESULT);
            }
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "VGO_IMAGE");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("fail to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "AVATAR_"+ timeStamp + ".jpg");
        return mediaFile;
    }

    public void onSetClicked(View view){

        if(!isPicturePicked){
            Toast.makeText(this, "Please pick a picture", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setMessage("Uploading...");
        pDialog.show();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Bitmap bitmap = getBitmapFromUri(target_uri);
                    final int RESIZE = 256;
                    bitmap = Bitmap.createScaledBitmap(bitmap, RESIZE, RESIZE, false);
                    JSONObject jsonObject = User.UploadAvatar(bitmap);
                    System.out.println(jsonObject.toString());
                    Global.NEED_TO_DOWNLOAD_TAB_D_AVATAR = true;

                    // temporary use the same ui(signUp3) for update avatar
                    if(getIntent().getBooleanExtra("isUpdate", false)){
                        SignUp3.this.finish();
                        return;
                    }

                    Intent intent = new Intent(SignUp3.this, Main.class);
                    startActivity(intent);
                    pDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();

    }

    public void onSkipClicked(View view){
        // temporary use the same ui(signUp3) for update avatar
        if(getIntent().getBooleanExtra("isUpdate", false)){
            this.finish();
            return;
        }

        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // from Camera
            if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {

                // start cropping activity
                System.out.println("intent not null");
                CropImage.activity(uriFromCamera)
                        .setFixAspectRatio(true)
                        .start(this);

            // from gallery
            }else if (requestCode == GALLERY_RESULT && resultCode == RESULT_OK
                    && data != null) {

                // start cropping activity
                CropImage.activity(data.getData())
                        .setFixAspectRatio(true)
                        .start(this);

            // start cropping
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        target_uri = result.getUri();

                        Bitmap bitmap = getBitmapFromUri(target_uri);
                        bitmap = Global.getCircularBitmap(bitmap);
                        imgView.setImageBitmap(bitmap);

                        isPicturePicked = true;

                        // on cropping error
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        Toast.makeText(this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws Exception{
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void onBackArrowClicked(View view){
        onBackPressed();
    }
}
