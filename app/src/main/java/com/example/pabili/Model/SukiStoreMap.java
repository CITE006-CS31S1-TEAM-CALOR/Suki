package com.example.pabili.Model;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.GeoPoint;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;


public class SukiStoreMap {

    static ArrayList<Attribute> storeGeopoints;
    static Instances instances;
    static Attribute latitude;
    static Attribute longitude;
    static KDTree kdTree;
    static Instances nearest;
    static Boolean isFirebaseInitialize = false;
    static FirebaseOptions options;
    static FileInputStream serviceAccount;

    static FirebaseFirestore db;

    public static void initializeMap() throws Exception {
        initializeInstances();
        db = FirebaseFirestore.getInstance();

        // Create a reference to the stores collection
        db.collection("stores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint gPoint = document.getGeoPoint("geopoint");

                                registerGeopoints(gPoint.getLatitude(), gPoint.getLongitude());
                                // System.out.println(gPoint.getLatitude() + "," +  gPoint.getLongitude());
                                //System.out.println(gPoint);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public static void initializeInstances() throws Exception{
        storeGeopoints = new ArrayList<>();
        latitude = new Attribute("latitude");
        storeGeopoints.add(latitude);
        longitude = new Attribute("longitude");
        storeGeopoints.add(longitude);

        instances = new Instances("test", storeGeopoints, 50);
    }

    public static void registerGeopoints(double inputLatitude, double inputLongitude){
        try {
            Instance i = new DenseInstance(2);
            i.setValue(latitude, inputLatitude);
            i.setValue(longitude, inputLongitude);
            instances.add(i);
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public static String findNearestStore(double inputLatitude, double inputLongitude , double rangeMeter) {

        ArrayList<SukiStore> arrStores = new ArrayList<>();

        String jsonNearestStores = "";

        Instance i = new DenseInstance(2);
        i.setValue(latitude, inputLatitude) ;
        i.setValue(longitude, inputLongitude);
        instances.add(0, i);
        kdTree = new KDTree();

        try {
            kdTree.setInstances(instances);
            nearest = kdTree.kNearestNeighbours(instances.get(0), 50);
            for(Instance near: nearest){

                double lat1 = inputLatitude;
                double lon1 = inputLongitude;
                double lat2 = near.value(latitude);
                double lon2 = near.value(longitude);

                double computedDistance = Location.computeDistance(lat1, lon1, lat2, lon2);

                if (computedDistance < rangeMeter){
                    arrStores.add(new SukiStore(findStoreName(lat2, lon2), lat2, lon2,  computedDistance));
                }

                instances.remove(0);

                jsonNearestStores = new Gson().toJson(arrStores);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonNearestStores;
    }

    private static String findStoreName(double lat2, double lon2) throws InterruptedException, ExecutionException {
        // Use the shorthand notation to retrieve the default app's services

        // Create a reference to the stores collection
        CollectionReference stores = db.collection("stores");

        // target gPoint
        GeoPoint targetGpoint = new GeoPoint(lat2, lon2);

        ArrayList<String> storeNames = new ArrayList<>();


        // Create a reference to the stores collection
        db.collection("stores").whereEqualTo("geopoint", targetGpoint)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                storeNames.add(document.getString("username"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        return new Gson().toJson(storeNames);
    }
}