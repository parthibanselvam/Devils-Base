package org.ets.core.bean;

import java.util.ArrayList;
import java.util.List;

public class ResearcherSchema {

    private String id;
    private String title;
    private String url;
    private String publicationType;
    private List<String> authors;
    private String year;
    private List<String> source;
    private List<String> keywords;
    private String publicationAbstract;
    private String refURL;
    private String pdfURL;
    private List<String> reportNumber;
    private String patentNumber;
    private String numPages;
    private String patentFamily;
    private String _text_;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String get_text_() { return _text_; }
    public void set_text_(String _text_) { this._text_ = _text_; }

    public String getNumPages() { return numPages; }
    public void setNumPages(String numPages) { this.numPages = numPages; }

    public String getPublicationType() { return publicationType; }
    public void setPublicationType(String publicationType) { this.publicationType = publicationType; }

    public List<String> getReportNumber() { return new ArrayList<>(reportNumber); }
    public void setReportNumber(List<String> reportNumber) { this.reportNumber = new ArrayList<>(reportNumber); }

    public List<String> getSource() { return new ArrayList<>(source); }
    public void setSource(List<String> source) { this.source = new ArrayList<>(source); }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }

    public String getPublicationAbstract() { return publicationAbstract; }
    public void setPublicationAbstract(String publicationAbstract) { this.publicationAbstract = publicationAbstract; }

    public String getRefURL() { return refURL; }
    public void setRefURL(String refURL) { this.refURL = refURL; }

    public String getPdfURL() { return pdfURL; }
    public void setPdfURL(String pdfURL) { this.pdfURL = pdfURL; }

    public String getPatentNumber() { return patentNumber; }
    public void setPatentNumber(String patentNumber) { this.patentNumber = patentNumber; }

    public String getPatentFamily() { return patentFamily; }
    public void setPatentFamily(String patentFamily) { this.patentFamily = patentFamily; }

    public List<String> getAuthors() { return new ArrayList<>(authors); }
    public void setAuthors(List<String> authors) { this.authors = new ArrayList<>(authors); }

    public List<String> getKeywords() {
        return new ArrayList<>(keywords);
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = new ArrayList<>(keywords);
    }

}