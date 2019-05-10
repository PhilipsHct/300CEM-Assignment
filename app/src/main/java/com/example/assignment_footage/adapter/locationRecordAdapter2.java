package com.example.assignment_footage.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_footage.R;
import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.LocationRecord;
import com.example.assignment_footage.models.Note;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class locationRecordAdapter2 extends ArrayAdapter<LocationRecord> {
    private final Context context;
    private final List<LocationRecord> locationRecords;

    public locationRecordAdapter2(Context context,  List<LocationRecord> locationRecords) {
        super(context, -1, locationRecords);
        this.context = context;
        this.locationRecords = locationRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_location_record_detail, parent, false);
        TextView tv_time = (TextView) rowView.findViewById(R.id.tv_lr_time);
        TextView tv_address = (TextView) rowView.findViewById(R.id.tv_address);
        ImageView iv_photo = (ImageView) rowView.findViewById(R.id.iv_photo);

        LocationRecord lr = locationRecords.get(position);
        tv_time.setText(lr.getRecordTime().toString());
        String photoPath = getPhoto(lr);
        if(photoPath != ""){
            iv_photo.setImageURI(Uri.parse(photoPath));
        }
//        tv_address.setText(getAddress(lr.getLongitude(),lr.getLatitude()));
        reverseGeocode(lr.getLongitude(),lr.getLatitude(), tv_address);
        return rowView;
    }

    private String getPhoto(LocationRecord lr){
        String imgsrc = "";
        FootageHelper footageHelper = FootageHelper.getInstance(context);
        List<Note> notes = footageHelper.getAllNotes(lr.getId());
        for(Note note : notes){
            if(note.getPhotoPath()!="") {
                imgsrc = note.getPhotoPath();
                break;
            }
        }
        return imgsrc;
    }

    private String getAddress(double longitude, double latitude){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses != null){
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAdderss = new StringBuilder();
                for(int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++){
                    strReturnedAdderss.append(returnedAddress.getAddressLine(i)).append("");
                }
                return strReturnedAdderss.toString();
            } else {
                return "No address";
            }
        }catch(IOException ex){
            return "Cannot get Address!";
        }
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
