package com.techcloud.isecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ManualInputActivity extends AppCompatActivity {

    public static final String TAG = ManualInputActivity.class.getSimpleName();

    private TextInputLayout guestIdLayout;
    private TextInputLayout guestNameLayout;
    private TextInputLayout guestPhoneNoLayout;
    private TextInputLayout guestReasonLayout;

    private TextInputEditText guestId;
    private TextInputEditText guestName;
    private TextInputEditText guestPhoneNo;
    private TextInputEditText guestReason;
    private Spinner guestCompany;
    private NestedScrollView scrollView;
    private AppCompatButton signIn;

    private int theGuestId;
    private String theGuestName;
    private long theGuestPhoneNo;
    private String theGuestReason;
    private String theGuestGender = "";
    private int theGuestCompanyId = 0;
    private int theGuestBuildingId = 0;

    private ApiService apiService;
    private CompositeDisposable disposable;
    private List<Company> companyList;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        initViews();

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        companyList = new ArrayList<>();

        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        dialog.show();
        disposable.add(apiService
                .getOneBuilding(Guard.jwtToken, HolderClass.guard.getBuilding_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject object) {
                        dialog.dismiss();
                        Log.d(TAG, "Response: " + object);
                        JsonArray companies = object.get("companies").getAsJsonArray();
                        for(Object theObject: companies) {
                            JsonObject jsonObject = (JsonObject) theObject;
                            int building_id = jsonObject.get("building_id").getAsInt();
                            String doorOrRoom = jsonObject.get("door_or_room").getAsString();
                            String email = jsonObject.get("email").getAsString();
                            int floorNo = jsonObject.get("floor_number").getAsInt();
                            int id = jsonObject.get("id").getAsInt();
                            String name = jsonObject.get("name").getAsString();
                            long phoneNo = jsonObject.get("phone_no").getAsLong();

                            doorOrRoom = doorOrRoom.replaceAll("^\"|\"$", "");
                            email = email.replaceAll("^\"|\"$", "");
                            name = name.replaceAll("^\"|\"$", "");

                            Company company = new Company(name, email, doorOrRoom, floorNo, phoneNo, building_id);
                            company.setCompany_id(id);
                            System.out.println(company);
                            companyList.add(company);
                        }

                        final ArrayList<String> companyNames = new ArrayList<>();
                        for (Company c : companyList) {
                            companyNames.add(c.getName() + ", " + c.getFloor_number() + " floor");
                        }

                        companyNames.set(0, "Choose Company:");

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ManualInputActivity.this,
                                android.R.layout.simple_spinner_item, companyNames);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        guestCompany.setAdapter(spinnerAdapter);
                        guestCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                companyNames.set(0, companyList.get(0).getName() + ", " + companyList.get(0).getFloor_number() + " floor");
                                Company selectedCompany = companyList.get(position);
                                theGuestCompanyId = selectedCompany.getCompany_id();
                                theGuestBuildingId = selectedCompany.getBuilding_id();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        HolderClass.showError(e, scrollView);
                    }
                }));
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManualInputActivity.this, R.style.AlertDialog);
                builder.setMessage("Please make sure you selected the right company for this employee!! Click OK if you have or Cancel to go back and do so.")
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
                                registerGuest();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void registerGuest() {
        if(validateGuest()) {
            dialog.show();
            Date now = new Date();
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String timeIn = formatter1.format(now);
            System.out.println("timeIn: " + timeIn);
            System.out.println("GuardID: " + HolderClass.guard.getGuard_db_id());
            Guest guest = new Guest(theGuestId, theGuestName, theGuestPhoneNo, theGuestGender, theGuestReason, timeIn, timeIn, theGuestBuildingId, theGuestCompanyId, HolderClass.guard.getGuard_db_id());
            disposable.add(apiService
            .createGuest(guest, Guard.jwtToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                @Override
                public void onSuccess(JsonObject object) {
                    dialog.dismiss();
                    Log.d(TAG, "Response: " + object);
                    Toast.makeText(ManualInputActivity.this, "Success! Guest Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ManualInputActivity.this, GuardHomeActivity.class);
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
        Intent intent = new Intent(ManualInputActivity.this, GuardHomeActivity.class);
        startActivity(intent);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    theGuestGender = "Male";
                break;
            case R.id.radio_female:
                if (checked)
                    theGuestGender = "Female";
                break;
        }
    }

    private void initViews() {
        guestId = findViewById(R.id.etr_guestId);
        guestName = findViewById(R.id.etr_guestName);
        guestPhoneNo = findViewById(R.id.etr_guestPhoneNo);
        guestReason = findViewById(R.id.etr_guestReason);


        guestIdLayout = findViewById(R.id.lr_guestId);
        guestNameLayout = findViewById(R.id.lr_guestName);
        guestPhoneNoLayout = findViewById(R.id.lr_guestPhoneNo);
        guestReasonLayout = findViewById(R.id.lr_guestReason);
        guestCompany = findViewById(R.id.spinner_r_guestCompanies);
        scrollView = findViewById(R.id.nestedScrollViewManualInput);
        signIn = findViewById(R.id.btn_register_guest);
    }

    private boolean validateGuest() {
        theGuestName = guestName.getText().toString();
        guestNameLayout.setError(null);
        if (!theGuestName.isEmpty() && theGuestName.length() < 5) {
            guestNameLayout.setError("Error! Guest's name is too short");
            return false;
        } else {
            guestNameLayout.setError(null);
        }

        if(!guestPhoneNo.getText().toString().isEmpty()) {
            try {
                theGuestPhoneNo = Long.parseLong(guestPhoneNo.getText().toString());
                guestPhoneNoLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                guestPhoneNoLayout.setError("Error! Phone Number must be a number not a text!");
                return false;
            }
            if (theGuestPhoneNo < 254700000001L || theGuestPhoneNo > 254799999999L) {
                guestPhoneNoLayout.setError("Please enter valid phone number!");
                return false;
            } else {
                guestPhoneNoLayout.setError(null);
            }
        }

        if(!guestId.getText().toString().isEmpty()) {
            try {
                theGuestId = Integer.parseInt(guestId.getText().toString());
                guestIdLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                guestIdLayout.setError("Error! ID number must be a number not a text!");
                return false;
            }
            if (theGuestId < 10000000  || theGuestId > 99999999) {
                guestIdLayout.setError("Please enter valid ID number!");
                return false;
            } else {
                guestIdLayout.setError(null);
            }
        }

        theGuestReason = guestReason.getText().toString();
        guestReasonLayout.setError(null);
        if (!theGuestReason.isEmpty() && theGuestReason.length() < 5) {
            guestReasonLayout.setError("Error! Guest's reason for visit is too short");
            return false;
        } else {
            guestReasonLayout.setError(null);
        }


        return true;
    }

}
