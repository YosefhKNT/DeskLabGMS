/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import BD_Connection.Conectar;
import com.formdev.flatlaf.FlatLightLaf;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import usuarios.Usuario;

/**
 *
 * @author yosef
 */
public class Login extends javax.swing.JFrame {

    private String id;
    private String nombre;
    private String contrasena;

    public Login() {
        initComponents();
        setLocationRelativeTo(null);
        setSize(800, 450);
        setResizable(false);
        setTitle("Inicio de Sesion");
        checarUsuario();
    }

    public void limpiarUsuarios() {
        cmb_usuario.removeAllItems();
    }

    public void checarUsuario() {
        int selectedIndex = cmb_rol.getSelectedIndex();
        boolean isRolSelected = (selectedIndex > 0);

        cmb_usuario.setEnabled(isRolSelected);
        txt_pass.setEnabled(isRolSelected);

        if (cmb_rol.getSelectedIndex() == 0) {
            cmb_usuario.setSelectedIndex(0);
            cmb_usuario.setEnabled(false);
            txt_pass.setText("");
            txt_pass.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cmb_rol = new javax.swing.JComboBox<>();
        btn_login = new javax.swing.JButton();
        txt_pass = new javax.swing.JTextField();
        cmb_usuario = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btn_llenar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        cmb_rol.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=== Selecionar puesto ===", "Administrador", "Laboratorista", "Recepcionista", "Quimico" }));
        cmb_rol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_rolActionPerformed(evt);
            }
        });

        btn_login.setBackground(new java.awt.Color(0, 102, 255));
        btn_login.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_login.setForeground(new java.awt.Color(255, 255, 255));
        btn_login.setText("Iniciar Sesion");
        btn_login.setEnabled(false);
        btn_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loginActionPerformed(evt);
            }
        });

        txt_pass.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_passKeyReleased(evt);
            }
        });

        cmb_usuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmb_usuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=== Seleccione Usuario ===" }));
        cmb_usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_usuarioActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 102, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Inicio de Sesión");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(180, 180, 180)
                .addComponent(jLabel1)
                .addContainerGap(257, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        btn_llenar.setText("Llenar");
        btn_llenar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_llenarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(328, 328, 328)
                        .addComponent(btn_llenar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(263, 263, 263)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_pass)
                    .addComponent(cmb_usuario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmb_rol, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_login, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                .addComponent(cmb_rol, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmb_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txt_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_login, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_llenar)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmb_rolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_rolActionPerformed
        // TODO add your handling code here:
        checarUsuario();
        limpiarUsuarios();
        String rol = cmb_rol.getSelectedItem().toString().toLowerCase();

        // Selecionar..., Administrador, Laboratorista, Recepcionista
        String sql = null;

        switch (rol) {
            case "administrador":
                sql = "SELECT * FROM `administrador` ORDER BY administrador.nombre ASC";
                break;
            case "laboratorista":
                sql = "SELECT * FROM `laboratorista` ORDER BY laboratorista.nombre ASC";
                break;
            case "recepcionista":
                sql = "SELECT * FROM `recepcionista` ORDER BY recepcionista.nombre ASC";
                break;
            case "quimico":
                sql = "SELECT * FROM `quimico` ORDER BY quimico.nombre ASC";
                break;
            default:
                return;
        }

        Conectar cc = new Conectar();
        Connection conect = cc.conexion();
        try {
            Statement st = (Statement) conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            cmb_usuario.addItem("=== Seleccione Usuario ===");
            while (rs.next()) {
                cmb_usuario.addItem(rs.getString("id") + "-" + rs.getString("nombre"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        }
    }//GEN-LAST:event_cmb_rolActionPerformed

    private void txt_passKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_passKeyReleased
        // TODO add your handling code here:
        if (txt_pass.getText().length() == 8) {
            btn_login.setEnabled(true);
        } else {
            btn_login.setEnabled(false);
        }
    }//GEN-LAST:event_txt_passKeyReleased

    private void btn_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loginActionPerformed
        String sql = null;
        String id_nombre = null;
        if (cmb_usuario.getSelectedIndex() >= 0) {
            id_nombre = cmb_usuario.getSelectedItem().toString();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Contraseña incorrecta");
        }

        String[] usuario = id_nombre.split("-");
        String id = usuario[0];
        String nombre = usuario[1];

        // Aquí se determina el rol según el nombre de usuario seleccionado
        String rol = cmb_rol.getSelectedItem().toString().toLowerCase();

        sql = "SELECT `contrasena` FROM `" + rol + "` WHERE `nombre` = '" + nombre + "' AND `id` = " + id;

        Conectar cc = new Conectar();
        Connection conect = cc.conexion();
        try {
            Statement st = (Statement) conect.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String contrasenaDB = rs.getString("contrasena");
                String contrasenaInput = txt_pass.getText();

                if (contrasenaDB.equals(contrasenaInput)) {
                    Usuario usuarioActual = new Usuario(nombre, contrasenaDB);
                    // Abre la ventana correspondiente según el rol
                    switch (rol) {
                        case "administrador":
                            User_Admin ventanaAdmin = new User_Admin(usuarioActual);
                            ventanaAdmin.setVisible(true);
                            break;
                        case "laboratorista":
                            User_Lab ventanaLab = new User_Lab(usuarioActual);
                            ventanaLab.setVisible(true);
                            break;
                        case "recepcionista":
                            User_Rec ventanaRec = new User_Rec(usuarioActual);
                            ventanaRec.setVisible(true);
                            break;
                        case "quimico":
                            User_Quim ventanaQuim = new User_Quim(usuarioActual);
                            ventanaQuim.setVisible(true);
                            break;
                        default:
                            // Rol no reconocido
                            JOptionPane.showMessageDialog(rootPane, "Rol no reconocido: " + rol);
                            break;
                    }

                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Contraseña incorrecta");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Usuario no encontrado");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        }

    }//GEN-LAST:event_btn_loginActionPerformed

    private void btn_llenarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_llenarActionPerformed
        // TODO add your handling code here:
        cmb_rol.setSelectedIndex(1);
        try {
            Thread.sleep(300); 
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        cmb_usuario.setSelectedIndex(2);
        txt_pass.setText("12345678");
        btn_login.setEnabled(true);
    }//GEN-LAST:event_btn_llenarActionPerformed

    private void cmb_usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_usuarioActionPerformed
        // TODO add your handling code here:
        if (cmb_usuario.getSelectedIndex() == 0) {
            txt_pass.setEnabled(false);
        } else {
            txt_pass.setEnabled(true);
        }
    }//GEN-LAST:event_cmb_usuarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_llenar;
    private javax.swing.JButton btn_login;
    private javax.swing.JComboBox<String> cmb_rol;
    private javax.swing.JComboBox<String> cmb_usuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txt_pass;
    // End of variables declaration//GEN-END:variables
}
