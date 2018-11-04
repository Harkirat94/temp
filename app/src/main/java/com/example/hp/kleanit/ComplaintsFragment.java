package com.example.hp.kleanit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.kleanit.HelperClasses.MyLocationListener;
import com.example.hp.kleanit.complaint.Complaint;
import com.example.hp.kleanit.complaint.ComplaintsAdapter;
import com.example.hp.kleanit.complaint.ComplaintsDatabaseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComplaintsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComplaintsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplaintsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //////////
    private final String URL_TO_GET_IMAGE = "http://10.0.0.4:8000/CleanMyCity/image/";
    private static final String URL_address = "http://10.0.0.4:8000/CleanMyCity/getAllComplaints";

    RecyclerView recycleView;
    ProgressDialog progressDialog;
    ComplaintsDatabaseHandler my_db;


    private OnFragmentInteractionListener mListener;
    private Button location ;

    public ComplaintsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplaintsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplaintsFragment newInstance(String param1, String param2) {
        ComplaintsFragment fragment = new ComplaintsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        my_db = new ComplaintsDatabaseHandler(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_complaints, container, false);
        recycleView = (RecyclerView) v.findViewById(R.id.rv);

        new AsyncForGetAllComplaints(getActivity()).execute();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(llm);
        recycleView.setHasFixedSize(true);

        return v;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

  /*  @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class AsyncForGetAllComplaints extends AsyncTask<Void, Void, List<Complaint>> {

        private Context mContext;

        public AsyncForGetAllComplaints(Context ctx){
            mContext = ctx;
        }

        AndroidHttpClient httpClientObj = AndroidHttpClient.newInstance("");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching All Complaints...");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<Complaint> doInBackground(Void... params) {
            HttpGet request = new HttpGet(URL_address);
            JSONParser responseHandler = new JSONParser();
            try {
                List<Complaint> response = httpClientObj.execute(request, responseHandler);
                return response;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Complaint> result) {
            if(httpClientObj != null){
                httpClientObj.close();
            }
            my_db.myOnUpgrade();
            for(Complaint com : result){
                my_db.insertEnrtyComplaintWise(com);
            }
            new AsyncToGetAndRenderImages(mContext).execute(result);
        }

    }

    private class JSONParser implements ResponseHandler<List<Complaint>> {

        @Override
        public List<Complaint> handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            List<Complaint> result = new ArrayList<Complaint>();
            String responseString = new BasicResponseHandler().handleResponse(response);
            JSONArray responseArray;
            try {
                responseArray = (JSONArray) new JSONArray(responseString);
                Complaint incoming_complaint;
                for(int i=0; i<responseArray.length(); i++){
                    incoming_complaint = new Complaint();
                    JSONObject complaintJSON = (JSONObject) responseArray.get(i);
                    incoming_complaint.setComplaintId(complaintJSON.getString("id"));
                    incoming_complaint.setPersonId(complaintJSON.getString("personId"));
                    incoming_complaint.setImagePath(complaintJSON.getString("imagePath"));
                    incoming_complaint.setDescription(complaintJSON.getString("description"));
                    incoming_complaint.setLatitude(complaintJSON.getString("latitude"));
                    incoming_complaint.setLongitude(complaintJSON.getString("longitude"));
                    incoming_complaint.setVolunteers_required(complaintJSON.getString("volunteers_required"));
                    incoming_complaint.setEventDate(complaintJSON.getString("eventDate"));
                    incoming_complaint.setEventTime(complaintJSON.getString("eventTime"));

                    incoming_complaint.setFeature(complaintJSON.getString("feature"));
                    incoming_complaint.setThoroughFare(complaintJSON.getString("thoroughFare"));
                    incoming_complaint.setSubLocality(complaintJSON.getString("subLocality"));
                    incoming_complaint.setLongitude(complaintJSON.getString("locality"));
                    incoming_complaint.setAdminArea(complaintJSON.getString("adminArea"));
                    incoming_complaint.setPostalCode(complaintJSON.getString("postalCode"));
                    incoming_complaint.setCountryName(complaintJSON.getString("countryName"));

                    result.add(incoming_complaint);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private class AsyncToGetAndRenderImages extends AsyncTask<List<Complaint>, Void, Void> {

        private Context mContext;

        public AsyncToGetAndRenderImages(Context ctx){
            mContext = ctx;
        }

        @Override
        protected Void doInBackground(List<Complaint>... params) {
            for(Complaint com : params[0]){
                Bitmap bitmap = null;
                try {
                    URL url = new URL(URL_TO_GET_IMAGE + com.getImagePath());

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, 100, 100);
                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                    File mypath=new File(directory,com.getImagePath());

                    FileOutputStream fos = null;
                    try {

                        fos = new FileOutputStream(mypath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    final SharedPreferences prefs = getActivity().getSharedPreferences("Images", Context.MODE_PRIVATE);
                    Log.i("TAG", "Saving shared preferes, id :");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(com.getImagePath(), mypath.getAbsolutePath());
                    Log.d("Putting key", com.getImagePath());
                    Log.d("Putting value",mypath.getAbsolutePath());
                    editor.commit();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            ArrayList<Complaint> allComplaints = new ArrayList<Complaint>();
            allComplaints = (ArrayList<Complaint>) my_db.getAllComplaints();
            recycleView.setAdapter(new ComplaintsAdapter(mContext, allComplaints, new ComplaintsAdapter.MyOnItemClickListener() {
                @Override
                public void onItemClick(Complaint item) {
                    Toast.makeText(getActivity().getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                    /*Intent i = new Intent();
                    //i.putExtra("xth", xthlineNo);
                    i.setAction(".ComplaintActivity");
                    startActivity(i);*/
                }
            }));
        }

        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

    }
}