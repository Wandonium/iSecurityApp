package com.techcloud.isecurity.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

public class GuestDetailsActivity extends AppCompatActivity {

    public static final String TAG = GuestDetailsActivity.class.getSimpleName();

    private TextInputLayout phoneNoLayout;
    private TextInputLayout reasonLayout;

    private TextInputEditText phoneNo;
    private TextInputEditText reason;

    private Spinner spinner;
    private AppCompatButton register;
    private LinearLayout linearLayout;

    private Guard guard;
    private Guest guest;
    private List<Company> companyList;

    private int theCompanyId = 0;
    private long guestPhoneNo;
    private String guestReason;

    private ApiService apiService;
    private CompositeDisposable disposable;
    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_details);
        initViews();

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();
        companyList = new ArrayList<>();
        guard = new Guard(HolderClass.guard);
        guest = new Guest((Guest) getIntent().getSerializableExtra("Guest"));
        System.out.println(guest);

        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        dialog.show();
        disposable.add(apiService
        .getOneBuilding(Guard.jwtToken, guard.getBuilding_id())
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

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(GuestDetailsActivity.this,
                        android.R.layout.simple_spinner_item, companyNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        companyNames.set(0, companyList.get(0).getName() + ", " + companyList.get(0).getFloor_number() + " floor");
                        Company selectedCompany = companyList.get(position);
                        theCompanyId = selectedCompany.getCompany_id();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        theCompanyId = companyList.get(0).getCompany_id();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                HolderClass.showError(e, linearLayout);
            }
        }));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    guest.setPhone_no(guestPhoneNo);
                    guest.setReason_for_visit(guestReason);
                    Date now = new Date();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    String timeIn = formatter1.format(now);
                    System.out.println("timeIn: " + timeIn);
                    guest.setTime_in(timeIn);
                    guest.setTime_out(timeIn);
                    guest.setBuilding_id(guard.getBuilding_id());
                    guest.setGuard_id(guard.getGuard_db_id());
                    guest.setCompany_id(theCompanyId == 0 ? companyList.get(0).getCompany_id() : theCompanyId);
                    System.out.println(guest);
                    dialog.show();
                    disposable.add(apiService
                    .createGuest(guest, Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject object) {
                            dialog.dismiss();
                            Log.d(TAG, "Response: " + object);
                            Toast.makeText(GuestDetailsActivity.this, "Succes! Guest created!", Toast.LENGTH_LONG).show();
                            if(HolderClass.user.equals("admin")) {
                                Intent intent = new Intent(GuestDetailsActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else if(HolderClass.user.equals("guard")) {
                                Intent intent = new Intent(GuestDetailsActivity.this, GuardHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            HolderClass.showError(e, linearLayout);
                        }
                    }));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(HolderClass.user.equals("admin")) {
            Intent intent = new Intent(GuestDetailsActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        } else if(HolderClass.user.equals("guard")) {
            Intent intent = new Intent(GuestDetailsActivity.this, GuardHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateInput() {
        try {
            guestPhoneNo = Long.parseLong(phoneNo.getText().toString());
            phoneNoLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            phoneNoLayout.setError("Error! Phone Number must be a number not a text!");
            return false;
        }
        if (guestPhoneNo < 254700000001L || guestPhoneNo > 254799999999L) {
            phoneNoLayout.setError("Please enter valid Phone number!");
            return false;
        } else {
            phoneNoLayout.setError(null);
        }

        guestReason = reason.getText().toString();
        reasonLayout.setError(null);
        if(guestReason.isEmpty()){
            reasonLayout.setError("Error! Guest's reason for visit cannot be empty");
            return false;
        }else if(guestReason.length() < 5) {
            reasonLayout.setError("Error! Guest's reason for visit is too short");
            return false;
        } else {
            reasonLayout.setError(null);
        }

        return true;
    }

    private void initViews() {
        phoneNoLayout = findViewById(R.id.l_phoneNo);
        reasonLayout = findViewById(R.id.l_reason);

        phoneNo = findViewById(R.id.et_phoneNo);
        reason = findViewById(R.id.et_reason);

        register = findViewById(R.id.btn_register_guest);
        if(HolderClass.user.equals("guard"))
            register.setText("Sign In");
        spinner = findViewById(R.id.guest_companies);
        linearLayout = findViewById(R.id.l_registerGuest);
    }
}
