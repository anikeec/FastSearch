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
public class ConcurrentCounter {
    private int value = 0;

    public synchronized int getValue() {
        return value;
    }

    public synchronized void intValue() {
        value++;
    }
    
    public synchronized void decValue() {
        if(value != 0)
                value--;
    }
}
