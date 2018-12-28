package com.example.smarthealth;

public class MessageEvent {
    Food food;
    MessageEvent(Food _food){
        food = _food;
    }
    public Food getFood(){
        return  food;
    }

}
