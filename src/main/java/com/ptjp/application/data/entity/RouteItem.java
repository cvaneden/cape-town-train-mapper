
package com.ptjp.application.data.entity;

import JourneyPlanner.Route;

import javax.persistence.Entity;

/**
 * Acts as row in the grid which displays route information 
 */
@Entity
public class RouteItem extends AbstractEntity {
    private String station;
    private String time;
    private int train;
    private boolean transfer;

    public RouteItem(String station, String time, int train, boolean transfer) {
        this.station = station;
        this.time = time;
        this.train = train;
        this.transfer = transfer;
    }

    public int getTrain() {
        return train;
    }

    public void setTrain(int train) {
        this.train = train;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public boolean getTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }
}
