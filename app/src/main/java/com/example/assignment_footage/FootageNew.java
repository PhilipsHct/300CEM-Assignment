package com.example.assignment_footage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_footage.adapter.locationRecordAdapter;
import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.Footage;
import com.example.assignment_footage.models.LocationRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

public class FootageNew extends AppCompatActivity {
    private int locationRequestCode = 1000;
    private static final int LOCATIONRECORD_REQUEST = 11;

    FootageNew footageNew;

    EditText et_footageName;
    TextView tv_today;
    LinearLayout bottom_new;
    Button btn_record;
    Button btn_cancel;
    LinearLayout bottom_recording;
    LinearLayout bottom_status;
    LinearLayout line3;
    Button btn_newLocation;
    Button btn_finish;
    Button btn_delete;
    Footage footage;
    ListView lv_LocationRecords;

    Date today;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footage_new);

        footageNew = this;

        et_footageName = (EditText) findViewById(R.id.et_footageName);
        tv_today = (TextView) findViewById(R.id.tv_today);
        bottom_new = (LinearLayout) findViewById(R.id.bottom_new);
        btn_record = (Button) findViewById(R.id.btn_record);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        bottom_recording = (LinearLayout) findViewById(R.id.bottom_recording);
        bottom_status = (LinearLayout) findViewById(R.id.bottom_status);
        line3 = (LinearLayout) findViewById(R.id.line3);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_newLocation = (Button) findViewById(R.id.btn_newLocation);
        lv_LocationRecords = (ListView) findViewById(R.id.lv_LocationRecords);
        footage = null;

        today = new Date(Calendar.getInstance().getTimeInMillis());
        tv_today.setText(today.toString());

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String footageName = et_footageName.getText().toString();
                if(!footageName.isEmpty()){
                    // Create new Footage
                    FootageHelper footageHelper = FootageHelper.getInstance(v.getContext());
                    footage = new Footage(footageName, today, "");
                    int footageId = footageHelper.addFootage(footage);
                    footage.setId(footageId);

                    et_footageName.setEnabled(false);
                    bottom_new.setVisibility(View.INVISIBLE);
                    bottom_recording.setVisibility(View.VISIBLE);
                    bottom_status.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(footageNew, "Name cannot be empty", Toast.LENGTH_LONG).show();
                }
                autoRecord();
            }
        }); // btn_record onclick end

        btn_newLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLocationService();
                if(ContextCompat.checkSelfPermission(footageNew, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                   ContextCompat.checkSelfPermission(footageNew, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    //ACCESS FAILED
                    ActivityCompat.requestPermissions(footageNew, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            locationRequestCode);

                } else {
                    client.getLastLocation()
                            .addOnCompleteListener(footageNew, new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    LocationRecord locationRecord = new LocationRecord(
                                            footage.getId(), location.getLongitude(), location.getLatitude(), new Time(Calendar.getInstance().getTimeInMillis())
                                    );
                                    FootageHelper footageHelper = FootageHelper.getInstance(footageNew);
                                    int row_id = footageHelper.addLocationRecord(locationRecord);
                                    locationRecord.setId(row_id);

                                    Intent intent = new Intent(footageNew, LocationRecordNew.class);
                                    intent.putExtra("LocationRecordID", String.valueOf(locationRecord.getId()));
                                    startActivityForResult(intent, LOCATIONRECORD_REQUEST);
                                }
                            });
                }
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoRecord();
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FootageHelper footageHelper = FootageHelper.getInstance(v.getContext());
                List<LocationRecord> locationRecords = footageHelper.getAllLocationRecords(footage.getId());
                footageHelper.deleteFootage(footage);
                footageHelper.deleteLocationRecord(footage);
                for(LocationRecord lr : locationRecords){
                    footageHelper.deleteNote(lr);
                }
                finish();
            }
        });
    }

    protected void autoRecord(){
        initLocationService();
        if(ContextCompat.checkSelfPermission(footageNew, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(footageNew, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //ACCESS FAILED
            ActivityCompat.requestPermissions(footageNew, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        }
        client.getLastLocation()
                .addOnCompleteListener(footageNew, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        LocationRecord locationRecord = new LocationRecord(
                                footage.getId(), location.getLongitude(), location.getLatitude(), new Time(Calendar.getInstance().getTimeInMillis())
                        );
                        FootageHelper footageHelper = FootageHelper.getInstance(footageNew);
                        footageHelper.addLocationRecord(locationRecord);
                    }
                });
    }

    protected void initLocationService(){
        client = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == LOCATIONRECORD_REQUEST){
            if(resultCode == RESULT_OK){
                refreshLocatioRecordList();

            }
        }
    }

    public void refreshLocatioRecordList(){
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        List<LocationRecord> locationRecords = footageHelper.getAllLocationRecords(footage.getId());
        ListAdapter listAdapter = new locationRecordAdapter(this, locationRecords);
        lv_LocationRecords.setAdapter(listAdapter);
    }
}
