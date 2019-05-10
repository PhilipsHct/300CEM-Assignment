package com.example.assignment_footage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.assignment_footage.R;
import com.example.assignment_footage.models.Footage;

import java.util.List;

public class footageAdapter extends ArrayAdapter<Footage> {
    private final Context context;
    private final List<Footage> footages;

    public footageAdapter(Context context,  List<Footage> footages) {
        super(context, -1, footages);
        this.context = context;
        this.footages = footages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_footage, parent, false);
        TextView tv_footageName = (TextView) rowView.findViewById(R.id.tv_footageName);
        TextView tv_footageDate = (TextView) rowView.findViewById(R.id.tv_footageDate);

        Footage footage = footages.get(position);
        tv_footageName.setText(footage.getName());
        tv_footageDate.setText(footage.getFootageDate().toString());


        return rowView;
    }
}
