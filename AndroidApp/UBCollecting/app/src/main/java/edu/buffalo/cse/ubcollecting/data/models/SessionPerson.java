package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class SessionPerson extends Model {

    private static final String TAG = SessionPerson.class.getSimpleName().toString();

    public String sessionId;
    public String personId;
    public String roleId;

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
        //TODO
        return "SESSION PERSON";
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionPerson that = (SessionPerson) o;

        if (!sessionId.equals(that.sessionId)) return false;
        if (!personId.equals(that.personId)) return false;
        return roleId.equals(that.roleId);
    }

    @Override
    public int hashCode() {
        int result = sessionId.hashCode();
        result = 31 * result + personId.hashCode();
        result = 31 * result + roleId.hashCode();
        return result;
    }

}
