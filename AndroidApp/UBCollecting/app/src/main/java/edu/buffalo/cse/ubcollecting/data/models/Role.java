package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class Role extends Model {

    private static final String TAG = Role.class.getSimpleName().toString();

    public String name;
    public int introRequired;
    public int photoRequired;
    public int onClient;

    public String getIdentifier() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntroRequired() {
        return introRequired;
    }

    public void setIntroRequired(int introRequired) {
        this.introRequired = introRequired;
    }

    public int getPhotoRequired() {
        return photoRequired;
    }

    public void setPhotoRequired(int photoRequired) {
        this.photoRequired = photoRequired;
    }

    public int getOnClient() {
        return onClient;
    }

    public void setOnClient(int onClient) {
        this.onClient = onClient;
    }


}
