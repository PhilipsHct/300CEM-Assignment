package com.example.assignment_footage;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment_footage.adapter.footageAdapter;
import com.example.assignment_footage.models.Footage;
import com.example.assignment_footage.database.FootageHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView body_footageList;
    TextView footer_count;
    private ArrayAdapter listAdapter;
    List<Footage> footages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        body_footageList = (ListView) findViewById(R.id.body_footageList);
        footer_count = (TextView) findViewById(R.id.footer_count);

        body_footageList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int footageId = footages.get(position).getId();
                Intent intent = new Intent(getApplicationContext(), FootageShow.class);
                intent.putExtra("FootageID", String.valueOf(footageId));
                startActivity(intent);
            }
        });

        refreshFootageList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_new:
                    Intent intent = new Intent(this, FootageNew.class);
                    startActivity(intent);
                break;
            case R.id.menu_about:
                break;
            case R.id.menu_quit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFootageList();
    }

    public void refreshFootageList(){
        FootageHelper footageHelper = FootageHelper.getInstance(this);
        footages = footageHelper.getAllFootages();
        int footageCount = footageHelper.getFootagesCount();
        listAdapter = new footageAdapter(this, footages);
        body_footageList.setAdapter(listAdapter);
        footer_count.setText(String.valueOf(footageCount));
    }
}
