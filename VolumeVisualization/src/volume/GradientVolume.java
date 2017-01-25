/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volume;

import util.VectorMath;

/**
 *
 * @author michel
 * @ Anna
 * This class contains the pre-computes gradients of the volume. This means calculates the gradient
 * at all voxel positions, and provides functions
 * to get the gradient at any position in the volume also continuous..
*/
public class GradientVolume {

    public GradientVolume(Volume vol) {
        volume = vol;
        dimX = vol.getDimX();
        dimY = vol.getDimY();
        dimZ = vol.getDimZ();
        data = new VoxelGradient[dimX * dimY * dimZ];
        compute();
    }

    public VoxelGradient getGradient(int x, int y, int z) {
        return data[x + dimX * (y + dimY * z)];
    }

    private void interpolate(VoxelGradient g0, VoxelGradient g1, float factor, VoxelGradient result) {
        /* To be implemented: this function linearly interpolates gradient vector g0 and g1 given the factor (t) 
            the resut is given at result. You can use it to tri-linearly interpolate the gradient */
        result.x = g0.x + (g1.x-g0.x)*factor;
        result.y = g0.x + (g1.y-g0.y)*factor;
        result.z = g0.x + (g1.z-g0.z)*factor;
    }
    
    public VoxelGradient getGradientNN(float[] coord) {
        /* Nearest neighbour interpolation applied to provide the gradient */
        if (coord[0] < 0 || coord[0] > (dimX-2) || coord[1] < 0 || coord[1] > (dimY-2)
                || coord[2] < 0 || coord[2] > (dimZ-2)) {
            return zero;
        }

        int x = (int) Math.round(coord[0]);
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);
        
        return getGradient(x, y, z);
    }

    
    public VoxelGradient getGradient(float[] coord) {
        /* To be implemented: Returns trilinear interpolated gradient based on the precomputed gradients. 
        *   Use function interpolate. Use getGradientNN as bases */
    
        if (coord[0] < 0 || coord[0] > (dimX-1) || coord[1] < 0 || coord[1] > (dimY-1)
                || coord[2] < 0 || coord[2] > (dimZ-1)) {
            return zero;
        }
        
        /* notice that in this framework we assume that the distance between neighbouring voxels is 1 in all directions*/
        
        float totalDistance = 0.0f;
        float[] distances = new float[8];
        float[] localCoord = new float[3];
        VoxelGradient[] colorValues = new VoxelGradient[8];
        
        VectorMath.setVector(localCoord, (float) Math.ceil(coord[0]), (float) Math.ceil(coord[1]), (float) Math.ceil(coord[2]));
        distances[0] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[0];
        colorValues[0] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.ceil(coord[0]), (float) Math.ceil(coord[1]), (float) Math.floor(coord[2]));
        distances[1] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[1];
        colorValues[1] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.ceil(coord[0]), (float) Math.floor(coord[1]), (float) Math.ceil(coord[2]));
        distances[2] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[2];
        colorValues[2] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.ceil(coord[0]), (float) Math.floor(coord[1]), (float) Math.floor(coord[2]));
        distances[3] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[3];
        colorValues[3] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.floor(coord[0]), (float) Math.ceil(coord[1]), (float) Math.ceil(coord[2]));
        distances[4] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[4];
        colorValues[4] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.floor(coord[0]), (float) Math.ceil(coord[1]), (float) Math.floor(coord[2]));
        distances[5] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[5];
        colorValues[5] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.floor(coord[0]), (float) Math.floor(coord[1]), (float) Math.ceil(coord[2]));
        distances[6] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[6];
        colorValues[6] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VectorMath.setVector(localCoord, (float) Math.floor(coord[0]), (float) Math.floor(coord[1]), (float) Math.floor(coord[2]));
        distances[7] = VectorMath.distance(coord, localCoord);
        totalDistance += distances[7];
        colorValues[7] = getGradient((int) localCoord[0], (int) localCoord[1], (int) localCoord[2]);
        
        VoxelGradient result = new VoxelGradient();
        float diff;
        
        for (int i = 0; i < 8; i++){
            diff = (distances[i] / totalDistance);
            result.x += diff*colorValues[i].x;
            result.y += diff*colorValues[i].y;
            result.z += diff*colorValues[i].z;
        }
        result.calculateMagnitude();

        return result;
    }
    
  
    public void setGradient(int x, int y, int z, VoxelGradient value) {
        data[x + dimX * (y + dimY * z)] = value;
    }

    public void setVoxel(int i, VoxelGradient value) {
        data[i] = value;
    }

    public VoxelGradient getVoxel(int i) {
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

    private void compute() {
        // Assume out of bounds values to be 0.
        
        short x1, x2, y1, y2, z1, z2;
        float xr, yr, zr;
        
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                for (int z = 0; z < dimZ; z++) {
                    // X value
                    x1 = x-1 < 0 ? 0 : volume.getVoxel(x-1, y, z);
                    x2 = x+1 >= dimX ? 0 : volume.getVoxel(x+1, y, z);
                    xr = (x2-x1)*0.5f;
                    
                    // Y value
                    y1 = y-1 < 0 ? 0 : volume.getVoxel(x, y-1, z);
                    y2 = y+1 >= dimY ? 0 : volume.getVoxel(x, y+1, z);
                    yr = (y2-y1)*0.5f;
                    
                    // Z value
                    z1 = z-1 < 0 ? 0 : volume.getVoxel(x, y, z-1);
                    z2 = z+1 >= dimZ ? 0 : volume.getVoxel(x, y, z+1);
                    zr = (z2-z1)*0.5f;
                    
                    VoxelGradient gr;
                    if (xr == 0 && yr == 0 && zr == 0) {
                        gr = zero;
                    }
                    else {
                        gr = new VoxelGradient(xr, yr, zr);
                    }
                    maxmag = Math.max(maxmag, gr.mag);
                    
                    setGradient(x, y, z, gr);
                }
            }
        }
     
    }
    
    public float getMaxGradientMagnitude() {
        return maxmag;
    }
    
    private int dimX, dimY, dimZ;
    private VoxelGradient zero = new VoxelGradient();
    VoxelGradient[] data;
    Volume volume;
    float maxmag;
}
