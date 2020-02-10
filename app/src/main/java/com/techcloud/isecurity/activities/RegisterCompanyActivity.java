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
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterCompanyActivity extends AppCompatActivity {

    public static final String TAG = RegisterCompanyActivity.class.getSimpleName();

    private TextInputLayout emailLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout phoneNoLayout;
    private TextInputLayout doorOrRoomLayout;
    private TextInputLayout floorNoLayout;

    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText phoneNo;
    private TextInputEditText doorOrRoom;
    private TextInputEditText floorNo;

    private AppCompatButton register;
    private NestedScrollView scrollView;
    private TextView backToLogin;
    private Spinner spinner;

    private String companyEmail;
    private String companyName;
    private long companyPhoneNo;
    private String companyDoorOrRoom;
    private int companyFloorNo;
    private int theBuildingId;

    private ApiService apiService;
    private CompositeDisposable disposable;
    private List<Building> buildingList;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

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

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(RegisterCompanyActivity.this,
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
                                Toast toast=Toast.makeText(RegisterCompanyActivity.this, "Error! Building cannot be empty", Toast.LENGTH_LONG);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterCompanyActivity.this, R.style.AlertDialog);
                builder.setMessage("Please make sure you selected the right building for this company!! Click OK if you have or Cancel to go back and do so.")
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
                                registerCompany();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void registerCompany() {
        if(validateInput()){
            dialog.show();
            final Company company = new Company(companyName, companyEmail, companyDoorOrRoom, companyFloorNo,
                    companyPhoneNo, theBuildingId);
            disposable.add(apiService
                    .createCompany(company, Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {
                            dialog.dismiss();
                            Log.d(TAG, "Response: " + jsonObject);
                            Toast.makeText(RegisterCompanyActivity.this, "Success! Company Created!", Toast.LENGTH_SHORT).show();
                            int company_id = Integer.parseInt(jsonObject.get("id").toString());
                            System.out.println("Company_id: " + company_id);
                            company.setCompany_id(company_id);
                            Intent intent = new Intent(RegisterCompanyActivity.this, AdminHomeActivity.class);
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
        Intent intent = new Intent(RegisterCompanyActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        emailLayout = findViewById(R.id.textInputLayoutEmail);
        nameLayout = findViewById(R.id.textInputLayoutCompanyName);
        phoneNoLayout = findViewById(R.id.textInputLayoutCompanyPhoneNo);
        doorOrRoomLayout = findViewById(R.id.textInputLayoutDoorOrRoom);
        floorNoLayout = findViewById(R.id.textInputLayoutFloorNo);

        name = findViewById(R.id.textInputEditTextCompanyName);
        email = findViewById(R.id.textInputEditTextEmail);
        phoneNo = findViewById(R.id.textInputEditTextCompanyPhoneNo);
        doorOrRoom = findViewById(R.id.textInputEditTextDoorOrRoom);
        floorNo = findViewById(R.id.textInputEditTextFloorNo);

        register = findViewById(R.id.appCompatButtonRegisterCompany);
        scrollView = findViewById(R.id.nestedScrollViewCompany);
        spinner = findViewById(R.id.spinner_companies);
    }

    private boolean validateInput() {
        companyName = name.getText().toString();
        nameLayout.setError(null);
        if (companyName.isEmpty()) {
            nameLayout.setError("Error! Company's name cannot be empty");
            return false;
        } else if (companyName.length() < 5) {
            nameLayout.setError("Error! Company's name too short");
            return false;
        } else {
            nameLayout.setError(null);
        }

        try {
            companyPhoneNo = Long.parseLong(phoneNo.getText().toString());
            phoneNoLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            phoneNoLayout.setError("Error! Phone Number must be a number not a text!");
            return false;
        }
        if (companyPhoneNo < 254700000001L || companyPhoneNo > 254799999999L) {
            phoneNoLayout.setError("Please enter valid phone number!");
            return false;
        } else {
            phoneNoLayout.setError(null);
        }

        companyDoorOrRoom = doorOrRoom.getText().toString();
        doorOrRoomLayout.setError(null);
        if (companyDoorOrRoom.isEmpty()) {
            doorOrRoomLayout.setError("Error! Company's door or Room number cannot be empty");
            return false;
        } else {
            doorOrRoomLayout.setError(null);
        }

        companyEmail = email.getText().toString();
        emailLayout.setError(null);
        if (companyEmail.isEmpty()) {
            emailLayout.setError("Error! Company's email cannot be empty");
            return false;
        } else if (companyEmail.length() < 5) {
            emailLayout.setError("Error! Company's email too short");
            return false;
        } else if (!isEmailValid(companyEmail)) {
            emailLayout.setError("Error! Invalid email address");
            return false;
        }else {
            emailLayout.setError(null);
        }

        try {
            companyFloorNo = Integer.parseInt(floorNo.getText().toString());
            floorNoLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            floorNoLayout.setError("Error! Floor Number must be a number not a text!");
            return false;
        }

        if (companyFloorNo < 1 || companyFloorNo >= 200) {
            floorNoLayout.setError("Please enter valid Floor number!");
            return false;
        } else {
            floorNoLayout.setError(null);
        }

        return true;
    }

    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }
}
