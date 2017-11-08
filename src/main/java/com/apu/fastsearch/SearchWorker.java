/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
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
    private String fileNameToSearch;
    private BlockingQueue<FileObject> resultQueue;
    private ExecutorService exService;
    private SearchWorkersCounter swc;
    private ConcurrentCounter scannedFilesCounter;

    public SearchWorker(String path, String fileNameToSearch, String strToSearch,   
                BlockingQueue<FileObject> resultQueue, ExecutorService exService, 
                SearchWorkersCounter swc, ConcurrentCounter counter) {
        this.path = path;
        this.strToSearch = strToSearch;
        this.fileNameToSearch = fileNameToSearch;
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
            find();
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SearchWorker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            swc.amountDec();
        }         
    }
    
    private void find() throws InterruptedException, UnsupportedEncodingException {
        File file = new File(path);
        FileObject fo = new FileObject(file);        
        if(file.exists()) {
            //compare file or directory name
            if(file.getName().toUpperCase().contains(fileNameToSearch.toUpperCase())) {                
                if((strToSearch.equals(""))&& (file.isFile())) { 
                    resultQueue.put(fo);
                    return;
                }               
                if(file.isFile()) { 
                    String str;                    
                    try {
//                        if(file.length() > 100000000) return;
                        FileReader fReader = new FileReader(file);
                        Scanner scanner = new Scanner(fReader);
                        while(scanner.hasNext()){
                            str = new String(scanner.nextLine().getBytes(), "Cp1251");//UTF-8
                            if(str.toUpperCase()
                                        .contains(strToSearch.toUpperCase())) {
                                String pathTemp = fo.getPath() + " - " + str;
                                fo.setPath(pathTemp);
                                resultQueue.put(fo);
                                return;
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        String pathTemp = fo.getPath() + " - Error open file!!!";
                        fo.setPath(pathTemp);
                        resultQueue.put(fo);
                    }
                    return;
                }
                resultQueue.put(fo);
            } 
            if(file.isFile()) { 
                return;
            }
            //search start
            File[] files = file.listFiles();
            SearchWorker sw;
            for(File fl : files) {
                sw = new SearchWorker(fl.getPath(), fileNameToSearch, strToSearch,  
                            resultQueue, exService, swc, scannedFilesCounter);
                exService.submit(sw);
            }
        }        
    }
    
}
