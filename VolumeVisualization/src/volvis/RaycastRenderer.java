/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import gui.RaycastRendererPanel;
import java.util.Arrays;
import gui.TransferFunction2DEditor;
import gui.TransferFunctionEditor;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.TFChangeListener;
import util.VectorMath;
import volume.GradientVolume;
import volume.Volume;
import volume.VoxelGradient;
import java.util.ArrayList;

/**
 *
 * @author michel
 * @Anna
 * This class has the main code that generates the raycasting result image. 
 * The connection with the interface is already given.  
 * The different modes mipMode, slicerMode, etc. are already correctly updated
 */
public class RaycastRenderer extends Renderer implements TFChangeListener {

    private Volume volume = null;
    private GradientVolume gradients = null;
    RaycastRendererPanel panel;
    TransferFunction tFunc;
    TransferFunctionEditor tfEditor;
    TransferFunction2DEditor tfEditor2D;
    public boolean mipMode = false;
    public boolean slicerMode = true;
    public boolean compositingMode = false;
    public boolean tf2dMode = false;
    public boolean shadingMode = false;
    
    private ArrayList<CustomThread> threads = new ArrayList<CustomThread>();
    private boolean firstTime = true;
    
    public RaycastRenderer() {
        panel = new RaycastRendererPanel(this);
        panel.setSpeedLabel("0");
    }

    public void setVolume(Volume vol) {
        System.out.println("Assigning volume");
        volume = vol;

        System.out.println("Computing gradients");
        gradients = new GradientVolume(vol);

        // set up image for storing the resulting rendering
        // the image width and height are equal to the length of the volume diagonal
        int imageSize = (int) Math.floor(Math.sqrt(vol.getDimX() * vol.getDimX() + vol.getDimY() * vol.getDimY()
                + vol.getDimZ() * vol.getDimZ()));
        if (imageSize % 2 != 0) {
            imageSize = imageSize + 1;
        }
        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        tFunc = new TransferFunction(volume.getMinimum(), volume.getMaximum());
        tFunc.setTestFunc();
        tFunc.addTFChangeListener(this);
        tfEditor = new TransferFunctionEditor(tFunc, volume.getHistogram());
        
        tfEditor2D = new TransferFunction2DEditor(volume, gradients);
        tfEditor2D.addTFChangeListener(this);

        System.out.println("Finished initialization of RaycastRenderer");
    }

    public RaycastRendererPanel getPanel() {
        return panel;
    }

    public TransferFunction2DEditor getTF2DPanel() {
        return tfEditor2D;
    }
    
    public TransferFunctionEditor getTFPanel() {
        return tfEditor;
    }
     
    public void setShadingMode(boolean mode) {
        shadingMode = mode;
        changed();
    }
    
    public void setMIPMode() {
        setMode(false, true, false, false);
    }
    
    public void setSlicerMode() {
        setMode(true, false, false, false);
    }
    
    public void setCompositingMode() {
        setMode(false, false, true, false);
    }
    
    public void setTF2DMode() {
        setMode(false, false, false, true);
    }
    
    private void setMode(boolean slicer, boolean mip, boolean composite, boolean tf2d) {
        slicerMode = slicer;
        mipMode = mip;
        compositingMode = composite;
        tf2dMode = tf2d;        
        changed();
    }
    
        
    private void drawBoundingBox(GL2 gl) {
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor4d(1.0, 1.0, 1.0, 1.0);
        gl.glLineWidth(1.5f);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glDisable(GL.GL_LINE_SMOOTH);
        gl.glDisable(GL.GL_BLEND);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopAttrib();

    }
    
    private boolean intersectLinePlane(float[] plane_pos, float[] plane_normal,
            float[] line_pos, float[] line_dir, float[] intersection) {

        float[] tmp = new float[3];

        for (int i = 0; i < 3; i++) {
            tmp[i] = plane_pos[i] - line_pos[i];
        }

        float denom = VectorMath.dotproduct(line_dir, plane_normal);
        if (Math.abs(denom) < 1.0e-8) {
            return false;
        }

        float t = VectorMath.dotproduct(tmp, plane_normal) / denom;

        for (int i = 0; i < 3; i++) {
            intersection[i] = line_pos[i] + t * line_dir[i];
        }

        return true;
    }

    private boolean validIntersection(float[] intersection, float xb, float xe, float yb,
            float ye, float zb, float ze) {

        return (((xb - 0.5) <= intersection[0]) && (intersection[0] <= (xe + 0.5))
                && ((yb - 0.5) <= intersection[1]) && (intersection[1] <= (ye + 0.5))
                && ((zb - 0.5) <= intersection[2]) && (intersection[2] <= (ze + 0.5)));

    }

    private void intersectFace(float[] plane_pos, float[] plane_normal,
            float[] line_pos, float[] line_dir, float[] intersection,
            float[] entryPoint, float[] exitPoint) {

        boolean intersect = intersectLinePlane(plane_pos, plane_normal, line_pos, line_dir,
                intersection);
        if (intersect) {

            //System.out.println("Plane pos: " + plane_pos[0] + " " + plane_pos[1] + " " + plane_pos[2]);
            //System.out.println("Intersection: " + intersection[0] + " " + intersection[1] + " " + intersection[2]);
            //System.out.println("line_dir * intersection: " + VectorMath.dotproduct(line_dir, plane_normal));

            float xpos0 = 0;
            float xpos1 = volume.getDimX();
            float ypos0 = 0;
            float ypos1 = volume.getDimY();
            float zpos0 = 0;
            float zpos1 = volume.getDimZ();

            if (validIntersection(intersection, xpos0, xpos1, ypos0, ypos1,
                    zpos0, zpos1)) {
                if (VectorMath.dotproduct(line_dir, plane_normal) > 0) {
                    entryPoint[0] = intersection[0];
                    entryPoint[1] = intersection[1];
                    entryPoint[2] = intersection[2];
                } else {
                    exitPoint[0] = intersection[0];
                    exitPoint[1] = intersection[1];
                    exitPoint[2] = intersection[2];
                }
            }
        }
    }
    
      int traceRayMIP(float[] entryPoint, float[] exitPoint, float[] viewVec, float sampleStep) {
        /*
        float[] dir = VectorMath.subtract(exitPoint, entryPoint);
        int steps = (int) (VectorMath.length(dir) / sampleStep);
        dir = VectorMath.normalize(dir);
        float[] step = VectorMath.multiply(dir, sampleStep);
        float volMax = volume.getMaximum();
        
        int counter = 0;
        */
        ////
        
        float temp[] = new float[3];
        float max = Integer.MIN_VALUE;
        float volMax = volume.getMaximum();
        
        temp[0] = exitPoint[0] - entryPoint[0];
        temp[1] = exitPoint[1] - entryPoint[1];
        temp[2] = exitPoint[2] - entryPoint[2];
        
        int t0 = Math.abs((int)temp[0]);
        int t1 = Math.abs((int)temp[1]);
        int t2 = Math.abs((int)temp[2]);
        

        float length = (float) Math.sqrt( temp[0]*temp[0] + temp[1]*temp[1] + temp[2]*temp[2] );
        //float length2 = (float) Math.sqrt( ((t0)<<1) + ((t1)<<(1)) + ((t2)<<(1)) );
        
        float temp2 = ( 1.0f / length ) * sampleStep;
        float steps = length / sampleStep;
        
        temp[0] *= temp2;
        temp[1] *= temp2;
        temp[2] *= temp2;
        
        float[] coord = new float[3];
        
        coord[0] = entryPoint[0];
        coord[1] = entryPoint[1];
        coord[2] = entryPoint[2];
        float val;
        
        for (int i = 0; i < steps; i++) {
            coord[0] += temp[0];
            coord[1] += temp[1];
            coord[2] += temp[2];
            
            val = volume.getVoxelInterpolate3(coord);
            /*float val2 = volume.getVoxelInterpolate3(coord);
            
            if ( Math.abs((short)val - (short)val2) > 0 ){
                counter++;
                System.out.println("error: " + val + " " + val2 );
            }*/
            
            if ( val > max )
                max = val;
   
            if (max == volMax ) {
                break;
            }
        }
        //if ( counter > 0)
        //System.out.println("counter: " + counter);
        
        int color = floatsToColor(1, max/volMax, max/volMax, max/volMax);
        return color;
    }
   
    
   
    void computeEntryAndExit(float[] p, float[] viewVec, float[] entryPoint, float[] exitPoint) {

        for (int i = 0; i < 3; i++) {
            entryPoint[i] = -1;
            exitPoint[i] = -1;
        }

        float[] plane_pos = new float[3];
        float[] plane_normal = new float[3];
        float[] intersection = new float[3];

        VectorMath.setVector(plane_pos, volume.getDimX(), 0, 0);
        VectorMath.setVector(plane_normal, 1, 0, 0);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);

        VectorMath.setVector(plane_pos, 0, 0, 0);
        VectorMath.setVector(plane_normal, -1, 0, 0);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);

        VectorMath.setVector(plane_pos, 0, volume.getDimY(), 0);
        VectorMath.setVector(plane_normal, 0, 1, 0);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);

        VectorMath.setVector(plane_pos, 0, 0, 0);
        VectorMath.setVector(plane_normal, 0, -1, 0);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);

        VectorMath.setVector(plane_pos, 0, 0, volume.getDimZ());
        VectorMath.setVector(plane_normal, 0, 0, 1);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);

        VectorMath.setVector(plane_pos, 0, 0, 0);
        VectorMath.setVector(plane_normal, 0, 0, -1);
        intersectFace(plane_pos, plane_normal, p, viewVec, intersection, entryPoint, exitPoint);
    }

    void raycast(float[] viewMatrix) {      
        /* To be partially implemented:
            This function traces the rays through the volume. Have a look and check that you understand how it works.
            You need to introduce here the different modalities MIP/Compositing/TF2/ etc...*/

        float[] viewVec = new float[3];
        float[] uVec = new float[3];
        float[] vVec = new float[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);

        int totalSizeX = image.getWidth();
        int totalSizeY = image.getHeight();
        
        int increment = 1;
        
        if ( DragState.isDragged() ){
            increment = 5;
        } 
        
        DragState.setDragged(false);
        
        float sampleStep = 0.2f;
        int imageCenter = image.getWidth() / 2;
        
        int splitCount = 2;
        int restX = totalSizeX % splitCount;
        int restY = totalSizeY % splitCount;
        int sizeX = totalSizeX / splitCount;
        int sizeY = totalSizeY / splitCount;
        
        for ( int x = 0; x < splitCount; x++ ){
            for ( int y = 0; y < splitCount; y++ ){
                int currentX = sizeX * x;
                int currentY = sizeY * y;

                int tempSizeX = sizeX;
                int tempSizeY = sizeY;
                
                if ( x == splitCount - 1 )  tempSizeX += restX;
                if ( y == splitCount - 1 )  tempSizeY += restY;
                
                if ( firstTime ){
                    CustomThread customThread = new CustomThread(x + " " + y, volume, this, viewMatrix, viewVec, uVec, vVec, increment, sampleStep, imageCenter, currentX, currentY, tempSizeX, tempSizeY );
                    threads.add( customThread );
                } else{
                    threads.get(x + y * splitCount ).setVariables(volume, this, viewMatrix, viewVec, uVec, vVec, increment, sampleStep, imageCenter, currentX, currentY, tempSizeX, tempSizeY );
                }
            }
        }

        for ( CustomThread thread : threads ){
            if ( firstTime ){
                thread.start();
                thread.go();
            } else{
                thread.go();
            }
        } 
        
             firstTime = false;
          
        boolean done = false;
        while( !done ){
            done = true;
            
            for ( CustomThread customThread : threads ){
                if ( customThread.isRunning() ){
                    done = false;
                }
            }
           
            try{ Thread.sleep(5); } catch (InterruptedException ex) { System.out.println("ERROR: void raycast(float[] viewMatrix)"); }
        }
    
        for ( CustomThread customThread : threads ){
            int[][] data = customThread.getData();
            int tempX = customThread.startX;
            int tempY = customThread.startY;
            int tempSizeX = customThread.sizeX;
            int tempSizeY = customThread.sizeY;
            
            for ( int x = tempX; x < tempX + tempSizeX; x++ ){
                for ( int y = tempY; y < tempY + tempSizeY; y++ ){
                    image.setRGB(x, y, data[x-tempX][y-tempY] );
                }
            }      
        }
    }
    
    /**
     * Using https://books.google.nl/books?id=GlOzDQAAQBAJ&pg=PA591&lpg=PA591&dq=compositing+front+to+back+pseudocode&source=bl&ots=F9YsDdfm92&sig=bt-kYkUr8gl1U-pCzCpYkyqbEg4&hl=en&sa=X&ved=0ahUKEwi_0YujqLrRAhXKOxoKHaYPD-UQ6AEIKDAC#v=onepage&q=compositing%20front%20to%20back%20pseudocode&f=false
     * and https://users.cg.tuwien.ac.at/bruckner/homepage/content/mastersthesis/html/node35.html as basis.
     * @param entryPoint
     * @param exitPoint
     * @param viewVec
     * @param sampleStep
     * @return 
     */
    int traceRayComposite(float[] entryPoint, float[] exitPoint, float[] viewVec, float sampleStep) {
        float[] dir = VectorMath.subtract(exitPoint, entryPoint);
        int steps = (int) (VectorMath.length(dir) / sampleStep);
        dir = VectorMath.normalize(dir);
        float[] step = VectorMath.multiply(dir, sampleStep);
        
        TFColor color = new TFColor(0,0,0,1);
        TFColor background = new TFColor(0,0,0,1);
        float opacity = 0;
        float error = 0.01F;
        
        for (int i = 0; i < steps; i++) {
            float[] coord = VectorMath.add(VectorMath.multiply(step, i), entryPoint);
            short val = volume.getVoxelInterpolate(coord);
            TFColor auxColor = tFunc.getColor(val);
            
            float alpha = auxColor.a;
            // Calculate corrected alpha for when samplestep does not equal 1.
            if (sampleStep != 1)
                alpha = (float) (1 - Math.pow(1-auxColor.a, sampleStep));
            
            color = TFColor.add(color, TFColor.multiply(auxColor, alpha * (1-opacity)));
            opacity = opacity + (1-opacity) * alpha;
            if (opacity > 1 - error) {
                break;
            }
        }
        color = TFColor.add(color, TFColor.multiply(background, 1 - opacity));
        
        return floatsToColor(1, color.r, color.g, color.b);
    }

    void slicer(float[] viewMatrix) {

        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        float[] viewVec = new float[3];
        float[] uVec = new float[3];
        float[] vVec = new float[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);

        // image is square
        int imageCenter = image.getWidth() / 2;

        float[] pixelCoord = new float[3];
        float[] volumeCenter = new float[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);

        // sample on a plane through the origin of the volume data
        float max = volume.getMaximum();
        TFColor voxelColor = new TFColor();
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                        + volumeCenter[0];
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                        + volumeCenter[1];
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                        + volumeCenter[2];

                
                

                //int val = volume.getVoxelInterpolateNearestNeightbour(pixelCoord);
                int val = volume.getVoxelInterpolate(pixelCoord);
                
                // Map the intensity to a grey value by linear scaling
                /*voxelColor.r = val/max;
                voxelColor.g = voxelColor.r;
                voxelColor.b = voxelColor.r;
                voxelColor.a = val > 0 ? 1.0f : 0.0f;  // this makes intensity 0 completely transparent and the rest opaque
                */
                // Alternatively, apply the transfer function to obtain a color
                TFColor auxColor = new TFColor(); 
                auxColor = tFunc.getColor(val);
                voxelColor.r=auxColor.r;
                voxelColor.g=auxColor.g;
                voxelColor.b=auxColor.b;
                voxelColor.a=auxColor.a;
                
                
                // BufferedImage expects a pixel color packed as ARGB in an int
                int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                image.setRGB(i, j, pixelColor);
            }
        }


    }
    
    /**
     * Converts a set of floats between 0 and 1 to an integer colour value in range 0 to 255.
     * @param a alpha channel
     * @param r red channel
     * @param g green channel
     * @param b blue channel
     * @return integer values containing the converted rgba colour.
     */
    private int floatsToColor(float a, float r, float g, float b) {
        int c_alpha = a <= 1.0 ? (int) Math.floor(a * 255) : 255;
        int c_red = r <= 1.0 ? (int) Math.floor(r * 255) : 255;
        int c_green = g <= 1.0 ? (int) Math.floor(g * 255) : 255;
        int c_blue = b <= 1.0 ? (int) Math.floor(b * 255) : 255;
        int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
        return pixelColor;
    }


    @Override
    public void visualize(GL2 gl) {


        if (volume == null) {
            return;
        }

        drawBoundingBox(gl);

        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, viewMatrix, 0);

        long startTime = System.currentTimeMillis();
        if (slicerMode) {
            slicer(viewMatrix);    
        } else {
            raycast(viewMatrix);
        }
        
        long endTime = System.currentTimeMillis();
        float runningTime = (endTime - startTime);
        panel.setSpeedLabel(Double.toString(runningTime));

        Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // draw rendered image as a billboard texture
        texture.enable(gl);
        texture.bind(gl);
        float halfWidth = image.getWidth() / 2.0f;
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-halfWidth, -halfWidth, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(-halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(halfWidth, -halfWidth, 0.0);
        gl.glEnd();
        texture.disable(gl);
        texture.destroy(gl);
        gl.glPopMatrix();

        gl.glPopAttrib();


        if (gl.glGetError() > 0) {
            System.out.println("some OpenGL error: " + gl.glGetError());
        }

    }
    public BufferedImage image;
    private float[] viewMatrix = new float[4 * 4];

    @Override
    public void changed() {
        for (int i=0; i < listeners.size(); i++) {
            listeners.get(i).changed();
        }
    }
}
