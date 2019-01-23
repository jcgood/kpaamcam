package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class Language extends Model {

    private static final String TAG = Language.class.getSimpleName().toString();


    public String name;
    public String description;
    public String otherNames;
    public String typeId;

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

    @Override
    public String getIdentifier() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Language) {
            Language lang = (Language) obj;
            return lang.getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
