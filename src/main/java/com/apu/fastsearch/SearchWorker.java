/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author apu
 */
public class SearchWorker implements Runnable {
    
    private String path;
    private String strToSearch;
    private BlockingQueue<FileObject> resultQueue;
    private ExecutorService exService;
    private SearchWorkersCounter swc;
    private ConcurrentCounter scannedFilesCounter;

    public SearchWorker(String path, String strToSearch,  
                BlockingQueue<FileObject> resultQueue, ExecutorService exService, 
                SearchWorkersCounter swc, ConcurrentCounter counter) {
        this.path = path;
        this.strToSearch = strToSearch;
        this.resultQueue = resultQueue;
        this.exService = exService;
        this.swc = swc;
        this.scannedFilesCounter = counter;
    }

    @Override
    public void run() {
        try {
            swc.amountInc();
            scannedFilesCounter.intValue();
            findName();
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchWorker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            swc.amountDec();
        }         
    }
    
    private void findName() throws InterruptedException {
        File file = new File(path);
        FileObject fo = new FileObject(file);        
        if(file.exists()) {
            //compare file or directory name
            if(file.getName().toUpperCase().contains(strToSearch.toUpperCase())) {
                resultQueue.put(fo);
            }
            if(file.isFile()) {                
                return;
            }
            //search start
            File[] files = file.listFiles();
            SearchWorker sw;
            for(File fl : files) {
                sw = new SearchWorker(fl.getPath(), strToSearch, resultQueue, 
                                        exService, swc, scannedFilesCounter);
                exService.submit(sw);
            }
        }        
    }
    
}
