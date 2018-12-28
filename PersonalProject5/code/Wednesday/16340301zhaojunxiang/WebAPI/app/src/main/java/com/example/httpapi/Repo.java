package com.example.httpapi;

import android.text.BoringLayout;

public class Repo {
    String id;
    String name;
    String description;
    Boolean has_issues;
    Integer open_issues;

    public Repo(String _id, String _name, String _description, Boolean _has_issues, Integer _open_issues){
        id = _id;
        name = _name;
        description = _description;
        has_issues = _has_issues;
        open_issues = _open_issues;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHas_issues() {
        return has_issues;
    }

    public Integer getOpen_issues() {
        return open_issues;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHas_issues(Boolean has_issues) {
        this.has_issues = has_issues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpen_issues(Integer open_issues) {
        this.open_issues = open_issues;
    }

}
