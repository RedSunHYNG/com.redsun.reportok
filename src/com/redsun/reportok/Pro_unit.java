package com.redsun.reportok;

import java.util.ArrayList;

public class Pro_unit {

    private String name_ ="";

    private ArrayList<String> Pass_ = new ArrayList<>();

    private ArrayList<String> NoPass_ = new ArrayList<>();

    private ArrayList<String> Module_ = new ArrayList<>();

    private Integer type_ = 0;

    private String DateTime = "";

    private String finger_ ="";

    private String bulidTool_ = "";

    private String url_ = "";

    public Pro_unit(){

    }

    public Pro_unit(String name_, Integer type_ , String url_){
        this.name_ = name_;
        this.type_ = type_;
        this.url_ = url_;
    }

    public void ReviseName(String name_){
        this.name_ = name_;
    }

    public void RevisePass(ArrayList<String> Pass_){
        this.Pass_ = Pass_;
    }

    public void ReviseNoPass(ArrayList<String> NoPass_){
        this.NoPass_ = NoPass_;
    }

    public void ReviseModule(ArrayList<String> Module_){
        this.Module_ = Module_;
    }

    public void ReviseType(Integer type_){
        this.type_ = type_;
    }

    public void ReviseDateTime(String DateTime){
        this.DateTime = DateTime;
    }

    public void ReviseFinger(String finger_){
        this.finger_ = finger_;
    }

    public void ReviseBulidTool(String bulidTool_){
        this.bulidTool_ = bulidTool_;
    }

    public void ReviseUrl(String url_){
        this.url_ = url_;
    }

    public String getName(){
        return name_;
    }

    public ArrayList<String> getPass(){
        return Pass_;
    }

    public ArrayList<String> getNoPass(){
        return NoPass_;
    }

    public ArrayList<String> getModule(){
        return Module_;
    }

    public Integer getType(){
        return type_;
    }

    public String getDateTime(){
        return DateTime;
    }

    public String getFinger(){
        return finger_;
    }

    public String getBulidTool(){
        return bulidTool_;
    }

    public String getUrl(){
        return url_;
    }
}
