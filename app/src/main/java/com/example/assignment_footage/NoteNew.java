 package com.example.assignment_footage;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.assignment_footage.database.FootageHelper;
import com.example.assignment_footage.models.Footage;
import com.example.assignment_footage.models.LocationRecord;
import com.example.assignment_footage.models.Note;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.media.MediaRecorder.VideoSource.CAMERA;

 public class NoteNew extends AppCompatActivity {
    private final static int photoRequestionCode = 1000;
    NoteNew noteNew;
    Button btn_photo;
    Button btn_save;
    Button btn_cancel;
    private final static int GALLERY_CODE = 10;

    ImageView iv;
    EditText et_content;

    LocationRecord locationRecord;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);

        noteNew = this;
        int locationRecordId;
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                locationRecordId = 1;
            }else {
                locationRecordId = Integer.parseInt(extras.getString("LocationRecordID","1"));
            }
        } else {
            locationRecordId = Integer.parseInt((String) savedInstanceState.getSerializable("LocationRecordId"));
        }

        final FootageHelper footageHelper = FootageHelper.getInstance(this);
        locationRecord = footageHelper.getLocationRecord(locationRecordId);
        note = new Note(locationRecord.getId(), "","");
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        iv = (ImageView) findViewById(R.id.iv);
        et_content = (EditText) findViewById(R.id.et_content);

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(noteNew, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(noteNew, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(noteNew, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    //ACCESS FAILED
                    ActivityCompat.requestPermissions(noteNew, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA},
                            photoRequestionCode);

                } else {
                    showPictureDialog();
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setContent(et_content.getText().toString());
                int row_Id = footageHelper.addNote(note);
                note.setId(row_Id);
                Intent data = new Intent();
                data.setData(Uri.parse(String.valueOf(row_Id)));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    protected void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Choose Action");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera"
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                gallery();
                                break;
                            case 1:
                                camera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
     public void gallery() {
         Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

         startActivityForResult(galleryIntent, GALLERY_CODE);
     }

     private void camera() {
         Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
         startActivityForResult(intent, CAMERA);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {

         super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == this.RESULT_CANCELED) {
             return;
         }
         if (requestCode == GALLERY_CODE) {
             if (data != null) {
                 Uri contentURI = data.getData();
                 try {
                     Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                     String path = saveImage(bitmap);
                     Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                     iv.setImageBitmap(bitmap);
                     note.setPhotoPath(path);

                 } catch (IOException e) {
                     e.printStackTrace();
                     Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                 }
             }

         } else if (requestCode == CAMERA) {
             Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
             iv.setImageBitmap(thumbnail);
             String path = saveImage(thumbnail);
             note.setPhotoPath(path);
         }
     }
     public String saveImage(Bitmap myBitmap) {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
         File wallpaperDirectory = new File(
                 Environment.getExternalStorageDirectory() + "/Image");
         // have the object build the directory structure, if needed.
         if (!wallpaperDirectory.exists()) {
             wallpaperDirectory.mkdirs();
         }

         try {
             File f = new File(wallpaperDirectory, Calendar.getInstance()
                     .getTimeInMillis() + ".jpg");
             f.createNewFile();
             FileOutputStream fo = new FileOutputStream(f);
             fo.write(bytes.toByteArray());
             MediaScannerConnection.scanFile(this,
                     new String[]{f.getPath()},
                     new String[]{"image/jpeg"}, null);
             fo.close();

             return f.getAbsolutePath();
         } catch (IOException e1) {
             e1.printStackTrace();
         }
         return "";
     }
}
