package edu.buffalo.cse.ubcollecting.ui;

/**
 * Interface between FireBaseSynch and any Activities/Fragments that need to be updated when new data is synchronized to the client from the cloud.
 */
public interface SyncCallback {

    public void displayUpdateBanner();
}
