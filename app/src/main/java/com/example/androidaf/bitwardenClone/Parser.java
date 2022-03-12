package com.example.androidaf.bitwardenClone;

import android.app.assist.AssistStructure;
import android.content.Context;

public class Parser {
    private final AssistStructure mStructure;
    private String mUri;
    private String mPackageName;
    private String mWebsite;
    public Context ApplicationContext;
    public FieldCollection FieldCollection = new FieldCollection();

    public Parser(AssistStructure structure, Context applicationContext)
    {
        this.mStructure = structure;
        ApplicationContext = applicationContext;
    }

    public String getmUri() {
        return "Asdasd";
    }


}
