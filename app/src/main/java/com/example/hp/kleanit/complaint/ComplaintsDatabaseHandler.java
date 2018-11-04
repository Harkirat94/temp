package com.example.hp.kleanit.complaint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harkirat on 30-Mar-16.
 */
public class ComplaintsDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;           // Version No
    private static final String DATABASE_NAME = "CleanMyCityDb";   // Database Name
    private static final String TABLE_COMPLAINT = "Complaint";     // Labs table name

    // Personal Table Columns names
    private static final String KEY_complaintId = "complaintId";
    private static final String KEY_personId = "personId";
    private static final String KEY_imagePath = "imagePath";
    private static final String KEY_description = "description";
    private static final String KEY_latitude = "latitude";
    private static final String KEY_longitude = "longitude";
    private static final String KEY_volunteers_required = "volunteers_required";
    private static final String KEY_eventDate = "eventDate";
    private static final String KEY_eventTime = "eventTime";
    private static final String KEY_feature = "feature";
    private static final String KEY_thoroughFare = "thoroughFare";
    private static final String KEY_subLocality = "subLocality";
    private static final String KEY_locality = "locality";
    private static final String KEY_adminArea = "adminArea";
    private static final String KEY_postalCode = "postalCode";
    private static final String KEY_countryName = "countryName";

    public ComplaintsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating test table
        String CREATE_COMPLAINT_TABLE = "CREATE TABLE " + TABLE_COMPLAINT + "("
                + KEY_complaintId + " TEXT,"
                + KEY_personId + " TEXT,"
                + KEY_imagePath + " TEXT,"
                + KEY_description + " TEXT,"
                + KEY_latitude + " TEXT,"
                + KEY_longitude + " TEXT,"
                + KEY_volunteers_required + " TEXT,"
                + KEY_eventDate + " TEXT,"
                + KEY_eventTime + " TEXT,"
                + KEY_feature + " TEXT,"
                + KEY_thoroughFare + " TEXT,"
                + KEY_subLocality + " TEXT,"
                + KEY_locality + " TEXT,"
                + KEY_adminArea + " TEXT,"
                + KEY_postalCode + " TEXT,"
                + KEY_countryName + " TEXT"
                +")";
        db.execSQL(CREATE_COMPLAINT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLAINT);

        // Create tables again
        onCreate(db);
    }


    public void myOnUpgrade() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLAINT);

        // Create tables again
        onCreate(db);
    }

    public void insertEnrty(String complaintId, String personId, String imagePath, String description, String latitude, String longitude,
                            String volunteers_required, String eventDate, String eventTime, String feature, String thoroughFare, String subLocality, String locality,
                            String adminArea, String postalCode, String countryName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues insertVal = new ContentValues();
        insertVal.put(KEY_complaintId, complaintId);
        insertVal.put(KEY_personId, personId);
        insertVal.put(KEY_imagePath, imagePath);
        insertVal.put(KEY_description, description);
        insertVal.put(KEY_latitude, latitude);
        insertVal.put(KEY_longitude, longitude);
        insertVal.put(KEY_volunteers_required, volunteers_required);
        insertVal.put(KEY_eventDate, eventDate);
        insertVal.put(KEY_eventTime, eventTime);
        insertVal.put(KEY_feature, feature);
        insertVal.put(KEY_thoroughFare, thoroughFare);
        insertVal.put(KEY_subLocality, subLocality);
        insertVal.put(KEY_locality, locality);
        insertVal.put(KEY_adminArea, adminArea);
        insertVal.put(KEY_postalCode, postalCode);
        insertVal.put(KEY_countryName, countryName);
        db.insert(TABLE_COMPLAINT, null, insertVal);
        db.close(); // Closing database connection
    }


    public void insertEnrtyComplaintWise(Complaint complaint){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues insertVal = new ContentValues();
        insertVal.put(KEY_complaintId, complaint.getComplaintId());
        insertVal.put(KEY_personId, complaint.getPersonId());
        insertVal.put(KEY_imagePath, complaint.getImagePath());
        insertVal.put(KEY_description, complaint.getDescription());
        insertVal.put(KEY_latitude, complaint.getLatitude());
        insertVal.put(KEY_longitude, complaint.getLongitude());
        insertVal.put(KEY_volunteers_required, complaint.getVolunteers_required());
        insertVal.put(KEY_eventDate, complaint.getEventDate());
        insertVal.put(KEY_eventTime, complaint.getEventTime());
        insertVal.put(KEY_feature, complaint.getFeature());
        insertVal.put(KEY_thoroughFare, complaint.getThoroughFare());
        insertVal.put(KEY_subLocality, complaint.getSubLocality());
        insertVal.put(KEY_locality, complaint.getLocality());
        insertVal.put(KEY_adminArea, complaint.getAdminArea());
        insertVal.put(KEY_postalCode, complaint.getPostalCode());
        insertVal.put(KEY_countryName, complaint.getCountryName());
        db.insert(TABLE_COMPLAINT, null, insertVal);
        db.close(); // Closing database connection
    }


    public boolean isEmptyPersonalTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM "+ TABLE_COMPLAINT;
        Cursor mcursor = db.rawQuery(count, null);
        if (mcursor.moveToFirst()){
            int icount = mcursor.getInt(0);
            if(icount>0){
                return false;
            }
            else{
                return true;
            }
        }
        return true;
    }

    // Getting All Contacts
    public List<Complaint> getAllComplaints() {
        List<Complaint> reportList = new ArrayList<Complaint>();

        String selectQuery = "SELECT  * FROM " + TABLE_COMPLAINT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            do {
                Complaint com = new Complaint();
                com.setComplaintId(cursor.getString(cursor.getColumnIndex(KEY_complaintId)));
                com.setPersonId(cursor.getString(cursor.getColumnIndex(KEY_personId)));
                com.setImagePath(cursor.getString(cursor.getColumnIndex(KEY_imagePath)));
                com.setDescription(cursor.getString(cursor.getColumnIndex(KEY_description)));
                com.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_latitude)));
                com.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_longitude)));
                com.setVolunteers_required(cursor.getString(cursor.getColumnIndex(KEY_volunteers_required)));
                com.setEventDate(cursor.getString(cursor.getColumnIndex(KEY_eventDate)));
                com.setEventTime(cursor.getString(cursor.getColumnIndex(KEY_eventTime)));

                com.setFeature(cursor.getString(cursor.getColumnIndex(KEY_feature)));
                com.setThoroughFare(cursor.getString(cursor.getColumnIndex(KEY_thoroughFare)));
                com.setSubLocality(cursor.getString(cursor.getColumnIndex(KEY_subLocality)));
                com.setLocality(cursor.getString(cursor.getColumnIndex(KEY_locality)));
                com.setAdminArea(cursor.getString(cursor.getColumnIndex(KEY_adminArea)));
                com.setPostalCode(cursor.getString(cursor.getColumnIndex(KEY_postalCode)));
                com.setCountryName(cursor.getString(cursor.getColumnIndex(KEY_countryName)));
                // Adding contact to list
                reportList.add(com);
            } while (cursor.moveToNext());
        }

        // return contact list
        return reportList;
    }

    public Complaint getComplaintDetailsById(String complaintId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COMPLAINT, null, KEY_complaintId + "="  + complaintId , null, null, null, null);

        Complaint com = new Complaint();
        cursor.moveToFirst();

        com.setComplaintId(cursor.getString(cursor.getColumnIndex(KEY_complaintId)));
        com.setPersonId(cursor.getString(cursor.getColumnIndex(KEY_personId)));
        com.setImagePath(cursor.getString(cursor.getColumnIndex(KEY_imagePath)));
        com.setDescription(cursor.getString(cursor.getColumnIndex(KEY_description)));
        com.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_latitude)));
        com.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_longitude)));
        com.setVolunteers_required(cursor.getString(cursor.getColumnIndex(KEY_volunteers_required)));
        com.setEventDate(cursor.getString(cursor.getColumnIndex(KEY_eventDate)));
        com.setEventTime(cursor.getString(cursor.getColumnIndex(KEY_eventTime)));

        com.setFeature(cursor.getString(cursor.getColumnIndex(KEY_feature)));
        com.setThoroughFare(cursor.getString(cursor.getColumnIndex(KEY_thoroughFare)));
        com.setSubLocality(cursor.getString(cursor.getColumnIndex(KEY_subLocality)));
        com.setLocality(cursor.getString(cursor.getColumnIndex(KEY_locality)));
        com.setAdminArea(cursor.getString(cursor.getColumnIndex(KEY_adminArea)));
        com.setPostalCode(cursor.getString(cursor.getColumnIndex(KEY_postalCode)));
        com.setCountryName(cursor.getString(cursor.getColumnIndex(KEY_countryName)));

        return com;
    }

}
