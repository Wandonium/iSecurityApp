package com.techcloud.isecurity.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
//import com.techcloud.isecurity.camera.HolderClass;
import com.techcloud.isecurity.helpers.HolderClass;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Guard;
import com.techcloud.isecurity.models.Guest;
import com.techcloud.isecurity.mrz.MrzParseException;
import com.techcloud.isecurity.mrz.MrzParser;
import com.techcloud.isecurity.mrz.MrzRange;
import com.techcloud.isecurity.mrz.MrzRecord;
import com.techcloud.isecurity.server.ApiClient;
import com.techcloud.isecurity.server.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CardActivity extends AppCompatActivity {

    public static final String TAG = CardActivity.class.getSimpleName();

    private Bitmap bitmap;
    private String photoPath;
    private CompositeDisposable disposable;
    private ApiService apiService;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    private LinearLayout nestedScrollView;
    private AppCompatTextView documentNo;
    private AppCompatTextView documentType;
    private AppCompatTextView issuingCountry;
    private AppCompatTextView serialNo;
    private AppCompatTextView name;
    private AppCompatTextView dob;
    private AppCompatTextView gender;
    private AppCompatTextView dateOfIssue;
    private AppCompatButton scanAgain;
    private AppCompatButton addDetails;
    private AppCompatButton manualInput;

    private Guest guest;
    private boolean state;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        documentNo = findViewById(R.id.tv_the_id_no);
        documentType = findViewById(R.id.tv_the_doc_type);
        issuingCountry = findViewById(R.id.tv_the_issuing_country);
        serialNo = findViewById(R.id.tv_the_serial_no);
        name = findViewById(R.id.tv_the_name);
        dob = findViewById(R.id.tv_the_dob);
        gender = findViewById(R.id.tv_the_gender);
        dateOfIssue = findViewById(R.id.tv_the_date_of_issue);
        scanAgain = findViewById(R.id.btn_scan_again);
        addDetails = findViewById(R.id.btn_add_details);
        manualInput = findViewById(R.id.btn_manual);

        builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_bar);
        dialog = builder.create();

        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        guest = new Guest();
        state = false;

        bitmap = HolderClass.theBitmap;
        File photoFile = null;
        try {
            photoFile = createImageFile();
            final FileOutputStream out = new FileOutputStream(photoFile);
            AsyncTask<Void, Void, Void> compress = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        out.flush();
                        out.close();
                        if(photoPath != null) {
                            dialog.show();
                            uploadImage();
                        } else {
                            System.out.println("currentPhotoPath is null!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            compress.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this, ScanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state) {
                    Intent intent = new Intent(CardActivity.this, GuestDetailsActivity.class);
                    intent.putExtra("Guest", guest);
                    startActivity(intent);
                    finish();
                } else {
                    String message = "Error! No Guest data! Please scan again!";
                    Toast toast=Toast.makeText(CardActivity.this,message,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM,0,0);
                    View view=toast.getView();
                    TextView view1 = view.findViewById(android.R.id.message);
                    view1.setTextColor(Color.YELLOW);
                    toast.show();
                }
            }
        });

        manualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this, ManualInputActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(HolderClass.user.equals("admin")) {
            Intent intent = new Intent(CardActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        } else if(HolderClass.user.equals("guard")) {
            Intent intent = new Intent(CardActivity.this, GuardHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    private void uploadImage() {
        //pass it like this
        File file = new File(photoPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        disposable.add(apiService
                .uploadImage(body, Guard.jwtToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonElements) {
                        dialog.dismiss();
                        JsonObject object = jsonElements.getAsJsonObject("result");
                        String mrz = object.get("detectedMrz").toString();
                        state = object.get("state").getAsBoolean();
                        mrz = mrz.replaceAll("^\"|\"$", "");
                        mrz = mrz.replaceAll("\\s", "");
                        if(state) {
                            int end = mrz.length();
                            String row1 = mrz.substring(0, 30);
                            String row2 = mrz.substring(32, end-32);
                            String row3 = mrz.substring(end-30, end);
                            String newMrz = row1 + "\n" + row2 + "\n" + row3;
                            System.out.println("MRZ: " + mrz);
                            System.out.println("newMrz:\n" + newMrz);
                            System.out.println("State: " + state);
                            try {
                                final MrzRecord record = MrzParser.parse(newMrz);
                                System.out.println(record);
                                Toast.makeText(getApplicationContext(), "Parse successful", Toast.LENGTH_LONG).show();
                                documentNo.setText(record.getDocumentNumber());
                                documentType.setText(record.getDocumentType());
                                issuingCountry.setText(record.getIssuingCountry());
                                serialNo.setText(record.getSerialNumber());
                                name.setText(record.getGivenNames());
                                dob.setText(record.getDateOfBirth().toString());
                                gender.setText(record.getSex().toString());
                                dateOfIssue.setText(record.getExpirationDate().toString());
                                guest.setGuestId(Integer.parseInt(record.getDocumentNumber()));
                                guest.setFull_names(record.getGivenNames());
                                guest.setGender(record.getSex().toString());

                            } catch (Exception ex) {
                                Log.e(TAG, "Parse failed" + mrz);
                                Snackbar snackbar = (Snackbar) Snackbar
                                        .make(nestedScrollView, "Fatal Error! Scan Failed!\nPress Scan Again to try again!", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                TextView textView = sbView.findViewById(R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();
                                if (ex instanceof MrzParseException) {
                                    final MrzParseException mpe = (MrzParseException) ex;
                                    final MrzRange r = mpe.range;
                                    System.out.println("MrzParseException...");
                                }
                                ex.printStackTrace();
                            }
                        } else {
                            Snackbar snackbar = (Snackbar) Snackbar
                                    .make(nestedScrollView, "Fatal Error! Poor Image!\nPlease take another photo!", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            TextView textView = sbView.findViewById(R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        HolderClass.showError(e, nestedScrollView);
                    }
                }));
    }
}
