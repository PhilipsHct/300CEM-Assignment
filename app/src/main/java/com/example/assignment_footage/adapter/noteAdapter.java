package com.example.assignment_footage.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_footage.R;
import com.example.assignment_footage.models.Note;

import java.util.List;

public class noteAdapter extends ArrayAdapter<Note> {
    private final Context context;
    private final List<Note> notes;

    public noteAdapter(Context context,  List<Note> notes) {
        super(context, -1, notes);
        this.context = context;
        this.notes = notes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_note, parent, false);
        ImageView iv_ln_photo = (ImageView) rowView.findViewById(R.id.iv_ln_photo);
        TextView tv_ln_comment = (TextView) rowView.findViewById(R.id.tv_ln_comment);

        Note note = notes.get(position);
        tv_ln_comment.setText(note.getContent());
        String photoPath = getPhoto(note);
        if(photoPath != ""){
            iv_ln_photo.setImageURI(Uri.parse(photoPath));
        }
        return rowView;
    }

    private String getPhoto(Note note){
        String imgsrc = note.getPhotoPath();
        return imgsrc;
    }
}
