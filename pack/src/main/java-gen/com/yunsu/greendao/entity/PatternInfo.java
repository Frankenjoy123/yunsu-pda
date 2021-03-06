package com.yunsu.greendao.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PATTERN_INFO".
 */
public class PatternInfo {

    private Long id;
    private String name;
    private String regex;
    private String example;

    public PatternInfo() {
    }

    public PatternInfo(Long id) {
        this.id = id;
    }

    public PatternInfo(Long id, String name, String regex, String example) {
        this.id = id;
        this.name = name;
        this.regex = regex;
        this.example = example;
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

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

}
