
package com.ptjp.application.data.entity;

import JourneyPlanner.Route;

import javax.persistence.Entity;

/**
 * Acts as row in the grid which displays route information 
 */
@Entity
public class RouteItem extends AbstractEntity {
    private String startStation;
    private String endStation;
    private String startTime;
    private String endTime;
    private int train;

    public RouteItem(String startStation, String startTime, String endStation, String endTime, int train) {
        this.startStation = startStation;
        this.startTime = startTime;
        this.endStation = endStation;
        this.endTime = endTime;
        this.train = train;
    }

    public int getTrain() {
        return train;
    }

    public void setTrain(int train) {
        this.train = train;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
