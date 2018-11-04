package com.example.hp.kleanit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.net.http.AndroidHttpClient;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hp.kleanit.HelperClasses.MyEventListener;
import com.example.hp.kleanit.HelperClasses.ProfileDetails;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;


// Facebook Logins
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity implements MyEventListener{

    static ProfileDetails userProfile = new ProfileDetails();

    // Variables for Auth Login
    ImageButton loginButton;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    protected static final int PICK_ACCOUNT_REQUEST = 10;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
    String ACCESS_TOKEN = null;
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    String chosenAccount = null;

    private static final String Profile_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String email_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private static final String SCOPE = "oauth2:" + Profile_SCOPE + " " + email_SCOPE;
    private static final String TAG = "Profile Activity";

    CountDownTimer timer;

    //FB Login
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "Inside Login Screen");

        // 1 Time Task to get FB Key Hash
        // Dont need to do it now
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.hp.kleanit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
               // System.out.println("YourKeyHash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        // OAuth Functionality
        // Check From Shared Pref if already Logged in !

        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        boolean isRegistered = prefs.getBoolean("is_logged_in", false);
        Intent i = new Intent();
        if (isRegistered) {
            i.setAction(".MainActivity");
            startActivity(i);
            finish();
        }

        // Create the Login Button
       /* loginButton = (ImageButton) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] accountTypes = new String[]{"com.google"};
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        accountTypes, false, null, null, null, null);
                startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
            }

        });*/

        // Gmail Login FAB (Might have to change to button he agar fb wala is button)
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] accountTypes = new String[]{"com.google"};
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        accountTypes, false, null, null, null, null);
                startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });


        // FB login
        fbLoginButton = (LoginButton) findViewById(R.id.login_button_fb);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
                System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Login Successful");

                // Getting Profile Details
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Getting Profile Details...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i(TAG, "AccessToken:"+accessToken);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "GraphResponse:"+response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        final SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);

                        Log.i(TAG, "Saving shared preferes,");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.putString("id", bFacebookData.getString("id"));
                        editor.putString("name", bFacebookData.getString("first_name") + bFacebookData.getString("last_name"));
                        editor.putString("email", bFacebookData.getString("email"));
                        editor.putString("gender", bFacebookData.getString("gender"));
                        editor.putString("birthday", bFacebookData.getString("birthday"));
                        editor.putString("profile_pic", bFacebookData.getString("profile_pic"));

                        Log.d(TAG, "profile_pic" + bFacebookData.getString("profile_pic"));
                        editor.commit();

                        Intent i = new Intent();
                        i.setAction(".MainActivity");
                        startActivity(i);
                        finish();

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login cancelled by user!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");
                Toast.makeText(LoginActivity.this, "Login Failed! Try Again", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Facebook Login Cancel ");

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Login unsuccessful!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");
                Log.i(TAG, "Facebook Login Error ");
            }
        });

        fab.hide();

        timer = new CountDownTimer(8000, 5000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                fab.show();

            }
        }.start();

    }

    // Getting the FB Data Function
    protected Bundle getFacebookData(JSONObject object) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");
            URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=150&height=");
            Log.i(TAG, "Profile Pic"+profile_pic + "");
            bundle.putString("profile_pic", profile_pic.toString());

            bundle.putString("id", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


// OAuth Functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "Pick an Account to Sign-in", Toast.LENGTH_SHORT).show();
            }
        }
        // Handle the result from exceptions
        else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            getUsername();
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername() {
        if (mEmail == null) {
            //Toast.makeText(this, "Username is null; No authentication will be performed", Toast.LENGTH_SHORT).show();
        } else {
            if (isDeviceOnline()) {
                new GetUsernameTask(LoginActivity.this, mEmail, SCOPE,this).execute();
            } else {
                Toast.makeText(this, "Not Online, Check Connection and Try Again", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean isDeviceOnline() {
        ConnectivityManager connectMgrObj = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //boolean ans = helpObj.isConnectedToNetwork(connectMgrObj);
        NetworkInfo networkInfo = connectMgrObj.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }

    }


    // The Asyn Task Class that makes the network request to get the Token from the emailId
    public class GetUsernameTask extends AsyncTask<Void, Void, String> {
        Activity mActivity;
        String mScope;
        String mEmail;
        private MyEventListener callback;

        GetUsernameTask(Activity activity, String name, String scope, MyEventListener cb) {
            this.mActivity = activity;
            this.mScope = scope;
            this.mEmail = name;
            callback = cb;
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    ACCESS_TOKEN = token;
                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.

                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.

            }
            return null;
        }

        /**
         * Gets an authentication token from Google and handles any
         * GoogleAuthException that may occur.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);

            } catch (UserRecoverableAuthException userRecoverableException) {
                Log.i("Oauth", "No valid Google Play Services APK found.");
                // GooglePlayServices.apk is either old, disabled, or not present
                // so we need to show the user some UI in the activity to recover.
                ((LoginActivity) mActivity).handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                // Some other type of unrecoverable exception has occurred.
                // Report and log the error as appropriate for your app.
                Log.i("oauth", "Some other type of unrecoverable exception has occurred.");
            }
            return null;
        }

        // On Post Execute here if u wanna add?
        @Override
        protected void onPostExecute(String result) {
          //  Toast.makeText(LoginActivity.this, ACCESS_TOKEN, Toast.LENGTH_LONG).show();
            if(callback != null) {
                callback.onEventCompleted();
            }
            //new AndroidHTTPGet().execute(result);
            //tv1.setText(result);

        }
    }

    // Handle Execeptions From OAuth
    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                }
            }
        });
    }





    @Override
    public void onEventCompleted() {
        // TODO Auto-generated method stub
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(ACCESS_TOKEN != null)
        {
            new AndroidHTTPGet(LoginActivity.this).execute(ACCESS_TOKEN);
        }
        else{
            Toast.makeText(getApplicationContext(), "Problem in registering account", Toast.LENGTH_SHORT).show();
        }
    }


    // Function to get UserInfo (Name , Picture , Etc ) From GoogleApi Using the token
    private class AndroidHTTPGet extends AsyncTask<String, Void, ProfileDetails>{

        private MyEventListener callback;
        AndroidHTTPGet(MyEventListener cb) {
            callback = cb;
        }

        private static final String URL_address = "https://www.googleapis.com/oauth2/v2/userinfo";

        AndroidHttpClient httpClientObj = AndroidHttpClient.newInstance("");

        @Override
        protected ProfileDetails doInBackground(String... params) {
            String APIrequest = URL_address+ "?access_token=" + params[0];
            HttpGet request = new HttpGet(APIrequest);
            JSONParser responseHandler = new JSONParser();
            try {
                ProfileDetails response = httpClientObj.execute(request, responseHandler);
                return response;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(ProfileDetails result) {
            if(httpClientObj != null){
                userProfile.name = result.name;
                userProfile.picture = result.picture;
                userProfile.email = result.email;
                userProfile.id = result.id;

                final SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
                Log.i(TAG, "Saving shared preferes, id :"+userProfile.id);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putString("id", userProfile.id);
                editor.putString("name",userProfile.name);
                editor.putString("email",userProfile.email);
                Log.d("HK","email is"+userProfile.email);
                editor.commit();

                httpClientObj.close();
                // Now Sending POST to Harry Server to save in Student Model
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("name", userProfile.name);
                data.put("email", userProfile.email);
                data.put("googleId", userProfile.id);


                Intent i = new Intent();
                i.setAction(".MainActivity");
                startActivity(i);
                finish();
                /*AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);
                asyncHttpPost.setListener(new AsyncHttpPost.Listener(){
                    @Override
                    public void onResult(String result) {
                        if (result.startsWith("ERROR")){
                            Toast.makeText(LoginActivity.this, result+"!Login Again", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, result+"!Login Again", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent();
                            i.setAction(".ProfileActivity");
                            startActivity(i);
                            finish();

                        }
                        // do something, using return value from network

                    }
                });
                asyncHttpPost.execute(STUDENT_CREATE_URL);
                */
            }


        }

        private class JSONParser implements ResponseHandler<ProfileDetails> {

            @Override
            public ProfileDetails handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                ProfileDetails receivedProfile = new ProfileDetails();

                String responseString = new BasicResponseHandler().handleResponse(response);
                JSONObject receivedJson;
                try {
                    receivedJson = (JSONObject) new JSONTokener(responseString).nextValue();
                    receivedProfile.name = (String) receivedJson.get("name");
                    receivedProfile.email = (String) receivedJson.get("email");
                    receivedProfile.id = receivedJson.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return receivedProfile;
            }
        }
    }



    private class RegisterHTTPGet extends AsyncTask<String, Void, ProfileDetails>{

        private MyEventListener callback;
        RegisterHTTPGet(MyEventListener cb) {
            callback = cb;
        }

        private static final String URL_address = "http://medlabs.tk/registerCustomer";

        AndroidHttpClient httpClientObj = AndroidHttpClient.newInstance("");

        @Override
        protected ProfileDetails doInBackground(String... params) {
            // take values
            SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
            String rName = prefs.getString("name", null);
            String rEmail = prefs.getString("email", null);
            String rname = rName.replaceAll(" ", "+");

            String APIrequest = URL_address+ "?emailId="+ rEmail + "&authCode=" + ACCESS_TOKEN + "&name=" +rname;
            //String APIrequest = URL_address+ "?emailId=test&authcode=test&name=test";
            HttpGet request = new HttpGet(APIrequest);
            Log.d("HK",APIrequest);
            Log.d("HK","requ gone");
            JSONParser2 responseHandler = new JSONParser2();
            try {
                ProfileDetails response = httpClientObj.execute(request, responseHandler);
                return response;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(ProfileDetails result) {
            if(httpClientObj != null){
                httpClientObj.close();
            }
        }

        private class JSONParser2 implements ResponseHandler<ProfileDetails>{

            @Override
            public ProfileDetails handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                String responseString = new BasicResponseHandler().handleResponse(response);
                JSONObject responseObject;
                try {
                    responseObject = new JSONObject(responseString.toString());
                    String reply;
                    reply = (String) responseObject.get("Created");
                    Log.d("asa",reply);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                return null;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
