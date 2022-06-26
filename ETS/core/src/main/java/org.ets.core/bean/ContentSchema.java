package org.ets.core.bean;


public class ContentSchema implements Comparable<ContentSchema> {

    private String title;
    private String description;
    private String image;
    private String path;
    private String date;
    private String name;
    private String alt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public int compareTo(ContentSchema o) {
        return this.getTitle().compareTo(o.getTitle());
    }
}