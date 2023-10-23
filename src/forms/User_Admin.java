/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import BD_Connection.Conectar;
import com.formdev.flatlaf.FlatLightLaf;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import usuarios.Usuario;

/**
 *
 * @author yosefh
 */
public class User_Admin extends javax.swing.JFrame {

    private String nombre;
    private String contrasena;
    Usuario usuarioActual;
    TableRowSorter<DefaultTableModel> sorterAdmin, sorterRecep, sorterLab, sorterCitas, sorterEstudio, sorterArea, sorterQuimico;

    public User_Admin() {
        initComponents();
        inicio();
    }

    public User_Admin(Usuario usuarioActual) {
        this.nombre = usuarioActual.getNombre();
        this.contrasena = usuarioActual.getContrasena();
        this.usuarioActual = usuarioActual;
        inicio();
    }

    public void inicio() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Administracion");
        setResizable(false);
        lbl_icon.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_titulo_admin.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_titulo_usuarios.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_titulo_tablas.setHorizontalAlignment(SwingConstants.CENTER);
        Contenedor.setTabPlacement(JTabbedPane.BOTTOM);
        for (int i = 0; i < Contenedor.getTabCount(); i++) {
            Contenedor.setEnabledAt(i, false);;
        }
        getusuario();

        //busqueda en tablas
        buscarTodoAdmin();
        buscarTodoRecep();
        buscarTodoLab();
        buscarTodoCitas();
        buscarTodoEstudio();
        buscarTodoArea();
        buscarTodoQuimico();

        // validar boton de crear admin
        validarBotonCrearAdmin();
        validarBotonCrearRecep();
        validarBotonCrearLab();
        validarBotonCrearQuimico();
        validarBotonCrearEstudio();
        validarBotonCrearArea();
        validarBotonCrearCita();

        //estilos de los botones 
        iconStyles();
        reiniciarEstilos();
        btn_admin.setBackground(new Color(204, 229, 255));
        btn_admin_crear.setEnabled(false);
        btn_admin_actualizar.setEnabled(false);

        // llenado de la tabla Admin (Es la primera que se carga)
        llenarTablaAdmin();

        //limatantes textfields
        limitarSoloNumeros(txt_admin_id);
        limitarSoloNumeros(txt_recep_id);
        limitarSoloNumeros(txt_lab_id);
        limitarSoloNumeros(txt_area_id);
        limitarSoloNumeros(txt_cita_id);
        limitarSoloNumeros(txt_quimico_id);
        limitarSoloNumeros(txt_estudio_id);

        limitarSoloNumeros(txt_admin_telefono);
        limitarSoloNumeros(txt_recep_telefono);
        limitarSoloNumeros(txt_lab_telefono);
        limitarSoloNumeros(txt_area_telefono);
        //limitarSoloNumeros(txt_cita_telefono);
        limitarSoloNumeros(txt_quimico_telefono);
        limitarSoloNumeros(txt_estudio_telefono);

    }

    public void getusuario() {
        lbl_usuario.setText(nombre);
        lbl_usuario.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void iconStyles() {
        try {
            // Cargar el icono desde un archivo (en este caso, se carga desde un archivo en la carpeta resources)
            InputStream inputStream = getClass().getResourceAsStream("/img/lupa-48.png");
            BufferedImage image = ImageIO.read(inputStream);

            // Escalar el icono al tamaño deseado
            int width = 34; // Tamaño deseado (ancho)
            int height = 34; // Tamaño deseado (alto)
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // Crear un ImageIcon con la imagen escalada
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // Establecer el icono en la JLabel
            btn_admin_buscar_id.setIcon(scaledIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //llenado de tablas
    private void reiniciarEstilos() {
        Color fondo = new Color(234, 234, 234);

        btn_admin.setBorder(null);
        btn_admin.setBackground(fondo);
        btn_citas.setBorder(null);
        btn_citas.setBackground(fondo);
        btn_estudios.setBorder(null);
        btn_estudios.setBackground(fondo);
        btn_lab.setBorder(null);
        btn_lab.setBackground(fondo);
        btn_recep.setBorder(null);
        btn_recep.setBackground(fondo);
        btn_area.setBorder(null);
        btn_area.setBackground(fondo);
        btn_quimico.setBorder(null);
        btn_quimico.setBackground(fondo);
    }

    // llenado de tablas
    private void llenarTablaAdmin() {
        String consulta = "SELECT * FROM `administrador`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel() {
                // Sobrescribir el método isCellEditable para hacer que todas las celdas sean no editables
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Todas las celdas no son editables
                }
            };

            TableCellEditor cellEditor = new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean isCellEditable(EventObject e) {
                    return false; // Todas las celdas no son editables
                }
            };

            tbl_admin.setDefaultEditor(Object.class, cellEditor);

            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                Object[] fila = {id, nombre, direccion, telefono};
                modeloA.addRow(fila);
            }

            tbl_admin.setModel(modeloA);

            sorterAdmin = new TableRowSorter<>(modeloA);
            tbl_admin.setRowSorter(sorterAdmin);

            TableColumn idColumna = tbl_admin.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(10);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_admin.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_admin.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            /*
            idColumna = tbl_admin.getColumnModel().getColumn(4);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);
             */
            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaRecepcionista() {
        String consulta = "SELECT * FROM `recepcionista`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloR = new DefaultTableModel();
            modeloR.addColumn("ID");
            modeloR.addColumn("Nombre");
            modeloR.addColumn("Dirección");
            modeloR.addColumn("Contraseña");
            modeloR.addColumn("Teléfono");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String contraseña = resultado.getString("password");
                String telefono = resultado.getString("telefono");
                Object[] fila = {id, nombre, direccion, contraseña, telefono};
                modeloR.addRow(fila);
            }

            tbl_recep.setModel(modeloR);

            sorterRecep = new TableRowSorter<>(modeloR);
            tbl_recep.setRowSorter(sorterRecep);

            TableColumn idColumna = tbl_recep.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_recep.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_recep.getColumnModel().getColumn(4);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_recep.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaLab() {
        String consulta = "SELECT * FROM `laboratorista`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloR = new DefaultTableModel();
            modeloR.addColumn("ID");
            modeloR.addColumn("Nombre");
            modeloR.addColumn("Dirección");
            modeloR.addColumn("Teléfono");
            modeloR.addColumn("Puesto");
            modeloR.addColumn("Contraseña");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                String puesto = resultado.getString("puesto");
                String contrasena = resultado.getString("password");
                Object[] fila = {id, nombre, direccion, telefono, puesto, contrasena};
                modeloR.addRow(fila);
            }

            tbl_lab.setModel(modeloR);

            sorterLab = new TableRowSorter<>(modeloR);
            tbl_lab.setRowSorter(sorterLab);

            TableColumn idColumna = tbl_lab.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_lab.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(40);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_lab.getColumnModel().getColumn(4);
            idColumna.setPreferredWidth(50);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_lab.getColumnModel().getColumn(5);
            idColumna.setPreferredWidth(40);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_lab.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaCitas() {
        String consulta = "SELECT * FROM `citas`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Clave");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Fecha");
            modeloA.addColumn("Hora");
            modeloA.addColumn("Telefono");
            modeloA.addColumn("Resultados");
            modeloA.addColumn("Estudios");
            modeloA.addColumn("Estatus");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String fecha = resultado.getString("fecha");
                String hora = resultado.getString("hora");
                String telefono = resultado.getString("telefono");
                String clave = resultado.getString("clave");
                String resultados = resultado.getString("resultados");
                String estudio_id = resultado.getString("estudio_id");
                String estatus = resultado.getString("estatus");

                String estudio = "";
                Statement stEst = (Statement) conect.createStatement();
                ResultSet resultadoEstudio = stEst.executeQuery("SELECT estudio FROM `estudio` WHERE id = " + estudio_id);

                if (resultadoEstudio.next()) {
                    estudio = resultadoEstudio.getString("estudio");
                } else {
                    estudio = "S/E";
                }
                Object[] fila = {id, clave, nombre, fecha, hora, telefono, resultados, estudio, estatus};
                modeloA.addRow(fila);
            }

            tbl_citas.setModel(modeloA);

            sorterCitas = new TableRowSorter<>(modeloA);
            tbl_citas.setRowSorter(sorterCitas);

            TableColumn idColumna = tbl_citas.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(1);
            idColumna.setPreferredWidth(55);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(2);
            idColumna.setPreferredWidth(100);

            idColumna = tbl_citas.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(50);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(4);
            idColumna.setPreferredWidth(50);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(60);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaEstudios() {
        String consulta = "SELECT * FROM `estudio`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloR = new DefaultTableModel();
            modeloR.addColumn("ID");
            modeloR.addColumn("Estudio");
            modeloR.addColumn("Descripcion");
            modeloR.addColumn("imagen");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String estudio = resultado.getString("estudio");
                String descripcion = resultado.getString("descripcion");
                String img = resultado.getString("ruta_imagen");
                Object[] fila = {id, estudio, descripcion, img};
                modeloR.addRow(fila);
            }

            tbl_estudios.setModel(modeloR);

            sorterEstudio = new TableRowSorter<>(modeloR);
            tbl_estudios.setRowSorter(sorterEstudio);

            TableColumn idColumna = tbl_estudios.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_estudios.getColumnModel().getColumn(3);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaArea() {
        String consulta = "SELECT * FROM `Area`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloR = new DefaultTableModel();
            modeloR.addColumn("ID");
            modeloR.addColumn("Area");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String area = resultado.getString("area");
                Object[] fila = {id, area};
                modeloR.addRow(fila);
            }

            tbl_areas.setModel(modeloR);

            sorterArea = new TableRowSorter<>(modeloR);
            tbl_areas.setRowSorter(sorterArea);

            TableColumn idColumna = tbl_areas.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarTablaQuimico() {
        String consulta = "SELECT * FROM `quimico`";

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloR = new DefaultTableModel();
            modeloR.addColumn("ID");
            modeloR.addColumn("Nombre");
            modeloR.addColumn("Dirección");
            modeloR.addColumn("Teléfono");
            modeloR.addColumn("Puesto");
            modeloR.addColumn("Contraseña");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                String puesto = resultado.getString("puesto");
                String contrasena = resultado.getString("password");
                Object[] fila = {id, nombre, direccion, telefono, puesto, contrasena};
                modeloR.addRow(fila);
            }

            tbl_quimico.setModel(modeloR);

            sorterQuimico = new TableRowSorter<>(modeloR);
            tbl_quimico.setRowSorter(sorterQuimico);

            TableColumn idColumna = tbl_quimico.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(5);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_quimico.getColumnModel().getColumn(2);
            idColumna.setPreferredWidth(80);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //boton de buscar
    public void buscarTodoAdmin() {
        txt_admin_buscar_todo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txt_admin_buscar_todo.getText();
                if (texto.trim().length() == 0) {
                    sorterAdmin.setRowFilter(null);
                } else {
                    try {
                        sorterAdmin.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoRecep() {
        txtBuscarRecep.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarRecep.getText();
                if (texto.trim().length() == 0) {
                    sorterRecep.setRowFilter(null);
                } else {
                    try {
                        sorterRecep.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoLab() {
        txtBuscarLab.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarLab.getText();
                if (texto.trim().length() == 0) {
                    sorterLab.setRowFilter(null);
                } else {
                    try {
                        sorterLab.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoCitas() {
        txtBuscarCita.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarCita.getText();
                if (texto.trim().length() == 0) {
                    sorterCitas.setRowFilter(null);
                } else {
                    try {
                        sorterCitas.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoEstudio() {
        txtBuscarEstudios.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarEstudios.getText();
                if (texto.trim().length() == 0) {
                    sorterEstudio.setRowFilter(null);
                } else {
                    try {
                        sorterEstudio.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoArea() {
        txtBuscarAreas.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarAreas.getText();
                if (texto.trim().length() == 0) {
                    sorterArea.setRowFilter(null);
                } else {
                    try {
                        sorterArea.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void buscarTodoQuimico() {
        txtBuscarQuimicos.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            private void filtrarTabla() {
                String texto = txtBuscarQuimicos.getText();
                if (texto.trim().length() == 0) {
                    sorterQuimico.setRowFilter(null);
                } else {
                    try {
                        sorterQuimico.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                    } catch (PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    // boton crear Validacion Admin
    public void validarBotonCrearAdmin() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearAdmin();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearAdmin();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearAdmin();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_admin_nombre.getDocument().addDocumentListener(documentListener);
        txt_admin_direccion.getDocument().addDocumentListener(documentListener);
        txt_admin_telefono.getDocument().addDocumentListener(documentListener);
        txt_admin_pass.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearAdmin() {
        // Obtener el contenido de los JTextField
        String nombre = txt_admin_nombre.getText();
        String direccion = txt_admin_direccion.getText();
        String telefono = txt_admin_telefono.getText();
        String pass = txt_admin_pass.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !pass.isEmpty()) {
            btn_admin_crear.setEnabled(true);
        } else {
            btn_admin_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && pass.isBlank()) {
            btn_admin_actualizar.setEnabled(true);
        } else {
            btn_admin_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion Recepcionista
    public void validarBotonCrearRecep() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearRecep();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearRecep();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearRecep();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_recep_nombre.getDocument().addDocumentListener(documentListener);
        txt_recep_direccion.getDocument().addDocumentListener(documentListener);
        txt_recep_telefono.getDocument().addDocumentListener(documentListener);
        txt_recep_pass.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearRecep() {
        // Obtener el contenido de los JTextField
        String nombre = txt_recep_nombre.getText();
        String direccion = txt_recep_direccion.getText();
        String telefono = txt_recep_telefono.getText();
        String pass = txt_recep_pass.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !pass.isEmpty()) {
            btn_recep_crear.setEnabled(true);
        } else {
            btn_recep_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && pass.isBlank()) {
            btn_recep_actualizar.setEnabled(true);
        } else {
            btn_recep_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion Laboratorista
    public void validarBotonCrearLab() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearLab();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearLab();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearLab();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_lab_nombre.getDocument().addDocumentListener(documentListener);
        txt_lab_direccion.getDocument().addDocumentListener(documentListener);
        txt_lab_telefono.getDocument().addDocumentListener(documentListener);
        txt_lab_pass.getDocument().addDocumentListener(documentListener);
        txt_lab_puesto.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearLab() {
        // Obtener el contenido de los JTextField
        String nombre = txt_lab_nombre.getText();
        String direccion = txt_lab_direccion.getText();
        String telefono = txt_lab_telefono.getText();
        String pass = txt_lab_pass.getText();
        String puesto = txt_lab_puesto.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !puesto.isEmpty() && !pass.isEmpty()) {
            btn_lab_crear.setEnabled(true);
        } else {
            btn_lab_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !puesto.isEmpty() && pass.isBlank()) {
            btn_lab_actualizar.setEnabled(true);
        } else {
            btn_lab_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion Quimico
    public void validarBotonCrearQuimico() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearQuimico();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearQuimico();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearQuimico();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_quimico_nombre.getDocument().addDocumentListener(documentListener);
        txt_quimico_direccion.getDocument().addDocumentListener(documentListener);
        txt_quimico_telefono.getDocument().addDocumentListener(documentListener);
        txt_quimico_pass.getDocument().addDocumentListener(documentListener);
        txt_quimico_puesto.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearQuimico() {
        // Obtener el contenido de los JTextField
        String nombre = txt_quimico_nombre.getText();
        String direccion = txt_quimico_direccion.getText();
        String telefono = txt_quimico_telefono.getText();
        String pass = txt_quimico_pass.getText();
        String puesto = txt_quimico_puesto.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !puesto.isEmpty() && !pass.isEmpty()) {
            btn_quimico_crear.setEnabled(true);
        } else {
            btn_quimico_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !puesto.isEmpty() && pass.isBlank()) {
            btn_quimico_actualizar.setEnabled(true);
        } else {
            btn_quimico_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion cita
    public void validarBotonCrearCita() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearCita();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearCita();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearCita();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_cita_nombre.getDocument().addDocumentListener(documentListener);
        txt_cita_direccion.getDocument().addDocumentListener(documentListener);
        //txt_cita_telefono.getDocument().addDocumentListener(documentListener);
        txt_cita_pass.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearCita() {
        // Obtener el contenido de los JTextField
        String nombre = txt_cita_nombre.getText();
        String direccion = txt_cita_direccion.getText();
        //String telefono = txt_cita_telefono.getText();
        String pass = txt_cita_pass.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() /*&& !telefono.isEmpty()*/ && !pass.isEmpty()) {
            btn_cita_crear.setEnabled(true);
        } else {
            btn_cita_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() &&/*!telefono.isEmpty()&& */ pass.isBlank()) {
            btn_cita_actualizar.setEnabled(true);
        } else {
            btn_cita_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion Area
    public void validarBotonCrearArea() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_area_nombre.getDocument().addDocumentListener(documentListener);
        txt_area_direccion.getDocument().addDocumentListener(documentListener);
        txt_area_telefono.getDocument().addDocumentListener(documentListener);
        txt_area_pass.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearArea() {
        // Obtener el contenido de los JTextField
        String nombre = txt_area_nombre.getText();
        String direccion = txt_area_direccion.getText();
        String telefono = txt_area_telefono.getText();
        String pass = txt_area_pass.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !pass.isEmpty()) {
            btn_area_crear.setEnabled(true);
        } else {
            btn_area_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && pass.isBlank()) {
            btn_area_actualizar.setEnabled(true);
        } else {
            btn_area_actualizar.setEnabled(false);
        }
    }

    // boton crear Validacion Estudio
    public void validarBotonCrearEstudio() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFieldsAndEnableButtonCrearArea();
            }
        };

        // Asociamos el DocumentListener a los JTextField
        txt_estudio_nombre.getDocument().addDocumentListener(documentListener);
        txt_estudio_direccion.getDocument().addDocumentListener(documentListener);
        txt_estudio_telefono.getDocument().addDocumentListener(documentListener);
        txt_estudio_pass.getDocument().addDocumentListener(documentListener);
    }

    private void checkFieldsAndEnableButtonCrearEstudio() {
        // Obtener el contenido de los JTextField
        String nombre = txt_estudio_nombre.getText();
        String direccion = txt_estudio_direccion.getText();
        String telefono = txt_estudio_telefono.getText();
        String pass = txt_estudio_pass.getText();

        // Habilitar el botón si ambos campos contienen caracteres, de lo contrario, deshabilitarlo
        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && !pass.isEmpty()) {
            btn_estudio_crear.setEnabled(true);
        } else {
            btn_estudio_crear.setEnabled(false);
        }

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty() && pass.isBlank()) {
            btn_estudio_actualizar.setEnabled(true);
        } else {
            btn_estudio_actualizar.setEnabled(false);
        }
    }

// limitar jtextfield a solo numeros
    public static void limitarSoloNumeros(JTextField textField) {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+") || text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        textField.setDocument(doc);
    }

    // Buscar por ID
    public void buscarID_Admin() {
        String consulta;

        if (txt_admin_id.getText().equals("0") || txt_admin_id.getText().equals("")) {
            consulta = "SELECT * FROM `administrador`";
        } else {
            consulta = "SELECT * FROM `administrador` WHERE id = '" + txt_admin_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                Object[] fila = {id, nombre, direccion, telefono};
                modeloA.addRow(fila);
            }

            tbl_admin.setModel(modeloA);

            sorterAdmin = new TableRowSorter<>(modeloA);
            tbl_admin.setRowSorter(sorterAdmin);

            TableColumn idColumna = tbl_admin.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_admin.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_admin.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarID_Recep() {
        String consulta;

        if (txt_recep_id.getText().equals("0") || txt_recep_id.getText().equals("")) {
            consulta = "SELECT * FROM `recepcionista`";
        } else {
            consulta = "SELECT * FROM `recepcionista` WHERE id = '" + txt_recep_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                Object[] fila = {id, nombre, direccion, telefono};
                modeloA.addRow(fila);
            }

            tbl_recep.setModel(modeloA);

            sorterRecep = new TableRowSorter<>(modeloA);
            tbl_recep.setRowSorter(sorterRecep);

            TableColumn idColumna = tbl_recep.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_recep.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_recep.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarID_Lab() {
        String consulta;

        if (txt_lab_id.getText().equals("0") || txt_lab_id.getText().equals("")) {
            consulta = "SELECT * FROM `laboratorista`";
        } else {
            consulta = "SELECT * FROM `laboratorista` WHERE id = '" + txt_lab_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");
            modeloA.addColumn("Puesto");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                String puesto = resultado.getString("puesto");
                Object[] fila = {id, nombre, direccion, telefono, puesto};
                modeloA.addRow(fila);
            }

            tbl_lab.setModel(modeloA);

            sorterLab = new TableRowSorter<>(modeloA);
            tbl_lab.setRowSorter(sorterLab);

            TableColumn idColumna = tbl_lab.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_lab.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_lab.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarID_Citas() {
        String consulta;

        if (txt_cita_id.getText().equals("0") || txt_cita_id.getText().equals("")) {
            consulta = "SELECT * FROM `citas`";
        } else {
            consulta = "SELECT * FROM `citas` WHERE id = '" + txt_cita_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");
            modeloA.addColumn("Puesto");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                String puesto = resultado.getString("puesto");
                Object[] fila = {id, nombre, direccion, telefono, puesto};
                modeloA.addRow(fila);
            }

            tbl_admin.setModel(modeloA);

            sorterAdmin = new TableRowSorter<>(modeloA);
            tbl_admin.setRowSorter(sorterAdmin);

            TableColumn idColumna = tbl_admin.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_admin.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_admin.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarID_Quimico() {
        String consulta;

        if (txt_quimico_id.getText().equals("0") || txt_quimico_id.getText().equals("")) {
            consulta = "SELECT * FROM `quimico`";
        } else {
            consulta = "SELECT * FROM `quimico` WHERE id = '" + txt_quimico_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Dirección");
            modeloA.addColumn("Teléfono");
            modeloA.addColumn("Puesto");
            modeloA.addColumn("Contraseña");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String direccion = resultado.getString("direccion");
                String telefono = resultado.getString("telefono");
                String puesto = resultado.getString("puesto");
                String pass = resultado.getString("password");
                Object[] fila = {id, nombre, direccion, telefono, puesto, pass};
                modeloA.addRow(fila);
            }

            tbl_quimico.setModel(modeloA);

            sorterQuimico = new TableRowSorter<>(modeloA);
            tbl_quimico.setRowSorter(sorterQuimico);

            TableColumn idColumna = tbl_quimico.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            TableColumn direccionColumna = tbl_quimico.getColumnModel().getColumn(2);
            direccionColumna.setPreferredWidth(200); // Ajustar el ancho de la columna "Dirección" a 200 píxeles

            idColumna = tbl_quimico.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(30);
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);
            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarID_Cita() {
        String consulta;

        if (txt_cita_id.getText().equals("0") || txt_cita_id.getText().equals("")) {
            consulta = "SELECT * FROM `citas`";
        } else {
            consulta = "SELECT * FROM `citas` WHERE id = '" + txt_cita_id.getText() + "'";
        }

        try {
            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(consulta);

            DefaultTableModel modeloA = new DefaultTableModel();
            modeloA.addColumn("ID");
            modeloA.addColumn("Clave");
            modeloA.addColumn("Nombre");
            modeloA.addColumn("Fecha");
            modeloA.addColumn("Hora");
            modeloA.addColumn("Telefono");
            modeloA.addColumn("Resultados");
            modeloA.addColumn("Estudios");
            modeloA.addColumn("Estatus");

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombre = resultado.getString("nombre");
                String fecha = resultado.getString("fecha");
                String hora = resultado.getString("hora");
                String telefono = resultado.getString("telefono");
                String clave = resultado.getString("clave");
                String resultados = resultado.getString("resultados");
                String estudio_id = resultado.getString("estudio_id");
                String estatus = resultado.getString("estatus");

                String estudio = "";
                Statement stEst = (Statement) conect.createStatement();
                ResultSet resultadoEstudio = stEst.executeQuery("SELECT estudio FROM `estudio` WHERE id = " + estudio_id);

                if (resultadoEstudio.next()) {
                    estudio = resultadoEstudio.getString("estudio");
                } else {
                    estudio = "S/E";
                }
                Object[] fila = {id, clave, nombre, fecha, hora, telefono, resultados, estudio, estatus};
                modeloA.addRow(fila);
            }

            tbl_citas.setModel(modeloA);

            sorterCitas = new TableRowSorter<>(modeloA);
            tbl_citas.setRowSorter(sorterCitas);

            TableColumn idColumna = tbl_citas.getColumnModel().getColumn(0);
            idColumna.setPreferredWidth(20);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(1);
            idColumna.setPreferredWidth(55);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(2);
            idColumna.setPreferredWidth(100);

            idColumna = tbl_citas.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(50);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(4);
            idColumna.setPreferredWidth(50);
            idColumna.setCellRenderer(centerRenderer);

            idColumna = tbl_citas.getColumnModel().getColumn(3);
            idColumna.setPreferredWidth(60);
            idColumna.setCellRenderer(centerRenderer);
            // Cerrar los recursos
            resultado.close();
            st.close();
            conect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////
    // CRUD DE ADMIN
    public void insertAdmin() {
        // Obtenemos los valores de los campos de texto
        String nombre = txt_admin_nombre.getText().trim();
        String direccion = txt_admin_direccion.getText().trim();
        String contrasena = txt_admin_pass.getText().trim();
        String telefono = txt_admin_telefono.getText().trim();

        // Verificamos que todos los campos estén llenados
        if (nombre.isEmpty() || direccion.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txt_admin_pass.getText().length() > 8) {
            JOptionPane.showMessageDialog(null, "La longitud de la contraseña no debe ser mayor a 8 caracteres.", "Error de longitid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String insert = "INSERT INTO `administrador` (`nombre`, `direccion`, `contrasena`, `telefono`) "
                    + "VALUES (?, ?, ?, ?)";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(insert);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, contrasena);
            statement.setString(4, telefono);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showInternalMessageDialog(null, "¡Se ha agregado un Administrador con éxito!", "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateAdmin() {
        // Obtenemos los valores de los campos de texto
        String id = txt_admin_id.getText().trim();
        String nombre = txt_admin_nombre.getText().trim();
        String direccion = txt_admin_direccion.getText().trim();
        String telefono = txt_admin_telefono.getText().trim();

        // Verificamos que todos los campos estén llenos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String update = "UPDATE `administrador` SET `nombre`=?, `direccion`=?, `telefono`=? WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(update);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, id); // Aquí se establece el id del registro que se quiere actualizar

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha actualizado el Administrador con éxito!", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la actualización
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteAdmin() {
        try {
            String id = txt_admin_id.getText();
            System.out.println("ID: " + id);

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, llene todos el campo de ID.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String delete = "DELETE FROM `administrador` WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(delete);

            // Configuramos el parámetro para el PreparedStatement
            statement.setString(1, id); // Aquí se establece el id del registro que se quiere eliminar

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha eliminado el Administrador con éxito!", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la eliminación
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /////////////////////
    //CRUD RECEPCIONISTA
    public void insertRecep() {
        // Obtenemos los valores de los campos de texto
        String nombre = txt_recep_nombre.getText().trim();
        String direccion = txt_recep_direccion.getText().trim();
        String contrasena = txt_recep_pass.getText().trim();
        String telefono = txt_recep_telefono.getText().trim();

        // Verificamos que todos los campos estén llenados
        if (nombre.isEmpty() || direccion.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txt_recep_pass.getText().length() > 8) {
            JOptionPane.showMessageDialog(null, "La longitud de la contraseña no debe ser mayor a 8 caracteres.", "Error de longitid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String insert = "INSERT INTO `recepcionista` (`nombre`, `Direccion`, `Telefono`, `password`) "
                    + "VALUES (?, ?, ?, ?)";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(insert);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, contrasena);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showInternalMessageDialog(null, "¡Se ha agregado un Recepcionista con éxito!", "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateRecep() {
        // Obtenemos los valores de los campos de texto
        String id = txt_recep_id.getText().trim();
        String nombre = txt_recep_nombre.getText().trim();
        String direccion = txt_recep_direccion.getText().trim();
        String telefono = txt_recep_telefono.getText().trim();

        // Verificamos que todos los campos estén llenos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String update = "UPDATE `recepcionista` SET `nombre`=?, `Direccion`=?, `Telefono`=? WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(update);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, id); // Aquí se establece el id del registro que se quiere actualizar

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha actualizado el Recepcionista"
                        + ""
                        + " con éxito!", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la actualización
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteRecep() {
        try {
            String id = txt_recep_id.getText();
            System.out.println("ID: " + id);

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, llene todos el campo de ID.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String delete = "DELETE FROM `recepcionista` WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(delete);

            // Configuramos el parámetro para el PreparedStatement
            statement.setString(1, id); // Aquí se establece el id del registro que se quiere eliminar

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha eliminado el Recepcionista con éxito!", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la eliminación
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /////////////////////
    //CRUD LABORATORISTA

    public void insertLab() {
        // Obtenemos los valores de los campos de texto
        String nombre = txt_lab_nombre.getText().trim();
        String direccion = txt_lab_direccion.getText().trim();
        String contrasena = txt_lab_pass.getText().trim();
        String telefono = txt_lab_telefono.getText().trim();
        String puesto = txt_lab_puesto.getText().trim();

        // Verificamos que todos los campos estén llenados
        if (nombre.isEmpty() || direccion.isEmpty() || contrasena.isEmpty() || telefono.isEmpty() || puesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txt_lab_pass.getText().length() > 8) {
            JOptionPane.showMessageDialog(null, "La longitud de la contraseña no debe ser mayor a 8 caracteres.", "Error de longitid", JOptionPane.WARNING_MESSAGE);
            txt_lab_pass.requestFocus();
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String insert = "INSERT INTO `laboratorista` (`nombre`, `Direccion`, `Telefono`, `password`, `puesto`) "
                    + "VALUES (?, ?, ?, ?, ?)";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(insert);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, contrasena);
            statement.setString(5, puesto);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showInternalMessageDialog(null, "¡Se ha agregado un Laboratorista con éxito!", "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateLab() {
        // Obtenemos los valores de los campos de texto
        String id = txt_recep_id.getText().trim();
        String nombre = txt_lab_nombre.getText().trim();
        String direccion = txt_lab_direccion.getText().trim();
        String telefono = txt_lab_telefono.getText().trim();
        String puesto = txt_lab_puesto.getText().trim();

        // Verificamos que todos los campos estén llenos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || puesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String update = "UPDATE `laboratorista` SET `nombre`=?, `direccion`=?, `telefono`=?, `puesto`=? WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(update);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, puesto);
            statement.setString(5, id); // Aquí se establece el id del registro que se quiere actualizar

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha actualizado el Laboratorista"
                        + " con éxito!", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la actualización
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteLab() {
        try {
            String id = txt_lab_id.getText();
            System.out.println("ID: " + id);

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, llene todos el campo de ID.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String delete = "DELETE FROM `laboratorista` WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(delete);

            // Configuramos el parámetro para el PreparedStatement
            statement.setString(1, id); // Aquí se establece el id del registro que se quiere eliminar

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha eliminado el Laboratorista con éxito!", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la eliminación
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /////////////////////
    //CRUD QUIMICO
    public void insertQuim() {
        // Obtenemos los valores de los campos de texto
        String nombre = txt_quimico_nombre.getText().trim();
        String direccion = txt_quimico_direccion.getText().trim();
        String contrasena = txt_quimico_pass.getText().trim();
        String telefono = txt_quimico_telefono.getText().trim();
        String puesto = txt_quimico_puesto.getText().trim();

        // Verificamos que todos los campos estén llenados
        if (nombre.isEmpty() || direccion.isEmpty() || contrasena.isEmpty() || telefono.isEmpty() || puesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txt_quimico_pass.getText().length() > 8) {
            JOptionPane.showMessageDialog(null, "La longitud de la contraseña no debe ser mayor a 8 caracteres.", "Error de longitid", JOptionPane.WARNING_MESSAGE);
            txt_quimico_pass.requestFocus();
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String insert = "INSERT INTO `quimico` (`nombre`, `direccion`, `telefono`, `password`, `puesto`) "
                    + "VALUES (?, ?, ?, ?, ?)";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(insert);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, contrasena);
            statement.setString(5, puesto);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showInternalMessageDialog(null, "¡Se ha agregado un Quimico con éxito!", "Inserción Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateQuim() {
        // Obtenemos los valores de los campos de texto
        String id = txt_quimico_id.getText().trim();
        String nombre = txt_quimico_nombre.getText().trim();
        String direccion = txt_quimico_direccion.getText().trim();
        String telefono = txt_quimico_telefono.getText().trim();
        String puesto = txt_quimico_puesto.getText().trim();

        // Verificamos que todos los campos estén llenos
        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || puesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Otras validaciones específicas pueden ser agregadas aquí según tus necesidades.
        // Por ejemplo, puedes verificar que el teléfono sea numérico, que la contraseña cumpla con ciertos criterios de seguridad, etc.
        try {
            String update = "UPDATE `quimico` SET `nombre`=?, `direccion`=?, `telefono`=?, `puesto`=? WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(update);

            // Configuramos los parámetros para el PreparedStatement
            statement.setString(1, nombre);
            statement.setString(2, direccion);
            statement.setString(3, telefono);
            statement.setString(4, puesto);
            statement.setString(5, id); // Aquí se establece el id del registro que se quiere actualizar

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha actualizado el Quimico"
                        + " con éxito!", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la actualización
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteQuim() {
        try {
            String id = txt_lab_id.getText();
            System.out.println("ID: " + id);

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, llene todos el campo de ID.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String delete = "DELETE FROM `quimico` WHERE `id`=?";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            PreparedStatement statement = (PreparedStatement) conect.prepareStatement(delete);

            // Configuramos el parámetro para el PreparedStatement
            statement.setString(1, id); // Aquí se establece el id del registro que se quiere eliminar

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "¡Se ha eliminado el Quimico con éxito!", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                llenarTablaAdmin(); // Opcional: Si deseas refrescar la tabla después de la eliminación
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro con ID: " + id, "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /////////////////////
    public void llenarCmbEstudio() {
        try {
            String sql = "SELECT estudio FROM estudio";

            Conectar cc = new Conectar();
            Connection conect = cc.conexion();
            Statement st = (Statement) conect.createStatement();
            ResultSet resultado = st.executeQuery(sql);

            ArrayList<String> data = new ArrayList<>();
            while (resultado.next()) {
                data.add(resultado.getString("estudio"));
            }
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(data.toArray(new String[0]));
            cmb_estudios.setModel(model);

            resultado.close();
            st.close();
            conect.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        pnl_cuerpo = new javax.swing.JPanel();
        pnl_header = new javax.swing.JPanel();
        lbl_titulo = new javax.swing.JLabel();
        lbl_icon = new javax.swing.JLabel();
        lbl_usuario = new javax.swing.JLabel();
        pnl_menu_izq = new javax.swing.JPanel();
        btn_close_sesion = new javax.swing.JButton();
        btn_recep = new javax.swing.JButton();
        btn_admin = new javax.swing.JButton();
        btn_lab = new javax.swing.JButton();
        btn_citas = new javax.swing.JButton();
        btn_estudios = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lbl_titulo_usuarios = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbl_titulo_tablas = new javax.swing.JLabel();
        btn_quimico = new javax.swing.JButton();
        btn_area = new javax.swing.JButton();
        pnl_admis = new javax.swing.JPanel();
        Contenedor = new javax.swing.JTabbedPane();
        pnl_admin = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_admin = new javax.swing.JTable();
        txt_admin_buscar_todo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_admin_nombre = new javax.swing.JTextField();
        txt_admin_direccion = new javax.swing.JTextField();
        txt_admin_telefono = new javax.swing.JTextField();
        btn_admin_crear = new javax.swing.JButton();
        btn_admin_actualizar = new javax.swing.JButton();
        btn_admin_borrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txt_admin_id = new javax.swing.JTextField();
        btn_admin_buscar_id = new javax.swing.JButton();
        txt_admin_pass = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_limpiar_campos_Admin = new javax.swing.JButton();
        lbl_titulo_admin = new javax.swing.JLabel();
        pnl_recep = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_recep = new javax.swing.JTable();
        txtBuscarRecep = new javax.swing.JTextField();
        lbl_titulo_admin1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_recep_nombre = new javax.swing.JTextField();
        txt_recep_direccion = new javax.swing.JTextField();
        txt_recep_telefono = new javax.swing.JTextField();
        btn_recep_crear = new javax.swing.JButton();
        btn_recep_actualizar = new javax.swing.JButton();
        btn_recep_borrar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txt_recep_id = new javax.swing.JTextField();
        btn_recep_buscar_id = new javax.swing.JButton();
        txt_recep_pass = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btn_limpiar_campos_recep = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        pnl_recep1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tbl_lab = new javax.swing.JTable();
        txtBuscarLab = new javax.swing.JTextField();
        lbl_titulo_admin2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_lab_nombre = new javax.swing.JTextField();
        txt_lab_direccion = new javax.swing.JTextField();
        txt_lab_telefono = new javax.swing.JTextField();
        btn_lab_crear = new javax.swing.JButton();
        btn_lab_actualizar = new javax.swing.JButton();
        btn_lab_borrar = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        txt_lab_id = new javax.swing.JTextField();
        btn_lab_buscar_id = new javax.swing.JButton();
        txt_lab_pass = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        btn_limpiar_campos_Lab = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        txt_lab_puesto = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        pnl_recep2 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tbl_citas = new javax.swing.JTable();
        txtBuscarCita = new javax.swing.JTextField();
        lbl_titulo_admin3 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txt_cita_nombre = new javax.swing.JTextField();
        txt_cita_direccion = new javax.swing.JTextField();
        btn_cita_crear = new javax.swing.JButton();
        btn_cita_actualizar = new javax.swing.JButton();
        btn_cita_borrar = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        txt_cita_id = new javax.swing.JTextField();
        btn_cita_buscar_id = new javax.swing.JButton();
        txt_cita_pass = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        btn_limpiar_campos_cita = new javax.swing.JButton();
        cmb_estudios = new javax.swing.JComboBox<>();
        jLabel50 = new javax.swing.JLabel();
        DateChooser_cita = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        pnl_recep3 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tbl_estudios = new javax.swing.JTable();
        txtBuscarEstudios = new javax.swing.JTextField();
        lbl_titulo_admin4 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txt_estudio_nombre = new javax.swing.JTextField();
        txt_estudio_direccion = new javax.swing.JTextField();
        txt_estudio_telefono = new javax.swing.JTextField();
        btn_estudio_crear = new javax.swing.JButton();
        btn_estudio_actualizar = new javax.swing.JButton();
        btn_estudio_borrar = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        txt_estudio_id = new javax.swing.JTextField();
        btn_estudio_buscar_id = new javax.swing.JButton();
        txt_estudio_pass = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        btn_limpiar_campos_estudios = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        pnl_recep4 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tbl_areas = new javax.swing.JTable();
        txtBuscarAreas = new javax.swing.JTextField();
        lbl_titulo_admin5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txt_area_nombre = new javax.swing.JTextField();
        txt_area_direccion = new javax.swing.JTextField();
        txt_area_telefono = new javax.swing.JTextField();
        btn_area_crear = new javax.swing.JButton();
        btn_area_actualizar = new javax.swing.JButton();
        btn_area_borrar = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        txt_area_id = new javax.swing.JTextField();
        btn_area_buscar_id = new javax.swing.JButton();
        txt_area_pass = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        btn_limpiar_campos_areas = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        pnl_recep5 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tbl_quimico = new javax.swing.JTable();
        txtBuscarQuimicos = new javax.swing.JTextField();
        lbl_titulo_admin6 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txt_quimico_nombre = new javax.swing.JTextField();
        txt_quimico_direccion = new javax.swing.JTextField();
        txt_quimico_telefono = new javax.swing.JTextField();
        btn_quimico_crear = new javax.swing.JButton();
        btn_quimico_actualizar = new javax.swing.JButton();
        btn_quimico_borrar = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        txt_quimico_id = new javax.swing.JTextField();
        btn_quimico_buscar_id = new javax.swing.JButton();
        txt_quimico_pass = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        btn_limpiar_campos_quimico = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        txt_quimico_puesto = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnl_cuerpo.setBackground(new java.awt.Color(255, 255, 255));

        pnl_header.setBackground(new java.awt.Color(0, 102, 255));

        lbl_titulo.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lbl_titulo.setForeground(new java.awt.Color(255, 255, 255));
        lbl_titulo.setText("Administración");

        lbl_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/user_admin.png"))); // NOI18N

        lbl_usuario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_usuario.setForeground(new java.awt.Color(255, 255, 255));
        lbl_usuario.setText("Usuario");

        javax.swing.GroupLayout pnl_headerLayout = new javax.swing.GroupLayout(pnl_header);
        pnl_header.setLayout(pnl_headerLayout);
        pnl_headerLayout.setHorizontalGroup(
            pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_headerLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbl_titulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_icon, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnl_headerLayout.setVerticalGroup(
            pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_headerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_headerLayout.createSequentialGroup()
                        .addComponent(lbl_icon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnl_menu_izq.setBackground(new java.awt.Color(234, 234, 234));
        pnl_menu_izq.setToolTipText("");

        btn_close_sesion.setBackground(new java.awt.Color(255, 51, 51));
        btn_close_sesion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_close_sesion.setForeground(new java.awt.Color(255, 255, 255));
        btn_close_sesion.setText("Volver al Inico");
        btn_close_sesion.setBorder(null);
        btn_close_sesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_close_sesionActionPerformed(evt);
            }
        });

        btn_recep.setBackground(new java.awt.Color(204, 204, 204));
        btn_recep.setText("Recepcionistas");
        btn_recep.setBorder(null);
        btn_recep.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_recep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recepActionPerformed(evt);
            }
        });

        btn_admin.setBackground(new java.awt.Color(204, 204, 204));
        btn_admin.setText("Administradores");
        btn_admin.setBorder(null);
        btn_admin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_adminActionPerformed(evt);
            }
        });

        btn_lab.setBackground(new java.awt.Color(204, 204, 204));
        btn_lab.setText("Laboratoristas");
        btn_lab.setBorder(null);
        btn_lab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_lab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_labActionPerformed(evt);
            }
        });

        btn_citas.setBackground(new java.awt.Color(204, 204, 204));
        btn_citas.setText("Citas");
        btn_citas.setBorder(null);
        btn_citas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_citas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_citasActionPerformed(evt);
            }
        });

        btn_estudios.setBackground(new java.awt.Color(204, 204, 204));
        btn_estudios.setText("Estudios");
        btn_estudios.setBorder(null);
        btn_estudios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_estudios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_estudiosActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 102, 255));

        lbl_titulo_usuarios.setBackground(new java.awt.Color(0, 102, 255));
        lbl_titulo_usuarios.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbl_titulo_usuarios.setForeground(new java.awt.Color(255, 255, 255));
        lbl_titulo_usuarios.setText("Usuarios");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_usuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_titulo_usuarios)
                .addGap(39, 39, 39))
        );

        jPanel3.setBackground(new java.awt.Color(0, 102, 255));

        lbl_titulo_tablas.setBackground(new java.awt.Color(0, 102, 255));
        lbl_titulo_tablas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbl_titulo_tablas.setForeground(new java.awt.Color(255, 255, 255));
        lbl_titulo_tablas.setText("Gestion");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_tablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_titulo_tablas)
                .addGap(39, 39, 39))
        );

        btn_quimico.setBackground(new java.awt.Color(204, 204, 204));
        btn_quimico.setText("Quimico");
        btn_quimico.setBorder(null);
        btn_quimico.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_quimico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quimicoActionPerformed(evt);
            }
        });

        btn_area.setBackground(new java.awt.Color(204, 204, 204));
        btn_area.setText("Areas");
        btn_area.setBorder(null);
        btn_area.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_area.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_areaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_menu_izqLayout = new javax.swing.GroupLayout(pnl_menu_izq);
        pnl_menu_izq.setLayout(pnl_menu_izqLayout);
        pnl_menu_izqLayout.setHorizontalGroup(
            pnl_menu_izqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn_admin, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
            .addComponent(btn_recep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_lab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_citas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_estudios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_close_sesion, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
            .addComponent(btn_quimico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_area, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnl_menu_izqLayout.setVerticalGroup(
            pnl_menu_izqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_menu_izqLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btn_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_recep, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_lab, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btn_quimico, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_citas, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_estudios, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_area, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_close_sesion, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnl_admis.setBackground(new java.awt.Color(255, 255, 255));

        pnl_admin.setBackground(new java.awt.Color(255, 255, 255));

        tbl_admin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_admin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_adminMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_admin);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Buscar");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Direccion");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Nombre");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Telefono");

        btn_admin_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_admin_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_admin_crear.setToolTipText("Agregar");
        btn_admin_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_admin_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_admin_crearActionPerformed(evt);
            }
        });

        btn_admin_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_admin_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_admin_actualizar.setToolTipText("Editar");
        btn_admin_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_admin_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_admin_actualizarActionPerformed(evt);
            }
        });

        btn_admin_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_admin_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_admin_borrar.setToolTipText("Eliminar");
        btn_admin_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_admin_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_admin_borrarActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("ID");

        txt_admin_id.setToolTipText("ID");
        txt_admin_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_admin_idKeyReleased(evt);
            }
        });

        btn_admin_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_admin_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_admin_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_admin_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_admin_buscar_idActionPerformed(evt);
            }
        });

        txt_admin_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_admin_passKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Contraseña");

        btn_limpiar_campos_Admin.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_Admin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_Admin.setToolTipText("Limpiar campos");
        btn_limpiar_campos_Admin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_Admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_AdminActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_admin_nombre)
                    .addComponent(txt_admin_direccion)
                    .addComponent(txt_admin_telefono)
                    .addComponent(txt_admin_pass)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txt_admin_id, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_admin_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btn_admin_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_admin_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_admin_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_Admin, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_admin_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_admin_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_admin_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_admin_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_admin_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_admin_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_admin_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_admin_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_admin_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_Admin, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        lbl_titulo_admin.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin.setText("Administradores");

        javax.swing.GroupLayout pnl_adminLayout = new javax.swing.GroupLayout(pnl_admin);
        pnl_admin.setLayout(pnl_adminLayout);
        pnl_adminLayout.setHorizontalGroup(
            pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_adminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_titulo_admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_adminLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addGroup(pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnl_adminLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_admin_buscar_todo, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        pnl_adminLayout.setVerticalGroup(
            pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_adminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_adminLayout.createSequentialGroup()
                        .addGroup(pnl_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_admin_buscar_todo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        Contenedor.addTab("Administradores", pnl_admin);

        pnl_recep.setBackground(new java.awt.Color(255, 255, 255));

        tbl_recep.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_recep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_recepMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_recep);

        lbl_titulo_admin1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin1.setText("Recepcionistas");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Direccion");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Nombre");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Telefono");

        txt_recep_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_recep_nombreKeyTyped(evt);
            }
        });

        txt_recep_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_recep_direccionKeyTyped(evt);
            }
        });

        txt_recep_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_recep_telefonoKeyTyped(evt);
            }
        });

        btn_recep_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_recep_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_recep_crear.setToolTipText("Agregar");
        btn_recep_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_recep_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recep_crearActionPerformed(evt);
            }
        });

        btn_recep_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_recep_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_recep_actualizar.setToolTipText("Editar");
        btn_recep_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_recep_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recep_actualizarActionPerformed(evt);
            }
        });

        btn_recep_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_recep_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_recep_borrar.setToolTipText("Eliminar");
        btn_recep_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_recep_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recep_borrarActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("ID");

        txt_recep_id.setToolTipText("ID");
        txt_recep_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_recep_idKeyReleased(evt);
            }
        });

        btn_recep_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_recep_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_recep_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_recep_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recep_buscar_idActionPerformed(evt);
            }
        });

        txt_recep_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_recep_passKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Contraseña");

        btn_limpiar_campos_recep.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_recep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_recep.setToolTipText("Limpiar campos");
        btn_limpiar_campos_recep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_recep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_recepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_recep_nombre)
                    .addComponent(txt_recep_direccion)
                    .addComponent(txt_recep_telefono)
                    .addComponent(txt_recep_pass)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txt_recep_id, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_recep_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btn_recep_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_recep_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_recep_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_recep, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_recep_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_recep_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_recep_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_recep_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_recep_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_recep_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_recep_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_recep_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_recep_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_recep, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Buscar");

        javax.swing.GroupLayout pnl_recepLayout = new javax.swing.GroupLayout(pnl_recep);
        pnl_recep.setLayout(pnl_recepLayout);
        pnl_recepLayout.setHorizontalGroup(
            pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recepLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recepLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recepLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarRecep, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recepLayout.setVerticalGroup(
            pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recepLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin1, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recepLayout.createSequentialGroup()
                        .addGroup(pnl_recepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarRecep, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Recepcionistas", pnl_recep);

        pnl_recep1.setBackground(new java.awt.Color(255, 255, 255));

        tbl_lab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_lab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_labMouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(tbl_lab);

        lbl_titulo_admin2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin2.setText("Laboratorista");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Direccion");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setText("Nombre");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setText("Telefono");

        txt_lab_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lab_nombreKeyTyped(evt);
            }
        });

        txt_lab_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lab_direccionKeyTyped(evt);
            }
        });

        txt_lab_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lab_telefonoKeyTyped(evt);
            }
        });

        btn_lab_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_lab_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_lab_crear.setToolTipText("Agregar");
        btn_lab_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_lab_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lab_crearActionPerformed(evt);
            }
        });

        btn_lab_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_lab_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_lab_actualizar.setToolTipText("Editar");
        btn_lab_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_lab_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lab_actualizarActionPerformed(evt);
            }
        });

        btn_lab_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_lab_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_lab_borrar.setToolTipText("Eliminar");
        btn_lab_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_lab_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lab_borrarActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setText("ID");

        txt_lab_id.setToolTipText("ID");
        txt_lab_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_lab_idKeyReleased(evt);
            }
        });

        btn_lab_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_lab_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_lab_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_lab_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_lab_buscar_idActionPerformed(evt);
            }
        });

        txt_lab_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_lab_passKeyTyped(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel23.setText("Contraseña");

        btn_limpiar_campos_Lab.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_Lab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_Lab.setToolTipText("Limpiar campos");
        btn_limpiar_campos_Lab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_Lab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_LabActionPerformed(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel43.setText("Puesto");

        txt_lab_puesto.setToolTipText("ID");
        txt_lab_puesto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_lab_puestoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_lab_nombre)
                    .addComponent(txt_lab_direccion)
                    .addComponent(txt_lab_telefono)
                    .addComponent(txt_lab_pass)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btn_lab_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_lab_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_lab_puesto, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(txt_lab_id)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_lab_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btn_lab_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                                .addComponent(btn_limpiar_campos_Lab, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_lab_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_lab_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_lab_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_lab_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btn_lab_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btn_lab_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_lab_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_lab_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_limpiar_campos_Lab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_lab_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_lab_puesto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setText("Buscar");

        javax.swing.GroupLayout pnl_recep1Layout = new javax.swing.GroupLayout(pnl_recep1);
        pnl_recep1.setLayout(pnl_recep1Layout);
        pnl_recep1Layout.setHorizontalGroup(
            pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recep1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recep1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep1Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarLab, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recep1Layout.setVerticalGroup(
            pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin2, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recep1Layout.createSequentialGroup()
                        .addGroup(pnl_recep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarLab, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Laboratorista", pnl_recep1);

        pnl_recep2.setBackground(new java.awt.Color(255, 255, 255));

        tbl_citas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_citas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_citasMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(tbl_citas);

        lbl_titulo_admin3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin3.setText("Citas");

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setText("Telefono");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setText("Nombre");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setText("Estudio");

        txt_cita_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_cita_nombreKeyTyped(evt);
            }
        });

        txt_cita_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_cita_direccionKeyTyped(evt);
            }
        });

        btn_cita_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_cita_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_cita_crear.setToolTipText("Agregar");
        btn_cita_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_cita_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cita_crearActionPerformed(evt);
            }
        });

        btn_cita_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_cita_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_cita_actualizar.setToolTipText("Editar");
        btn_cita_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_cita_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cita_actualizarActionPerformed(evt);
            }
        });

        btn_cita_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_cita_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_cita_borrar.setToolTipText("Eliminar");
        btn_cita_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_cita_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cita_borrarActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel28.setText("ID");

        txt_cita_id.setToolTipText("ID");
        txt_cita_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_cita_idKeyReleased(evt);
            }
        });

        btn_cita_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_cita_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_cita_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_cita_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cita_buscar_idActionPerformed(evt);
            }
        });

        txt_cita_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_cita_passKeyTyped(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel29.setText("Estudio");

        btn_limpiar_campos_cita.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_cita.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_cita.setToolTipText("Limpiar campos");
        btn_limpiar_campos_cita.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_cita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_citaActionPerformed(evt);
            }
        });

        cmb_estudios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_estudiosActionPerformed(evt);
            }
        });

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel50.setText("Fecha");

        DateChooser_cita.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DateChooser_citaPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_cita_nombre)
                    .addComponent(txt_cita_direccion)
                    .addComponent(txt_cita_pass)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(btn_cita_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_cita_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_cita_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_cita, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(txt_cita_id, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_cita_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DateChooser_cita, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_estudios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_cita_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_cita_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmb_estudios, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(DateChooser_cita, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_cita_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_cita_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_cita_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_cita_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_cita_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_cita_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_cita, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel30.setText("Buscar");

        javax.swing.GroupLayout pnl_recep2Layout = new javax.swing.GroupLayout(pnl_recep2);
        pnl_recep2.setLayout(pnl_recep2Layout);
        pnl_recep2Layout.setHorizontalGroup(
            pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recep2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recep2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep2Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarCita, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recep2Layout.setVerticalGroup(
            pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin3, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recep2Layout.createSequentialGroup()
                        .addGroup(pnl_recep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarCita, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Citas", pnl_recep2);

        pnl_recep3.setBackground(new java.awt.Color(255, 255, 255));

        tbl_estudios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_estudios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_estudiosMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(tbl_estudios);

        lbl_titulo_admin4.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin4.setText("Estudios");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel31.setText("Direccion");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setText("Nombre");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setText("Telefono");

        txt_estudio_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_estudio_nombreKeyTyped(evt);
            }
        });

        txt_estudio_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_estudio_direccionKeyTyped(evt);
            }
        });

        txt_estudio_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_estudio_telefonoKeyTyped(evt);
            }
        });

        btn_estudio_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_estudio_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_estudio_crear.setToolTipText("Agregar");
        btn_estudio_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_estudio_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_estudio_crearActionPerformed(evt);
            }
        });

        btn_estudio_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_estudio_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_estudio_actualizar.setToolTipText("Editar");
        btn_estudio_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_estudio_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_estudio_actualizarActionPerformed(evt);
            }
        });

        btn_estudio_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_estudio_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_estudio_borrar.setToolTipText("Eliminar");
        btn_estudio_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_estudio_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_estudio_borrarActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel34.setText("ID");

        txt_estudio_id.setToolTipText("ID");
        txt_estudio_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_estudio_idKeyReleased(evt);
            }
        });

        btn_estudio_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_estudio_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_estudio_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_estudio_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_estudio_buscar_idActionPerformed(evt);
            }
        });

        txt_estudio_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_estudio_passKeyTyped(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setText("Contraseña");

        btn_limpiar_campos_estudios.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_estudios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_estudios.setToolTipText("Limpiar campos");
        btn_limpiar_campos_estudios.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_estudios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_estudiosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_estudio_nombre)
                    .addComponent(txt_estudio_direccion)
                    .addComponent(txt_estudio_telefono)
                    .addComponent(txt_estudio_pass)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txt_estudio_id, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_estudio_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(btn_estudio_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_estudio_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_estudio_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_estudios, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_estudio_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_estudio_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_estudio_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_estudio_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_estudio_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_estudio_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_estudio_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_estudio_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_estudio_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_estudios, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setText("Buscar");

        javax.swing.GroupLayout pnl_recep3Layout = new javax.swing.GroupLayout(pnl_recep3);
        pnl_recep3.setLayout(pnl_recep3Layout);
        pnl_recep3Layout.setHorizontalGroup(
            pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recep3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recep3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep3Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarEstudios, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recep3Layout.setVerticalGroup(
            pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin4, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recep3Layout.createSequentialGroup()
                        .addGroup(pnl_recep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarEstudios, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Estudios", pnl_recep3);

        pnl_recep4.setBackground(new java.awt.Color(255, 255, 255));

        tbl_areas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_areas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_areasMouseReleased(evt);
            }
        });
        jScrollPane9.setViewportView(tbl_areas);

        lbl_titulo_admin5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin5.setText("Areas");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel37.setText("Direccion");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel38.setText("Nombre");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel39.setText("Telefono");

        txt_area_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_area_nombreKeyTyped(evt);
            }
        });

        txt_area_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_area_direccionKeyTyped(evt);
            }
        });

        txt_area_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_area_telefonoKeyTyped(evt);
            }
        });

        btn_area_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_area_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_area_crear.setToolTipText("Agregar");
        btn_area_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_area_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_area_crearActionPerformed(evt);
            }
        });

        btn_area_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_area_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_area_actualizar.setToolTipText("Editar");
        btn_area_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_area_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_area_actualizarActionPerformed(evt);
            }
        });

        btn_area_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_area_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_area_borrar.setToolTipText("Eliminar");
        btn_area_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_area_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_area_borrarActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel40.setText("ID");

        txt_area_id.setToolTipText("ID");
        txt_area_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_area_idKeyReleased(evt);
            }
        });

        btn_area_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_area_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_area_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_area_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_area_buscar_idActionPerformed(evt);
            }
        });

        txt_area_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_area_passKeyTyped(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel41.setText("Contraseña");

        btn_limpiar_campos_areas.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_areas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_areas.setToolTipText("Limpiar campos");
        btn_limpiar_campos_areas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_areas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_areasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_area_nombre)
                    .addComponent(txt_area_direccion)
                    .addComponent(txt_area_telefono)
                    .addComponent(txt_area_pass)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(txt_area_id, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_area_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btn_area_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_area_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_area_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_areas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_area_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_area_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_area_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_area_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_area_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_area_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_area_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_area_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_area_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_limpiar_campos_areas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel42.setText("Buscar");

        javax.swing.GroupLayout pnl_recep4Layout = new javax.swing.GroupLayout(pnl_recep4);
        pnl_recep4.setLayout(pnl_recep4Layout);
        pnl_recep4Layout.setHorizontalGroup(
            pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recep4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recep4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep4Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarAreas, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recep4Layout.setVerticalGroup(
            pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin5, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recep4Layout.createSequentialGroup()
                        .addGroup(pnl_recep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarAreas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Areas", pnl_recep4);

        pnl_recep5.setBackground(new java.awt.Color(255, 255, 255));

        tbl_quimico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_quimico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbl_quimicoMouseReleased(evt);
            }
        });
        jScrollPane10.setViewportView(tbl_quimico);

        lbl_titulo_admin6.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        lbl_titulo_admin6.setText("Quimico");

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Direccion");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel44.setText("Nombre");

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel45.setText("Telefono");

        txt_quimico_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_quimico_nombreKeyTyped(evt);
            }
        });

        txt_quimico_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_quimico_direccionKeyTyped(evt);
            }
        });

        txt_quimico_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_quimico_telefonoKeyTyped(evt);
            }
        });

        btn_quimico_crear.setBackground(new java.awt.Color(255, 255, 255));
        btn_quimico_crear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add-48.png"))); // NOI18N
        btn_quimico_crear.setToolTipText("Agregar");
        btn_quimico_crear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_quimico_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quimico_crearActionPerformed(evt);
            }
        });

        btn_quimico_actualizar.setBackground(new java.awt.Color(255, 255, 255));
        btn_quimico_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/update-48.png"))); // NOI18N
        btn_quimico_actualizar.setToolTipText("Editar");
        btn_quimico_actualizar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_quimico_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quimico_actualizarActionPerformed(evt);
            }
        });

        btn_quimico_borrar.setBackground(new java.awt.Color(255, 255, 255));
        btn_quimico_borrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/borrar-48.png"))); // NOI18N
        btn_quimico_borrar.setToolTipText("Eliminar");
        btn_quimico_borrar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_quimico_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quimico_borrarActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel46.setText("ID");

        txt_quimico_id.setToolTipText("ID");
        txt_quimico_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_quimico_idKeyReleased(evt);
            }
        });

        btn_quimico_buscar_id.setBackground(new java.awt.Color(255, 255, 255));
        btn_quimico_buscar_id.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btn_quimico_buscar_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/lupa-48.png"))); // NOI18N
        btn_quimico_buscar_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_quimico_buscar_idActionPerformed(evt);
            }
        });

        txt_quimico_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_quimico_passKeyTyped(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel47.setText("Contraseña");

        btn_limpiar_campos_quimico.setBackground(new java.awt.Color(255, 255, 255));
        btn_limpiar_campos_quimico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/limpiar-campos-48.png"))); // NOI18N
        btn_limpiar_campos_quimico.setToolTipText("Limpiar campos");
        btn_limpiar_campos_quimico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        btn_limpiar_campos_quimico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_limpiar_campos_quimicoActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel48.setText("Puesto");

        txt_quimico_puesto.setToolTipText("ID");
        txt_quimico_puesto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_quimico_puestoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_quimico_nombre)
                    .addComponent(txt_quimico_direccion)
                    .addComponent(txt_quimico_telefono)
                    .addComponent(txt_quimico_pass)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(btn_quimico_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_quimico_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_quimico_puesto, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(txt_quimico_id)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_quimico_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(btn_quimico_borrar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                                .addComponent(btn_limpiar_campos_quimico, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_quimico_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_quimico_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_quimico_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_quimico_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btn_quimico_buscar_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btn_quimico_actualizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_quimico_borrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_quimico_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_limpiar_campos_quimico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_quimico_id, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_quimico_puesto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel49.setText("Buscar");

        javax.swing.GroupLayout pnl_recep5Layout = new javax.swing.GroupLayout(pnl_recep5);
        pnl_recep5.setLayout(pnl_recep5Layout);
        pnl_recep5Layout.setHorizontalGroup(
            pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_recep5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_recep5Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addGroup(pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep5Layout.createSequentialGroup()
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarQuimicos, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addComponent(lbl_titulo_admin6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pnl_recep5Layout.setVerticalGroup(
            pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_recep5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_titulo_admin6, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnl_recep5Layout.createSequentialGroup()
                        .addGroup(pnl_recep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarQuimicos, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Contenedor.addTab("Quimico", pnl_recep5);

        javax.swing.GroupLayout pnl_admisLayout = new javax.swing.GroupLayout(pnl_admis);
        pnl_admis.setLayout(pnl_admisLayout);
        pnl_admisLayout.setHorizontalGroup(
            pnl_admisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_admisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Contenedor))
        );
        pnl_admisLayout.setVerticalGroup(
            pnl_admisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Contenedor)
        );

        javax.swing.GroupLayout pnl_cuerpoLayout = new javax.swing.GroupLayout(pnl_cuerpo);
        pnl_cuerpo.setLayout(pnl_cuerpoLayout);
        pnl_cuerpoLayout.setHorizontalGroup(
            pnl_cuerpoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_cuerpoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnl_cuerpoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnl_cuerpoLayout.createSequentialGroup()
                        .addComponent(pnl_menu_izq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnl_admis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnl_cuerpoLayout.setVerticalGroup(
            pnl_cuerpoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_cuerpoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnl_cuerpoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_menu_izq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_admis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_cuerpo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_cuerpo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_close_sesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_close_sesionActionPerformed
        // TODO add your handling code here:
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_close_sesionActionPerformed

    private void btn_adminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adminActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(0);
        reiniciarEstilos();
        btn_admin.setBackground(new Color(204, 229, 255));
        llenarTablaAdmin();
    }//GEN-LAST:event_btn_adminActionPerformed

    private void btn_recepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recepActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(1);
        reiniciarEstilos();
        btn_recep.setBackground(new Color(204, 229, 255));
        llenarTablaRecepcionista();
    }//GEN-LAST:event_btn_recepActionPerformed

    private void btn_labActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_labActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(2);
        reiniciarEstilos();
        btn_lab.setBackground(new Color(204, 229, 255));
        llenarTablaLab();
    }//GEN-LAST:event_btn_labActionPerformed

    private void btn_citasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_citasActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(3);
        reiniciarEstilos();
        btn_citas.setBackground(new Color(204, 229, 255));
        llenarTablaCitas();
        llenarCmbEstudio();
    }//GEN-LAST:event_btn_citasActionPerformed

    private void btn_estudiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_estudiosActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(4);
        reiniciarEstilos();
        btn_estudios.setBackground(new Color(204, 229, 255));
        llenarTablaEstudios();
    }//GEN-LAST:event_btn_estudiosActionPerformed

    private void tbl_adminMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_adminMouseReleased
        // TODO add your handling code here:
        String id = tbl_admin.getValueAt(tbl_admin.getSelectedRow(), 0).toString();
        String nombre = tbl_admin.getValueAt(tbl_admin.getSelectedRow(), 1).toString();
        String direccion = tbl_admin.getValueAt(tbl_admin.getSelectedRow(), 2).toString();
        String telefono = tbl_admin.getValueAt(tbl_admin.getSelectedRow(), 3).toString();

        txt_admin_id.setText(id);
        txt_admin_nombre.setText(nombre);
        txt_admin_direccion.setText(direccion);
        txt_admin_telefono.setText(telefono);
        txt_admin_pass.setText("");
    }//GEN-LAST:event_tbl_adminMouseReleased

    private void btn_limpiar_campos_AdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_AdminActionPerformed
        // TODO add your handling code here:
        txt_admin_id.setText("0");
        txt_admin_nombre.setText("");
        txt_admin_direccion.setText("");
        txt_admin_telefono.setText("");
        txt_admin_pass.setText("");
    }//GEN-LAST:event_btn_limpiar_campos_AdminActionPerformed

    private void txt_admin_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_admin_passKeyTyped
        // TODO add your handling code here:
        String text = txt_admin_pass.getText().trim();

        if (text.isEmpty()) {
            btn_admin_actualizar.setEnabled(true);
        } else {
            btn_admin_actualizar.setEnabled(false);
        }
    }//GEN-LAST:event_txt_admin_passKeyTyped

    private void btn_admin_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_admin_buscar_idActionPerformed
        // TODO add your handling code here:
        buscarID_Admin();
    }//GEN-LAST:event_btn_admin_buscar_idActionPerformed

    private void txt_admin_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_admin_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_admin_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaAdmin();
        }
    }//GEN-LAST:event_txt_admin_idKeyReleased

    private void btn_admin_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_admin_borrarActionPerformed
        // TODO add your handling code here:
        deleteAdmin();
        txt_admin_id.setText("");
        txt_admin_nombre.setText("");
        txt_admin_direccion.setText("");
        txt_admin_telefono.setText("");
        txt_admin_pass.setText("");
    }//GEN-LAST:event_btn_admin_borrarActionPerformed

    private void btn_admin_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_admin_actualizarActionPerformed
        // TODO add your handling code here:
        updateAdmin();
    }//GEN-LAST:event_btn_admin_actualizarActionPerformed

    private void btn_admin_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_admin_crearActionPerformed
        // TODO add your handling code here:
        insertAdmin();
    }//GEN-LAST:event_btn_admin_crearActionPerformed

    private void txt_recep_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_recep_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_recep_nombreKeyTyped

    private void txt_recep_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_recep_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_recep_direccionKeyTyped

    private void txt_recep_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_recep_telefonoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_recep_telefonoKeyTyped

    private void btn_recep_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recep_crearActionPerformed
        // TODO add your handling code here:
        insertRecep();
    }//GEN-LAST:event_btn_recep_crearActionPerformed

    private void btn_recep_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recep_actualizarActionPerformed
        // TODO add your handling code here:
        updateRecep();
    }//GEN-LAST:event_btn_recep_actualizarActionPerformed

    private void btn_recep_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recep_borrarActionPerformed
        // TODO add your handling code here:
        deleteRecep();
    }//GEN-LAST:event_btn_recep_borrarActionPerformed

    private void txt_recep_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_recep_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_recep_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaAdmin();
        }
    }//GEN-LAST:event_txt_recep_idKeyReleased

    private void btn_recep_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recep_buscar_idActionPerformed
        // TODO add your handling code here:
        buscarID_Recep();
    }//GEN-LAST:event_btn_recep_buscar_idActionPerformed

    private void txt_recep_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_recep_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_recep_passKeyTyped

    private void btn_limpiar_campos_recepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_recepActionPerformed
        // TODO add your handling code here:
        txt_recep_id.setText("0");
        txt_recep_nombre.setText("");
        txt_recep_direccion.setText("");
        txt_recep_telefono.setText("");
        txt_recep_pass.setText("");
    }//GEN-LAST:event_btn_limpiar_campos_recepActionPerformed

    private void tbl_recepMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_recepMouseReleased
        // TODO add your handling code here:
        String id = tbl_recep.getValueAt(tbl_recep.getSelectedRow(), 0).toString();
        String nombre = tbl_recep.getValueAt(tbl_recep.getSelectedRow(), 1).toString();
        String direccion = tbl_recep.getValueAt(tbl_recep.getSelectedRow(), 2).toString();
        String telefono = tbl_recep.getValueAt(tbl_recep.getSelectedRow(), 3).toString();

        txt_recep_id.setText(id);
        txt_recep_nombre.setText(nombre);
        txt_recep_direccion.setText(direccion);
        txt_recep_telefono.setText(telefono);
    }//GEN-LAST:event_tbl_recepMouseReleased

    private void tbl_labMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_labMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_labMouseReleased

    private void txt_lab_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_lab_nombreKeyTyped

    private void txt_lab_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_lab_direccionKeyTyped

    private void txt_lab_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_telefonoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_lab_telefonoKeyTyped

    private void btn_lab_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lab_crearActionPerformed
        // TODO add your handling code here:
        insertLab();
        llenarTablaLab();
    }//GEN-LAST:event_btn_lab_crearActionPerformed

    private void btn_lab_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lab_actualizarActionPerformed
        // TODO add your handling code here:
        updateLab();
    }//GEN-LAST:event_btn_lab_actualizarActionPerformed

    private void btn_lab_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lab_borrarActionPerformed
        // TODO add your handling code here:
        deleteLab();
    }//GEN-LAST:event_btn_lab_borrarActionPerformed

    private void txt_lab_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_lab_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaLab();
        }
    }//GEN-LAST:event_txt_lab_idKeyReleased

    private void btn_lab_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_lab_buscar_idActionPerformed
        // TODO add your handling code here:
        buscarID_Lab();
    }//GEN-LAST:event_btn_lab_buscar_idActionPerformed

    private void txt_lab_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_lab_passKeyTyped

    private void btn_limpiar_campos_LabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_LabActionPerformed
        // TODO add your handling code here:
        txt_lab_id.setText("0");
        txt_lab_nombre.setText("");
        txt_lab_direccion.setText("");
        txt_lab_telefono.setText("");
        txt_lab_pass.setText("");
        txt_lab_puesto.setText("");
    }//GEN-LAST:event_btn_limpiar_campos_LabActionPerformed

    private void tbl_citasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_citasMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_citasMouseReleased

    private void txt_cita_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cita_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cita_nombreKeyTyped

    private void btn_cita_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cita_crearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cita_crearActionPerformed

    private void btn_cita_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cita_actualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cita_actualizarActionPerformed

    private void btn_cita_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cita_borrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cita_borrarActionPerformed

    private void txt_cita_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cita_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_cita_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaCitas();
        }
    }//GEN-LAST:event_txt_cita_idKeyReleased

    private void btn_cita_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cita_buscar_idActionPerformed
        // TODO add your handling code here:
        buscarID_Cita();
    }//GEN-LAST:event_btn_cita_buscar_idActionPerformed

    private void txt_cita_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cita_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cita_passKeyTyped

    private void btn_limpiar_campos_citaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_citaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_limpiar_campos_citaActionPerformed

    private void tbl_estudiosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_estudiosMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_estudiosMouseReleased

    private void txt_estudio_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estudio_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_estudio_nombreKeyTyped

    private void txt_estudio_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estudio_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_estudio_direccionKeyTyped

    private void txt_estudio_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estudio_telefonoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_estudio_telefonoKeyTyped

    private void btn_estudio_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_estudio_crearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_estudio_crearActionPerformed

    private void btn_estudio_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_estudio_actualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_estudio_actualizarActionPerformed

    private void btn_estudio_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_estudio_borrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_estudio_borrarActionPerformed

    private void txt_estudio_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estudio_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_estudio_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaEstudios();
        }
    }//GEN-LAST:event_txt_estudio_idKeyReleased

    private void btn_estudio_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_estudio_buscar_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_estudio_buscar_idActionPerformed

    private void txt_estudio_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_estudio_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_estudio_passKeyTyped

    private void btn_limpiar_campos_estudiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_estudiosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_limpiar_campos_estudiosActionPerformed

    private void tbl_areasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_areasMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_areasMouseReleased

    private void txt_area_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_area_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_area_nombreKeyTyped

    private void txt_area_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_area_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_area_direccionKeyTyped

    private void txt_area_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_area_telefonoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_area_telefonoKeyTyped

    private void btn_area_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_area_crearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_area_crearActionPerformed

    private void btn_area_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_area_actualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_area_actualizarActionPerformed

    private void btn_area_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_area_borrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_area_borrarActionPerformed

    private void txt_area_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_area_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_area_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaArea();
        }
    }//GEN-LAST:event_txt_area_idKeyReleased

    private void btn_area_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_area_buscar_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_area_buscar_idActionPerformed

    private void txt_area_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_area_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_area_passKeyTyped

    private void btn_limpiar_campos_areasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_areasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_limpiar_campos_areasActionPerformed

    private void tbl_quimicoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_quimicoMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_quimicoMouseReleased

    private void txt_quimico_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_nombreKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quimico_nombreKeyTyped

    private void txt_quimico_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quimico_direccionKeyTyped

    private void txt_quimico_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_telefonoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quimico_telefonoKeyTyped

    private void btn_quimico_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quimico_crearActionPerformed
        // TODO add your handling code here:

        llenarTablaQuimico();
    }//GEN-LAST:event_btn_quimico_crearActionPerformed

    private void btn_quimico_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quimico_actualizarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_quimico_actualizarActionPerformed

    private void btn_quimico_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quimico_borrarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_quimico_borrarActionPerformed

    private void txt_quimico_idKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_idKeyReleased
        // TODO add your handling code here:
        String id_buscada = txt_quimico_id.getText().replace(" ", "");
        if (id_buscada.isEmpty()) {
            llenarTablaQuimico();
        }
    }//GEN-LAST:event_txt_quimico_idKeyReleased

    private void btn_quimico_buscar_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quimico_buscar_idActionPerformed
        // TODO add your handling code here:
        buscarID_Quimico();
    }//GEN-LAST:event_btn_quimico_buscar_idActionPerformed

    private void txt_quimico_passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_passKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quimico_passKeyTyped

    private void btn_limpiar_campos_quimicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_limpiar_campos_quimicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_limpiar_campos_quimicoActionPerformed

    private void btn_quimicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_quimicoActionPerformed
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(6);
        reiniciarEstilos();
        btn_quimico.setBackground(new Color(204, 229, 255));
        llenarTablaQuimico();
    }//GEN-LAST:event_btn_quimicoActionPerformed

    private void btn_areaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_areaActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        Contenedor.setSelectedIndex(5);
        reiniciarEstilos();
        btn_area.setBackground(new Color(204, 229, 255));
        llenarTablaArea();
    }//GEN-LAST:event_btn_areaActionPerformed

    private void txt_lab_puestoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_lab_puestoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_lab_puestoKeyReleased

    private void txt_quimico_puestoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_quimico_puestoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quimico_puestoKeyReleased

    private void txt_cita_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cita_direccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cita_direccionKeyTyped

    private void cmb_estudiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_estudiosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_estudiosActionPerformed
    private boolean warningShown = false;
    private void DateChooser_citaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DateChooser_citaPropertyChange
        // Verificar si la propiedad "date" cambió
        // Verificar si la propiedad "date" cambió
        if ("date".equals(evt.getPropertyName())) {
            Date selectedDate = (Date) evt.getNewValue();
            Date currentDate = new Date();  // Obtener la fecha actual

            if (selectedDate != null) {
                // Comparar la fecha seleccionada con la fecha actual
                if (selectedDate.before(currentDate)) {
                    // La fecha seleccionada es anterior a la fecha actual
                    if (!warningShown) {
                        // Mostrar mensaje de advertencia solo si no se ha mostrado antes
                        JOptionPane.showMessageDialog(this, "La fecha seleccionada ya ha pasado.", "Fecha Pasada", JOptionPane.WARNING_MESSAGE);
                        warningShown = true; // Establecer la bandera para indicar que se ha mostrado el mensaje
                    } else {
                        warningShown = false; // Restablecer la bandera si la fecha seleccionada es anterior pero no se muestra el mensaje
                    }
                    // Establecer la fecha actual en el JDateChooser
                    ((JDateChooser) evt.getSource()).setDate(currentDate);
                } else {
                    warningShown = false; // Restablecer la bandera si la fecha seleccionada es posterior a la fecha actual
                }
            }
        }
    }//GEN-LAST:event_DateChooser_citaPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new User_Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Contenedor;
    private com.toedter.calendar.JDateChooser DateChooser_cita;
    private javax.swing.JButton btn_admin;
    private javax.swing.JButton btn_admin_actualizar;
    private javax.swing.JButton btn_admin_borrar;
    private javax.swing.JButton btn_admin_buscar_id;
    private javax.swing.JButton btn_admin_crear;
    private javax.swing.JButton btn_area;
    private javax.swing.JButton btn_area_actualizar;
    private javax.swing.JButton btn_area_borrar;
    private javax.swing.JButton btn_area_buscar_id;
    private javax.swing.JButton btn_area_crear;
    private javax.swing.JButton btn_cita_actualizar;
    private javax.swing.JButton btn_cita_borrar;
    private javax.swing.JButton btn_cita_buscar_id;
    private javax.swing.JButton btn_cita_crear;
    private javax.swing.JButton btn_citas;
    private javax.swing.JButton btn_close_sesion;
    private javax.swing.JButton btn_estudio_actualizar;
    private javax.swing.JButton btn_estudio_borrar;
    private javax.swing.JButton btn_estudio_buscar_id;
    private javax.swing.JButton btn_estudio_crear;
    private javax.swing.JButton btn_estudios;
    private javax.swing.JButton btn_lab;
    private javax.swing.JButton btn_lab_actualizar;
    private javax.swing.JButton btn_lab_borrar;
    private javax.swing.JButton btn_lab_buscar_id;
    private javax.swing.JButton btn_lab_crear;
    private javax.swing.JButton btn_limpiar_campos_Admin;
    private javax.swing.JButton btn_limpiar_campos_Lab;
    private javax.swing.JButton btn_limpiar_campos_areas;
    private javax.swing.JButton btn_limpiar_campos_cita;
    private javax.swing.JButton btn_limpiar_campos_estudios;
    private javax.swing.JButton btn_limpiar_campos_quimico;
    private javax.swing.JButton btn_limpiar_campos_recep;
    private javax.swing.JButton btn_quimico;
    private javax.swing.JButton btn_quimico_actualizar;
    private javax.swing.JButton btn_quimico_borrar;
    private javax.swing.JButton btn_quimico_buscar_id;
    private javax.swing.JButton btn_quimico_crear;
    private javax.swing.JButton btn_recep;
    private javax.swing.JButton btn_recep_actualizar;
    private javax.swing.JButton btn_recep_borrar;
    private javax.swing.JButton btn_recep_buscar_id;
    private javax.swing.JButton btn_recep_crear;
    private javax.swing.JComboBox<String> cmb_estudios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel lbl_icon;
    private javax.swing.JLabel lbl_titulo;
    private javax.swing.JLabel lbl_titulo_admin;
    private javax.swing.JLabel lbl_titulo_admin1;
    private javax.swing.JLabel lbl_titulo_admin2;
    private javax.swing.JLabel lbl_titulo_admin3;
    private javax.swing.JLabel lbl_titulo_admin4;
    private javax.swing.JLabel lbl_titulo_admin5;
    private javax.swing.JLabel lbl_titulo_admin6;
    private javax.swing.JLabel lbl_titulo_tablas;
    private javax.swing.JLabel lbl_titulo_usuarios;
    private javax.swing.JLabel lbl_usuario;
    private javax.swing.JPanel pnl_admin;
    private javax.swing.JPanel pnl_admis;
    private javax.swing.JPanel pnl_cuerpo;
    private javax.swing.JPanel pnl_header;
    private javax.swing.JPanel pnl_menu_izq;
    private javax.swing.JPanel pnl_recep;
    private javax.swing.JPanel pnl_recep1;
    private javax.swing.JPanel pnl_recep2;
    private javax.swing.JPanel pnl_recep3;
    private javax.swing.JPanel pnl_recep4;
    private javax.swing.JPanel pnl_recep5;
    private javax.swing.JTable tbl_admin;
    private javax.swing.JTable tbl_areas;
    private javax.swing.JTable tbl_citas;
    private javax.swing.JTable tbl_estudios;
    private javax.swing.JTable tbl_lab;
    private javax.swing.JTable tbl_quimico;
    private javax.swing.JTable tbl_recep;
    private javax.swing.JTextField txtBuscarAreas;
    private javax.swing.JTextField txtBuscarCita;
    private javax.swing.JTextField txtBuscarEstudios;
    private javax.swing.JTextField txtBuscarLab;
    private javax.swing.JTextField txtBuscarQuimicos;
    private javax.swing.JTextField txtBuscarRecep;
    private javax.swing.JTextField txt_admin_buscar_todo;
    private javax.swing.JTextField txt_admin_direccion;
    private javax.swing.JTextField txt_admin_id;
    private javax.swing.JTextField txt_admin_nombre;
    private javax.swing.JTextField txt_admin_pass;
    private javax.swing.JTextField txt_admin_telefono;
    private javax.swing.JTextField txt_area_direccion;
    private javax.swing.JTextField txt_area_id;
    private javax.swing.JTextField txt_area_nombre;
    private javax.swing.JTextField txt_area_pass;
    private javax.swing.JTextField txt_area_telefono;
    private javax.swing.JTextField txt_cita_direccion;
    private javax.swing.JTextField txt_cita_id;
    private javax.swing.JTextField txt_cita_nombre;
    private javax.swing.JTextField txt_cita_pass;
    private javax.swing.JTextField txt_estudio_direccion;
    private javax.swing.JTextField txt_estudio_id;
    private javax.swing.JTextField txt_estudio_nombre;
    private javax.swing.JTextField txt_estudio_pass;
    private javax.swing.JTextField txt_estudio_telefono;
    private javax.swing.JTextField txt_lab_direccion;
    private javax.swing.JTextField txt_lab_id;
    private javax.swing.JTextField txt_lab_nombre;
    private javax.swing.JTextField txt_lab_pass;
    private javax.swing.JTextField txt_lab_puesto;
    private javax.swing.JTextField txt_lab_telefono;
    private javax.swing.JTextField txt_quimico_direccion;
    private javax.swing.JTextField txt_quimico_id;
    private javax.swing.JTextField txt_quimico_nombre;
    private javax.swing.JTextField txt_quimico_pass;
    private javax.swing.JTextField txt_quimico_puesto;
    private javax.swing.JTextField txt_quimico_telefono;
    private javax.swing.JTextField txt_recep_direccion;
    private javax.swing.JTextField txt_recep_id;
    private javax.swing.JTextField txt_recep_nombre;
    private javax.swing.JTextField txt_recep_pass;
    private javax.swing.JTextField txt_recep_telefono;
    // End of variables declaration//GEN-END:variables
}
