package com.example.assignment_footage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.assignment_footage.R;
import com.example.assignment_footage.models.LocationRecord;

import java.util.List;

public class locationRecordAdapter extends ArrayAdapter<LocationRecord> {
    private final Context context;
    private final List<LocationRecord> locationRecords;

    public locationRecordAdapter(Context context,  List<LocationRecord> locationRecords) {
        super(context, -1, locationRecords);
        this.context = context;
        this.locationRecords = locationRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_location_record, parent, false);
        TextView tv_recordTime = (TextView) rowView.findViewById(R.id.tv_RecordTime);
        TextView tv_Longitude = (TextView) rowView.findViewById(R.id.tv_Longitude);
        TextView tv_Latitude = (TextView) rowView.findViewById(R.id.tv_Latitude);

        LocationRecord lr = locationRecords.get(position);
        tv_recordTime.setText(lr.getRecordTime().toString());
        tv_Longitude.setText(String.valueOf(lr.getLongitude()));
        tv_Latitude.setText(String.valueOf(lr.getLatitude()));

        return rowView;
    }
}
