package com.breathetaichi.classtracker;

public class Assessment {

    private long id;
    private long courseID;
    private String title;
    private String type;
    private String dueDate;
    private String dueTime;

    public Assessment(long id, long courseID, String title, String type, String dueDate, String dueTime) {
        this.id = id;
        this.courseID = courseID;
        this.title = title;
        this.type = type;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
    }

    public Assessment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}
