/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import java.io.File;
import java.io.IOException;
import util.VectorMath;

/**
 *
 * @author michel
 * @Anna 
 * Volume object: This class contains the object and assumes that the distance between the voxels in x,y and z are 1 
 */
public class Volume {
    
    public Volume(int xd, int yd, int zd) {
        data = new short[xd*yd*zd];
        dimX = xd;
        dimY = yd;
        dimZ = zd;
    }
    
    public Volume(File file) {
        
        try {
            VolumeIO reader = new VolumeIO(file);
            dimX = reader.getXDim();
            dimY = reader.getYDim();
            dimZ = reader.getZDim();
            data = reader.getData().clone();
            this.minimum = computeMinimum();
            this.maximum = computeMaximum();
            computeHistogram();
        } catch (IOException ex) {
            System.out.println("IO exception");
        }
        
    }
    
    public short getVoxel(int[] coord) {
        return data[coord[0] + dimX*(coord[1] + dimY * coord[2])];
    }
    
    public short getVoxel(int x, int y, int z) {
        return data[x + dimX*(y + dimY * z)];
    }
    
    public void setVoxel(int x, int y, int z, short value) {
        data[x + dimX*(y + dimY*z)] = value;
        this.minimum = value < this.minimum ? value : this.minimum;
        this.maximum = value > this.maximum ? value : this.maximum;
    }

    public void setVoxel(int i, short value) {
        data[i] = value;
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
     * Round a float value up ( 7.4 --> 8, 7.7 --> 8)
     * @param value
     * @return up-rounded value (int)
     */
    private static int roundUp( float value ){
        return (int) (value + 0.5);
    } 
    
    /**
     * Round a float value up ( 7.4 --> 7, 7.7 --> 7)
     * @param value
     * @return down-rounded value (int)
     */
    private static int roundDown( float value ){
        return (int) value;
    } 
    
    public short getVoxelInterpolate(float[] coord) {
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return 0;
        }
        /* notice that in this framework we assume that the distance between neighbouring voxels is 1 in all directions*/
        
        float totalDistance = 0.0f;
        float distances[] = new float[8];
        float localCoord[] = new float[3];
        float colorValues[] = new float[8];
        
        VectorMath.setVector( localCoord, roundUp( coord[0] ), roundUp( coord[1] ), roundUp( coord[2] ) );
        distances[0] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[0];
        colorValues[0] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundUp( coord[0] ), roundUp( coord[1] ), roundDown( coord[2] ) );
        distances[1] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[1];
        colorValues[1] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundUp( coord[0] ), roundDown( coord[1] ), roundUp( coord[2] ) );
        distances[2] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[2];
        colorValues[2] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundUp( coord[0] ), roundDown( coord[1] ), roundDown( coord[2] ) );
        distances[3] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[3];
        colorValues[3] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundDown( coord[0] ), roundUp( coord[1] ), roundUp( coord[2] ) );
        distances[4] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[4];
        colorValues[4] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundDown( coord[0] ), roundUp( coord[1] ), roundDown( coord[2] ) );
        distances[5] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[5];
        colorValues[5] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundDown( coord[0] ), roundDown( coord[1] ), roundUp( coord[2] ) );
        distances[6] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[6];
        colorValues[6] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        VectorMath.setVector( localCoord, roundDown( coord[0] ), roundDown( coord[1] ), roundDown( coord[2] ) );
        distances[7] = VectorMath.distance( coord, localCoord );
        totalDistance += distances[7];
        colorValues[7] = getVoxel( VectorMath.castToInteger( localCoord) );
        
        int result = 0;
        
        for ( int i = 0; i < 8; i++ ){
            result += ( distances[i] / totalDistance ) * colorValues[i];
        }
        
        if ( result > 255 ){
            System.err.println("too high.." + result  );
        }
        
        return (short) result;
    }
    
    public short getVoxel(int i) {
        return data[i];
    }
    
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
        short min = data[0];
        for (int i=0; i<data.length; i++) {
            min = data[i] < min ? data[i] : min;
        }
        return min;
    }
    
    public short getMinimum() {
        return this.minimum;
    }

    private short computeMaximum() {
        short max = data[0];
        for (int i=0; i<data.length; i++) {
            max = data[i] > max ? data[i] : max;
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
        for (int i=0; i<data.length; i++) {
            histogram[data[i]]++;
        }
    }
    
    private int dimX, dimY, dimZ;
    private short minimum, maximum;
    private short[] data;
    private int[] histogram;
}
