package com.nakul.reportassignment.helper;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by nakul on 9/16/2017.
 */


public class JsonHelper {

    private static final String LOG_TAG = JsonHelper.class.getSimpleName();
    private static final String FILE_PATH = "jsonInput.json";

    public static final String FIELD_KEY = "field-name";
    public static final String TYPE_KEY = "type";
    public static final String REQUIRED_KEY = "required";
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    public static final String OPTIONS_KEY = "options";



    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        Map<String, Object> mapOrdered  = new TreeMap(Collections.reverseOrder());
        mapOrdered.putAll(map);
        return mapOrdered;
    }



    public static String getUpperCaseString(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    public static JSONArray getJsonData(Context context) {
        JSONArray array = new JSONArray();
        try {
            InputStream iStream = context.getAssets().open(FILE_PATH);

            int size = iStream.available();

            byte[] buffer = new byte[size];

            iStream.read(buffer);

            iStream.close();
            array = new JSONArray(new String(buffer, "UTF-8"));


        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return array;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
}
