package com.nakul.reportassignment.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nakul on 9/25/2017.
 */

public class ReportListModel implements Serializable {
   private List<ReportDataModel> reportDataModelList;

    public List<ReportDataModel> getReportDataModelList() {
        return reportDataModelList;
    }

    public void setReportDataModelList(List<ReportDataModel> reportDataModelList) {
        this.reportDataModelList = reportDataModelList;
    }
}
