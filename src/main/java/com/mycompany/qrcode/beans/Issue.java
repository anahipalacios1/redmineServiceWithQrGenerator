package com.mycompany.qrcode.beans;

import java.util.List;

public class Issue {
    private int id;
    private Project project;
    private Tracker tracker;
    private Status status;
    private Priority priority;
    private Author author;
    private String subject;
    private String description;
    private String start_date;
    private String due_date;
    private int done_ratio;
    private boolean is_private;
    private String estimated_hours;
    private List<CustomField> custom_fields;
    private String created_on;
    private String updated_on;
    private String closed_on;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }    
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getDone_ratio() {
        return done_ratio;
    }

    public void setDone_ratio(int done_ratio) {
        this.done_ratio = done_ratio;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }

    public String getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(String estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getClosed_on() {
        return closed_on;
    }

    public void setClosed_on(String closed_on) {
        this.closed_on = closed_on;
    }

    public List<CustomField> getCustom_fields() {
        return custom_fields;
    }

    public void setCustom_fields(List<CustomField> custom_fields) {
        this.custom_fields = custom_fields;
    }
        
    
}
