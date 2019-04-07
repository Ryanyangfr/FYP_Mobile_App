package com.smu.engagingu.Objects;
/*
 * Hotspot object to store location name, longitude, latitude and its respective narrative
 * this is used for display location pins on the homepage google map
 */
public class Hotspot {
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String narrative;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public Hotspot(String locationName, Double latitude, Double longitude, String narrative) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.narrative = narrative;

    }
}
