package com.example.assignment_footage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.LocationRecord;
import com.example.assignment_footage.models.Note;

import org.w3c.dom.Text;

public class LocationRecordNew extends AppCompatActivity {
    private static final int NOTE_REQUEST = 10;

    LocationRecordNew locationRecordNew;
    LocationRecord locationRecord;
    TextView tv_RecordTime;
    TextView tv_Longitude;
    TextView tv_Latitude;

    LinearLayout ll_Note;
    LinearLayout ll_NoteNew;

    ImageButton ib_addNote;
    ImageView iv_NotePhoto;
    TextView tv_NoteContent;
    Button btn_back;

    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_record_new);

        locationRecordNew = this;
        tv_RecordTime = (TextView) findViewById(R.id.tv_RecordTime);
        tv_Longitude = (TextView) findViewById(R.id.tv_Longitude);
        tv_Latitude = (TextView) findViewById(R.id.tv_Latitude);
        ll_Note = (LinearLayout) findViewById(R.id.ll_Note);
        ll_NoteNew = (LinearLayout) findViewById(R.id.ll_NoteNew);
        iv_NotePhoto = (ImageView) findViewById(R.id.iv_NotePhoto);
        tv_NoteContent = (TextView) findViewById(R.id.tv_NoteComment);
        btn_back = (Button) findViewById(R.id.btn_back);
        ib_addNote = (ImageButton) findViewById(R.id.ib_addNote);

        FootageHelper footageHelper = FootageHelper.getInstance(this);
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
        locationRecord = footageHelper.getLocationRecord(locationRecordId);
        tv_RecordTime.setText(locationRecord.getRecordTime().toString());
        tv_Longitude.setText(String.format("%.4f",locationRecord.getLongitude()));
        tv_Latitude.setText(String.format("%.4f",locationRecord.getLatitude()));

        ib_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(locationRecordNew, NoteNew.class);
                intent.putExtra("LocationRecordID", String.valueOf(locationRecord.getId()));
                startActivityForResult(intent, NOTE_REQUEST);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == NOTE_REQUEST){
            if(resultCode == RESULT_OK){
                String returnedResult = data.getData().toString();
                FootageHelper footageHelper = FootageHelper.getInstance(this);
                note = footageHelper.getNote(Integer.parseInt(returnedResult));

                iv_NotePhoto.setImageURI(Uri.parse(note.getPhotoPath()));
                tv_NoteContent.setText(note.getContent());
                ll_NoteNew.setVisibility(View.INVISIBLE);
                ll_Note.setVisibility(View.VISIBLE);
            } else if(resultCode == RESULT_CANCELED){

            }
        }
    }
}
