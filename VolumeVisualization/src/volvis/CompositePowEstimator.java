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
public class CompositePowEstimator {
    
    private static int numberOfSamples = 10000;
    private static float[] result = new float[ numberOfSamples + 1 ];
    private static boolean initDone = false;
    
    public static void init( float sampleStep ){
        float delta = 1.0f / (float) numberOfSamples;
        float current = 0.0f;
        
        for ( int i = 0; i < numberOfSamples; i++ ){
            result[i] = (float) Math.pow(1.0f - current, sampleStep);
            current += delta;
        }
        
        result[numberOfSamples] =  result[numberOfSamples-1];
        
        System.out.println("CompositePowEstimator: Init done..");
        initDone = true;
    }
    
    public static boolean isInitialised(){
        return initDone;
    }
    
    public static float getValue( float base ){
        return result[(int) ( base * (float) numberOfSamples) ];
    }
}
