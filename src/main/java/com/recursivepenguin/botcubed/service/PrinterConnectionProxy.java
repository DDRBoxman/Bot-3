package com.recursivepenguin.botcubed.service;

public interface PrinterConnectionProxy {

    /*
    Send a print command, that does not belong to a print job.
     */
    public abstract void injectManualCommand(String command);
}
