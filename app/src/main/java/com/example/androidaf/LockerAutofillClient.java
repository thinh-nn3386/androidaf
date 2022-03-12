package com.example.androidaf;

import static android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;


import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import android.widget.Button;
import android.util.ArrayMap;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.androidaf.af.AutofillData;
import com.example.androidaf.af.AutofillDataKeychain;
import com.example.androidaf.af.LockerAutoFillService;
import com.example.androidaf.af.LoginList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


@RequiresApi(api = Build.VERSION_CODES.O)
public class LockerAutofillClient extends AppCompatActivity {
    private static final String DOMAIN = "domain";
    private static final String EXTRA_HINTS = "hints";
    private static final String EXTRA_IDS = "ids";
    private static int sPendingIntentId = 0;

    public static IntentSender newIntentSenderForResponse(@NonNull Context context,
                                                          @NonNull ArrayMap<String, AutofillId> fields, String domain) {

        Intent intent = new Intent(context, LockerAutofillClient.class);

        int size = fields.size();
        String[] hints = new String[size];
        AutofillId[] ids = new AutofillId[size];
        for (int i = 0; i < size; i++) {
            hints[i] = fields.keyAt(i);
            ids[i] = fields.valueAt(i);
        }

        intent.putExtra(EXTRA_HINTS, hints);
        intent.putExtra(EXTRA_IDS, ids);
        intent.putExtra(DOMAIN, domain);

        return PendingIntent.getActivity(context, ++sPendingIntentId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT).getIntentSender();
    }


    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private final ActivityResultLauncher<Intent> loginList =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::lockerLauncherResult);

    private ArrayList<AutofillData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                preformLoginList();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BIOMETRIC_STRONG|DEVICE_CREDENTIAL)
                .setConfirmationRequired(false)
                .build();

        biometricPrompt.authenticate(promptInfo);

        setContentView(R.layout.activity_locker_autofill_client);
        AutofillDataKeychain autoFillHelper = new AutofillDataKeychain();
        List<AutofillData> loginList = new ArrayList<>();
        for (int i = 0 ; i < 10; i ++){
            AutofillData data  = new AutofillData(String.valueOf(i), String.valueOf(i),"asd",String.valueOf(i),String.valueOf(i));
            autoFillHelper.credentials.add(data);
//            autoFillHelper.otherCredentials.add(data);
        }
        data = autoFillHelper.credentials;


        TextView email  = (TextView) findViewById(R.id.mp_email);
        email.setText(autoFillHelper.email);


//        // Prompt appears when user clicks "Log in".
//        // Consider integrating with the keystore to unlock cryptographic operations,
//        // if needed by your app.
//        Button biometricLoginButton = findViewById(R.id.biometric_login);
//        biometricLoginButton.setOnClickListener(view -> {
//
//        });
    }



    private void lockerLauncherResult(final ActivityResult result) {
        if (result.getResultCode() == 1) {
            Intent intent = result.getData();
            if (intent != null) {
                AutofillData data = (AutofillData) intent.getSerializableExtra("autofill_data");
                onFillPassword(data);
            }
        }
        else {
            cancel();
        }
    }

    private void onFillPassword(AutofillData data) {

        Intent myIntent = getIntent();
        Intent replyIntent = new Intent();

        String[] hints = myIntent.getStringArrayExtra(EXTRA_HINTS);
        Parcelable[] ids = myIntent.getParcelableArrayExtra(EXTRA_IDS);
        int size = hints.length;
        ArrayMap<String, AutofillId> fields = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            fields.put(hints[i], (AutofillId) ids[i]);
        }
        RemoteViews presentation = new RemoteViews(getPackageName(), R.layout.remote_locker_app); // crash ?
        Dataset response =
                LockerAutoFillService.newUnlockDataset(fields, data, presentation);
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
        Intent i = new Intent(this, LoginList.class);
        i.putExtra("data", data);
        loginList.launch(i);
    }
}