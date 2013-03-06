package com.recursivepenguin.botcubed;

import java.util.List;

public class Printer {

    double extruderTemp;
    double bedTemp;
    double xPos;
    double yPos;
    double zPos;
    double extruderPos;

    List<String> sdCardFiles;

    public double getBedTemp() {
        return bedTemp;
    }

    public void setBedTemp(double bedTemp) {
        this.bedTemp = bedTemp;
    }

    public double getExtruderTemp() {
        return extruderTemp;
    }

    public void setExtruderTemp(double extruderTemp) {
        this.extruderTemp = extruderTemp;
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public double getzPos() {
        return zPos;
    }

    public void setzPos(double zPos) {
        this.zPos = zPos;
    }

    public double getExtruderPos() {
        return extruderPos;
    }

    public void setExtruderPos(double extruderPos) {
        this.extruderPos = extruderPos;
    }

    public void setSdCardFiles() {

    }

    public List<String> getSdCardFiles() {
         return sdCardFiles;
    }
}
