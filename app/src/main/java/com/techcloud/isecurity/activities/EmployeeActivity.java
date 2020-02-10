package com.techcloud.isecurity.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.adapters.EmployeeAdapter;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.helpers.MyDividerItemDecoration;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Company;
import com.techcloud.isecurity.models.Employee;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class EmployeeActivity extends AppCompatActivity implements EmployeeAdapter.EmployeeAdapterListener {

    public static final String TAG = EmployeeActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<Employee> employeeList;
    private List<Company> companyList;
    private EmployeeAdapter adapter;
    private SearchView searchView;
    private ApiService apiService;
    private CompositeDisposable disposable;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.activity_emp);
        // toolbar fancy stuff
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title_2);

        builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_bar);
        dialog = builder.create();

        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        recyclerView = findViewById(R.id.recycler_emps);
        employeeList = new ArrayList<>();
        companyList = new ArrayList<>();
        adapter = new EmployeeAdapter(employeeList, companyList,this, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(adapter);

        dialog.show();
        fetchEmployees();
    }

    private void fetchEmployees() {
        // get companies
        disposable.add(apiService
                .getCompanies(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        Log.d(TAG, "Response: " + jsonElements);
                        for(Object object: jsonElements) {
                            JsonObject jsonObject = (JsonObject) object;
                            int building_id = jsonObject.get("building_id").getAsInt();
                            String doorOrRoom = jsonObject.get("door_or_room").getAsString();
                            String email = jsonObject.get("email").toString();
                            int floorNo = jsonObject.get("floor_number").getAsInt();
                            long phone_no = jsonObject.get("phone_no").getAsLong();
                            String name = jsonObject.get("name").toString();
                            int id = jsonObject.get("id").getAsInt();
                            doorOrRoom = doorOrRoom.replaceAll("^\"|\"$", "");
                            email = email.replaceAll("^\"|\"$", "");
                            name = name.replaceAll("^\"|\"$", "");
                            Company company = new Company(name, email, doorOrRoom,floorNo, phone_no, building_id);
                            company.setCompany_id(id);
                            //System.out.println(company);
                            companyList.add(company);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        HolderClass.showError(e, coordinatorLayout);
                    }
                }));

        // get employees
        disposable.add(apiService
                .getEmployees(Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonArray>() {
                    @Override
                    public void onSuccess(JsonArray jsonElements) {
                        dialog.dismiss();
                        Log.d(TAG, "Employee Response: " + jsonElements);
                        for(Object object: jsonElements) {
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
                            employee.setPassword("null");
                            System.out.println(employee);
                            employeeList.add(employee);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        HolderClass.showError(e, coordinatorLayout);
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_guest, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
        if(HolderClass.user.equals("admin")) {
            Intent intent = new Intent(EmployeeActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        } else if(HolderClass.user.equals("guard")) {
            Intent intent = new Intent(EmployeeActivity.this, GuardHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onEmployeeSelected(final List<Employee> employees, final int position, String btn, final EmployeeAdapter adapter) {
        final Employee employee = employees.get(position);
        if(btn.equals("signIn")) {
            if(employee.getTime_out().equals("null")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EmployeeActivity.this, R.style.AlertDialog);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EmployeeActivity.this, R.style.AlertDialog);
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
                                Toast.makeText(EmployeeActivity.this, "New Employee Sign In added!", Toast.LENGTH_SHORT).show();
                                employees.add(employee);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                HolderClass.showError(e, coordinatorLayout);
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
                                Toast.makeText(EmployeeActivity.this, "Success! Employee Signed Out!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                HolderClass.showError(e, coordinatorLayout);
                            }
                        }));
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EmployeeActivity.this, R.style.AlertDialog);
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
