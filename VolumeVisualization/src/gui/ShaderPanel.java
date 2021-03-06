/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.ArrayList;
import util.TFChangeListener;
import volvis.RaycastRenderer;

/**
 *
 * @author nomen
 */
public class ShaderPanel extends javax.swing.JPanel {
    
    private ArrayList<TFChangeListener> listeners = new ArrayList<TFChangeListener>();
    
    public float ambient = 0.3f;
    public float diffuse = 0.7f;
    public float specular = 0.2f;
    public float shininess = 1f;
    
    /**
     * Creates new form ShaderPanel
     */
    public ShaderPanel() {
        initComponents();
        updateText();
    }
    
    public void addTFChangeListener(TFChangeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void changed() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).changed();
        }
    }
    
    private void updateText() {
        txtAmbient.setText(String.format("%.1f", ambient));
        txtDiffuse.setText(String.format("%.1f", diffuse));
        txtSpecular.setText(String.format("%.1f", specular));
        txtShininess.setText(String.format("%.1f", shininess));
    }
    
    public boolean doAmbient() {
        return this.chkAmbient.isSelected();
    }
    
    public boolean doDiffuse() {
        return this.chkDiffuse.isSelected();
    }
    
    public boolean doSpecular() {
        return this.chkSpecular.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAmbient = new javax.swing.JTextField();
        chkAmbient = new javax.swing.JCheckBox();
        chkDiffuse = new javax.swing.JCheckBox();
        chkSpecular = new javax.swing.JCheckBox();
        txtDiffuse = new javax.swing.JTextField();
        txtSpecular = new javax.swing.JTextField();
        txtShininess = new javax.swing.JTextField();

        jLabel1.setText("Phong Shading");

        jLabel2.setText("Specular");

        jLabel3.setText("Shininess");

        txtAmbient.setText("jTextField1");
        txtAmbient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmbientActionPerformed(evt);
            }
        });

        chkAmbient.setSelected(true);
        chkAmbient.setText("Ambient");
        chkAmbient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAmbientActionPerformed(evt);
            }
        });

        chkDiffuse.setSelected(true);
        chkDiffuse.setText("Diffuse");
        chkDiffuse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDiffuseActionPerformed(evt);
            }
        });

        chkSpecular.setSelected(true);
        chkSpecular.setText("Specular");
        chkSpecular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSpecularActionPerformed(evt);
            }
        });

        txtDiffuse.setText("jTextField2");
        txtDiffuse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiffuseActionPerformed(evt);
            }
        });

        txtSpecular.setText("jTextField3");
        txtSpecular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSpecularActionPerformed(evt);
            }
        });

        txtShininess.setText("jTextField4");
        txtShininess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtShininessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSpecular)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(7, 7, 7)
                                .addComponent(txtSpecular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtShininess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkAmbient)
                            .addComponent(chkDiffuse))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDiffuse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAmbient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(260, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkAmbient)
                    .addComponent(txtAmbient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkDiffuse)
                    .addComponent(txtDiffuse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSpecular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSpecular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtShininess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkAmbientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAmbientActionPerformed
        changed();
    }//GEN-LAST:event_chkAmbientActionPerformed

    private void txtAmbientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmbientActionPerformed
        try {
            float value = Float.parseFloat(txtAmbient.getText());
            if (value < 0) {
                value = 0;
            }
            if (value > 1) {
                value = 1;
            }
            ambient = value;
        } catch (NumberFormatException e) {
            ambient = 0.3f;
        }
        if (chkAmbient.isSelected()) {
            changed();
        }
        updateText();
    }//GEN-LAST:event_txtAmbientActionPerformed

    private void chkDiffuseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDiffuseActionPerformed
        changed();
    }//GEN-LAST:event_chkDiffuseActionPerformed

    private void chkSpecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSpecularActionPerformed
        changed();
    }//GEN-LAST:event_chkSpecularActionPerformed

    private void txtDiffuseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiffuseActionPerformed
        try {
            float value = Float.parseFloat(txtDiffuse.getText());
            if (value < 0) {
                value = 0;
            }
            diffuse = value;
        } catch (NumberFormatException e) {
            diffuse = 0.3f;
        }
        if (chkDiffuse.isSelected()) {
            changed();
        }
        updateText();
    }//GEN-LAST:event_txtDiffuseActionPerformed

    private void txtSpecularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSpecularActionPerformed
        try {
            float value = Float.parseFloat(txtSpecular.getText());
            if (value < 0) {
                value = 0;
            }
            specular = value;
        } catch (NumberFormatException e) {
            specular = 0.3f;
        }
        if (chkSpecular.isSelected()) {
            changed();
        }
        updateText();
    }//GEN-LAST:event_txtSpecularActionPerformed

    private void txtShininessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtShininessActionPerformed
        try {
            float value = Float.parseFloat(txtShininess.getText());
            if (value < 0) {
                value = 0;
            }
            shininess = value;
        } catch (NumberFormatException e) {
            shininess = 0.3f;
        }
        if (chkSpecular.isSelected()) {
            changed();
        }
        updateText();
    }//GEN-LAST:event_txtShininessActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAmbient;
    private javax.swing.JCheckBox chkDiffuse;
    private javax.swing.JCheckBox chkSpecular;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField txtAmbient;
    private javax.swing.JTextField txtDiffuse;
    private javax.swing.JTextField txtShininess;
    private javax.swing.JTextField txtSpecular;
    // End of variables declaration//GEN-END:variables
}
