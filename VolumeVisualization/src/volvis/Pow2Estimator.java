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
public class Pow2Estimator {
    
    public static final int numberOfSamples = 10000;
    public static final float numberOfSamples_norm = (float) numberOfSamples;
    private static float[] result = new float[ numberOfSamples + 1 ];
    private static boolean initDone = false;
    
    public static void init(){
        float delta = 1.0f / (float) numberOfSamples;
        float current = 0.0f;
        
        for ( int i = 0; i < numberOfSamples; i++ ){
            result[i] = (float) Math.pow(current, 2);
            current += delta;
        }
        
        result[numberOfSamples] =  result[numberOfSamples-1];
        
        initDone = true;
        System.out.println("Pow2Estimator: Init done..");
    }
        
    public static boolean isInitialised(){
        return initDone;
    }
       
    public static float getValue( int index ){
        return result[ index ];
    }
}
