/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

/**
 *
 * @author apu
 */
public class SearchWorkersCounter {
    private int amountOfThreads = 0;

    public synchronized int getAmount() {
        return amountOfThreads;
    }

    public synchronized void amountInc() {
        amountOfThreads++;
    }
    
    public synchronized void amountDec() {
        if(amountOfThreads != 0)
                amountOfThreads--;
    }
    
}
