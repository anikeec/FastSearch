/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author apu
 */
public class Main {
    
    private static final int AMOUNT_OF_THREADS = 50;
    private static final int QUEUE_SIZE = 50;
    
    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        System.out.print("Enter path for search (example: D:\\):  ");
        Scanner scannerInput = new Scanner(System.in);
        String path = scannerInput.nextLine();
        System.out.print("Enter filename or part for search:  ");
        String fileNameToSearch = scannerInput.nextLine();
        System.out.print("Enter string or substring for search in files (press enter if search only file names):  ");
        String strToSearch = scannerInput.nextLine();
        String resultFileName = "searchResults.txt"; 
        
        File file = new File(path);
        
        if(!file.exists())  {
            System.out.println("Error!!! Path for search unavailable.");
            return;
        }
        
        SearchWorkersCounter swc = new SearchWorkersCounter();
        BlockingQueue<FileObject> queue = new ArrayBlockingQueue(QUEUE_SIZE, true);
        ExecutorService exService = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        ConcurrentCounter scannedFilesCounter = new ConcurrentCounter();
        SearchWorker sw = new SearchWorker(path, fileNameToSearch, strToSearch, 
                                    queue, exService, swc, scannedFilesCounter);
        exService.submit(sw);
        
        Thread.sleep(200);
        
        System.out.println("Start scan filesystem. Wait for results.");
        List<String> list = new ArrayList<>();
        FileObject fobj;
        while (true) { 
            if(!queue.isEmpty()) {
                fobj = queue.take();
                list.add(fobj.getPath());
                System.out.println(fobj.toString());
            }
            if(swc.getAmount() == 0)    break;
        }
        
        PrintStream  sourceOutputStream = System.out;        
        PrintStream  newPrintStream = null;
        try {
            newPrintStream = 
                new PrintStream(new BufferedOutputStream(new FileOutputStream(resultFileName)));
            System.setOut(newPrintStream);
        } catch (FileNotFoundException ex) {
            System.setOut(sourceOutputStream);
            System.out.println("Error - can't write to output file");            
        }

        System.out.println("List size: " + list.size());
        System.out.println("Scanned: " + scannedFilesCounter.getValue());
        System.out.println();
        
        Collections.sort(list);
        for(String str : list) {
            System.out.println(str);
        } 
        
        if(newPrintStream != null) {
                newPrintStream.flush();
                newPrintStream.close();
        }
        
        exService.shutdown();
        System.setOut(sourceOutputStream);
        System.out.print("Results stored to file. Press any key to out.");
        scannerInput.nextLine();
    }
    
}
