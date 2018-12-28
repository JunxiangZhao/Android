package com.example.smarthealth;


import java.io.Serializable;

public class Food implements Serializable {
    private String name;
    private String content;
    private String type;
    private String nutrition;
    private String color;
    private Boolean isCollected;

    public Food(String _name, String _content,String _type, String _nutrition, String _color){
        name = _name;
        content = _content;
        type = _type;
        nutrition = _nutrition;
        color = _color;
        isCollected = false;
    }

    public String getName(){
        return  name;
    }

    public String getContent()
    {
        return content;
    }

    public String getType(){
        return type;
    }

    public String getNutrition(){
        return  nutrition;
    }

    public String getColor(){
        return color;
    }

    public Boolean getIsCollected(){
        return isCollected;
    }

    public void setName(String _name){
        name = _name;
    }

    public void setType(String _type){
        type = _type;
    }

    public void setContent(String _content){
        content = _content;
    }

    public void setNutrition(String _nutrition){
        nutrition = _nutrition;
    }

    public void setColor(String _color){
        color = _color;
    }

    public void setIsCollected(Boolean _isCollected){
        isCollected = _isCollected;
    }

}
