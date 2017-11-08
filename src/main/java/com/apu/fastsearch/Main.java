/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author apu
 */
public class Main {
    
    private static final int AMOUNT_OF_THREADS = 10;
    private static final int QUEUE_SIZE = 50;
    
    public static void main(String[] args) throws InterruptedException {
        String path = "D:\\";
        String strToSearch = "AMEC";
        
        SearchWorkersCounter swc = new SearchWorkersCounter();
        BlockingQueue<FileObject> queue = new ArrayBlockingQueue(QUEUE_SIZE, true);
        ExecutorService exService = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        ConcurrentCounter scannedFilesCounter = new ConcurrentCounter();
        SearchWorker sw = new SearchWorker(path, strToSearch, queue, exService, swc, scannedFilesCounter);
        exService.submit(sw);
        
        Thread.sleep(100);
        
        while (true) { 
            if(!queue.isEmpty()) {
                System.out.println(queue.take().toString());            
            }
            if(swc.getAmount() == 0)    break;
        }
        System.out.println(swc.getAmount());
        System.out.println(scannedFilesCounter.getValue());
        exService.shutdown();
    }
    
}
