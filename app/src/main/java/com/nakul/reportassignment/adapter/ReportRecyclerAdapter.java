package com.nakul.reportassignment.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nakul.reportassignment.R;
import com.nakul.reportassignment.activity.MainActivity;
import com.nakul.reportassignment.helper.JsonHelper;
import com.nakul.reportassignment.model.ReportDataModel;
import com.nakul.reportassignment.model.ReportListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nakul on 9/16/2017.
 */

public class ReportRecyclerAdapter extends RecyclerView.Adapter<ReportRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> reportsMap;
    private List<String> visibleFields;

    public ReportRecyclerAdapter(Context context, List<Map<String, Object>> reportsMap, List<String> visibleFields) {
        this.context = context;
        this.reportsMap = reportsMap;
        this.visibleFields = visibleFields;
    }

    @Override
    public ReportRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_report_list_row, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ReportRecyclerAdapter.ViewHolder holder, final int position) {
        final Map<String, Object> mapItem = reportsMap.get(position);
        String[] visible = visibleFields.get(position).split(",");
        Set<String> set = mapItem.keySet();
        final JSONObject jsonValues = new JSONObject();
        try {
            for (String string : set) {
                jsonValues.put(string, String.valueOf(mapItem.get(string)));

                if (visible[0].contains(string.toLowerCase())) {
                    holder.labelFirstTxt.setText(JsonHelper.getUpperCaseString(String.valueOf(string + ": ")));
                    holder.valueFirstTxt.setText(String.valueOf(mapItem.get(string)));
                }

                if (visible[1].contains(string.toLowerCase())) {

                    holder.labelSecondTxt.setText(JsonHelper.getUpperCaseString(String.valueOf(string + ": ")));
                    holder.valueSecondTxt.setText(String.valueOf(mapItem.get(string)));
                }

                holder.containerCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).loadReportEditor(jsonValues, position);
                    }
                });


            }
        } catch (JSONException e) {
            Log.e("Json Exception", e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return reportsMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView labelFirstTxt, labelSecondTxt, valueFirstTxt, valueSecondTxt;
        CardView containerCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            labelFirstTxt = (TextView) itemView.findViewById(R.id.labelFirstTxt);
            labelSecondTxt = (TextView) itemView.findViewById(R.id.labelSecondTxt);
            valueFirstTxt = (TextView) itemView.findViewById(R.id.valueFirstTxt);
            valueSecondTxt = (TextView) itemView.findViewById(R.id.valueSecondTxt);
            containerCardView = (CardView) itemView.findViewById(R.id.containerCardView);

        }
    }
}
