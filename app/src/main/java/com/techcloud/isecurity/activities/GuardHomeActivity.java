package com.techcloud.isecurity.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class GuardHomeActivity extends AppCompatActivity {

    public static final String TAG = GuardHomeActivity.class.getSimpleName();

    private AppCompatButton logout;
    private CardView SignInGuest, SignOutGuest, Employee, instructions;
    private LinearLayout scrollView;
    private boolean doubleBackToExitPressedOnce = false;

    private CompositeDisposable disposable;
    private ApiService apiService;

    private android.app.AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_home);

        builder = new android.app.AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress_bar);
        }
        dialog = builder.create();

        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        SignInGuest = findViewById(R.id.cv_signInGuest);
        SignOutGuest = findViewById(R.id.cv_signOutGuest);
        Employee = findViewById(R.id.cv_employee);
        logout = findViewById(R.id.btn_logout);
        instructions = findViewById(R.id.cv_instructions);
        scrollView = findViewById(R.id.nestedScrollViewGuardHome);

        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GuardHomeActivity.this, "Tap on above cards then search by tapping on the toolbar!", Toast.LENGTH_SHORT).show();
            }
        });

        SignInGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GuardHomeActivity.this, R.style.AlertDialog);
                builder.setMessage("To sign in a new guest you have to scan their ID card or Passport using the device camera. Press OK to launch the camera...")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                HolderClass.user = "guard";
                                Intent intent = new Intent(GuardHomeActivity.this, ScanActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        SignOutGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HolderClass.user = "guard";
                Intent intent = new Intent(GuardHomeActivity.this, GuestActivity.class);
                startActivity(intent);
            }
        });

        Employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HolderClass.user = "guard";
                Intent intent = new Intent(GuardHomeActivity.this, EmployeeActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Toast.makeText(GuardHomeActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(GuardHomeActivity.this, MainActivity.class);
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
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
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
