/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.fastsearch;

import java.io.File;

/**
 *
 * @author apu
 */
public class FileObject {
    private String path;
    private boolean directory;

    public FileObject(String path) {
        this.path = path;
    }

    public FileObject(File file) {
        this(file.getPath());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return path;
    }   
    
}
