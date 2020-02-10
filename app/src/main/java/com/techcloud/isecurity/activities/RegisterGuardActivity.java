package com.techcloud.isecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterGuardActivity extends AppCompatActivity {

    public static final String TAG = RegisterGuardActivity.class.getSimpleName();

    private TextInputLayout guardIdLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout phoneNoLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout securityCompanyLayout;

    private TextInputEditText name;
    private TextInputEditText guardId;
    private TextInputEditText phoneNo;
    private TextInputEditText password;
    private TextInputEditText securityCompany;

    private AppCompatButton register;
    private NestedScrollView scrollView;
    private TextView backToLogin;
    private Spinner spinner;

    private int guard_Id;
    private String guardName;
    private long guardPhoneNo;
    private String guardPassword;
    private String guardSecurityCompany;
    private int theBuildingId;

    private ApiService apiService;
    private CompositeDisposable disposable;
    private List<Building> buildingList;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guard);

        initViews();
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        buildingList = new ArrayList<>();

        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();


        dialog.show();
        disposable.add(apiService
        .getBuildings(Guard.jwtToken)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<JsonArray>() {
            @Override
            public void onSuccess(JsonArray jsonElements) {
                dialog.dismiss();
                Log.d(TAG, "Response: " + jsonElements);
                for(Object object: jsonElements) {
                    JsonObject jsonObject = (JsonObject) object;
                    String city = jsonObject.get("city").toString();
                    int id = jsonObject.get("id").getAsInt();
                    float latitude = jsonObject.get("latitude").getAsFloat();
                    float longitude = jsonObject.get("longitude").getAsFloat();
                    String name = jsonObject.get("name").toString();
                    int no_of_floors = jsonObject.get("no_of_floors").getAsInt();
                    String street = jsonObject.get("street").getAsString();

                    city = city.replaceAll("^\"|\"$", "");
                    name = name.replaceAll("^\"|\"$", "");
                    street = street.replaceAll("^\"|\"$", "");

                    Building building = new Building(name, street, city, no_of_floors, longitude, latitude);
                    building.setBuilding_id(id);
                    buildingList.add(building);
                }

                final ArrayList<String> buildingNames = new ArrayList<>();
                for(Building b: buildingList){
                    buildingNames.add(b.getName() + ", " + b.getCity());
                }

                buildingNames.set(0, "Choose Building:");

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(RegisterGuardActivity.this,
                        android.R.layout.simple_spinner_item, buildingNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        buildingNames.set(0, buildingList.get(0).getName() + ", " + buildingList.get(0).getCity());
                        Building selectedBuilding = buildingList.get(position);
                        theBuildingId = selectedBuilding.getBuilding_id();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast toast=Toast.makeText(RegisterGuardActivity.this, "Error! Building cannot be empty", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM,0,0);
                        View view=toast.getView();
                        TextView view1 = view.findViewById(android.R.id.message);
                        view1.setTextColor(Color.YELLOW);
                        toast.show();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                HolderClass.showError(e, scrollView);
            }
        }));


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterGuardActivity.this, R.style.AlertDialog);
                builder.setMessage("Please make sure you selected the right building for this guard!! Click OK if you have or Cancel to go back and do so.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                registerGuard();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }



    private void registerGuard() {
        if(validateInput()) {
            dialog.show();
            Guard guard = new Guard(guard_Id, guardName, guardPhoneNo,
                    guardPassword, guardSecurityCompany, theBuildingId);
            disposable.add(apiService
                    .createGuard(guard)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {
                            dialog.dismiss();
                            Log.d(TAG, "Response: " + jsonObject);
                            JsonObject guardResponse = jsonObject.getAsJsonObject("guard");
                            String jwtToken = jsonObject.get("jwt_token").toString();
                            // remove quotation marks
                            jwtToken = jwtToken.replaceAll("^\"|\"$", "");
                            System.out.println("jwtToken: " + jwtToken);
                            Toast.makeText(RegisterGuardActivity.this, "Success! Guard Created!", Toast.LENGTH_SHORT).show();
                            int theGuardId = guardResponse.get("id").getAsInt();
                            System.out.println("Guard ID: " + theGuardId);
                            Intent intent = new Intent(RegisterGuardActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            HolderClass.showError(e, scrollView);
                        }
                    }));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterGuardActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        guardIdLayout = findViewById(R.id.textInputLayoutGuardID);
        nameLayout = findViewById(R.id.textInputLayoutGuardName);
        phoneNoLayout = findViewById(R.id.textInputLayoutPhoneNo);
        passwordLayout = findViewById(R.id.textInputLayoutPassword);
        securityCompanyLayout = findViewById(R.id.textInputLayoutSecurityCompany);

        guardId = findViewById(R.id.textInputEditTextGuardID);
        name = findViewById(R.id.textInputEditTextGuardName);
        phoneNo = findViewById(R.id.textInputEditTextPhoneNo);
        password = findViewById(R.id.textInputEditTextPassword);
        securityCompany = findViewById(R.id.textInputEditTextSecurityCompany);

        register = findViewById(R.id.appCompatButtonRegisterGuard);
        scrollView = findViewById(R.id.nestedScrollViewGuard);
        spinner = findViewById(R.id.spinner);
    }

    private boolean validateInput(){
        try {
            guard_Id = Integer.parseInt(guardId.getText().toString());
            guardIdLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            guardIdLayout.setError("Error! ID Number must be a number not a text!");
            return false;
        }

        if (guard_Id < 10000000 || guard_Id > 99999999) {
            guardIdLayout.setError("Please enter valid ID number!");
            return false;
        } else {
            guardIdLayout.setError(null);
        }

        guardName = name.getText().toString();
        nameLayout.setError(null);
        if(guardName.isEmpty()){
            nameLayout.setError("Error! Guard's name cannot be empty");
            return false;
        }else if(guardName.length() < 5) {
            nameLayout.setError("Error! Guard's name too short");
            return false;
        } else {
            nameLayout.setError(null);
        }

        try {
            guardPhoneNo = Long.parseLong(phoneNo.getText().toString());
            phoneNoLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            phoneNoLayout.setError("Error! Phone Number must be a number not a text!");
            return false;
        }
        if (guardPhoneNo < 254700000001L || guardPhoneNo > 254799999999L) {
            phoneNoLayout.setError("Please enter valid Phone number!");
            return false;
        } else {
            phoneNoLayout.setError(null);
        }

        guardPassword = password.getText().toString();
        passwordLayout.setError(null);
        if(guardPassword.isEmpty()){
            passwordLayout.setError("Error! Guard's password cannot be empty");
            return false;
        }else if(guardPassword.length() < 5) {
            passwordLayout.setError("Error! Guard's password too short");
            return false;
        } else {
            passwordLayout.setError(null);
        }

        guardSecurityCompany = securityCompany.getText().toString();
        securityCompanyLayout.setError(null);
        if(guardSecurityCompany.isEmpty()){
            securityCompanyLayout.setError("Error! Guard's security Company cannot be empty");
            return false;
        }else if(guardSecurityCompany.length() < 5) {
            securityCompanyLayout.setError("Error! Guard's security Company too short");
            return false;
        } else {
            securityCompanyLayout.setError(null);
        }

        return true;
    }
}
