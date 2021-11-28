package com.example.AnimalCheck;

public class Device {

    private String name;
    private String intensity;
    private int heartBeat;
    private double temperature;
    private int age;
    private double weight;
    private boolean connected = false;
    private boolean change = false;
    private boolean alarm = false;

    public Device(String name, String intensity) {
        this.name = name;
        this.intensity = intensity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getIntensity() {
        return intensity;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public boolean isConnected() {return connected;}

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }
}
