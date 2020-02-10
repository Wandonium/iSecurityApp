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
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Employee;
import com.techcloud.isecurity.models.Guard;
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

public class RegisterEmployeeActivity extends AppCompatActivity {

    public static final String TAG = RegisterEmployeeActivity.class.getSimpleName();

    private TextInputLayout empIdLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout phoneNoLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText name;
    private TextInputEditText empId;
    private TextInputEditText phoneNo;
    private TextInputEditText password;

    private AppCompatButton register;
    private NestedScrollView scrollView;
    private Spinner spinner;

    private int emp_Id;
    private String empName;
    private long empPhoneNo;
    private String empPassword;
    private String empRole;
    private int theCompanyId;
    private int theBuildingId;

    private ApiService apiService;
    private CompositeDisposable disposable;
    private List<Company> companyList;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);
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

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(RegisterEmployeeActivity.this,
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
                        HolderClass.showError(e, scrollView);
                    }
                }));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterEmployeeActivity.this, R.style.AlertDialog);
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
                                registerEmployee();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void registerEmployee() {
        if(validateInput()) {
            dialog.show();
            Date now = new Date();
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String timeIn = formatter1.format(now);
            System.out.println("timeIn: " + timeIn);
            System.out.println("GuardID: " + HolderClass.guard.getGuard_db_id());
            Employee emp = new Employee(emp_Id, empName, empPhoneNo, empRole, timeIn, timeIn, theCompanyId, HolderClass.guard.getGuard_db_id(), theBuildingId);
            emp.setPassword(empPassword);
            System.out.println(emp);
            disposable.add(apiService
            .createEmployee(emp, Guard.jwtToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                @Override
                public void onSuccess(JsonObject object) {
                    dialog.dismiss();
                    Log.d(TAG, "Response: " + object);
                    Toast.makeText(RegisterEmployeeActivity.this, "Success! Employee Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterEmployeeActivity.this, AdminHomeActivity.class);
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
        Intent intent = new Intent(RegisterEmployeeActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        empIdLayout = findViewById(R.id.lr_empId);
        nameLayout = findViewById(R.id.lr_empName);
        phoneNoLayout = findViewById(R.id.lr_empPhoneNo);
        passwordLayout = findViewById(R.id.lr_empPassword);

        name = findViewById(R.id.etr_empName);
        empId = findViewById(R.id.etr_empId);
        phoneNo = findViewById(R.id.etr_empPhoneNo);
        password = findViewById(R.id.etr_empPassword);

        register = findViewById(R.id.btn_register_emp);
        scrollView = findViewById(R.id.nestedScrollViewEmployee);
        spinner = findViewById(R.id.spinner_r_empCompanies);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_receptionist:
                if (checked)
                    empRole = "Receptionist";
                break;
            case R.id.radio_other:
                if (checked)
                    empRole = "Other";
                break;
        }
    }

    private boolean validateInput() {
        try {
            emp_Id = Integer.parseInt(empId.getText().toString());
            empIdLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            empIdLayout.setError("Error! ID Number must be a number not a text!");
            return false;
        }

        if (emp_Id < 10000000 || emp_Id > 99999999) {
            empIdLayout.setError("Please enter valid ID number!");
            return false;
        } else {
            empIdLayout.setError(null);
        }

        empName = name.getText().toString();
        nameLayout.setError(null);
        if(empName.isEmpty()){
            nameLayout.setError("Error! Employee's name cannot be empty");
            return false;
        }else if(empName.length() < 5) {
            nameLayout.setError("Error! Employee's name too short");
            return false;
        } else {
            nameLayout.setError(null);
        }

        try {
            empPhoneNo = Long.parseLong(phoneNo.getText().toString());
            phoneNoLayout.setError(null);
        } catch (Exception e) {
            e.printStackTrace();
            phoneNoLayout.setError("Error! Phone Number must be a number not a text!");
            return false;
        }
        if (empPhoneNo < 254700000001L || empPhoneNo > 254799999999L) {
            phoneNoLayout.setError("Please enter valid Phone number!");
            return false;
        } else {
            phoneNoLayout.setError(null);
        }

        empPassword = password.getText().toString();
        passwordLayout.setError(null);
        if(empPassword.isEmpty()){
            passwordLayout.setError("Error! Employee's password cannot be empty");
            return false;
        }else if(empPassword.length() < 5) {
            passwordLayout.setError("Error! Employee's password too short");
            return false;
        } else {
            passwordLayout.setError(null);
        }

        return true;
    }
}
