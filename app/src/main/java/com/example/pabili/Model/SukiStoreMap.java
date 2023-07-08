package com.example.pabili.Model;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
                                //Log.d("registerGeopoints", gPoint.toString());
                                //assert gPoint != null : " NULL";

                                registerGeopoints(gPoint.getLatitude(), gPoint.getLongitude());

                                // System.out.println(gPoint.getLatitude() + "," +  gPoint.getLongitude());
                                //System.out.println("initializeMAP() = "+gPoint);

                                Log.d("registerGeopoints", document.get("username") + " | Lat:"+gPoint.getLatitude() + ", Long:"+gPoint.getLongitude());
                                //Log.d(TAG, document.getId() + " => " + document.getData());
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

        //GeoLocation center = new GeoLocation(inputLatitude,inputLongitude);
//
        //List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center,rangeMeter);
        //final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        //for(GeoQueryBounds b : bounds){
        //    Query q = db.collection("stores")
        //            .orderBy("geopoint")
        //            .startAt(b.startHash)
        //            .endAt(b.endHash);
        //    tasks.add(q.get());
        //}
//
        //Tasks.whenAllComplete(tasks)
        //        .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
        //                                   @Override
        //                                   public void onComplete(@NonNull Task<List<Task<?>>> t) {
        //                                       List<DocumentSnapshot> matchingDocs = new ArrayList<>();
        //                                       for(Task<QuerySnapshot> task :tasks){
        //                                           QuerySnapshot snap = task.getResult();
        //                                           for(DocumentSnapshot doc : snap.getDocuments()){
        //                                               double lat = doc.getDouble("lat");
        //                                               double lng = doc.getDouble("lng");
        //                                               Log.d("Task","Latitude: " + lat + " | Longtitude: " + lng);
//
        //                                               GeoLocation docLocation = new GeoLocation(lat ,lng);
        //                                               double distanceInM = GeoFireUtils.getDistanceBetween(docLocation,center);
        //                                               if(distanceInM <= rangeMeter){
        //                                                   Log.d("GeoQueory Bounds", "Name: " + doc.get("username") + " | " + doc.getGeoPoint("geopoint"));
        //                                               }
//
        //                                           }
        //                                       }
        //                                   }
        //                               });

            try {
                kdTree.setInstances(instances);
                nearest = kdTree.kNearestNeighbours(instances.get(0), 50);
                for(Instance near: nearest){
                    double lat1 = inputLatitude;
                    double lon1 = inputLongitude;
                    double lat2 = near.value(latitude);
                    double lon2 = near.value(longitude);
                    Log.d("NearValue","Lat: "+lat2+" | Long: "+lon2);
                    double computedDistance = Location.computeDistance(lat1, lon1, lat2, lon2);
                    System.out.println(computedDistance + " <? "+rangeMeter);
                    if (computedDistance < rangeMeter){
                        //arrStores.add(new SukiStore(findStoreName(lat2, lon2), lat2, lon2,  computedDistance));
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
        //CollectionReference stores = db.collection("stores");

        // target gPoint
        GeoPoint targetGpoint = new GeoPoint(lat2, lon2);

        ArrayList<String> storeNames = new ArrayList<>();
        Log.d("targetGpoint", String.valueOf(targetGpoint));
        //db.collection("stores")
        //    .get()
        //    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //        @Override
        //        public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //            if(task.isSuccessful()){
        //                for(QueryDocumentSnapshot doc : task.getResult()){
        //                    Log.d("Checking", "Username: "+doc.getString("username")+" | Geopoint: "+doc.getGeoPoint("geopoint"));
        //                }
        //            }
        //        }
        //    });
//

        // Create a reference to the stores collection
        db.collection("stores")
                //.whereEqualTo("geopoint", targetGpoint)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(document.getString("username"), document.getGeoPoint("geopoint") +" == "+ targetGpoint);
                                GeoPoint toCompare = document.getGeoPoint("geopoint");
                                //if(document.getGeoPoint("geopoint") == targetGpoint){
                                if(toCompare.getLongitude() == targetGpoint.getLongitude() && toCompare.getLatitude() == targetGpoint.getLatitude()){
                                    Log.d(document.getString("username"), "ADD++++++");
                                    storeNames.add(document.getString("username"));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        return new Gson().toJson(storeNames);
    }

    //private static String findStoreDescription
}