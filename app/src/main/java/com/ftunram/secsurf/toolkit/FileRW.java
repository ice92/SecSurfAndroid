/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ftunram.secsurf.toolkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author I
 */
public class FileRW {
    public String[] read(String file){
        String sCurrentLine[] = new String[1000] ;
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{

			
                        int i=0;
			while ((sCurrentLine[i]=br.readLine()) != null) {                                                                               
                            i++;				
			}

		} catch (IOException e) {                    
                    sCurrentLine[0]="null";
		} 
        return sCurrentLine;

    }
    public boolean write(String input,File file){
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(input);
			bw.close();
        }
        catch(Exception e){
        return false;}
        return true;
    }
}
