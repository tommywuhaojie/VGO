package v_go.version10.ActivityClasses;

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
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileDescriptor;
import v_go.version10.ApiClasses.User;
import v_go.version10.R;

public class SignUp3 extends AppCompatActivity {

    private static final int GALLERY_RESULT = 100;
    private static final int CAMERA_RESULT = 200;
    private Uri target_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_3);
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_RESULT);
        }
    }

    public void onSetClicked(View view){

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Bitmap bitmap = getBitmapFromUri(target_uri);
                    final int RESIZE = 256;
                    bitmap = Bitmap.createScaledBitmap(bitmap, RESIZE, RESIZE, false);
                    User.UploadAvatar(bitmap);

                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        });
        networkThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // from Camera
            if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK
                    && null != data) {

                // start cropping activity
                CropImage.activity(data.getData())
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

                        ImageView imgView = (ImageView) findViewById(R.id.avator);
                        Bitmap bitmap = getBitmapFromUri(target_uri);

                        bitmap = getCircularBitmap(bitmap);
                        imgView.setImageBitmap(bitmap);

                        // on cropping error
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        Toast.makeText(this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
            }else{
                Toast.makeText(this, "You didn't pick a picture",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                    .show();
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

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float r = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
