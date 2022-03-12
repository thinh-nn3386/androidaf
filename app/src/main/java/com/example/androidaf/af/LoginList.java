package com.example.androidaf.af;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.androidaf.R;

import java.util.ArrayList;


@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginList extends AppCompatActivity {
    LoginListAdapter listAdapter;
    ListView listView;
    SearchView searchView;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_list);

        Intent intent = getIntent();
        ArrayList<AutofillData> loginList = (ArrayList<AutofillData>) intent.getSerializableExtra("data");

        listAdapter = new LoginListAdapter(this, loginList);
        listView = findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutofillData data = (AutofillData) listAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("autofill_data", data);
                setResult(1, intent);
                finish();
            }
        });

        searchView = findViewById(R.id.search_bar);
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void cancel(View view){
        setResult(0);
        finish();
    }

}