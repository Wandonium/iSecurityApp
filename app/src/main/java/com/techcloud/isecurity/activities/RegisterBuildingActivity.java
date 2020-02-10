package com.techcloud.isecurity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class RegisterBuildingActivity extends AppCompatActivity {

    static final Integer COURSE_LOC = 0x3;
    static final Integer FINE_LOC = 0x4;
    public static final String TAG = RegisterBuildingActivity.class.getSimpleName();

    private TextInputLayout nameLayout;
    private TextInputLayout streetLayout;
    private TextInputLayout cityLayout;
    private TextInputLayout noOfFloorsLayout;

    private TextInputEditText name;
    private TextInputEditText street;
    private TextInputEditText city;
    private TextInputEditText noOfFloors;

    private AppCompatButton register;
    private NestedScrollView scrollView;
    private TextView backToLogin;

    private String buildingName;
    private String buildingStreet;
    private String buildingCity;
    private int buildingNoOfFloors;
    private float buildingLat;
    private float buildingLong;
    private ApiService apiService;
    private CompositeDisposable disposable;

    private boolean isGpsEnabled, isNetworkLocationEnabled;
    private Location currentLocation;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_building);

        initViews();
        currentLocation = getLocationWithCheckNetworkAndGPS(this);
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();

        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    dialog.show();
                    final Building building = new Building(buildingName, buildingStreet,
                            buildingCity, buildingNoOfFloors, buildingLong, buildingLat);
                    disposable.add(apiService
                            .createBuilding(building)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                @Override
                                public void onSuccess(JsonObject jsonObject) {
                                    dialog.dismiss();
                                    Log.d(TAG, "Response: " + jsonObject);
                                    Toast.makeText(RegisterBuildingActivity.this, "Success! Building Created! Please remember to register companies and guards for this building!", Toast.LENGTH_SHORT).show();
                                    int building_id = jsonObject.get("id").getAsInt();
                                    System.out.println("Building ID: " + building_id);
                                    building.setBuilding_id(building_id);
                                    Intent intent = new Intent(RegisterBuildingActivity.this, AdminHomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    dialog.dismiss();
                                    HolderClass.showError(e, scrollView);
                                }
                            })
                    );
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterBuildingActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        name = findViewById(R.id.textInputEditTextName);
        street = findViewById(R.id.textInputEditTextStreet);
        city = findViewById(R.id.textInputEditTextCity);
        noOfFloors = findViewById(R.id.textInputEditTextNoOfFloors);

        nameLayout = findViewById(R.id.textInputLayoutName);
        streetLayout = findViewById(R.id.textInputLayoutStreet);
        cityLayout = findViewById(R.id.textInputLayoutCity);
        noOfFloorsLayout = findViewById(R.id.textInputLayoutNoOfFloors);

        register = findViewById(R.id.appCompatButtonRegister);

        scrollView = findViewById(R.id.nestedScrollViewRegisterBuilding);
    }

    private boolean validateInput() {
        buildingName = name.getText().toString();
        nameLayout.setError(null);
        buildingStreet = street.getText().toString();
        streetLayout.setError(null);
        buildingCity = city.getText().toString();
        cityLayout.setError(null);
        try {
            buildingNoOfFloors = Integer.parseInt(noOfFloors.getText().toString());
            noOfFloorsLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            noOfFloorsLayout.setError("Error! Enter numbers instead of text");
            return false;
        }
        buildingLong = (float) currentLocation.getLongitude() + 7;
        buildingLat = (float) currentLocation.getLatitude() + 7;
        Log.d(TAG, "Lat: " + buildingLat + "\nLong: " + buildingLong);

        if(buildingNoOfFloors <= 0 || buildingNoOfFloors >= 200){
            noOfFloorsLayout.setError("Error! No. of floors entered is either too small or too large!");
            return false;
        } else {
            noOfFloorsLayout.setError(null);
        }

        if(buildingName.isEmpty()){
            nameLayout.setError("Error! Building name cannot be empty");
            return false;
        }else if(buildingName.length() < 5) {
            nameLayout.setError("Error! Building name too short");
            return false;
        } else {
            nameLayout.setError(null);
        }

        if(buildingStreet.isEmpty()){
            streetLayout.setError("Error! Building street cannot be empty");
            return false;
        }else if(buildingStreet.length() < 5) {
            streetLayout.setError("Error! Building street too short");
            return false;
        }else {
            streetLayout.setError(null);
        }

        if(buildingCity.isEmpty()){
            cityLayout.setError("Error! Building city cannot be empty");
            return false;
        }else if(buildingCity.length() < 5) {
            cityLayout.setError("Error! Building city too short");
            return false;
        }else {
            cityLayout.setError(null);
        }

        return true;
    }

    private Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLoacation = null, gpsLocation = null, finalLoc = null;
        if (isGpsEnabled) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOC);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOC);
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COURSE_LOC);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COURSE_LOC);
                }

            }
            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            showAlert();
        }
        if (isNetworkLocationEnabled) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, FINE_LOC);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, FINE_LOC);
                }
            }
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            showAlert();
        }

        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy())
                return finalLoc = networkLoacation;
            else
                return finalLoc = gpsLocation;

        } else {

            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }
        return finalLoc;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }
}
