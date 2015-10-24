package com.aisleshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Activities extends Fragment {
    private SharedPreferences sp;
    // // TODO: update ACTIVITY_NAME with a com.Activities.MESSAGE variable
    public final static String ACTIVITY_NAME = "com.ShoppingList.MESSAGE";
    public final static String ACTIVITY_PREF = "ActivityPreferences";
    private ListView listView;
    private ArrayList<String> activities;
    private Set<String> activitySet;
    private ArrayAdapter<String> itemAdapter;
    private Context dashboard;
    private TextView emptyNotice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dashboard = getActivity();
        sp = dashboard.getSharedPreferences(ACTIVITY_PREF, Context.MODE_PRIVATE);
        listView = (ListView) getView().findViewById(R.id.activities);
        Set<String> defSet = new HashSet<>();
        activitySet = sp.getStringSet("ActivitySets", defSet);
        activities = new ArrayList<>(activitySet);
        emptyNotice = (TextView) getView().findViewById(R.id.empty_notice);

        if(activities.size() == 0){
            emptyNotice.setVisibility(View.VISIBLE);
        }

        itemAdapter = new ArrayAdapter<>(dashboard,R.layout.row_dashboard, activities);
        listView.setAdapter(itemAdapter);

        FloatingActionButton addButton = (FloatingActionButton) getView().findViewById(R.id.float_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addActivityDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Intent intent = new Intent(dashboard, CurrentActivity.class);
                String name = activities.get(pos);
                intent.putExtra(ACTIVITY_NAME, name);
                startActivity(intent);
            }
        });
    }

    // Popup for adding an Activity
    public void addActivityDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(dashboard);
        dialog.setContentView(R.layout.dialog_add_name);
        dialog.setTitle("Add a New Activity");

        final EditText activityName = (EditText) dialog.findViewById(R.id.Name);
        final Button cancel = (Button) dialog.findViewById(R.id.Cancel);
        final Button done = (Button) dialog.findViewById(R.id.Done);

        // Open keyboard automatically
        activityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activityName.getText().toString().isEmpty()) {
                    String name = activityName.getText().toString();

                    for(int index = 0; index < activities.size(); index++){
                        if(activities.get(index).equals(name)){
                            activityName.setError("Activity already exists...");
                            return;
                        }
                    }

                    dialog.dismiss();

                    Intent intent = new Intent(dashboard, CurrentActivity.class);

                    activitySet.add(name);
                    activities.add(name);
                    itemAdapter.notifyDataSetChanged();
                    updateStorage();
                    emptyNotice.setVisibility(View.INVISIBLE);

                    intent.putExtra(ACTIVITY_NAME, name);
                    startActivity(intent);
                }
                else{
                    activityName.setError("Name is empty...");
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        updateStorage();
    }

    @Override
    public void onStop(){
        super.onStop();
        updateStorage();
    }

    /*@Override
    public void onBackPressed(){
        super.onBackPressed();
        updateStorage();
    }*/

    @Override
    public void onPause(){
        super.onPause();
        updateStorage();
    }

    public void updateStorage(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("ActivitySets", activitySet);
        editor.commit();
        editor.apply();

        editor.remove("ActivitySets");
        editor.apply();
        editor.putStringSet("ActivitySets", activitySet);
        editor.apply();
    }
}
