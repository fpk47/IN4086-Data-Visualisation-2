/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.jogamp.opengl.GL2;
import volvis.DragState;

/**
 *
 * @author michel
 */
public class TrackballInteractor {

    private int lmx = 0, lmy = 0;	//remembers last mouse location
    private float[] trackballXform = new float[16];
    private float[] lastPos = new float[3];
    private float[] axis = new float[3];
    private float angle;
    private int width, height;
    private boolean rotating = false;

    public TrackballInteractor(int width, int height) {
        this.width = width;
        this.height = height;
        trackballXform[0] = 1.0f;
        trackballXform[5] = 1.0f;
        trackballXform[10] = 1.0f;
        trackballXform[15] = 1.0f;
    }

    public void setDimensions(int w, int h) {
        width = w;
        height = h;
    }

    public float[] getTransformationMatrix() {
        return trackballXform;
    }

    public void setMousePos(int x, int y) {
        lmx = x;
        lmy = y;
        trackball_ptov(lmx, lmy, width, height, lastPos);
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean flag) {
        rotating = flag;
    }

    private void trackball_ptov(int x, int y, int width, int height, float v[]) {
        float d, a;

        // project x,y onto a hemi-sphere centered within width, height
        float radius = Math.min(width, height) - 20;
        v[0] = (2.0f * x - width) / radius;
        v[1] = (height - 2.0f * y) / radius;

        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        v[2] = (float) Math.cos((Math.PI / 2.0) * ((d < 1.0) ? d : 1.0));
        a = (float) (1.0f / Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]));
        v[0] *= a;
        v[1] *= a;
        v[2] *= a;

    }

    public void drag(int mx, int my) {
        DragState.setDragged(true);
        float[] curPos = new float[3];

        trackball_ptov(mx, my, width, height, curPos);

        float dx = curPos[0] - lastPos[0];
        float dy = curPos[1] - lastPos[1];
        float dz = curPos[2] - lastPos[2];

        if ((dx != 0) || (dy != 0) || (dz != 0)) {
            angle = 90.0f * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            axis[0] = lastPos[1] * curPos[2] - lastPos[2] * curPos[1];
            axis[1] = lastPos[2] * curPos[0] - lastPos[0] * curPos[2];
            axis[2] = lastPos[0] * curPos[1] - lastPos[1] * curPos[0];

            lastPos[0] = curPos[0];
            lastPos[1] = curPos[1];
            lastPos[2] = curPos[2];
        }
    }

    public void updateTransform(GL2 gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glRotated(angle, axis[0], axis[1], axis[2]);
        gl.glMultMatrixf(trackballXform, 0);
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, trackballXform, 0);
        gl.glPopMatrix();
        setRotating(false);
    }
    
}
