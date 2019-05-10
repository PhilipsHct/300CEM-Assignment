package com.example.assignment_footage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment_footage.adapter.noteAdapter;
import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.LocationRecord;
import com.example.assignment_footage.models.Note;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRecordShow extends AppCompatActivity {
    TextView tv_lrs_time, tv_lrs_address, tv_lrs_longitude, tv_lrs_latitude;
    ListView lv_lrs_note;
    LocationRecord locationRecord;
    List<Note> notes;
    LocationRecordShow locationRecordShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_record_show);

        locationRecordShow = this;

        tv_lrs_time = (TextView) findViewById(R.id.tv_lrs_time);
        tv_lrs_address = (TextView) findViewById(R.id.tv_lrs_address);
        tv_lrs_longitude = (TextView) findViewById(R.id.tv_lrs_longitude);
        tv_lrs_latitude = (TextView) findViewById(R.id.tv_lrs_latitude);
        lv_lrs_note = (ListView) findViewById(R.id.lv_lrs_note);

        int locationRecordId;
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                locationRecordId = 1;
            }else {
                locationRecordId = Integer.parseInt(extras.getString("LocationRecordID","1"));
            }
        } else {
            locationRecordId = Integer.parseInt((String) savedInstanceState.getSerializable("LocationRecordID"));
        }
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        locationRecord = footageHelper.getLocationRecord(locationRecordId);

        tv_lrs_time.setText(locationRecord.getRecordTime().toString());
        tv_lrs_longitude.setText(String.format("%.3f",locationRecord.getLongitude()));
        tv_lrs_latitude.setText(String.format("%.3f", locationRecord.getLatitude()));
        reverseGeocode(locationRecord.getLongitude(), locationRecord.getLatitude(), tv_lrs_address);
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
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

    public void refreshList(){
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        int locationRecordId = locationRecord.getId();
        notes = footageHelper.getAllNotes(locationRecordId);
        ListAdapter listAdapter = new noteAdapter(this, notes);
        lv_lrs_note.setAdapter(listAdapter);
    }

    protected void reverseGeocode(final double longitude, final double latitude, final TextView tv_address){
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoidGluZzE5OTUwNzI4IiwiYSI6ImNqdmhsZXhmOTA0dzU0OW50emlwNWZhcmoifQ.r8AGQiXOqsU_pyTk5ycxEA")
                .query(Point.fromLngLat(longitude, latitude))
                .build();
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();
                Log.d("features", "num:" + results.size());

                if (results.size() > 0) {
                    Log.d("reverse", "CAPTURE RESULTS");
                    // Log the first results Point.
                    String firstResult = results.get(0).placeName();
                    tv_address.setText(firstResult);

                } else {
                    // No result for your request were found.
                    Log.d("reverse", "No result");
                    tv_address.setText("Unknown");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
