package com.recursivepenguin.botcubed;

public class Printer {

    double extruderTemp;
    double bedTemp;

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
}
