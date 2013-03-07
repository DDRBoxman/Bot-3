package com.recursivepenguin.botcubed.service;

import com.recursivepenguin.botcubed.Printer;

import java.util.ArrayList;

public interface PrinterConnectionProxy {

    /*
    Send a print command, that does not belong to a print job.
     */
    public abstract void injectManualCommand(String command);

    public abstract Printer getPrinter();

    public abstract void setGcode(ArrayList<String> gcode);

    public abstract void startPrint();

    public abstract void pausePrint();
}
