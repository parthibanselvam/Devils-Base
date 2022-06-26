package org.ets.core.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ETSAuthorSchema {
    private Map<String,String> primaryFields;
    private Map<String,List<String>> multiValueFields;
    private List<String> authorList;
    private List<String> subjectList;
    private List<String> reportNumList;
    public Map<String, String> getPrimaryFields() {
        return primaryFields;
    }
    public List<String> getAuthorList() {
        return new ArrayList<>(authorList);
    }
    public List<String> getSubjectList() {
        return new ArrayList<>(subjectList);
    }
    public void setPrimaryFields(Map<String, String> primaryFields) {
        this.primaryFields = primaryFields;
    }
    public void setAuthorList(List<String> authorList) {
        this.authorList = new ArrayList<>(authorList);
    }
    public void setSubjectList(List<String> subjectList) {
        this.subjectList = new ArrayList<>(subjectList);
    }
    public List<String> getReportNumList() {
        return new ArrayList<>(reportNumList);
    }
    public void setReportNumList(List<String> reportNumList) {
        this.reportNumList = new ArrayList<>(reportNumList);
    }
    public Map<String, List<String>> getMultiValueFields() {
        return multiValueFields;
    }
    public void setMultiValueFields(Map<String, List<String>> multiValueFields) {
        this.multiValueFields = multiValueFields;
    }
}