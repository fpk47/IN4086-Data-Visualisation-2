/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author michel
 */
public class TransferFunction2DView extends javax.swing.JPanel {

    TransferFunction2DEditor ed;
    private final int DOTSIZE = 8;
    public Ellipse2D.Double baseControlPoint, radiusControlPoint, minControlPoint, maxControlPoint;
    boolean selectedBaseControlPoint, selectedRadiusControlPoint, selectedMinControlPoint, selectedMaxControlPoint;
    
    
    /**
     * Creates new form TransferFunction2DView
     * @param ed
     */
    public TransferFunction2DView(TransferFunction2DEditor ed) {
        initComponents();
        
        this.ed = ed;
        selectedBaseControlPoint = false;
        selectedRadiusControlPoint = false;
        selectedMinControlPoint = false;
        selectedMaxControlPoint = false;
        addMouseMotionListener(new TriangleWidgetHandler());
        addMouseListener(new SelectionHandler());
    }
    
    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        int w = this.getWidth();
        int h = this.getHeight();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, w, h);
        
        float maxHistoMagnitude = ed.histogram[0];
        for (int i = 0; i < ed.histogram.length; i++) {
            maxHistoMagnitude = ed.histogram[i] > maxHistoMagnitude ? ed.histogram[i] : maxHistoMagnitude;
        }
        
        float binWidth = (float) w / (float) ed.xbins;
        float binHeight = (float) h / (float) ed.ybins;
        maxHistoMagnitude = (float) Math.log(maxHistoMagnitude);
        
        
        for (int y = 0; y < ed.ybins; y++) {
            for (int x = 0; x < ed.xbins; x++) {
                if (ed.histogram[y * ed.xbins + x] > 0) {
                    int intensity = (int) Math.floor(255 * (1.0 - ((float) Math.log(ed.histogram[y * ed.xbins + x])) / maxHistoMagnitude));
                    g2.setColor(new Color(intensity, intensity, intensity));
                    g2.fill(new Rectangle2D.Double(x * binWidth, h - (y * binHeight), binWidth, binHeight));
                }
            }
        }
        
        int ypos = h;
        int xpos = (int) (ed.triangleWidget.baseIntensity * binWidth);
        g2.setColor(Color.black);
        baseControlPoint = new Ellipse2D.Double(xpos - DOTSIZE / 2, ypos - DOTSIZE, DOTSIZE, DOTSIZE);
        g2.fill(baseControlPoint);
        g2.drawLine(xpos, ypos, xpos - (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude), 0);
        g2.drawLine(xpos, ypos, xpos + (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude), 0);
        radiusControlPoint = new Ellipse2D.Double(xpos + (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude) - DOTSIZE / 2,  0, DOTSIZE, DOTSIZE);
        g2.fill(radiusControlPoint);
        
        //Draw max magnitude line
        int yMax = h - (int) ((ed.triangleWidget.maxMagnitude / ed.maxGradientMagnitude) * h);
        float partMax = ed.triangleWidget.maxMagnitude / ed.maxGradientMagnitude;
        maxControlPoint = new Ellipse2D.Double(xpos - DOTSIZE / 2, yMax - DOTSIZE / 2, DOTSIZE, DOTSIZE);
        g2.setColor(Color.red);
        g2.fill(maxControlPoint);
        g2.drawLine(xpos - (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude * partMax), yMax,
                xpos + (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude * partMax), yMax);
        
        //Draw min magnitude line
        int yMin = h - (int) ((ed.triangleWidget.minMagnitude / ed.maxGradientMagnitude) * h);
        float partMin = ed.triangleWidget.minMagnitude / ed.maxGradientMagnitude;
        minControlPoint = new Ellipse2D.Double(xpos - DOTSIZE / 2, yMin - DOTSIZE / 2, DOTSIZE, DOTSIZE);
        g2.setColor(Color.blue);
        g2.fill(minControlPoint);
        g2.drawLine(xpos - (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude * partMin), yMin,
                xpos + (int) (ed.triangleWidget.radius * binWidth * ed.maxGradientMagnitude * partMin), yMin);
    }
    
    
    private class TriangleWidgetHandler extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            if (baseControlPoint.contains(e.getPoint()) || radiusControlPoint.contains(e.getPoint())
                    || minControlPoint.contains(e.getPoint()) || maxControlPoint.contains(e.getPoint())) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedBaseControlPoint || selectedRadiusControlPoint
                    || selectedMinControlPoint || selectedMaxControlPoint) {
                Point dragEnd = e.getPoint();
                
                if (selectedBaseControlPoint) {
                    // restrain to horizontal movement
                    dragEnd.setLocation(dragEnd.x, baseControlPoint.getCenterY());
                } else if (selectedRadiusControlPoint) {
                    // restrain to horizontal movement and avoid radius getting 0
                    dragEnd.setLocation(dragEnd.x, radiusControlPoint.getCenterY());
                    if (dragEnd.x - baseControlPoint.getCenterX() <= 0) {
                        dragEnd.x = (int) (baseControlPoint.getCenterX() + 1);
                    }
                } else if (selectedMinControlPoint || selectedMaxControlPoint) {
                    // restrain to vertical movement
                    dragEnd.setLocation(baseControlPoint.getCenterY(), dragEnd.y);
                }
                if (dragEnd.x < 0) {
                    dragEnd.x = 0;
                }
                if (dragEnd.x >= getWidth()) {
                    dragEnd.x = getWidth() - 1;
                }
                if (dragEnd.y < 0) {
                    dragEnd.y = 0;
                }
                if (dragEnd.y >= getHeight()) {
                    dragEnd.y = getHeight();
                }
                float w = getWidth();
                float h = getHeight();
                float binWidth = (float) w / (float) ed.xbins;
                
                if (selectedBaseControlPoint) {
                    ed.triangleWidget.baseIntensity = (short) (dragEnd.x / binWidth);
                } else if (selectedRadiusControlPoint) {
                    ed.triangleWidget.radius = (dragEnd.x - (ed.triangleWidget.baseIntensity * binWidth))/(binWidth*ed.maxGradientMagnitude);
                } else if (selectedMinControlPoint) {
                    ed.triangleWidget.minMagnitude = Math.min(((h - dragEnd.y) / h) * ed.maxGradientMagnitude, ed.triangleWidget.maxMagnitude);
                } else if (selectedMaxControlPoint) {
                    ed.triangleWidget.maxMagnitude = Math.max(((h - dragEnd.y) / h) * ed.maxGradientMagnitude, ed.triangleWidget.minMagnitude);
                }
                ed.setSelectedInfo();
                
                repaint();
            } 
        }

    }
    
    
    private class SelectionHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (minControlPoint.contains(e.getPoint())) {
                selectedMinControlPoint = true;
            } else if (maxControlPoint.contains(e.getPoint())) {
                selectedMaxControlPoint = true;
            } else if (radiusControlPoint.contains(e.getPoint())) {
                selectedRadiusControlPoint = true;
            } else if (baseControlPoint.contains(e.getPoint())) {
                selectedBaseControlPoint = true;
            }
            else {
                selectedRadiusControlPoint = false;
                selectedBaseControlPoint = false;
                selectedMinControlPoint = false;
                selectedMaxControlPoint = false;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            selectedRadiusControlPoint = false;
            selectedBaseControlPoint = false;
            selectedMinControlPoint = false;
            selectedMaxControlPoint = false;
            ed.changed();
            repaint();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
