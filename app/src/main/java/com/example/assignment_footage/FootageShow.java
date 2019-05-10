package com.example.assignment_footage;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment_footage.adapter.locationRecordAdapter2;
import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.Footage;
import com.example.assignment_footage.models.LocationRecord;

import java.sql.Time;
import java.util.List;


public class FootageShow extends AppCompatActivity {
    TextView tv_RecordCount;
    TextView tv_FootageDistance;
    TextView tv_FootageTime;

    ListView lv_locationRecords;
    Footage footage;
    FootageShow footageShow;
    List<LocationRecord> locationRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footage_show);

        footageShow = this;
        int footageId;
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                footageId = 1;
            }else {
                footageId = Integer.parseInt(extras.getString("FootageID","1"));
            }
        } else {
            footageId = Integer.parseInt((String) savedInstanceState.getSerializable("FootageID"));
        }
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        footage = footageHelper.getFootage(footageId);
        locationRecords = footageHelper.getAllLocationRecords(footageId);

        tv_RecordCount = (TextView) findViewById(R.id.tv_RecordCount);
        tv_FootageDistance = (TextView) findViewById(R.id.tv_FootageDistance);
        tv_FootageTime = (TextView) findViewById(R.id.tv_FootageTime);
        lv_locationRecords = (ListView) findViewById(R.id.lv_LocationRecords);

        refreshLocationRecordList();
        refreshTimeAndDistance();
        lv_locationRecords.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int locationRecordId = locationRecords.get(position).getId();
                Intent intent = new Intent(footageShow, LocationRecordShow.class);
                intent.putExtra("LocationRecordID", String.valueOf(locationRecordId));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshLocationRecordList();
        refreshTimeAndDistance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.footage_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshLocationRecordList(){
        ListAdapter listAdapter = new locationRecordAdapter2(this, locationRecords);
        lv_locationRecords.setAdapter(listAdapter);
        tv_RecordCount.setText(String.valueOf(locationRecords.size()));
    }

    private void refreshTimeAndDistance(){
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        locationRecords = footageHelper.getAllLocationRecords(footage.getId());
        Time start, end;
        start = locationRecords.get(0).getRecordTime();
        end = locationRecords.get(locationRecords.size()-1).getRecordTime();
        double distance = getDistance(locationRecords);

        long diff = end.getTime() - start.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        String timeDiff = String.format("%2d h %2d m %2d s", diffHours, diffMinutes % 60, diffSeconds % 60);

        tv_FootageTime.setText(timeDiff);
        tv_FootageDistance.setText(String.format("%.3f KM", distance));
    }

    private double getDistance(List<LocationRecord> lrs){
        double distance = 0;
        for(int i =0; i<lrs.size();i++){
            if(i != lrs.size()-1){
                LocationRecord lr =  lrs.get(i);
                LocationRecord next = lrs.get(i+1);
                distance += getDistanceDiff(lr.getLatitude(), next.getLatitude(), lr.getLongitude(), next.getLongitude());
            }
        }
        return distance;
}

    private double getDistanceDiff(double lat1, double lat2, double lon1, double lon2){
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c ; // convert to kilometers
        return distance;
    }
}
