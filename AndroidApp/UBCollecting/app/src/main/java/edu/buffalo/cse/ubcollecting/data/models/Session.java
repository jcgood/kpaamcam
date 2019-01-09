package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class Session extends Model {

    private static final String TAG = Session.class.getSimpleName().toString();

    //  Need to appropriately configure type for start time
    public String label;
    public String name;
    public String startTime;
    public String location;
    public String description;
    public String fieldTripId;

    public String getIdentifier() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFieldTripId() {
        return fieldTripId;
    }

    public void setFieldTripId(String fieldTripId) {
        this.fieldTripId = fieldTripId;
    }


}
