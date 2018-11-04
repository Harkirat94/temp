package com.example.hp.kleanit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hp.kleanit.ComplaintsFragment;
import com.example.hp.kleanit.HelperClasses.AndroidMultiPartEntity;
import com.example.hp.kleanit.HelperClasses.FireUploadDialog;
import com.example.hp.kleanit.HelperClasses.MyLocationListener;
import com.example.hp.kleanit.ProfileFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class MainActivity extends AppCompatActivity implements FireUploadDialog.NoticeDialogListener{

    public static final String COMPLAINT_UPLOAD_URL = "http://10.0.0.4:8000/CleanMyCity/complaintUpload";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    LocationManager locManagerObj;
    MyLocationListener locListenerObj;


    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // To setup Tabs / Fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locManagerObj = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locListenerObj = new MyLocationListener(getBaseContext());
                boolean en = locManagerObj.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (en == true) {
                    Log.d("HK", "Entering loop");
                    //Toast.makeText(this.getActivity().getBaseContext(), "Network Enabled", Toast.LENGTH_SHORT).show();
                    locManagerObj.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 15, 10, locListenerObj);
                }
                if (en == false) {
                    //Toast.makeText(myAct.getBaseContext(), "Network Not Enabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.d("ERROR","ERROR");
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Log.d("Creating","Creating");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        showNoticeDialog();
                    }
                    else{
                        Log.d("FILE","File not Created");
                    }

                    //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
            }
        });
    }


    // Method to generate filename n save camera Image in it.
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();

        return image;
    }

    // OnResult Returns back the Image from Camera
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
           // Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
            Log.d("Correct", mCurrentPhotoPath);
        }
    }

        // Functions to setup Tabs and view pager for Fragments
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ComplaintsFragment(), "Complaints");
        adapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new FireUploadDialog();
        dialog.show(getSupportFragmentManager(), "FireUploadDailog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        new UploadFileToServer().execute();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Toast.makeText(MainActivity.this, "Complaint Cancelled", Toast.LENGTH_SHORT).show();

    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            //progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(COMPLAINT_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity();

                File sourceFile = new File(mCurrentPhotoPath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                SharedPreferences prefsLogin = getSharedPreferences("Login", Context.MODE_PRIVATE);
                String id = prefsLogin.getString("id", null);
                entity.addPart("PersonId", new StringBody(id));

                SharedPreferences prefs = getSharedPreferences("Complaint", Context.MODE_PRIVATE);
                String feature = prefs.getString("feature", null);
                String Desc = prefs.getString("Description", null);
                String lat = prefs.getString("latitude", null);
                String longi = prefs.getString("longitude", null);
                String volunteerReq = prefs.getString("Voluteer_required",null);
                String eventDate = prefs.getString("Date",null);
                String eventTime = prefs.getString("Time",null);


                entity.addPart("Description", new StringBody(Desc));
                entity.addPart("Latitude", new StringBody(lat));
                entity.addPart("Longitude", new StringBody(longi));
                entity.addPart("EventDate", new StringBody(eventDate));
                entity.addPart("EventTime", new StringBody(eventTime));
                entity.addPart("Volunteers_required", new StringBody(volunteerReq));
                entity.addPart("Feature", new StringBody(feature));

                entity.addPart("ThoroughFare", new StringBody(prefs.getString("thoroughFare","-1")));
                entity.addPart("SubLocality", new StringBody(prefs.getString("subLocality","-1")));
                entity.addPart("Locality", new StringBody(prefs.getString("locality","-1")));
                entity.addPart("AdminArea", new StringBody(prefs.getString("adminArea","-1")));
                entity.addPart("PostalCode", new StringBody((prefs.getString("postalCode","-1"))));
                entity.addPart("CountryName", new StringBody(prefs.getString("countryName","-1")));




               // totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("TAGGG", "Response from server: " + result);

            // showing the server response in an alert dialog
            //showAlert(result);

            super.onPostExecute(result);
        }

    }


}
