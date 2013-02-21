package com.recursivepenguin.botcubed.service;

import com.recursivepenguin.botcubed.Printer;

public interface PrinterConnectionProxy {

    /*
    Send a print command, that does not belong to a print job.
     */
    public abstract void injectManualCommand(String command);

    public abstract Printer getPrinter();
}
