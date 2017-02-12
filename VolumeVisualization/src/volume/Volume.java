/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.File;
import java.io.IOException;
import util.VectorMath;
import volvis.Pow2Estimator;

/**
 *
 * @author michel
 * @Anna 
 * Volume object: This class contains the object and assumes that the distance between the voxels in x,y and z are 1 
 */
public class Volume {
    
    public Volume(int xd, int yd, int zd) {
        dimX = xd;
        dimY = yd;
        dimZ = zd;
        data = new short[(xd+1)][(yd+1)][(zd+1)];
    }
    
    public Volume(File file) {
        
        try {
            VolumeIO reader = new VolumeIO(file);
            dimX = reader.getXDim();
            dimY = reader.getYDim();
            dimZ = reader.getZDim();
            convert(reader.getData().clone());
            this.minimum = computeMinimum();
            this.maximum = computeMaximum();
            computeHistogram();
        } catch (IOException ex) {
            System.out.println("IO exception");
        }
        
    }
    
    private void convert( short[] data ){
        if ( dimX > 0 && dimY > 0 && dimZ > 0 ){
            this.data = new short[(dimX+2)][(dimY+2)][(dimZ+2)];
            
            for (int x=0; x<dimX+2; x++) {
                for (int y=0; y<dimY+2; y++) {
                    for (int z=0; z<dimZ+2; z++) {
                        this.data[x][y][z] = 0;
                    }
                }
            }

            for (int x=0; x<dimX; x++) {
                for (int y=0; y<dimY; y++) {
                    for (int z=0; z<dimZ; z++) {
                        this.data[x][y][z] = data[x + dimX*(y + dimY * z)];
                    }
                }
            }
        }
    }
    
    public short getVoxel(int[] coord) {
        return data[ coord[0] ][ coord[1] ][ coord[2] ];
    }
    
    public short getVoxel(int x, int y, int z) {
        return data[x][y][z];
    }
    
    public void setVoxel(int x, int y, int z, short value) {
        data[x][y][z] = value;
        this.minimum = value < this.minimum ? value : this.minimum;
        this.maximum = value > this.maximum ? value : this.maximum;
    }

    public short getVoxelNearestNeightbour(float[] coord) {
    /* to be implemented: get the trilinear interpolated value. 
        The current implementation gets the Nearest Neightbour */
        
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }
        /* notice that in this framework we assume that the distance between neighbouring voxels is 1 in all directions*/
        
        int x = (int) Math.round(coord[0]); 
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);
    
        return getVoxel(x, y, z);
        
    }
    
    /**
     * Round a float value up ( 7.4 --> 7, 7.7 --> 7)
     * @param value
     * @return down-rounded value (int)
     */
    private static int roundDown( float value ){
        return (int) value;
    } 

    private float temp_distance( float x, float y, float z){
        return (float) Math.sqrt(x*x +y*y +z*z);
    }
    
    // Faster than temp_distance
    private float temp_distance1( int x, int y, int z){
        float temp = Pow2Estimator.getValue(x) + Pow2Estimator.getValue(y) + Pow2Estimator.getValue(z);
        return (float) Math.sqrt(temp);
    }
    
     // Faster than getVoxelInterpolate2
     public float getVoxelInterpolate3(float[] coord) {
        //if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1) || coord[2] < 0 || coord[2] > (dimZ-1)) {
        //    return 0;
        //}
        
        int[] roundDown = { (int) coord[0], (int) coord[1], (int) coord[2] };
        int[] roundUp = { roundDown[0] + 1, roundDown[1] + 1, roundDown[2] + 1 };

        float deltaX_0_f = coord[0] - (float) roundDown[0];
        float deltaY_0_f = coord[1] - (float) roundDown[1];
        float deltaZ_0_f = coord[2] - (float) roundDown[2];
        
        if ( deltaX_0_f < 0 ) deltaX_0_f *= -1.0f;
        if ( deltaY_0_f < 0 ) deltaY_0_f *= -1.0f;
        if ( deltaZ_0_f < 0 ) deltaZ_0_f *= -1.0f;
        
        
        int deltaX_0 = (int) ( deltaX_0_f * Pow2Estimator.numberOfSamples_norm );
        int deltaY_0 = (int) ( deltaY_0_f * Pow2Estimator.numberOfSamples_norm );
        int deltaZ_0 = (int) ( deltaZ_0_f * Pow2Estimator.numberOfSamples_norm );
        
        int deltaX_1 = Pow2Estimator.numberOfSamples - deltaX_0;
        int deltaY_1 = Pow2Estimator.numberOfSamples - deltaY_0;
        int deltaZ_1 = Pow2Estimator.numberOfSamples - deltaZ_0;
        
        float totalDistance = 0.0f;
        float distance;
        float temp = 0.0f;
        
        distance = temp_distance1( deltaX_1, deltaY_1, deltaZ_1 );
        totalDistance += distance;
        temp += distance * getVoxel( roundUp[0], roundUp[1], roundUp[2] );
  
        distance = temp_distance1( deltaX_1, deltaY_1, deltaZ_0 );
        totalDistance += distance;
        temp += distance * getVoxel( roundUp[0], roundUp[1], roundDown[2] );
        
        distance = temp_distance1( deltaX_1, deltaY_0, deltaZ_1 );
        totalDistance += distance;
        temp += distance * getVoxel( roundUp[0], roundDown[1], roundUp[2] );
        
        distance = temp_distance1( deltaX_1, deltaY_0, deltaZ_0 );
        totalDistance += distance;
        temp += distance * getVoxel( roundUp[0], roundDown[1], roundDown[2] );
        
        distance = temp_distance1( deltaX_0, deltaY_1, deltaZ_1 );
        totalDistance += distance;
        temp += distance * getVoxel( roundDown[0], roundUp[1], roundUp[2] );

        distance = temp_distance1( deltaX_0, deltaY_1, deltaZ_0 );
        totalDistance += distance;
        temp += distance * getVoxel( roundDown[0], roundUp[1], roundDown[2] );
        
        distance = temp_distance1( deltaX_0, deltaY_0, deltaZ_1 );
        totalDistance += distance;
        temp += distance * getVoxel( roundDown[0], roundDown[1], roundUp[2] );
    
        distance = temp_distance1( deltaX_0, deltaY_0, deltaZ_0 );
        totalDistance += distance;
        temp += distance * getVoxel( roundDown[0], roundDown[1], roundDown[2] );  
        
        return ( (float) temp / (float) totalDistance );
     }

      // Faster than getVoxelInterpolate
     public float getVoxelInterpolate2(float[] coord) {
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1) || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }
        
        int[] roundDown = { (int) coord[0], (int) coord[1], (int) coord[2] };
        int[] roundUp = { roundDown[0] + 1, roundDown[1] + 1, roundDown[2] + 1 };

        float deltaX_0 = coord[0] - (float) roundDown[0];
        float deltaY_0 = coord[1] - (float) roundDown[1];
        float deltaZ_0 = coord[2] - (float) roundDown[2];
        
        float deltaX_1 = 1.0f - deltaX_0;
        float deltaY_1 = 1.0f - deltaY_0;
        float deltaZ_1 = 1.0f - deltaZ_0;
        
        float totalDistance = 0.0f;
        float distances[] = new float[8];
        float colorValues[] = new float[8];
        
        distances[0] = temp_distance( deltaX_1, deltaY_1, deltaZ_1 );
        totalDistance += distances[0];
        colorValues[0] = getVoxel( roundUp[0], roundUp[1], roundUp[2] );
        
        distances[1] = temp_distance( deltaX_1, deltaY_1, deltaZ_0 );
        totalDistance += distances[1];
        colorValues[1] = getVoxel( roundUp[0], roundUp[1], roundDown[2] );
        
        distances[2] = temp_distance( deltaX_1, deltaY_0, deltaZ_1 );
        totalDistance += distances[2];
        colorValues[2] = getVoxel( roundUp[0], roundDown[1], roundUp[2] );
        
        distances[3] = temp_distance( deltaX_1, deltaY_0, deltaZ_0 );
        totalDistance += distances[3];
        colorValues[3] = getVoxel( roundUp[0], roundDown[1], roundDown[2] );
        
        distances[4] = temp_distance( deltaX_0, deltaY_1, deltaZ_1 );
        totalDistance += distances[4];
        colorValues[4] = getVoxel( roundDown[0], roundUp[1], roundUp[2] );
        
        distances[5] = temp_distance( deltaX_0, deltaY_1, deltaZ_0 );
        totalDistance += distances[5];
        colorValues[5] = getVoxel( roundDown[0], roundUp[1], roundDown[2] );
        
        distances[6] = temp_distance( deltaX_0, deltaY_0, deltaZ_1 );
        totalDistance += distances[6];
        colorValues[6] = getVoxel( roundDown[0], roundDown[1], roundUp[2] );
        
        distances[7] = temp_distance( deltaX_0, deltaY_0, deltaZ_0 );
        totalDistance += distances[7];
        colorValues[7] = getVoxel( roundDown[0], roundDown[1], roundDown[2] );  
        
        float result = 0;

        for ( int i = 0; i < 8; i++ ){
            result += ( distances[i] / totalDistance ) * colorValues[i];
        }
        
        return result;
     }
     
    public short getVoxelInterpolate(float[] coord) {
       
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }

        float totalDistance = 0.0f;
        float distances[] = new float[8];
        float localCoord[] = new float[3];
        float colorValues[] = new float[8];
        
        int[] roundDown = { (int) coord[0], (int) coord[1], (int) coord[2] };
        int[] roundUp = { roundDown[0] + 1, roundDown[1] + 1, roundDown[2] + 1 };

        if ( roundUp[0] != roundDown[0] + 1 ){
           System.out.println( roundUp[0] + " " + (roundDown[0] + 1) + " " + coord[0] + " " + roundDown[0] );
        }

       
        VectorMath.setVector( localCoord, roundUp[0], roundUp[1], roundUp[2] );
        distances[0] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[0];
        colorValues[0] = getVoxel( roundUp[0], roundUp[1], roundUp[2] );
        
    
        
        VectorMath.setVector( localCoord, roundUp[0], roundUp[1], roundDown[2] );
        distances[1] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[1];
        colorValues[1] = getVoxel( roundUp[0], roundUp[1], roundDown[2] );
        
                  
        
        VectorMath.setVector( localCoord, roundUp[0], roundDown[1], roundUp[2] );
        distances[2] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[2];
        colorValues[2] = getVoxel( roundUp[0], roundDown[1], roundUp[2] );
        
        VectorMath.setVector( localCoord, roundUp[0], roundDown[1], roundDown[2] );
        distances[3] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[3];
        colorValues[3] = getVoxel( roundUp[0], roundDown[1], roundDown[2] );
        
        VectorMath.setVector( localCoord, roundDown[0], roundUp[1], roundUp[2] );
        distances[4] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[4];
        colorValues[4] = getVoxel( roundDown[0], roundUp[1], roundUp[2] );
        
        VectorMath.setVector( localCoord, roundDown[0], roundUp[1], roundDown[2] );
        distances[5] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[5];
        colorValues[5] = getVoxel( roundDown[0], roundUp[1], roundDown[2] );
        
        VectorMath.setVector( localCoord, roundDown[0], roundDown[1], roundUp[2] );
        distances[6] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[6];
        colorValues[6] = getVoxel( roundDown[0], roundDown[1], roundUp[2] );
        
        VectorMath.setVector( localCoord, roundDown[0], roundDown[1], roundDown[2] );
        distances[7] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[7];
        colorValues[7] = getVoxel( roundDown[0], roundDown[1], roundDown[2] );

         //System.out.println("1: " + totalDistance +  " " + (coord[2] - localCoord[2] ) + " " + coord[2] + " " + localCoord[2] );
        
        
        float result = 0;
        
        for ( int i = 0; i < 8; i++ ){
            result += ( distances[i] / totalDistance ) * colorValues[i];
        }
        
        if ( result > 255 ){
            System.err.println("too high.." + result  );
        }
        
        return (short) result;
 
    }
    /*
    public short getVoxel(int i) {
        return data[i];
    }
    */
    
    public int getDimX() {
        return dimX;
    }
    
    public int getDimY() {
        return dimY;
    }
    
    public int getDimZ() {
        return dimZ;
    }

    private short computeMinimum() {
        short min = Short.MAX_VALUE;
        
        for (int x=0; x<dimX; x++) {
            for (int y=0; y<dimY; y++) {
                for (int z=0; z<dimZ; z++) {
                    if ( data[x][y][z] < min )
                        min = data[x][y][z];
                }
            }
        }
        return min;
    }
    
    public short getMinimum() {
        return this.minimum;
    }

    private short computeMaximum() {
        short max = Short.MIN_VALUE;
        
        for (int x=0; x<dimX; x++) {
            for (int y=0; y<dimY; y++) {
                for (int z=0; z<dimZ; z++) {
                    if ( data[x][y][z] > max )
                        max = data[x][y][z];
                }
            }
        }
        return max;
    }
    
    public short getMaximum() {
        return this.maximum;
    }
 
    public int[] getHistogram() {
        return histogram;
    }
    
    private void computeHistogram() {
        histogram = new int[getMaximum() + 1];
        for (int x=0; x<dimX; x++) {
            for (int y=0; y<dimY; y++) {
                for (int z=0; z<dimZ; z++) {
                    histogram[data[x][y][z]]++;
                }
            }
        }
    }
    
    public int dimX, dimY, dimZ;
    private short minimum, maximum;
    private short[][][] data;
    private int[] histogram;
}
