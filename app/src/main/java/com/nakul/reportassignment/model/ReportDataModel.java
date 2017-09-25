package com.nakul.reportassignment.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nakul on 9/25/2017.
 */

public class ReportDataModel implements Serializable {

    private String fieldName, type, min, max;;
    private boolean required;
    private List<String> options;
    private List<ReportDataModel> composite;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<ReportDataModel> getComposite() {
        return composite;
    }

    public void setComposite(List<ReportDataModel> composite) {
        this.composite = composite;
    }
}
