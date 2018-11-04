package com.example.hp.kleanit.complaint;

import java.util.List;

/**
 * Created by Harkirat on 29-Mar-16.
 */
public class Complaint {

    public String complaintId;
    public String personId;
    public String imagePath;
    public String description;
    public String latitude;
    public String longitude;
    public String volunteers_required;
    public String eventDate;
    public String eventTime;
    public String feature;
    public String thoroughFare;
    public String subLocality;
    public String locality;
    public String adminArea;
    public String postalCode;
    public String countryName;
    //public List<String> volunteersEnrolled;


    public String getComplaintId() {
        return complaintId;
    }
    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeature() {
        return feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public String getThoroughFare() {
        return thoroughFare;
    }
    public void setThoroughFare(String thoroughFare) {
        this.thoroughFare = thoroughFare;
    }
    public String getSubLocality() {
        return subLocality;
    }
    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }
    public String getLocality() {
        return locality;
    }
    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getAdminArea() {
        return adminArea;
    }
    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getVolunteers_required() {
        return volunteers_required;
    }
    public void setVolunteers_required(String volunteers_required) {
        this.volunteers_required = volunteers_required;
    }
    public String getEventDate() {
        return eventDate;
    }
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}

