package com.example.androidaf.autofill.activities;

import static android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.androidaf.R;
import com.example.androidaf.autofill.AutofillItem;
import com.example.androidaf.autofill.AutofillDataKeychain;
import com.example.androidaf.autofill.Data;
import com.example.androidaf.autofill.Field;
import com.example.androidaf.autofill.LockerAutoFillService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VerifyMasterPasswordActivity extends AppCompatActivity {
    public static final String DOMAIN = "domain";
    public static final String EXTRA_FILLABLE = "autofill";
    public static final String EXTRA_LOGIN_DATA = "login_data";
    private static int sPendingIntentId = 0;


    public static IntentSender newIntentSenderForResponse(@NonNull Context context,
                                                          ArrayList<Field> fields, String domain) {

        Intent intent = new Intent(context, VerifyMasterPasswordActivity.class);
//        intent.putExtra(DOMAIN, domain);
        intent.putExtra(EXTRA_FILLABLE, (Serializable) fields);

        return PendingIntent.getActivity(context, 1001, intent,
                PendingIntent.FLAG_CANCEL_CURRENT).getIntentSender();
    }

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private final ActivityResultLauncher<Intent> loginList =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::lockerLauncherResult);

    private ArrayList<AutofillItem> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_autofill_client);

//        getSupportActionBar().hide();
//        Log.d("njsbdfijhdabf", getIntent().getStringExtra(DOMAIN));
//        ArrayList<Field> fields = (ArrayList<Field>) getIntent().getSerializableExtra(EXTRA_FILLABLE);
//        Log.d("asd", String.valueOf(fields.size()));
        //        executor = ContextCompat.getMainExecutor(this);
//        biometricPrompt = new BiometricPrompt(this,
//                executor, new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode,
//                                              @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                Toast.makeText(getApplicationContext(),
//                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
//                        .show();
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(
//                    @NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                preformLoginList();
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                Toast.makeText(getApplicationContext(), "Authentication failed",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("Biometric login for my app")
//                .setSubtitle("Log in using your biometric credential")
//                .setAllowedAuthenticators(BIOMETRIC_STRONG|DEVICE_CREDENTIAL)
//                .setConfirmationRequired(false)
//                .build();
//
//        biometricPrompt.authenticate(promptInfo);

        AutofillDataKeychain autoFillHelper = new AutofillDataKeychain();


        for (int i = 0 ; i < 10; i ++){
            AutofillItem data  = new AutofillItem(String.valueOf(i), String.valueOf(i),"asd",String.valueOf(i),String.valueOf(i));
            autoFillHelper.credentials.add(data);
//            autoFillHelper.otherCredentials.add(data);
        }
        data = autoFillHelper.credentials;

//

        TextView email  = findViewById(R.id.mp_email);
        email.setText(autoFillHelper.email);
    }
    private void lockerLauncherResult(final ActivityResult result) {
        if (result.getResultCode() == 1) {
            Intent intent = result.getData();
            if (intent != null) {
                AutofillItem data = intent.getParcelableExtra(EXTRA_LOGIN_DATA);
                onFillPassword(data);
            }
        }
        else {
            cancel();
        }
    }

    private void onFillPassword(AutofillItem data) {

        ArrayList<Field> fields = (ArrayList<Field>) getIntent().getSerializableExtra(EXTRA_FILLABLE);

        RemoteViews presentation = new RemoteViews(getPackageName(), R.layout.remote_locker_app); // crash ?
        Dataset response = LockerAutoFillService.newUnlockDataset(fields, data, presentation);

        Intent replyIntent = new Intent();

        replyIntent.putExtra(EXTRA_AUTHENTICATION_RESULT, response);

        setResult(RESULT_OK, replyIntent);
        finish();
    }
    public void unlock(View view) {
        preformLoginList();
    }

    public void biometricAuthen(View view) {
    }

    public void cancel(View view) {
        cancel();
    }

    private void cancel(){
        setResult(RESULT_CANCELED);
        finish();
    }
    private void preformLoginList() {
//        Intent i = new Intent(this, LoginListActivity.class);
//        i.putParcelableArrayListExtra(EXTRA_LOGIN_DATA, data);
//        i.putExtra(DOMAIN, getIntent().getStringExtra(DOMAIN));
//        loginList.launch(i);


        ArrayList<Data> lists = new ArrayList<>();
        lists.add(new Data(12, "1212"));
        lists.add(new Data(12, "1212"));
        lists.add(new Data(12, "1212"));
        lists.add(new Data(12, "1212"));
        Log.d("asd1", String.valueOf(lists.size()));


        Intent intent = new Intent(this, LoginListActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_LOGIN_DATA, data);

        intent.putExtra("StringKey",lists);
        intent.putExtra("asd","Asdasd");

        startActivity(intent);
    }
}