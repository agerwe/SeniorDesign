package com.example.lawyerapp;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FILES.
 */
public class Files {

    private Long id;
    private String name;
    private Long parentID;
    private String type;
    private String path;

    public Files() {
    }

    public Files(Long id) {
        this.id = id;
    }

    public Files(Long id, String name, Long parentID, String type, String path) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
        this.type = type;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentID() {
        return parentID;
    }

    public void setParentID(Long parentID) {
        this.parentID = parentID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
