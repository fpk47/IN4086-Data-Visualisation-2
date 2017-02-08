/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author michel
 */
public class VectorMath {

    // assign coefficients c0..c2 to vector v
    public static void setVector(float[] v, float c0, float c1, float c2) {
        v[0] = c0;
        v[1] = c1;
        v[2] = c2;
    }

    // compute dotproduct of vectors v and w
    public static float dotproduct(float[] v, float[] w) {
        float r = 0;
        for (int i=0; i<3; i++) {
            r += v[i] * w[i];
        }
        return r;
    }

    // compute distance between vectors v and w
    public static float distance( float[] v, float[] w) {
        double result = Math.pow( v[0]-w[0], 2 ) + Math.pow( v[1]-w[1], 2 ) + Math.pow( v[2]-w[2], 2 );
        return (float) Math.sqrt(result);
    }

    // compute dotproduct of v and w
    public static float[] crossproduct(float[] v, float[] w, float[] r) {
        r[0] = v[1] * w[2] - v[2] * w[1];
        r[1] = v[2] * w[0] - v[0] * w[2];
        r[2] = v[0] * w[1] - v[1] * w[0];
        return r;
    }
    
    // compute dotproduct of v and w
    public static float[] crossproduct(float[] v, float[] w) {
        float[] r = new float[3];
        r[0] = v[1] * w[2] - v[2] * w[1];
        r[1] = v[2] * w[0] - v[0] * w[2];
        r[2] = v[0] * w[1] - v[1] * w[0];
        return r;
    }
    
    // compute length of vector v
    public static float length(float[] v) {
        return (float) Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    }
    
    public static float[] add(float[] v, float[] w) {
        float[] r = new float[3];
        r[0] = v[0] + w[0];
        r[1] = v[1] + w[1];
        r[2] = v[2] + w[2];
        return r;
    }
    
    /**
     * Subtracts w from v, returns the result in a new float[3].
     * @param v
     * @param w
     * @return 
     */
    public static float[] subtract(float[] v, float[] w) {
        float[] r = new float[3];
        r[0] = v[0] - w[0];
        r[1] = v[1] - w[1];
        r[2] = v[2] - w[2];
        return r;
    }
    
    public static float[] multiply(float[] v, float s) {
        float[] r = new float[3];
        r[0] = v[0] * s;
        r[1] = v[1] * s;
        r[2] = v[2] * s;
        return r;
    }
    
    public static float[] divide(float[] v, float s) {
        float[] r = new float[3];
        r[0] = v[0] / s;
        r[1] = v[1] / s;
        r[2] = v[2] / s;
        return r;
    }
    
    public static float[] normalize(float[] v) {
        float len = length(v);
        float[] r = new float[3];
        r[0] = v[0] / len;
        r[1] = v[1] / len;
        r[2] = v[2] / len;
        return r;
    }
    
    public static int[] castToInteger(float[] v) {
        int[] r = new int[3];
        r[0] = (int) v[0];
        r[1] = (int) v[1];
        r[2] = (int) v[2];
        return r;
    }
}
