package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class QuestionPropertyDef extends Model {

    private static final String TAG = QuestionPropertyDef.class.getSimpleName().toString();

    public String name;

    public double version;
    public String notes;
    public int deleted;

    public double getVersion(){
        return version;
    }

    public void setVersion(double version){
        this.version=version;
    }

    public int getDeleted(){
        return deleted;
    }

    public void setDeleted(int deleted){
        this.deleted=deleted;
    }

    public void setNotes(String note){
        this.notes=note;
    }

    public String getNotes(){
        return  notes;
    }

    public String getIdentifier() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuestionPropertyDef) {
            QuestionPropertyDef quesPropDef = (QuestionPropertyDef) obj;
            return quesPropDef.getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
