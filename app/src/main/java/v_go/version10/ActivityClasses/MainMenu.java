package v_go.version10.ActivityClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

import v_go.version10.R;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        Boolean isFacebookLogin = intent.getBooleanExtra("isFacebookLogin", false);

        if(isFacebookLogin){
            TextView textView = (TextView) findViewById(R.id.user_name);

            String name = intent.getStringExtra("name").trim();
            name = name.substring(0, name.indexOf(" "));
            textView.setText("Hello, " + name);
            final String[] id = new String[1];
            id[0] = intent.getStringExtra("id");

            class MyAsyncTask extends AsyncTask<Void, Void, String>
            {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected String doInBackground(Void... params)
                {
                    try {
                        URL image_value = new URL("https://graph.facebook.com/"+id[0]+"/picture?width=200&height=200" );
                        final Bitmap profPict = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = (ImageView) findViewById(R.id.icon);
                                imageView.setImageBitmap(profPict);
                            }
                        });

                    }catch (Exception e){

                    }
                    return null;
                }
                @Override
                protected void onPostExecute (String result){

                }
            }

            new MyAsyncTask().execute();

        }else{

            /**...............if NOT Facebook login.......................*/

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.logout){
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("logout", true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scheduleIsClicked(View view){
        Intent intent = new Intent(this, Schedule.class);
        startActivity(intent);
    }

    public void newTripIsClicked(View view){

        if(isNetworkAvailable()) {
            Intent intent = new Intent(this, NewTrip.class);
            startActivity(intent);
        }else{
            //CHECK INTERNET CONNECTION
            AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
            alertDialog.setTitle("NO INTERNET CONNECTION");
            alertDialog.setMessage("Please check your network connection and try again.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.show();
        }
    }

    public void findTripIsClicked(View view){
        Intent intent = new Intent(this, FindTrip.class);
        startActivity(intent);
    }

    // Check Internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
