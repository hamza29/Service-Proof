package com.glowingsoft.serviceproof;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText name, type, size, concern;
    public static final String PREFERENCE = "preference";
    public static final String PREF_NAME = "nam";
    public static final String PREF_TYPE = "type";
    public static final String PREF_SIZE = "sze";
    public static final String PREF_CONCERN = "cncrn";
    public static final String PREF_STATUS = "status";
    SharedPreferences mSharedPreferences;
    MaterialSpinner materialSpinner;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.input_name);
        btnNext = findViewById(R.id.next);
        materialSpinner = findViewById(R.id.spinner_po);
        type = findViewById(R.id.input_type);
        size = findViewById(R.id.input_size);
        concern = findViewById(R.id.input_concern);
        mSharedPreferences = getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        List<String> list = new ArrayList<>();
        list.add("Preliminary Inspection");
        list.add("Before");
        list.add("During");
        list.add("After");
        list.add("Quality Assurance");
        materialSpinner.setItems(list);
        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                mSharedPreferences.edit().putString(PREF_STATUS, "" + item).commit();

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mSharedPreferences.edit().putString(PREF_NAME, "" + name.getText().toString()).commit();
                mSharedPreferences.edit().putString(PREF_TYPE, "" + type.getText().toString()).commit();
                mSharedPreferences.edit().putString(PREF_SIZE, "" + size.getText().toString()).commit();
                mSharedPreferences.edit().putString(PREF_CONCERN, "" + concern.getText().toString()).commit();
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
    }
}
