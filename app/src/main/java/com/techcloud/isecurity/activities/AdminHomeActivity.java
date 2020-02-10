package com.techcloud.isecurity.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.fragments.AdminHomeFragment;
import com.techcloud.isecurity.fragments.BuildingFragment;
import com.techcloud.isecurity.fragments.CompanyFragment;
import com.techcloud.isecurity.fragments.EmployeeFragment;
import com.techcloud.isecurity.fragments.GuardFragment;
import com.techcloud.isecurity.fragments.GuestFragment;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.helpers.InputException;
import com.techcloud.isecurity.adapters.MyBuildingRecyclerViewAdapter;
import com.techcloud.isecurity.adapters.MyCompanyRecyclerViewAdapter;
import com.techcloud.isecurity.adapters.MyEmployeeRecyclerViewAdapter;
import com.techcloud.isecurity.adapters.MyGuardRecyclerViewAdapter;
import com.techcloud.isecurity.adapters.MyGuestRecyclerViewAdapter;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.helpers.ServerException;
import com.techcloud.isecurity.models.Building;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Employee;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.techcloud.isecurity.activities.RegisterCompanyActivity.isEmailValid;

public class AdminHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BuildingFragment.OnListFragmentInteractionListener,
        GuardFragment.OnListFragmentInteractionListener,
        GuestFragment.OnListFragmentInteractionListener,
        CompanyFragment.OnListFragmentInteractionListener,
        EmployeeFragment.OnListFragmentInteractionListener {

    public static final String TAG = AdminHomeActivity.class.getSimpleName();

    private int menuItem;
    private ApiService apiService;
    private CompositeDisposable disposable;

    private android.app.AlertDialog.Builder builder;
    private Dialog dialog;

    private TextInputLayout nameLayout;
    private TextInputLayout streetLayout;
    private TextInputLayout cityLayout;
    private TextInputLayout noOfFloorsLayout;

    private TextInputEditText name;
    private TextInputEditText street;
    private TextInputEditText city;
    private TextInputEditText noOfFloors;

    private String buildingName;
    private String buildingStreet;
    private String buildingCity;
    private int buildingNoOfFloors;

    private TextInputLayout guardIdLayout;
    private TextInputLayout guardNameLayout;
    private TextInputLayout guardPhoneNoLayout;
    private TextInputLayout guardPasswordLayout;
    private TextInputLayout guardSecurityCompanyLayout;

    private TextInputEditText guardId;
    private TextInputEditText guardName;
    private TextInputEditText guardPhoneNo;
    private TextInputEditText guardPassword;
    private TextInputEditText guardSecurityCompany;
    private Spinner guardBuilding;

    private int theGuardId;
    private String theGuardName;
    private long theGuardPhoneNo;
    private String theGuardPassword;
    private String theGuardSecurityCompany;
    private int theBuildingId = 0;

    private TextInputLayout companyEmailLayout;
    private TextInputLayout companyNameLayout;
    private TextInputLayout companyPhoneNoLayout;
    private TextInputLayout companyDoorOrRoomLayout;
    private TextInputLayout companyFloorNoLayout;

    private TextInputEditText companyEmail;
    private TextInputEditText companyName;
    private TextInputEditText companyPhoneNo;
    private TextInputEditText companyDoorOrRoom;
    private TextInputEditText companyFloorNo;
    private Spinner companyBuilding;

    private int theCompanyFloorNo;
    private String theCompanyName;
    private long theCompanyPhoneNo;
    private String theCompanyEmail;
    private String theCompanyDoorOrRoom;
    private int theCompanyBuildingId = 0;

    private TextInputLayout guestIdLayout;
    private TextInputLayout guestNameLayout;
    private TextInputLayout guestPhoneNoLayout;
    private TextInputLayout guestReasonLayout;

    private TextInputEditText guestId;
    private TextInputEditText guestName;
    private TextInputEditText guestPhoneNo;
    private TextInputEditText guestReason;
    private Spinner guestCompany;

    private int theGuestId;
    private String theGuestName;
    private long theGuestPhoneNo;
    private String theGuestReason;
    private String theGuestGender = "";
    private int theGuestCompanyId = 0;
    private int theGuestBuildingId = 0;

    private TextInputLayout employeeIdLayout;
    private TextInputLayout employeeNameLayout;
    private TextInputLayout employeePhoneNoLayout;

    private TextInputEditText employeeId;
    private TextInputEditText employeeName;
    private TextInputEditText employeePhoneNo;
    private Spinner employeeCompany;

    private int theEmployeeId;
    private String theEmployeeName;
    private long theEmployeePhoneNo;
    private String theEmployeeRole = "";
    private int theEmployeeCompanyId = 0;
    private int theEmployeeBuildingId = 0;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuItem = 1;
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();

        builder = new android.app.AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (menuItem) {
                    case 1:
                        Snackbar.make(view, "No entity selected. Please select an entity in the navigation drawer to the left.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    case 2:
                        Intent registerBuilding = new Intent(AdminHomeActivity.this, RegisterBuildingActivity.class);
                        startActivity(registerBuilding);
                        break;
                    case 3:
                        Intent registerGuard = new Intent(AdminHomeActivity.this, RegisterGuardActivity.class);
                        startActivity(registerGuard);
                        break;
                    case 4:
                        Intent registerCompany = new Intent(AdminHomeActivity.this, RegisterCompanyActivity.class);
                        startActivity(registerCompany);
                        break;
                    case 5:
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                        builder.setMessage("To register a guest you have to scan their ID card or Passport using the device camera. Press OK to launch the camera...")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        HolderClass.user = "admin";
                                        Intent intent = new Intent(AdminHomeActivity.this, ScanActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    case 6:
                        Intent registerEmployee = new Intent(AdminHomeActivity.this, RegisterEmployeeActivity.class);
                        startActivity(registerEmployee);
                        break;
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AdminHomeFragment homeFragment = AdminHomeFragment.newInstance();
        transaction.add(R.id.frameLayoutAdmin, homeFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            JsonObject postParams = new JsonObject();
            try{
                postParams.addProperty("user_id", HolderClass.guard.getGuardId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.show();
            disposable.add(apiService
            .logoutGuard(postParams)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                @Override
                public void onSuccess(JsonObject object) {
                    dialog.dismiss();
                    Log.d(TAG, "Logout Response: " + object);
                    Toast.makeText(AdminHomeActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    dialog.dismiss();
                    showError(e);
                }
            }));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_home) {
            menuItem = 1;
            fragmentClass = AdminHomeFragment.class;
        } else if (id == R.id.nav_buildings) {
            menuItem = 2;
            fragmentClass = BuildingFragment.class;
        } else if (id == R.id.nav_security_guards) {
            menuItem = 3;
            fragmentClass = GuardFragment.class;

        } else if (id == R.id.nav_companies) {
            menuItem = 4;
            fragmentClass = CompanyFragment.class;
        } else if (id == R.id.nav_guests) {
            menuItem = 5;
            fragmentClass = GuestFragment.class;
        } else if (id == R.id.nav_employees) {
            menuItem = 6;
            fragmentClass = EmployeeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutAdmin, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean validateBuilding() {
        buildingName = name.getText().toString();
        nameLayout.setError(null);
        buildingStreet = street.getText().toString();
        streetLayout.setError(null);
        buildingCity = city.getText().toString();
        cityLayout.setError(null);

        if(!noOfFloors.getText().toString().isEmpty()) {
            try {
                buildingNoOfFloors = Integer.parseInt(noOfFloors.getText().toString());
                noOfFloorsLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                noOfFloorsLayout.setError("Error! Enter numbers instead of text");
                return false;
            }

            if(buildingNoOfFloors <= 0 || buildingNoOfFloors >= 200){
                noOfFloorsLayout.setError("Error! No. of floors entered is either too small or too large!");
                return false;
            } else {
                noOfFloorsLayout.setError(null);
            }
        }

        if(!buildingName.isEmpty() && buildingName.length() < 5) {
            nameLayout.setError("Error! Building name too short");
            return false;
        } else {
            nameLayout.setError(null);
        }

        if(!buildingStreet.isEmpty() && buildingStreet.length() < 5) {
            streetLayout.setError("Error! Building street too short");
            return false;
        }else {
            streetLayout.setError(null);
        }

        if(!buildingCity.isEmpty() && buildingCity.length() < 5) {
            cityLayout.setError("Error! Building city too short");
            return false;
        }else {
            cityLayout.setError(null);
        }

        return true;
    }

    private boolean validateGuard() {
        if(!guardId.getText().toString().isEmpty()){
            try {
                theGuardId = Integer.parseInt(guardId.getText().toString());
                guardIdLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                guardIdLayout.setError("Error! ID Number must be a number not a text!");
                return false;
            }
            if (theGuardId < 10000000 || theGuardId > 99999999) {
                guardIdLayout.setError("Please enter valid ID number!");
                return false;
            } else {
                guardIdLayout.setError(null);
            }
        }

        theGuardName = guardName.getText().toString();
        guardNameLayout.setError(null);
        if(!theGuardName.isEmpty() && theGuardName.length() < 5) {
            guardNameLayout.setError("Error! Guard's name is too short");
            return false;
        } else {
            guardNameLayout.setError(null);
        }

        if(!guardPhoneNo.getText().toString().isEmpty()) {
            try {
                theGuardPhoneNo = Long.parseLong(guardPhoneNo.getText().toString());
                guardPhoneNoLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                guardPhoneNoLayout.setError("Error! Phone Number must be a number not a text!");
                return false;
            }
            if (theGuardPhoneNo < 254700000001L || theGuardPhoneNo > 254799999999L) {
                guardPhoneNoLayout.setError("Please enter valid Phone number!");
                return false;
            } else {
                guardPhoneNoLayout.setError(null);
            }
        }

        theGuardPassword = guardPassword.getText().toString();
        guardPasswordLayout.setError(null);
        if(!theGuardPassword.isEmpty() && theGuardPassword.length() < 5) {
            guardPasswordLayout.setError("Error! Guard's password is too short");
            return false;
        } else {
            guardPasswordLayout.setError(null);
        }

        theGuardSecurityCompany = guardSecurityCompany.getText().toString();
        guardSecurityCompanyLayout.setError(null);
        if(!theGuardSecurityCompany.isEmpty() && theGuardSecurityCompany.length() < 5) {
            guardSecurityCompanyLayout.setError("Error! Guard's security Company too short");
            return false;
        } else {
            guardSecurityCompanyLayout.setError(null);
        }

        return true;
    }

    @Override
    public void onListFragmentInteraction(final List<Building> buildings, String btn, final int position, final MyBuildingRecyclerViewAdapter adapter) {
        final Building building = buildings.get(position);
        if(btn.equals("card")){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.update_building, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            nameLayout = view.findViewById(R.id.textInputLayoutUBName);
            streetLayout = view.findViewById(R.id.textInputLayoutUStreet);
            cityLayout = view.findViewById(R.id.textInputLayoutUCity);
            noOfFloorsLayout = view.findViewById(R.id.textInputLayoutUNoOfFloors);

            name = view.findViewById(R.id.textInputEditTextUBName);
            street = view.findViewById(R.id.textInputEditTextUStreet);
            city = view.findViewById(R.id.textInputEditTextUCity);
            noOfFloors = view.findViewById(R.id.textInputEditTextUNoOfFloors);

            builder
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show toast message when no text is entered
                    if (validateBuilding()) {
                        dialog.show();
                        building.setName(buildingName.isEmpty() ? building.getName() : buildingName);
                        building.setStreet(buildingStreet.isEmpty() ? building.getStreet() : buildingStreet);
                        building.setCity(buildingCity.isEmpty() ? building.getCity() : buildingCity);
                        building.setNo_of_floors(buildingNoOfFloors == 0 ? building.getNo_of_floors() : buildingNoOfFloors);
                        disposable.add(apiService
                        .updateBuilding(building.getBuilding_id(), building, Guard.jwtToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject object) {
                                dialog.dismiss();
                                Log.d(TAG, "Response" + object);
                                buildings.set(position, building);
                                adapter.notifyItemChanged(position);
                                Toast.makeText(AdminHomeActivity.this, "Success! Building Updated!", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                showError(e);
                            }
                        }));

                    }
                }
            });
        } else if(btn.equals("delete")) {
            dialog.show();
            disposable.add(apiService
            .deleteBuilding(building.getBuilding_id(), Guard.jwtToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                    dialog.dismiss();
                    Log.d(TAG, "Building " + building.getBuilding_id() + " deleted!");

                    // Remove and notify adapter about item deletion
                    buildings.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, buildings.size());
                    Toast.makeText(AdminHomeActivity.this, "Success! Building Deleted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    dialog.dismiss();
                    showError(e);
                }
            }));
        }
    }

    @Override
    public void onListFragmentInteraction(final List<Guard> guards, String btn, final int position, final MyGuardRecyclerViewAdapter adapter, final List<Building> buildings) {
        final Guard guard = guards.get(position);
        if(btn.equals("card")){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.update_guard, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            guardIdLayout = view.findViewById(R.id.textInputLayoutUGId);
            guardNameLayout = view.findViewById(R.id.textInputLayoutUGName);
            guardPhoneNoLayout = view.findViewById(R.id.textInputLayoutUGPhoneNo);
            guardPasswordLayout = view.findViewById(R.id.textInputLayoutUGPassword);
            guardSecurityCompanyLayout = view.findViewById(R.id.textInputLayoutUGSecurityCompany);

            guardId = view.findViewById(R.id.textInputEditTextUGId);
            guardName = view.findViewById(R.id.textInputEditTextUGName);
            guardPhoneNo = view.findViewById(R.id.textInputEditTextUGPhoneNo);
            guardPassword = view.findViewById(R.id.textInputEditTextUGPassword);
            guardSecurityCompany = view.findViewById(R.id.textInputEditTextUGSecurityCompany);
            guardBuilding = view.findViewById(R.id.spinner_buildings);

            /*System.out.println("Before sort.");
            for(Building b: buildings)
                System.out.println(b);*/

            for(int i = 0; i < buildings.size(); i++) {
                Building b = buildings.get(i);
                if(b.getBuilding_id() == guard.getBuilding_id()) {
                    Building building = new Building(buildings.get(0));
                    buildings.set(0, b);
                    buildings.set(i, building);
                }
            }

            /*System.out.println("After sort.");
            for(Building b: buildings)
                System.out.println(b);*/

            final ArrayList<String> buildingNames = new ArrayList<>();
            for(Building b: buildings){
                buildingNames.add(b.getName() + ", " + b.getCity());
            }

            buildingNames.set(0, "Update Building:");

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, buildingNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            guardBuilding.setAdapter(spinnerAdapter);
            guardBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    buildingNames.set(0, buildings.get(0).getName() + ", " + buildings.get(0).getCity());
                    Building selectedBuilding = buildings.get(position);
                    theBuildingId = selectedBuilding.getBuilding_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    System.out.println("Current Building: " + guard.getBuilding_id());
                    guard.setBuilding_id(guard.getBuilding_id());
                }
            });

            builder
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show toast message when no text is entered
                    if (validateGuard()) {
                        dialog.show();
                        guard.setGuardId(theGuardId == 0 ? guard.getGuardId() : theGuardId);
                        guard.setGuard_name(theGuardName.isEmpty() ? guard.getGuard_name() : theGuardName);
                        guard.setPhone_no(theGuardPhoneNo == 0 ? guard.getPhone_no() : theGuardPhoneNo);
                        guard.setPassword(theGuardPassword.isEmpty() ? guard.getPassword() : theGuardPassword);
                        guard.setSecurity_company(theGuardSecurityCompany.isEmpty() ? guard.getSecurity_company() : theGuardSecurityCompany);
                        guard.setBuilding_id(theBuildingId == 0 ? guard.getBuilding_id() : theBuildingId);
                        disposable.add(apiService
                                .updateGuard(guard.getGuard_db_id(), guard, Guard.jwtToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                    @Override
                                    public void onSuccess(JsonObject object) {
                                        dialog.dismiss();
                                        Log.d(TAG, "Response" + object);
                                        guards.set(position, guard);
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(AdminHomeActivity.this, "Success! Guard Updated!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        dialog.dismiss();
                                        showError(e);
                                    }
                                }));

                    }
                }
            });
        } else if(btn.equals("delete")) {
            dialog.show();
            disposable.add(apiService
                    .deleteGuard(guard.getGuard_db_id(), Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            dialog.dismiss();
                            Log.d(TAG, "Guard " + guard.getGuard_db_id() + " deleted!");

                            // Remove and notify adapter about item deletion
                            guards.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, guards.size());
                            Toast.makeText(AdminHomeActivity.this, "Success! Guard Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            showError(e);
                        }
                    }));
        }
    }

    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof InputException || e instanceof ServerException) {
                message = e.getMessage();
            } else if (e instanceof IOException) {
                message = "Error! No internet connection.";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Please consult system admin.";
        }
        Toast toast=Toast.makeText(AdminHomeActivity.this,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM,0,0);
        View view=toast.getView();
        TextView view1 = view.findViewById(android.R.id.message);
        view1.setTextColor(Color.YELLOW);
        toast.show();
    }

    private boolean validateCompany() {
        theCompanyName = companyName.getText().toString();
        companyNameLayout.setError(null);
        if (!theCompanyName.isEmpty() && theCompanyName.length() < 5) {
            companyNameLayout.setError("Error! Company's companyName too short");
            return false;
        } else {
            companyNameLayout.setError(null);
        }

        if(!companyPhoneNo.getText().toString().isEmpty()) {
            try {
                theCompanyPhoneNo = Long.parseLong(companyPhoneNo.getText().toString());
                companyPhoneNoLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                companyPhoneNoLayout.setError("Error! Phone Number must be a number not a text!");
                return false;
            }
            if (theCompanyPhoneNo < 254700000001L || theCompanyPhoneNo > 254799999999L) {
                companyPhoneNoLayout.setError("Please enter valid phone number!");
                return false;
            } else {
                companyPhoneNoLayout.setError(null);
            }
        }

        theCompanyDoorOrRoom = companyDoorOrRoom.getText().toString();
        companyDoorOrRoomLayout.setError(null);

        theCompanyEmail = companyEmail.getText().toString();
        companyEmailLayout.setError(null);
        if(!theCompanyEmail.isEmpty()) {
            if (theCompanyEmail.length() < 5) {
                companyEmailLayout.setError("Error! Company's email too short");
                return false;
            } else if (!isEmailValid(theCompanyEmail)) {
                companyEmailLayout.setError("Error! Invalid email address");
                return false;
            }else {
                companyEmailLayout.setError(null);
            }
        }

        if(!companyFloorNo.getText().toString().isEmpty()) {
            try {
                theCompanyFloorNo = Integer.parseInt(companyFloorNo.getText().toString());
                companyFloorNoLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                companyFloorNoLayout.setError("Error! Floor Number must be a number not a text!");
                return false;
            }

            if (theCompanyFloorNo < 1 || theCompanyFloorNo >= 200) {
                companyFloorNoLayout.setError("Please enter valid Floor number!");
                return false;
            } else {
                companyFloorNoLayout.setError(null);
            }
        }

        return true;
    }

    @Override
    public void onListFragmentInteraction(final List<Company> companies, String btn, final int position, final MyCompanyRecyclerViewAdapter adapter, final List<Building> buildings) {
        final Company company = companies.get(position);
        if(btn.equals("card")){
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.update_company, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);

            companyNameLayout = view.findViewById(R.id.textInputLayoutUCName);
            companyEmailLayout = view.findViewById(R.id.textInputLayoutUCEmail);
            companyDoorOrRoomLayout = view.findViewById(R.id.textInputLayoutUCDoorOrRoom);
            companyFloorNoLayout = view.findViewById(R.id.textInputLayoutUCFloorNo);
            companyPhoneNoLayout = view.findViewById(R.id.textInputLayoutUCPhoneNo);


           companyName = view.findViewById(R.id.textInputEditTextUCName);
           companyEmail = view.findViewById(R.id.textInputEditTextUCEmail);
           companyDoorOrRoom = view.findViewById(R.id.textInputEditTextUCDoorOrRoom);
           companyFloorNo = view.findViewById(R.id.textInputEditTextUCFloorNo);
           companyPhoneNo = view.findViewById(R.id.textInputEditTextUCPhoneNo);
           companyBuilding = view.findViewById(R.id.spinner_u_companies);

            /*System.out.println("Before sort.");
            for(Building b: buildings)
                System.out.println(b);*/

            for(int i = 0; i < buildings.size(); i++) {
                Building b = buildings.get(i);
                if(b.getBuilding_id() == company.getBuilding_id()) {
                    Building building = new Building(buildings.get(0));
                    buildings.set(0, b);
                    buildings.set(i, building);
                }
            }

            /*System.out.println("After sort.");
            for(Building b: buildings)
                System.out.println(b);*/

            final ArrayList<String> buildingNames = new ArrayList<>();
            for(Building b: buildings){
                buildingNames.add(b.getName() + ", " + b.getCity());
            }

            buildingNames.set(0, "Update Building:");

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, buildingNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            companyBuilding.setAdapter(spinnerAdapter);
            companyBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    buildingNames.set(0, buildings.get(0).getName() + ", " + buildings.get(0).getCity());
                    Building selectedBuilding = buildings.get(position);
                    theCompanyBuildingId = selectedBuilding.getBuilding_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    System.out.println("Current Building: " + company.getBuilding_id());
                    company.setBuilding_id(company.getBuilding_id());
                }
            });

            builder
                    .setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show toast message when no text is entered
                    if (validateCompany()) {
                        dialog.show();
                        company.setName(theCompanyName.isEmpty() ? company.getName() : theCompanyName);
                        company.setEmail(theCompanyEmail.isEmpty() ? company.getEmail() : theCompanyEmail);
                        company.setDoor_or_room(theCompanyDoorOrRoom.isEmpty() ? company.getDoor_or_room() : theCompanyDoorOrRoom);
                        company.setPhone_no(theCompanyPhoneNo == 0 ? company.getPhone_no() : theCompanyPhoneNo);
                        company.setFloor_number(theCompanyFloorNo == 0 ? company.getFloor_number() : theCompanyFloorNo);
                        company.setBuilding_id(theCompanyBuildingId == 0 ? company.getBuilding_id() : theCompanyBuildingId);
                        disposable.add(apiService
                                .updateCompany(company.getCompany_id(), company, Guard.jwtToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                    @Override
                                    public void onSuccess(JsonObject object) {
                                        dialog.dismiss();
                                        Log.d(TAG, "Response" + object);
                                        companies.set(position, company);
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(AdminHomeActivity.this, "Success! Company Updated!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        dialog.dismiss();
                                        showError(e);
                                    }
                                }));

                    }
                }
            });
        } else if(btn.equals("delete")) {
            dialog.show();
            disposable.add(apiService
                    .deleteCompany(company.getCompany_id(), Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            dialog.dismiss();
                            Log.d(TAG, "Company " + company.getCompany_id() + " deleted!");

                            // Remove and notify adapter about item deletion
                            companies.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, companies.size());
                            Toast.makeText(AdminHomeActivity.this, "Success! Company Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            showError(e);
                        }
                    }));
        }
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

    @Override
    public void onListFragmentInteraction(final List<Company> companies, List<Building> buildings, final List<Guest> guests, String btn, final int position, final MyGuestRecyclerViewAdapter adapter) {
        final Guest guest = guests.get(position);
        if(btn.equals("card")){
            if(guest.getTime_out().equals("null")) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.update_guest, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);

                guestId = view.findViewById(R.id.et_UGuestId);
                guestName = view.findViewById(R.id.et_UGuestName);
                guestPhoneNo = view.findViewById(R.id.et_UGuestPhoneNo);
                guestReason = view.findViewById(R.id.et_UGuestReason);


                guestIdLayout = view.findViewById(R.id.layoutUGuestId);
                guestNameLayout = view.findViewById(R.id.layoutUGuestName);
                guestPhoneNoLayout = view.findViewById(R.id.layoutUGuestPhoneNo);
                guestReasonLayout = view.findViewById(R.id.layoutUGuestReason);
                guestCompany = view.findViewById(R.id.spinner_guestCompanies);

                /*System.out.println("Before sort.");
                for(Building b: buildings)
                    System.out.println(b);*/

                for (int i = 0; i < companies.size(); i++) {
                    Company c = companies.get(i);
                    if (c.getCompany_id() == guest.getCompany_id()) {
                        Company company = new Company(companies.get(0));
                        companies.set(0, c);
                        companies.set(i, company);
                    }
                }

                /*System.out.println("After sort.");
                for(Building b: buildings)
                    System.out.println(b);*/

                final ArrayList<String> companyNames = new ArrayList<>();
                for (Company c : companies) {
                    companyNames.add(c.getName() + ", " + c.getFloor_number() + " floor");
                }

                companyNames.set(0, "Update Company:");

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, companyNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                guestCompany.setAdapter(spinnerAdapter);
                guestCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        companyNames.set(0, companies.get(0).getName() + ", " + companies.get(0).getFloor_number() + " floor");
                        Company selectedCompany = companies.get(position);
                        theGuestCompanyId = selectedCompany.getCompany_id();
                        theGuestBuildingId = selectedCompany.getBuilding_id();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                builder
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show toast message when no text is entered
                        if (validateGuest()) {
                            dialog.show();
                            guest.setGuestId(theGuestId == 0 ? guest.getGuestId() : theGuestId);
                            guest.setFull_names(theGuestName.isEmpty() ? guest.getFull_names() : theGuestName);
                            guest.setPhone_no(theGuestPhoneNo == 0 ? guest.getPhone_no() : theGuestPhoneNo);
                            guest.setGender(theGuestGender.isEmpty() ? guest.getGender() : theGuestGender);
                            guest.setReason_for_visit(theGuestReason.isEmpty() ? guest.getReason_for_visit() : theGuestReason);
                            guest.setCompany_id(theGuestCompanyId == 0 ? guest.getCompany_id() : theGuestCompanyId);
                            guest.setBuilding_id(theGuestBuildingId == 0 ? guest.getBuilding_id() : theGuestBuildingId);
                            Date now = new Date();
                            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                            String timeOut = formatter1.format(now);
                            System.out.println("timeOut: " + timeOut);
                            guest.setTime_out(timeOut);
                            disposable.add(apiService
                                    .updateGuest(guest.getGuest_db_id(), guest, Guard.jwtToken)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject object) {
                                            dialog.dismiss();
                                            Log.d(TAG, "Response" + object);
                                            guests.set(position, guest);
                                            adapter.notifyItemChanged(position);
                                            Toast.makeText(AdminHomeActivity.this, "Success! Guest Updated!", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            showError(e);
                                        }
                                    }));

                        }
                    }
                });
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Sorry, you can't update this guest. He/she has already been signed out.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        } else if(btn.equals("delete")) {
            dialog.show();
            disposable.add(apiService
                    .deleteGuest(guest.getGuest_db_id(), Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            dialog.dismiss();
                            Log.d(TAG, "Guest " + guest.getGuest_db_id() + " deleted!");

                            // Remove and notify adapter about item deletion
                            guests.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, guests.size());
                            Toast.makeText(AdminHomeActivity.this, "Success! Guest Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            showError(e);
                        }
                    }));
        } else if(btn.equals("signIn")) {
            if(guest.getTime_out().equals("null")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Error! Sign out this guest before signing them in again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            } else {
                final Guest newGuest = new Guest(guest);
                Date now = new Date();
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                String timeIn = formatter1.format(now);
                System.out.println("timeIn: " + timeIn);
                newGuest.setTime_in(timeIn);
                newGuest.setTime_out(timeIn);
                dialog.show();
                disposable.add(apiService
                .createGuest(newGuest, Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject object) {
                        dialog.dismiss();
                        Log.d(TAG, "Sign In Response: " + object);
                        JsonObject jsonObject = object;
                        String name = jsonObject.get("full_names").getAsString();
                        String gender = jsonObject.get("gender").getAsString();
                        int guestId = jsonObject.get("guestId").getAsInt();
                        int id = jsonObject.get("id").getAsInt();
                        long phoneNo = jsonObject.get("phone_no").getAsLong();
                        String reason = jsonObject.get("reason_for_visit").getAsString();
                        String time_in = jsonObject.get("time_in").getAsString();
                        String time_out = jsonObject.get("time_out").isJsonNull() ? "null" : jsonObject.get("time_out").getAsString();
                        JsonObject guard = (JsonObject) jsonObject.getAsJsonArray("guards").get(0);
                        int guardId = guard.get("id").getAsInt();
                        JsonObject building = (JsonObject) jsonObject.getAsJsonArray("buildings").get(0);
                        int building_id = building.get("id").getAsInt();
                        JsonObject company = (JsonObject) jsonObject.getAsJsonArray("companies").get(0);
                        int company_id = company.get("id").getAsInt();

                        name = name.replaceAll("^\"|\"$", "");
                        gender = gender.replaceAll("^\"|\"$", "");
                        reason = reason.replaceAll("^\"|\"$", "");
                        time_in = time_in.replaceAll("^\"|\"$", "");
                        time_out = time_out.replaceAll("^\"|\"$", "");

                        Guest guest = new Guest(guestId, name, phoneNo, gender, reason, time_in, time_out, building_id,
                                company_id, guardId);
                        guest.setGuest_db_id(id);
                        System.out.println(guest);
                        Toast.makeText(AdminHomeActivity.this, "New Guest Sign In added!", Toast.LENGTH_SHORT).show();
                        guests.add(guest);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        showError(e);
                    }
                }));
            }
        } else if(btn.equals("signOut")) {
            if(guest.getTime_out().equals("null")) {
                Date now = new Date();
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                String timeOut = formatter1.format(now);
                System.out.println("timeOut: " + timeOut);
                guest.setTime_out(timeOut);
                dialog.show();
                disposable.add(apiService
                        .updateGuest(guest.getGuest_db_id(), guest, Guard.jwtToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject object) {
                                dialog.dismiss();
                                Log.d(TAG, "Response" + object);
                                guests.set(position, guest);
                                adapter.notifyItemChanged(position);
                                Toast.makeText(AdminHomeActivity.this, "Success! Guest Signed Out!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                showError(e);
                            }
                        }));
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Error! This guest has already been signed out!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
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
            case R.id.radio_receptionist:
                if (checked)
                    theEmployeeRole = "Receptionist";
                break;
            case R.id.radio_other:
                if (checked)
                    theEmployeeRole = "Other";
                break;
        }
    }

    private boolean validateEmployee() {
        theEmployeeName = employeeName.getText().toString();
        employeeNameLayout.setError(null);
        if (!theEmployeeName.isEmpty() && theEmployeeName.length() < 5) {
            employeeNameLayout.setError("Error! Guest's name is too short");
            return false;
        } else {
            employeeNameLayout.setError(null);
        }

        if(!employeePhoneNo.getText().toString().isEmpty()) {
            try {
                theEmployeePhoneNo = Long.parseLong(employeePhoneNo.getText().toString());
                employeePhoneNoLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                employeePhoneNoLayout.setError("Error! Phone Number must be a number not a text!");
                return false;
            }
            if (theEmployeePhoneNo < 254700000001L || theEmployeePhoneNo > 254799999999L) {
                employeePhoneNoLayout.setError("Please enter valid phone number!");
                return false;
            } else {
                employeePhoneNoLayout.setError(null);
            }
        }

        if(!employeeId.getText().toString().isEmpty()) {
            try {
                theEmployeeId = Integer.parseInt(employeeId.getText().toString());
                employeeIdLayout.setError(null);
            } catch (Exception e) {
                e.printStackTrace();
                employeeIdLayout.setError("Error! ID number must be a number not a text!");
                return false;
            }
            if (theEmployeeId < 10000000  || theEmployeeId > 99999999) {
                employeeIdLayout.setError("Please enter valid ID number!");
                return false;
            } else {
                employeeIdLayout.setError(null);
            }
        }

        return true;
    }


    @Override
    public void onListFragmentInteraction(final List<Company> companies, List<Guard> guards, List<Building> buildings, final List<Employee> employees, final MyEmployeeRecyclerViewAdapter adapter, String btn, final int position) {
        final Employee employee = employees.get(position);
        if(btn.equals("card")){
            if(employee.getTime_out().equals("null")) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.update_employee, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);

                employeeName = view.findViewById(R.id.et_empName);
                employeeId = view.findViewById(R.id.et_empId);
                employeePhoneNo = view.findViewById(R.id.et_empPhoneNo);

                employeeIdLayout = view.findViewById(R.id.l_empId);
                employeeNameLayout = view.findViewById(R.id.l_empName);
                employeePhoneNoLayout = view.findViewById(R.id.l_empPhoneNo);

                employeeCompany = view.findViewById(R.id.spinner_empCompanies);

                /*System.out.println("Before sort.");
                for(Building b: buildings)
                    System.out.println(b);*/

                for (int i = 0; i < companies.size(); i++) {
                    Company c = companies.get(i);
                    if (c.getCompany_id() == employee.getCompany_id()) {
                        Company company = new Company(companies.get(0));
                        companies.set(0, c);
                        companies.set(i, company);
                    }
                }

                /*System.out.println("After sort.");
                for(Building b: buildings)
                    System.out.println(b);*/

                final ArrayList<String> companyNames = new ArrayList<>();
                for (Company c : companies) {
                    companyNames.add(c.getName() + ", " + c.getFloor_number() + " floor");
                }

                companyNames.set(0, "Update Company:");

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, companyNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeCompany.setAdapter(spinnerAdapter);
                employeeCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        companyNames.set(0, companies.get(0).getName() + ", " + companies.get(0).getFloor_number() + " floor");
                        Company selectedCompany = companies.get(position);
                        theEmployeeCompanyId = selectedCompany.getCompany_id();
                        theEmployeeBuildingId = selectedCompany.getBuilding_id();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                builder
                        .setCancelable(false)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show toast message when no text is entered
                        if (validateEmployee()) {
                            dialog.show();
                            employee.setEmployeeId(theEmployeeId == 0 ? employee.getEmployeeId() : theEmployeeId);
                            employee.setName(theEmployeeName.isEmpty() ? employee.getName() : theEmployeeName);
                            employee.setPhone_no(theEmployeePhoneNo == 0 ? employee.getPhone_no() : theEmployeePhoneNo);
                            employee.setRole(theEmployeeRole.isEmpty() ? employee.getRole() : theEmployeeRole);
                            employee.setCompany_id(theEmployeeCompanyId == 0 ? employee.getCompany_id() : theEmployeeCompanyId);
                            employee.setBuilding_id(theEmployeeBuildingId == 0 ? employee.getBuilding_id() : theEmployeeBuildingId);
                            Date now = new Date();
                            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                            String timeOut = formatter1.format(now);
                            System.out.println("timeOut: " + timeOut);
                            employee.setTime_out(timeOut);
                            disposable.add(apiService
                                    .updateEmployee(employee.getEmployee_db_id(), employee, Guard.jwtToken)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject object) {
                                            dialog.dismiss();
                                            Log.d(TAG, "Response" + object);
                                            employees.set(position, employee);
                                            adapter.notifyItemChanged(position);
                                            Toast.makeText(AdminHomeActivity.this, "Success! Employee Updated!", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            dialog.dismiss();
                                            showError(e);
                                        }
                                    }));

                        }
                    }
                });
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Sorry, you can't update this employee. He/she has already been signed out.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        } else if(btn.equals("delete")) {
            dialog.show();
            disposable.add(apiService
                    .deleteEmployee(employee.getEmployee_db_id(), Guard.jwtToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            dialog.dismiss();
                            Log.d(TAG, "Employee " + employee.getEmployee_db_id() + " deleted!");

                            // Remove and notify adapter about item deletion
                            employees.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, employees.size());
                            Toast.makeText(AdminHomeActivity.this, "Success! Employee Deleted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            showError(e);
                        }
                    }));
        } else if(btn.equals("signIn")) {
            if(employee.getTime_out().equals("null")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Error! Sign out this employee before signing them in again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            } else if(employee.getRole().equals("Receptionist")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Error! Can't sign in a Receptionist! Please tell this employee to sign themselves in using the app!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            } else {
                final Employee emp = new Employee(employee);
                Date now = new Date();
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                String timeIn = formatter1.format(now);
                System.out.println("timeIn: " + timeIn);
                emp.setTime_in(timeIn);
                emp.setTime_out(timeIn);
                emp.setPassword("null");
                dialog.show();
                disposable.add(apiService
                        .createEmployee(emp, Guard.jwtToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject object) {
                                dialog.dismiss();
                                Log.d(TAG, "Sign In Response: " + object);
                                JsonObject jsonObject = (JsonObject) object;
                                String name = jsonObject.get("name").getAsString();
                                int employeeId = jsonObject.get("employeeId").getAsInt();
                                int id = jsonObject.get("id").getAsInt();
                                long phoneNo = jsonObject.get("phone_no").getAsLong();
                                String role = jsonObject.get("role").getAsString();
                                String time_in = jsonObject.get("time_in").getAsString();
                                String time_out = jsonObject.get("time_out").isJsonNull() ? "null" : jsonObject.get("time_out").getAsString();
                                JsonObject guard = (JsonObject) jsonObject.getAsJsonArray("guards").get(0);
                                int guardId = guard.get("id").getAsInt();
                                int building_id = guard.get("building_id").getAsInt();
                                int company_id = jsonObject.get("company_id").getAsInt();

                                name = name.replaceAll("^\"|\"$", "");
                                role = role.replaceAll("^\"|\"$", "");
                                time_in = time_in.replaceAll("^\"|\"$", "");
                                time_out = time_out.replaceAll("^\"|\"$", "");

                                Employee employee = new Employee(employeeId, name, phoneNo, role, time_in, time_out, company_id, guardId, building_id);
                                employee.setEmployee_db_id(id);
                                System.out.println(employee);
                                Toast.makeText(AdminHomeActivity.this, "New Employee Sign In added!", Toast.LENGTH_SHORT).show();
                                employees.add(employee);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                showError(e);
                            }
                        }));
            }
        } else if(btn.equals("signOut")) {
            if(employee.getTime_out().equals("null")) {
                Date now = new Date();
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                String timeOut = formatter1.format(now);
                System.out.println("timeOut: " + timeOut);
                employee.setTime_out(timeOut);
                dialog.show();
                disposable.add(apiService
                        .updateEmployee(employee.getEmployee_db_id(), employee, Guard.jwtToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject object) {
                                dialog.dismiss();
                                Log.d(TAG, "Response" + object);
                                employees.set(position, employee);
                                adapter.notifyItemChanged(position);
                                Toast.makeText(AdminHomeActivity.this, "Success! Employee Signed Out!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                showError(e);
                            }
                        }));
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("Error! This Employee has already been signed out!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }
}
