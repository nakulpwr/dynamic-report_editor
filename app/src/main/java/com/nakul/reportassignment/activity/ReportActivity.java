package com.nakul.reportassignment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nakul.reportassignment.helper.JsonHelper;
import com.nakul.reportassignment.R;
import com.nakul.reportassignment.model.ReportDataModel;
import com.nakul.reportassignment.model.ReportListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private static final String LOG_TAG = ReportActivity.class.getSimpleName();

    private static final String TYPE_DROPDOWN = "dropdown";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_MULTILINE = "multiline";
    private static final String TYPE_COMPOSITE = "composite";

    public static final String INTENT_DATA_KEY = "jsonResult";
    public static final String EDIT_KEY = "editedData";
    public static final String INTENT_DATA_VISIBLE_KEY = "firstShownFields";
    public String firstShownFields = "";
    public static final int COMPOSITE_REQUEST = 201;

    private LinearLayout parentLayout;
    private HashMap<Integer, ReportDataModel> validationDataMap;
    private List<ReportDataModel> reportDataModelsList;
    private Map<String, Object> keyValues;
    private boolean isEdit = false;
    private boolean isComposite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        validationDataMap = new HashMap<>();
        reportDataModelsList = new ArrayList<>();
        keyValues = new HashMap<>();
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        Button button = (Button) findViewById(R.id.doneBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataGeneric();
            }
        });
        Intent dataIntent = getIntent();
        if (!dataIntent.getBooleanExtra("isEdit", false) && !dataIntent.getBooleanExtra("isComposite", false))
            manifestGenericUI(JsonHelper.getJsonData(this));
        else if (dataIntent.getBooleanExtra("isComposite", false)) {
            isComposite = true;
            try {
                keyValues = JsonHelper.toMap(new JSONObject(dataIntent.getStringExtra("valuesJson")));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            ReportListModel reportListModel = (ReportListModel) dataIntent.getSerializableExtra("data");
            showEditScreen(reportListModel);
        } else {
            ReportListModel reportListModel = (ReportListModel) dataIntent.getSerializableExtra("data");
            try {
                keyValues = JsonHelper.toMap(new JSONObject(dataIntent.getStringExtra("valuesJson")));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            isEdit = true;
            showEditScreen(reportListModel);
        }

    }


    private void showEditScreen(ReportListModel reportListModel) {
        int counter = 0;
        for (ReportDataModel reportDataModel : reportListModel.getReportDataModelList()) {
            insertTextViewLabel(reportDataModel.getFieldName());
            if (reportDataModel.getType().equalsIgnoreCase(TYPE_DROPDOWN)) {
                genericDropDown(reportDataModel, counter);
            } else if (reportDataModel.getType().equalsIgnoreCase(TYPE_COMPOSITE)) {
                addComposite(reportDataModel, counter);
            } else {
                genericEditText(reportDataModel, counter);
            }
            counter++;
        }

    }

    private void genericDropDown(ReportDataModel reportDataModel, int counter) {

        Spinner spinner = new Spinner(this);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reportDataModel.getOptions());
        spinner.setAdapter(adapter);
        spinner.setTag(reportDataModel.getFieldName());
        spinner.setId(counter);
        String searchValue = "";
        if (isEdit) {

            if (keyValues.containsKey(JsonHelper.getUpperCaseString(reportDataModel.getFieldName())))
                searchValue = (String) keyValues.get(JsonHelper.getUpperCaseString(reportDataModel.getFieldName()));
            else
                searchValue = (String) keyValues.get(reportDataModel.getFieldName());

            int spinnerPosition = adapter.getPosition(searchValue);
            spinner.setSelection(spinnerPosition);

        }
        reportDataModelsList.add(reportDataModel);
        parentLayout.addView(spinner);

    }

    private void genericEditText(ReportDataModel reportDataModel, int counter) {
        EditText editText = new EditText(this);
        editText.setBackgroundResource(android.R.drawable.editbox_background);
        if (reportDataModel.getMin() != null || reportDataModel.getMax() != null || reportDataModel.isRequired())
            validationDataMap.put(counter, reportDataModel);
        try {
            if (reportDataModel.getType().equalsIgnoreCase(TYPE_NUMBER)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            } else if (reportDataModel.getType().equalsIgnoreCase(TYPE_MULTILINE)) {
                editText.setSingleLine(false);
            }
            editText.setTag(reportDataModel.getFieldName());
            editText.setId(counter);
            if (isEdit) {
                if (keyValues.containsKey(JsonHelper.getUpperCaseString(reportDataModel.getFieldName())))
                    editText.setText((String) keyValues.get(JsonHelper.getUpperCaseString(reportDataModel.getFieldName())));
                else
                    editText.setText((String) keyValues.get(reportDataModel.getFieldName()));
            }


        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        reportDataModelsList.add(reportDataModel);
        parentLayout.addView(editText);
    }

    private void manifestGenericUI(JSONArray inputJson) {
        try {
            for (int i = 0; i < inputJson.length(); i++) {
                ReportDataModel reportdataModel = new ReportDataModel();
                JSONObject object = inputJson.getJSONObject(i);
                String objectString = object.toString();
                String fieldName = object.getString(JsonHelper.FIELD_KEY);
                reportdataModel.setFieldName(fieldName);
                if (objectString.contains(JsonHelper.TYPE_KEY)) {
                    reportdataModel.setType(object.getString(JsonHelper.TYPE_KEY));
                    if (reportdataModel.getType().equalsIgnoreCase(TYPE_COMPOSITE)) {
                        insertTextViewLabel(object.getString(JsonHelper.FIELD_KEY));
                        JSONArray compositeArray = object.getJSONArray("value");
                        reportdataModel.setComposite(manifestComposite(compositeArray));
                        addComposite(reportdataModel, i);
                    }
                }
                if (objectString.contains(JsonHelper.MAX_KEY)) {
                    reportdataModel.setMax(String.valueOf(object.getInt(JsonHelper.MAX_KEY)));
                }
                if (objectString.contains(JsonHelper.MIN_KEY)) {
                    reportdataModel.setMin(String.valueOf(object.getInt(JsonHelper.MIN_KEY)));
                }
                if (objectString.contains(JsonHelper.REQUIRED_KEY)) {
                    reportdataModel.setRequired(object.getBoolean(JsonHelper.REQUIRED_KEY));
                }
                if (objectString.contains(JsonHelper.OPTIONS_KEY)) {
                    reportdataModel.setOptions(stringUppercase(object.getJSONArray(JsonHelper.OPTIONS_KEY)));
                }
                if (i < 2)
                    firstShownFields += fieldName + ",";
                insertTextViewLabel(object.getString(JsonHelper.FIELD_KEY));
                if (reportdataModel.getType().equalsIgnoreCase(TYPE_DROPDOWN)) {
                    genericDropDown(reportdataModel, i);
                } else {
                    genericEditText(reportdataModel, i);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void addComposite(final ReportDataModel reportdataModel, int id) {
        Map<String, Object> compositeKeyValue = new HashMap<>();
        JSONObject json = new JSONObject();

        if (!keyValues.isEmpty()) {
            try {
                json = new JSONObject(keyValues.get(reportdataModel.getFieldName()).toString());
                compositeKeyValue = JsonHelper.toMap(json);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        LinearLayout compositeLayout = new LinearLayout(this);
        compositeLayout.setId(id);
        compositeLayout.setTag(reportdataModel.getFieldName());
        compositeLayout.setLayoutParams(parentLayout.getLayoutParams());
        compositeLayout.setOrientation(LinearLayout.VERTICAL);
        for (ReportDataModel reportModel : reportdataModel.getComposite()) {
            TextView compositeTxtView = new TextView(this);
            compositeTxtView.setText(reportModel.getFieldName());
            if (compositeKeyValue.containsKey(JsonHelper.getUpperCaseString(reportModel.getFieldName())))
                compositeTxtView.setText((String) compositeKeyValue.get(JsonHelper.getUpperCaseString(reportModel.getFieldName())));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 50, 0, 10);
            compositeTxtView.setLayoutParams(layoutParams);
            compositeLayout.addView(compositeTxtView);
        }
        compositeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!keyValues.containsKey(reportdataModel.getFieldName())) {
                    ReportListModel listModel = new ReportListModel();
                    listModel.setReportDataModelList(reportdataModel.getComposite());
                    Intent compositeIntent = new Intent(ReportActivity.this, ReportActivity.class);
                    compositeIntent.putExtra("data", (Serializable) listModel);
                    compositeIntent.putExtra("isComposite", true);
                    compositeIntent.putExtra("valuesJson", new JSONObject().toString());
                    compositeIntent.putExtra("field", reportdataModel.getFieldName());
                    startActivityForResult(compositeIntent, COMPOSITE_REQUEST);
                } else {
                    ReportListModel listModel = new ReportListModel();
                    listModel.setReportDataModelList(reportdataModel.getComposite());
                    Intent compositeIntent = new Intent(ReportActivity.this, ReportActivity.class);
                    compositeIntent.putExtra("data", (Serializable) listModel);
//                    compositeIntent.putExtra("isComposite", true);
                    compositeIntent.putExtra("isEdit", true);
                    compositeIntent.putExtra("valuesJson", String.valueOf(keyValues.get(reportdataModel.getFieldName())));
                    compositeIntent.putExtra("field", reportdataModel.getFieldName());
                    startActivityForResult(compositeIntent, COMPOSITE_REQUEST);
                }
            }
        });
        reportDataModelsList.add(reportdataModel);
        parentLayout.addView(compositeLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == COMPOSITE_REQUEST) {
            keyValues.put(data.getStringExtra("field"), data.getStringExtra(INTENT_DATA_KEY));
            refreshCompositeValues(data);
        }
    }

    private void refreshCompositeValues(Intent data) {
        LinearLayout compositeLayout = (LinearLayout) parentLayout.findViewWithTag(data.getStringExtra("field"));
        try {
            JSONObject object = new JSONObject(keyValues.get(compositeLayout.getTag().toString()).toString());
            for (int i = 0; i < compositeLayout.getChildCount(); i++) {
                View childView = compositeLayout.getChildAt(i);
                if (childView instanceof TextView) {
                    TextView text = (TextView) childView;
                    if (!text.getText().toString().equalsIgnoreCase(data.getStringExtra("field")))
                        text.setText(object.getString(JsonHelper.getUpperCaseString(text.getText().toString())));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private List<ReportDataModel> manifestComposite(JSONArray inputJson) {
        List<ReportDataModel> dataModelList = new ArrayList<>();
        try {
            for (int i = 0; i < inputJson.length(); i++) {
                ReportDataModel reportdataModel = new ReportDataModel();
                JSONObject object = inputJson.getJSONObject(i);
                String objectString = object.toString();
                String fieldName = object.getString(JsonHelper.FIELD_KEY);
                reportdataModel.setFieldName(fieldName);
                if (objectString.contains(JsonHelper.TYPE_KEY)) {
                    reportdataModel.setType(object.getString(JsonHelper.TYPE_KEY));
                }
                if (objectString.contains(JsonHelper.MAX_KEY)) {
                    reportdataModel.setMax(String.valueOf(object.getInt(JsonHelper.MAX_KEY)));
                }
                if (objectString.contains(JsonHelper.MIN_KEY)) {
                    reportdataModel.setMin(String.valueOf(object.getInt(JsonHelper.MIN_KEY)));
                }
                if (objectString.contains(JsonHelper.REQUIRED_KEY)) {
                    reportdataModel.setRequired(object.getBoolean(JsonHelper.REQUIRED_KEY));
                }
                if (objectString.contains(JsonHelper.OPTIONS_KEY)) {
                    reportdataModel.setOptions(stringUppercase(object.getJSONArray(JsonHelper.OPTIONS_KEY)));
                }
                if (reportdataModel.getType().equalsIgnoreCase(TYPE_COMPOSITE)) {
                    JSONArray compositeArray = object.getJSONArray("value");
                    reportdataModel.setComposite(manifestComposite(compositeArray));
                }
                dataModelList.add(reportdataModel);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return dataModelList;
    }


    private List<String> stringUppercase(JSONArray options) {
        List<String> resultStringList = new ArrayList<>();
        try {
            resultStringList.addAll(JsonHelper.toList(options));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return resultStringList;
    }

    private void insertTextViewLabel(String label) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 50, 0, 10);
        TextView labelTxt = new TextView(this);
        String upperCaseString = label.substring(0, 1).toUpperCase() + label.substring(1);
        labelTxt.setText(upperCaseString);
        labelTxt.setLayoutParams(layoutParams);
        parentLayout.addView(labelTxt);
    }


    private void saveDataGeneric() {

        int count = parentLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = parentLayout.getChildAt(i);
            if (childView instanceof Spinner) {
                Spinner spinner = ((Spinner) childView);
                keyValues.put(JsonHelper.getUpperCaseString(spinner.getTag().toString()), spinner.getSelectedItem().toString());
            } else if (childView instanceof EditText) {

                EditText editText = (EditText) childView;
                int id = editText.getId();
                if (validationDataMap.containsKey(id)) {
                    if (genericIsValid(validationDataMap.get(id), editText))
                        insertValues(editText);
                    else {
                        keyValues = new HashMap<String, Object>();
                        return;
                    }
                } else {
                    insertValues(editText);
                }
            }
        }
        if (!isEdit && !isComposite)
            setResultToMainActivity();
        else if (isEdit)
            setResultForEdit();
        else
            setResultForSelf();
        Log.i(LOG_TAG, keyValues.toString());

    }

    private boolean genericIsValid(ReportDataModel data, EditText editText) {
        boolean isValid = true;
        String textValStr = editText.getText().toString();
        try {

            if (data.isRequired()) {
                if (TextUtils.isEmpty(editText.getText())) {
                    String error = getString(R.string.err_required_field);
                    editText.setError(error);
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (data.getMin() != null) {
                int textVal = TextUtils.isEmpty(textValStr) ? 0 : Integer.valueOf(textValStr);
                int minVal = Integer.parseInt(data.getMin());
                if (minVal > textVal) {
                    String error = getString(R.string.err_minimum_val) + " " + minVal;
                    editText.setError(error);
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            }

            if (data.getMax() != null) {
                int textVal = TextUtils.isEmpty(textValStr) ? 0 : Integer.valueOf(textValStr);
                int maxVal = Integer.parseInt(data.getMax());
                if (maxVal < textVal) {
                    String error = getString(R.string.err_maximum_val) + " " + maxVal;
                    editText.setError(error);
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            isValid = false;
        }
        return isValid;

    }

    private void insertValues(EditText editText) {

        if (editText.getInputType() == InputType.TYPE_CLASS_NUMBER)
            keyValues.put(JsonHelper.getUpperCaseString(editText.getTag().toString()), Integer.valueOf(editText.getText().toString()));
        else if (editText.getInputType() == InputType.TYPE_NUMBER_FLAG_DECIMAL)
            keyValues.put(JsonHelper.getUpperCaseString(editText.getTag().toString()), Float.parseFloat(editText.getText().toString()));
        else
            keyValues.put(JsonHelper.getUpperCaseString(editText.getTag().toString()), editText.getText().toString());


    }

    private void setResultToMainActivity() {
        ReportListModel listModel = new ReportListModel();
        listModel.setReportDataModelList(reportDataModelsList);
        Intent data = new Intent();
        data.putExtra(INTENT_DATA_KEY, new JSONObject(keyValues).toString());
        data.putExtra(EDIT_KEY, (Serializable) listModel);
        data.putExtra(INTENT_DATA_VISIBLE_KEY, firstShownFields);
        setResult(RESULT_OK, data);
        finish();
    }

    private void setResultForEdit() {
        ReportListModel listModel = new ReportListModel();
        listModel.setReportDataModelList(reportDataModelsList);
        Intent data = new Intent();
        data.putExtra(EDIT_KEY, (Serializable) listModel);
        data.putExtra(INTENT_DATA_KEY, new JSONObject(keyValues).toString());
        data.putExtra("field", getIntent().getStringExtra("field"));
        data.putExtra("data", (Serializable) listModel);
        setResult(RESULT_OK, data);
        finish();
    }

    private void setResultForSelf() {
        ReportListModel listModel = new ReportListModel();
        listModel.setReportDataModelList(reportDataModelsList);
        Intent data = new Intent();
        data.putExtra(INTENT_DATA_KEY, new JSONObject(keyValues).toString());
        data.putExtra("field", getIntent().getStringExtra("field"));
        data.putExtra("data", (Serializable) listModel);
        setResult(RESULT_OK, data);
        finish();
    }
}
