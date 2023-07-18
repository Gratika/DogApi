package com.gatikahome.dogimage.models;

import java.util.List;

public class Breed {
    private String name;
    public  Breed(String name){
        this.name = name;
    }
    public  void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public String toString(){
        return this.name;
    }


}
