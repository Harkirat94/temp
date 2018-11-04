package com.example.hp.kleanit.complaint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.kleanit.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Harkirat on 29-Mar-16.
 */
public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintViewHolder> {

    private final String URL_TO_GET_IMAGE = "http://192.168.49.199:8000/CleanMyCity/image/";
    private List<Complaint> allComplaints;
    private MyOnItemClickListener listener;
    private Context mContext;

    public ComplaintsAdapter(Context ctx, List<Complaint> allComplaints, MyOnItemClickListener listener) {
        this.allComplaints = allComplaints;
        this.listener = listener;
        this.mContext = ctx;
    }

    public interface MyOnItemClickListener {
        void onItemClick(Complaint item);
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textViewComplaintSubLocality;
        TextView textViewComplaintEventDate;
        TextView textViewComplaintDescription;
        ImageView imageViewComplaintImage;

        ComplaintViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            textViewComplaintSubLocality = (TextView)itemView.findViewById(R.id.id_subLocality);
            textViewComplaintEventDate = (TextView)itemView.findViewById(R.id.id_eventDate);
            textViewComplaintDescription = (TextView)itemView.findViewById(R.id.id_description);
            imageViewComplaintImage = (ImageView)itemView.findViewById(R.id.id_image);
        }

        public void bind(final Complaint item, final MyOnItemClickListener listener) {
            //textViewComplaintDescription.setText(item.getDescription());
            //textViewComplaintLocality.setText(item.getLocality());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public ComplaintsAdapter.ComplaintViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ComplaintViewHolder pvh = new ComplaintViewHolder(v);
        return pvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ComplaintViewHolder holder, int position) {
        holder.textViewComplaintSubLocality.setText(allComplaints.get(position).getSubLocality());
        holder.textViewComplaintSubLocality.setTypeface(null, Typeface.BOLD);
        holder.textViewComplaintEventDate.setText(allComplaints.get(position).getEventDate());
        holder.textViewComplaintEventDate.setTextColor(Color.parseColor("#D3D3D3"));
        /*String fullDesp = allComplaints.get(position).getDescription();
        String setDesp;
        if(fullDesp.length()>90){
            StringBuilder temp = new StringBuilder(fullDesp.substring(0,31));
            temp.append("...");
            setDesp = temp.toString();
        }
        else{
            setDesp = fullDesp;
        }
        holder.textViewComplaintDescription.setText(setDesp);*/
        holder.textViewComplaintDescription.setText(allComplaints.get(position).getDescription());
        SharedPreferences dirPrefs = mContext.getSharedPreferences("Images", Context.MODE_PRIVATE);
        String imagePathStr = dirPrefs.getString(allComplaints.get(position).getImagePath(), null);
        Log.d("Getting key", allComplaints.get(position).getImagePath());
        Log.d("Getting value", imagePathStr);
        // setting size of imagebox //
        ViewGroup.LayoutParams layoutParams = holder.imageViewComplaintImage.getLayoutParams();
        layoutParams.width = 125;
        layoutParams.height = 125;
        holder.imageViewComplaintImage.setLayoutParams(layoutParams);
        // setting size of imagebox //
        File imageFilePath = new File(imagePathStr);
        try {
            Bitmap bitmap = null;
            FileInputStream fis = new FileInputStream(imageFilePath);
           /* BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;*/
            bitmap = BitmapFactory.decodeStream(fis);

            holder.imageViewComplaintImage.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        holder.bind(allComplaints.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return allComplaints.size();
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

