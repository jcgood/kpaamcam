package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */
public class Question extends Model {

    private static final String TAG = Question.class.getSimpleName().toString();

    public String type;

    public String displayText;

    public double version;
    public String notes;
    public int deleted;

    public int minLength;
    public int maxLength;

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
        return displayText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public void setMinLength(int min_length) {
      this.minLength = min_length;
    }

    public void setMaxLength(int max_length) {
    this.maxLength = max_length;
  }

    public int getMinLength() {
      return minLength;
    }

    public int getMaxLength() {
    return maxLength;
  }

}
