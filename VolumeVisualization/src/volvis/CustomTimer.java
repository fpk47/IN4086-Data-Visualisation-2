/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

/**
 *
 * @author Frits
 */
public class CustomTimer {
    public static long totalTime = 0;
    public static long startTime = 0;
    
    public static void reset(){
        totalTime = 0;
    }
    
    public static void start(){
        startTime = System.nanoTime();
    }
    
    public static void stop(){
        totalTime += System.nanoTime() - startTime;
    }
    
    public static void printTime(){
        System.out.println("TotalTime: " + ((totalTime / 1000000)-900) + "  ms");
    }
}
