package com.andreslab.digitalpark;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    Context context = this;
    FloatingActionButton fab;

    ProgressBar progressBar;
    GridView gridView;
    TextView title;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "CategoryActivity";

    private Double _latittude;
    private Double _longitude;
    private String _namePlace;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String cat = "Mamifero";

    ArrayList<GroupAnimals> grupoAnimales = new ArrayList<GroupAnimals>();

    Boolean ARCoreIsSupport;

    //---------------------------------

    private Location location;
    //private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    //---------------------------------

    ArrayList<String> arrayName = new ArrayList<String>();
    ArrayList<String> arrayLatitude = new ArrayList<String>();
    ArrayList<String> arrayLongitude = new ArrayList<String>();

    double minDistanceMeters = 5.0; //rango de distance en metros
    ArrayList<String> animalNameNearst = new ArrayList<String>();

    void resetArrays(){
        arrayName.clear();
        arrayLatitude.clear();
        arrayLongitude.clear();
        grupoAnimales.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();

        resetArrays();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ARCoreIsSupport = false;

        //maybeEnableArButton();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        progressBar = findViewById(R.id.progressBar);
        gridView = (GridView)findViewById(R.id.gridview);
        title = (TextView)findViewById(R.id.title_category);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                final CategoryAdapter categoryAdapter = new CategoryAdapter(getApplicationContext(), grupoAnimales, animalNameNearst);

                GroupAnimals animal = grupoAnimales.get(position);

                if (animalNameNearst.contains(animal.getName())) {

                    if (ARCoreIsSupport) {
                        Intent i = new Intent(getApplicationContext(), CameraARCoreActivity.class);
                        i.putExtra("latitudes", animal.getLatitude());
                        i.putExtra("longitudes", animal.getLongitude());
                        i.putExtra("name", animal.getName());
                        startActivity(i);
                    }else{
                        /*Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                        i.putExtra("latitudes", animal.getLatitude());
                        i.putExtra("longitudes", animal.getLongitude());
                        i.putExtra("name", animal.getName());
                        startActivity(i);*/

                        Intent i = new Intent(getApplicationContext(), PopUpActivity.class);
                        i.putExtra("name", animal.getName());
                        startActivity(i);
                    }


                    // This tells the GridView to redraw itself
                    // in turn calling your BooksAdapter's getView method again for each cell
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });



        progressBar.setVisibility(View.GONE);


        /*btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); //to show
                showAlertToSaveName();
            }
        });*/

        fab = findViewById(R.id.fab_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); //to show
                showAlertToSaveName();
            }
        });

        resetParams();


        //CATCH GPS TIMER
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


        Bundle extras = getIntent().getExtras();
        int category = 0;
        if(extras != null) {
            category = extras.getInt("category");
            progressBar.setVisibility(View.VISIBLE); //to show
        }else{
            Toast.makeText(context, "A ocurrido un problema", Toast.LENGTH_SHORT).show();
        }

        switch (category) {
            case 0:
                Log.i(TAG, "Category: MAMIFERO");
                cat = "Mamifero";
                title.setText("MAMÍFERO");
                readPlacesFirestore();
                break;
            case 1:
                Log.i(TAG, "Category: AVES");
                cat = "Aves";
                title.setText("AVES");
                readPlacesFirestore();
                break;
            case 2:
                Log.i(TAG, "Category: REPTILES");
                cat = "Reptiles";
                title.setText("RÉPTILES");
                readPlacesFirestore();
                break;
        }


    }

    private void resetParams(){
        _latittude = 0.0;
        _longitude = 0.0;
        _namePlace = "";
        fab.setEnabled(false);
        fab.setAlpha(0.5f);
        progressBar.setVisibility(View.GONE);
    }

    private void activeBtnSave(){
        fab.setEnabled(true);
        fab.setAlpha(1f);
    }

    private void savePlaceFirestore(Double lat, Double log, String namePlace){
        //ADD DATA FIRESTORE

        if (lat != 0.0 && log != 0.0 && lat !=null && log != null){
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("name", namePlace);
            user.put("latitude", lat);
            user.put("longitude", log);

// Add a new document with a generated ID
            db.collection(cat)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            resetParams();
                            readPlacesFirestore();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Error adding document", e);
                            resetParams();
                        }
                    });
        }else{
            Toast toast1 = Toast.makeText(getApplicationContext(), "GPS ERROR...", Toast.LENGTH_SHORT);
            //toast1.setGravity(Gravity.CENTER, , );
            toast1.show();
        }

    }

    private void readPlacesFirestore(){
        //READ DATA FIRESTORE

        db.collection(cat)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG,  "LOADING DATA...");
                            //Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                            ArrayList<Animal> arrayAnimals = new ArrayList<Animal>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(TAG, document.getId() + " => " + document.getData());

                                Double latitude = Double.valueOf(document.getData().get("latitude").toString());
                                Double longitude = Double.valueOf(document.getData().get("longitude").toString());


                                arrayAnimals.add(new Animal(document.getData().get("name").toString(),
                                        latitude,
                                        longitude));

                            }

                            //ArrayList<GroupAnimals> grupoAnimales = new ArrayList<GroupAnimals>();
                            grupoAnimales.clear();
                            ArrayList<String> animalsSave = new ArrayList<String>();
                            Boolean continueProcess = true;

                            for (int i = 0; i < arrayAnimals.size(); i ++){

                                Log.i(TAG, "name: " + arrayAnimals.get(i).getName());

                                continueProcess = true;
                                String name = arrayAnimals.get(i).getName();

                                //si el animal se encuentra en la lista de los animales procesados, activa flag para no continuar el proceso
                                if (animalsSave.contains(name)) {
                                    continueProcess = false;
                                }

                                ArrayList<Double> arrayLatitude = new ArrayList<Double>();
                                ArrayList<Double> arrayLongitude = new ArrayList<Double>();
                                for (int e = 0; e < arrayAnimals.size(); e ++){
                                    //if (e != i) {

                                        if (arrayAnimals.get(e).getName() == name){
                                            double la = arrayAnimals.get(e).getLatitude();
                                            double lo = arrayAnimals.get(e).getLongitude();
                                            arrayLatitude.add(la);
                                            arrayLongitude.add(lo);
                                            double distanceKM = distanceBetweenTwoPoint(_latittude, _longitude, la, lo);
                                            Log.i(TAG, "distance: " + distanceKM);
                                            double distanceMeters = distanceKM * 1000;
                                            if (distanceMeters <= minDistanceMeters) {
                                                Log.i(TAG, "animal cercano: " + name);
                                                if (!animalNameNearst.contains(name)){
                                                    animalNameNearst.add(name);
                                                }
                                            }
                                        }
                                    //}
                                }


                                if (continueProcess) {
                                    animalsSave.add(name);
                                    Double[] a_latitude = new Double[arrayLatitude.size()];
                                    Double[] a_longitude = new Double[arrayLongitude.size()];

                                    Log.i(TAG, "list name: " + name);
                                    Log.i(TAG, "list latitude: " + arrayLatitude.toString());
                                    Log.i(TAG, "list longitude: " + arrayLongitude.toString());

                                    for(int a = 0; a<arrayLatitude.size();a++){
                                        a_latitude[a] = arrayLatitude.get(a);
                                        a_longitude[a] = arrayLongitude.get(a);
                                    }

                                /*Double[] a_latitude = (Double[]) arrayLatitude.toArray();
                                Double[] a_longitude = (Double[]) arrayLongitude.toArray();*/
                                    grupoAnimales.add(new GroupAnimals(name,a_latitude, a_longitude));
                                }

                            }

                            //-----------------------------------------------------

                            /*for (Animal animal_name : arrayAnimals){

                                Log.i(TAG, "name: " + animal_name.getName());

                                continueProcess = true;

                                String name = animal_name.getName();
                                for (String animalSave : animalsSave) {
                                    if (animalSave == name){
                                        continueProcess = false;
                                    }
                                }

                                ArrayList<Double> arrayLatitude = new ArrayList<Double>();
                                ArrayList<Double> arrayLongitude = new ArrayList<Double>();
                                for (Animal animal_locate : arrayAnimals){
                                    if (name == animal_name.getName()) {
                                        arrayLatitude.add(animal_locate.getLatitude());
                                        arrayLongitude.add(animal_locate.getLongitude());
                                    }

                                }

                                if (continueProcess) {
                                    animalsSave.add(name);
                                    Double[] a_latitude = new Double[arrayLatitude.size()];
                                    Double[] a_longitude = new Double[arrayLongitude.size()];

                                    Log.i(TAG, "list name: " + name);
                                    Log.i(TAG, "list latitude: " + arrayLatitude.toString());
                                    Log.i(TAG, "list longitude: " + arrayLongitude.toString());

                                    for(int i = 0; i<arrayLatitude.size();i++){
                                        a_latitude[i] = arrayLatitude.get(i);
                                        a_longitude[i] = arrayLongitude.get(i);
                                    }


                                    grupoAnimales.add(new GroupAnimals(name,a_latitude, a_longitude));
                                }

                            }*/

                            Log.i(TAG, "animal: " + grupoAnimales.get(0).getLatitude().toString());

                            //Animal[] animals = (Animal[]) arrayAnimals.toArray();

                            CategoryAdapter categoryAdapter = new CategoryAdapter(getApplicationContext(), grupoAnimales, animalNameNearst);
                            gridView.setAdapter(categoryAdapter);

                            /*intent.putExtra("listName", arrayName);
                            intent.putExtra("listLatitude", arrayLatitude);
                            intent.putExtra("listLongitude", arrayLongitude);
                            startActivity(intent);*/
                        } else {
                            Log.i(TAG, "Error getting documents.", task.getException());

                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /*private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double latittude = location.getLatitude();
                                Double longitude = location.getLongitude();
                                _latittude = latittude;
                                _longitude = longitude;
                                savePlaceFirestore(_latittude, _longitude, _namePlace);
                                user_location.setText("LAST PLACE : " + _namePlace.toUpperCase());
                            }
                        }
                    });
        }
    }*/

    private void showAlertToSaveName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese el nombre del lugar");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _namePlace = input.getText().toString();
                //fetchLocation();
                savePlaceFirestore(_latittude, _longitude, _namePlace);

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetParams();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{
            }
        }
    }*/


    // CATCH GPS TIMER

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(context, "Ocurrió un error...", Toast.LENGTH_SHORT).show();
            //locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            _latittude = location.getLatitude();
            _longitude = location.getLongitude();
            activeBtnSave();
            Log.i(TAG, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            _latittude = location.getLatitude();
            _longitude = location.getLongitude();
            activeBtnSave();
            Log.i(TAG, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(CategoryActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    //DISTANCE CONTROLLER
    private void checkPlaceInRange(Double minDistance_meters){
        Double minDistanceKm = minDistance_meters / 1000; //convertir metros a kilometros
        Pair<String, Double> data = detectNearstPlaces();
        if (data.second <= minDistanceKm) {
            //esta en el rango
            switch (data.first) {
                case "fayh":
                    Log.i(TAG, "Code: fayh");

                case "come1":
                    Log.i(TAG, "Code come1");

                case "cien":
                    Log.i(TAG, "Code: cien");

                    //test
                case "bom":
                    Log.i(TAG, "code: bom");

            }
        }
    }

    private Pair<String, Double> detectNearstPlaces(){
        String namePlaceNearst = "";
        Double nearstDistance = 200.0; //distancia mas corta, valor random alto, medida en KM
        for (int i = 0; i < arrayName.size(); i++){

            Double endLatitude = Double.valueOf(arrayLatitude.get(i));
            Double endLongitude = Double.valueOf(arrayLongitude.get(i));
            Double distance = distanceBetweenTwoPoint(_latittude, _longitude, endLatitude, endLongitude);

            if (distance < nearstDistance){
                //la nueva distancia es mas pequena que la guardada
                nearstDistance = distance;
                namePlaceNearst = arrayName.get(i);
            }
        }

        return new Pair<String, Double>(namePlaceNearst, nearstDistance);
    }

    public static double distanceBetweenTwoPoint(double startLat, double startLong,
                                                 double endLat, double endLong){
        //KM
        return HaversineDistance.distance(startLat, startLong,
                endLat, endLong);
    }


    //GRAYSCALE
    public static Bitmap grayScaleImage(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            Log.i(TAG, "Soporta ARCORE");
            ARCoreIsSupport = true;
            // indicator on the button.
        } else { // Unsupported or unknown.
            Log.i(TAG, "No Soporta ARCORE");
            ARCoreIsSupport = false;
        }
    }
}