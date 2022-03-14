package com.example.androidaf.autofill;

import android.app.assist.AssistStructure;
import android.os.Build;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Utils {
    // The URLs are blacklisted from autofilling
    public static HashSet<String> BlacklistedUris = new HashSet<String>(
            Arrays.asList("com.android.settings",
                    "com.example.androidaf",
                    "com.android.settings.intelligence",
                    "com.cystack.locker"
            )
    );
    @NonNull
    public static AssistStructure getLatestAssistStructure(@NonNull FillRequest request) {
        List<FillContext> fillContexts = request.getFillContexts();
        return fillContexts.get(fillContexts.size() - 1).getStructure();
    }

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().isEmpty();
    }
}
