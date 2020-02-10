package com.techcloud.isecurity.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.text.Html.fromHtml;


public class MainActivity extends AppCompatActivity {

    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x5;
    public static final String TAG = MainActivity.class.getSimpleName();

    private TextInputEditText guardId;
    private TextInputEditText password;
    private TextInputLayout guardIdLayout;
    private TextInputLayout passwordLayout;
    private Button btnLogin;
    private int theGuardId;
    private String thePassword;
    private NestedScrollView scrollView;

    private ApiService apiService;
    private CompositeDisposable disposable;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCreateAccountTextView();
        initViews();

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable = new CompositeDisposable();

        builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ask() && validate()) {
                    theGuardId = Integer.parseInt(guardId.getText().toString());
                    thePassword = password.getText().toString();
                    dialog.show();
                    JsonObject postParams = new JsonObject();
                    try{
                        postParams.addProperty("guardId", theGuardId);
                        postParams.addProperty("password", thePassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    disposable.add(apiService
                            .loginGuard(postParams)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                                @Override
                                public void onSuccess(JsonObject object) {
                                    dialog.dismiss();
                                    Log.d(TAG, "Response: " + object);
                                    JsonObject guardResponse = object.getAsJsonObject("guard");
                                    String jwtToken = object.get("jwt_token").toString();
                                    // remove quotation marks
                                    jwtToken = jwtToken.replaceAll("^\"|\"$", "");
                                    System.out.println("jwtToken: " + jwtToken);
                                    Guard.jwtToken = jwtToken;
                                    String role = object.get("role").toString();
                                    // remove quotation marks
                                    role = role.replaceAll("^\"|\"$", "");
                                    System.out.println("role: " + role);
                                    int theGuardDBId = guardResponse.get("id").getAsInt();
                                    System.out.println("Guard ID: " + theGuardDBId);
                                    int guardId = guardResponse.get("guardId").getAsInt();
                                    String guard_name = guardResponse.get("guard_name").toString();
                                    String password = guardResponse.get("password").toString();
                                    long phone_no = guardResponse.get("phone_no").getAsLong();
                                    String security_company = guardResponse.get("security_company").toString();
                                    int building_id = guardResponse.get("building_id").getAsInt();
                                    guard_name = guard_name.replaceAll("^\"|\"$", "");
                                    password = password.replaceAll("^\"|\"$", "");
                                    security_company = security_company.replaceAll("^\"|\"$", "");
                                    Guard guard = new Guard(guardId, guard_name, phone_no, password, security_company, building_id);
                                    guard.setGuard_db_id(theGuardDBId);
                                    System.out.println(guard);
                                    HolderClass.guard = new Guard(guard);
                                    if(role.equals("admin")) {
                                        Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, GuardHomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
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


    private void initCreateAccountTextView() {
        TextView textViewCreateAccount = findViewById(R.id.textViewCreateAccount);
        textViewCreateAccount.setText(fromHtml("<font color='#000000'>I don't have account yet. </font><font color='#0c0099'>create one</font>"));
        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Please contact the system admin to create an account";
                Toast toast=Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM,0,0);
                View v=toast.getView();
                TextView view1 = v.findViewById(android.R.id.message);
                view1.setTextColor(Color.YELLOW);
                toast.show();
            }
        });
    }

    //This method is used to validate input given by user
    public boolean validate() {
        boolean valid = false;
        int guard_id = 0;

        //Get values from EditText fields
        try {
            guard_id = Integer.parseInt(guardId.getText().toString());
        } catch(Exception e) {
            e.printStackTrace();
            valid = false;
        }
        String Password = password.getText().toString();

        //Handling validation for guard_id field
        if (guard_id < 10000000 || guard_id > 99999999) {
            valid = false;
            guardId.setError("Please enter valid ID number!");
        } else {
            valid = true;
            guardId.setError(null);
        }
        //Handling validation for Password field
        if (Password.isEmpty()) {
            valid = false;
            password.setError("Please enter valid password!");
        } else {
            if (Password.length() > 5) {
                valid = true;
                password.setError(null);
            } else {
                valid = false;
                password.setError("Password is to short!");
            }
        }

        return valid;
    }



    private void initViews() {
        guardId = findViewById(R.id.textInputEditTextGuardID);
        guardIdLayout = findViewById(R.id.textInputLayoutGuardID);
        password = findViewById(R.id.textInputEditTextPassword);
        passwordLayout = findViewById(R.id.textInputLayoutPassword);
        btnLogin = findViewById(R.id.appCompatButtonLogin);
        scrollView = findViewById(R.id.nestedScrollViewMain);
    }


    private boolean askForPermission(String permission, Integer requestCode) {
        if(ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
            return false;
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean ask() {
        boolean answer = false;
        if(askForPermission(Manifest.permission.CAMERA, CAMERA)) {
            if(askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXST)) {
                if(askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST)) {
                    answer = true;
                }
            }
        } else {
            answer = false;
        }
        return answer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(MainActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        System.exit(0);
    }
}
