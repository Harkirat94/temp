package com.example.hp.kleanit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hp.kleanit.complaint.Complaint;
import com.example.hp.kleanit.complaint.ComplaintsAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//import com.example.hp.kleanit


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageView profileImage;
    Bitmap bitmap;
    ProgressDialog pDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String imageUrl = prefs.getString("profile_pic", "null");
        profileImage = (ImageView) v.findViewById(R.id.profile_image);
        new LoadImage().execute(imageUrl);

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

        return v;

       /*Bitmap bitmap = null;
        try {
            SharedPreferences prefs = this.getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
            String imageUrl = prefs.getString("profile_pic", "null");

            URL url = new URL(imageUrl);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            // Calculate inSampleSize
            /*options.inSampleSize = calculateInSampleSize(options, 100, 100);
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
        }*/

    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                profileImage.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(getActivity().getBaseContext(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
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
        //Toast.makeText(this.getActivity().getBaseContext(), "Network Enabled", Toast.LENGTH_SHORT).show();

        void onFragmentInteraction(Uri uri);
    }
}
