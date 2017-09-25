package com.nakul.reportassignment.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nakul.reportassignment.R;
import com.nakul.reportassignment.adapter.ReportRecyclerAdapter;
import com.nakul.reportassignment.helper.JsonHelper;
import com.nakul.reportassignment.model.ReportDataModel;
import com.nakul.reportassignment.model.ReportListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ReportRecyclerAdapter adapter;
    private List<Map<String, Object>> reportListMap;
    private List<ReportListModel> reportDataModelsList;
    private List<String> visibleFields;
    private String position;

    public static final int REQUEST_CODE = 101;
    public static final int EDIT_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reportListMap = new ArrayList<>();
        visibleFields = new ArrayList<>();
        reportDataModelsList = new ArrayList<>();
        RecyclerView reportRecyclerView = (RecyclerView) findViewById(R.id.reportListRecyclerView);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportRecyclerAdapter(this, reportListMap, visibleFields);
        reportRecyclerView.setAdapter(adapter);
    }


    public void loadReportActivity(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void loadReportEditor(JSONObject jsonObject, int position) {
        Intent editIntent = new Intent(this, ReportActivity.class);
        editIntent.putExtra("data", (Serializable) reportDataModelsList.get(position));
        editIntent.putExtra("valuesJson", jsonObject.toString());
        editIntent.putExtra("isEdit", true);
        this.position = String.valueOf(position);
        startActivityForResult(editIntent, EDIT_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    reportListMap.add(JsonHelper.toMap(new JSONObject(data.getStringExtra(ReportActivity.INTENT_DATA_KEY))));
                    reportDataModelsList.add((ReportListModel) data.getSerializableExtra(ReportActivity.EDIT_KEY));
                    visibleFields.add(data.getStringExtra(ReportActivity.INTENT_DATA_VISIBLE_KEY));
                    adapter.notifyDataSetChanged();
                    TextView counter = (TextView) findViewById(R.id.reportCountTxt);
                    counter.setText(String.valueOf(adapter.getItemCount()));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            } else if (requestCode == EDIT_REQUEST_CODE) {
                try {
                    reportListMap.remove(Integer.parseInt(position));
                    reportListMap.add(Integer.parseInt(position),JsonHelper.toMap(new JSONObject(data.getStringExtra(ReportActivity.INTENT_DATA_KEY))));
                    reportDataModelsList.remove(Integer.parseInt(position));
                    reportDataModelsList.add(Integer.parseInt(position),(ReportListModel) data.getSerializableExtra(ReportActivity.EDIT_KEY));
                    adapter.notifyDataSetChanged();
                    TextView counter = (TextView) findViewById(R.id.reportCountTxt);
                    counter.setText(String.valueOf(adapter.getItemCount()));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }
    }
}
