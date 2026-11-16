package sv.innovacion.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import sv.innovacion.model.Usuario;
import sv.innovacion.service.UsuarioService;
import sv.innovacion.service.VentaService;
import sv.innovacion.util.Estilo;
import java.time.format.DateTimeFormatter;
import sv.innovacion.model.Venta;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;
import java.awt.event.ActionListener;
import java.util.function.IntConsumer;
import sv.innovacion.model.Estado;
import sv.innovacion.util.MetodoPago;
import sv.innovacion.util.Transaccion;
import java.sql.SQLException;
import sv.innovacion.ui.ResumenPagosPanel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final UsuarioService servicio;
    private final Usuario usuario;
    private final VentaService ventaServicio;
    private final JPanel contenido = new JPanel(new BorderLayout());
    private DefaultTableModel tablaModelo;

    public DashboardFrame(UsuarioService servicio, Usuario usuario, VentaService ventaServicio) {
        this.servicio = servicio;
        this.usuario = usuario;
        this.ventaServicio = ventaServicio;
        
        
        setTitle("Innovación SV | Panel principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        construir();
        mostrarInicio();
    }

    private void construir() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(menuLateral(), BorderLayout.WEST);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Estilo.FONDO);
        centro.add(barraSuperior(), BorderLayout.NORTH);
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));
        centro.add(contenido, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel menuLateral() {
        JPanel p = new JPanel();
        p.setBackground(Estilo.AZUL);
        p.setPreferredSize(new Dimension(225, 0));
        p.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("◆  INNOVACIÓN SV");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        p.add(logo);
        p.add(Box.createVerticalStrut(38));
        agregarMenu(p, "Resumen", this::mostrarInicio);
        agregarMenu(p, "Transacciones", this::mostrarTransacciones);
        agregarMenu(p,"Venta", this::mostrarVenta );
        agregarMenu(p, " Resumen de pagos", this::mostrarResumenPagos);
        //agregarMenu(p, "Clientes", () -> mostrarAviso("Clientes", "Módulo preparado para una futura conexión."));
        //agregarMenu(p, "Reportes", () -> mostrarAviso("Reportes", "Los reportes estarán disponibles en una siguiente etapa."));
        //agregarMenu(p, "Configuración", () -> mostrarAviso("Configuración", "Opciones de configuración de demostración."));
        p.add(Box.createVerticalGlue());
        agregarMenu(p, "Cerrar sesión", this::cerrarSesion);
        return p;
    }
    
    
    private void cargarVentas(DefaultTableModel modelo) {
        
        if(modelo == null){
            return;
        }
        
        modelo.setRowCount(0);

        try {

            DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Venta venta : ventaServicio.listarTodas()) {
                
                String accion;
                if(venta.getEstado() == Estado.PENDIENTE){
                    accion = "Confirmar pago";
                }else{
                    accion = "No disponible";
                }
                

                modelo.addRow(new Object[] {
                    venta.getId(),
                    venta.getFecha().format(formato),
                    String.format("$%.2f", venta.getTotal()),
                    venta.getEstado(),
                    accion
                });
            }

        } catch (java.sql.SQLException ex) {

            JOptionPane.showMessageDialog(
                this,
                "No se pudieron cargar las ventas desde MySQL.\n"
                + ex.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
  
    private void mostrarVenta() {

        contenido.removeAll();

        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false);

        // ENCABEZADO
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        textos.add(Componentes.titulo("Gestión de ventas", 25));
        textos.add(Box.createVerticalStrut(6));

        JLabel descripcion = new JLabel(
            "Administra y consulta todas las ventas registradas."
        );

        descripcion.setForeground(Estilo.GRIS);
        textos.add(descripcion);

        // BOTONES
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        JButton actualizar = new JButton("ACTUALIZAR");
        JButton nuevaVenta = Componentes.botonPrincipal("NUEVA VENTA");

        actualizar.addActionListener(e -> cargarVentas(tablaModelo));

        nuevaVenta.addActionListener(e -> {

            VentaDialog dialog = new VentaDialog(this, ventaServicio);
            dialog.setVisible(true);

            cargarVentas(tablaModelo);
        });

        botones.add(actualizar);
        botones.add(nuevaVenta);

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);

        p.add(encabezado, BorderLayout.NORTH);

        // TABLA DE VENTAS
        String[] columnas = {
            "ID", "FECHA", "TOTAL", "ESTADO", "ACCIÓN"
        };

        tablaModelo = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable tabla = new JTable(tablaModelo);

        tabla.setRowHeight(42);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(Estilo.TEXTO);

        tabla.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );

        tabla.getTableHeader().setBackground(Color.WHITE);
        tabla.getTableHeader().setForeground(Estilo.TEXTO);

        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 239, 243));

        // Configurar botón de la columna ACCIÓN
        tabla.getColumnModel()
             .getColumn(4)
             .setCellRenderer(new BotonPagoRenderer());

        tabla.getColumnModel()
             .getColumn(4)
             .setCellEditor(
                 new BotonPagoEditor(this::abrirPago)
             );

        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(BorderFactory.createLineBorder(
            new Color(228, 234, 240)
        ));

        scroll.getViewport().setBackground(Color.WHITE);

        p.add(scroll, BorderLayout.CENTER);

        contenido.add(p, BorderLayout.CENTER);

        // CARGAR REGISTROS
        cargarVentas(tablaModelo);

        contenido.revalidate();
        contenido.repaint();
    }
    
    //Boton para accion  
    private class BotonPagoRenderer extends JButton
            implements TableCellRenderer {

        public BotonPagoRenderer() {

            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            String accion = value == null ? "" : value.toString();

            setText(accion);

            if ("Confirmar pago".equals(accion)) {

                setBackground(new Color(37, 99, 235));
                setForeground(Color.WHITE);

            } else {

                setBackground(new Color(229, 231, 235));
                setForeground(new Color(107, 114, 128));
            }

            return this;
        }
    }
    
    //editor que ejecuta el boton 
    private class BotonPagoEditor extends AbstractCellEditor
            implements TableCellEditor {

        private final JButton boton;
        private final IntConsumer accion;

        private int idVenta;
        private String valorAccion;

        public BotonPagoEditor(IntConsumer accion) {

            this.accion = accion;

            boton = new JButton();

            boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {

                // Evitar ejecutar acciones no disponibles.
                if (!"Confirmar pago".equals(valorAccion)) {
                    fireEditingStopped();
                    return;
                }

                int id = idVenta;

                fireEditingStopped();

                accion.accept(id);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {

            int filaModelo = table.convertRowIndexToModel(row);

            idVenta = Integer.parseInt(
                table.getModel().getValueAt(filaModelo, 0).toString()
            );

            valorAccion = value == null ? "" : value.toString();

            boton.setText(valorAccion);

            boolean disponible = "Confirmar pago".equals(valorAccion);

            boton.setEnabled(disponible);

            if (disponible) {

                boton.setBackground(new Color(37, 99, 235));
                boton.setForeground(Color.WHITE);

            } else {

                boton.setBackground(new Color(229, 231, 235));
                boton.setForeground(new Color(107, 114, 128));
            }

            return boton;
        }

        @Override
        public Object getCellEditorValue() {
            return valorAccion;
        }
    }

    private void abrirPago(int id) {

        try {

            // 1. Obtener la venta seleccionada.
            Venta venta = ventaServicio.buscarPorId(id);

            if (venta == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "No se encontró la venta seleccionada.",
                    "Venta no encontrada",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (venta.getEstado() != Estado.PENDIENTE) {
                JOptionPane.showMessageDialog(
                    this,
                    "Esta venta ya no está pendiente de pago.",
                    "Pago no disponible",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 2. Abrir el formulario dinámico.
            PagoDialog dialog = new PagoDialog(this, venta.getTotal());
            dialog.setVisible(true);

            // 3. Comprobar si el usuario confirmó.
            if (!dialog.isConfirmado()) {
                return;
            }

            // 4. Obtener el método seleccionado.
            MetodoPago metodo = dialog.getMetodoPago();

            // 5. Procesar y guardar el pago.
            Transaccion transaccion =
                ventaServicio.confirmarPago(id, metodo);

            // 6. Mostrar el resultado.
            if (transaccion.getEstado() == Estado.PAGADA) {

                JOptionPane.showMessageDialog(
                    this,
                    "¡Pago realizado correctamente!\n\n"
                    + transaccion,
                    "Pago exitoso",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "El pago fue rechazado.\n"
                    + "La venta continúa pendiente.\n\n"
                    + transaccion,
                    "Pago rechazado",
                    JOptionPane.WARNING_MESSAGE
                );
            }

            // 7. Actualizar la tabla de ventas.
            tablaModelo.setRowCount(0);
            cargarVentas(tablaModelo);

        } catch (IllegalArgumentException | IllegalStateException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "No se pudo procesar el pago",
                JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Error al guardar el pago en la base de datos:\n"
                + e.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

 
    private double obtenerTotalVenta(int id) {

        for (int fila = 0; fila < tablaModelo.getRowCount(); fila++) {

            int idTabla = Integer.parseInt(
                tablaModelo.getValueAt(fila, 0).toString()
            );

            if (idTabla == id) {

                String totalTexto =
                    tablaModelo.getValueAt(fila, 2).toString();

                return Double.parseDouble(
                    totalTexto.replace("$", "").replace(",", "").trim()
                );
            }
        }

        throw new IllegalArgumentException(
            "No se encontró la venta seleccionada."
        );
    }

    private void agregarMenu(JPanel panel, String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setMaximumSize(new Dimension(195, 43));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(new Color(235, 243, 250));
        b.setBackground(Estilo.AZUL);
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 8));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> accion.run());
        panel.add(b);
        panel.add(Box.createVerticalStrut(5));
    }

    private JPanel barraSuperior() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        JLabel titulo = new JLabel("Plataforma de gestión");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Estilo.TEXTO);
        JButton perfil = new JButton(usuario.getNombreCompleto() + "  ▾");
        perfil.setFocusPainted(false);
        perfil.setBackground(Color.WHITE);
        perfil.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        perfil.addActionListener(e -> mostrarPerfil());
        p.add(titulo, BorderLayout.WEST);
        p.add(perfil, BorderLayout.EAST);
        return p;
    }

    //Es la vista principal del sistema 
    private void mostrarInicio() {

        contenido.removeAll();

        try {

            // Obtener las ventas reales desde MySQL.
            List<Venta> ventas = new ArrayList<>(ventaServicio.listarTodas());

            LocalDate hoy = LocalDate.now();

            long ventasHoy = ventas.stream()
                    .filter(v -> v.getFecha().toLocalDate().equals(hoy))
                    .count();

            double montoRecaudadoHoy = ventas.stream()
                    .filter(v -> v.getFecha().toLocalDate().equals(hoy))
                    .filter(v -> v.getEstado() == Estado.PAGADA)
                    .mapToDouble(Venta::getTotal)
                    .sum();

            long ventasPendientes = ventas.stream()
                    .filter(v -> v.getEstado() == Estado.PENDIENTE)
                    .count();

            // Panel principal.
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            p.add(Componentes.titulo("Resumen general", 26));
            p.add(Box.createVerticalStrut(6));

            JLabel bienvenida = new JLabel(
                    "Bienvenido/a, " + usuario.getNombreCompleto()
                    + ". Aquí tienes el estado actual de la plataforma."
            );

            bienvenida.setForeground(Estilo.GRIS);
            p.add(bienvenida);

            p.add(Box.createVerticalStrut(25));

            // Tarjetas con datos reales.
            JPanel tarjetas = new JPanel(new GridLayout(1, 3, 15, 0));
            tarjetas.setOpaque(false);

            tarjetas.add(tarjeta(
                    "Ventas de hoy",
                    String.valueOf(ventasHoy),
                    "Ventas registradas durante el día"
            ));

            tarjetas.add(tarjeta(
                    "Monto recaudado hoy",
                    String.format("$%,.2f", montoRecaudadoHoy),
                    "Total de ventas pagadas hoy"
            ));

            tarjetas.add(tarjeta(
                    "Ventas pendientes",
                    String.valueOf(ventasPendientes),
                    "Ventas que esperan confirmación de pago"
            ));

            p.add(tarjetas);
            p.add(Box.createVerticalStrut(25));

            p.add(Componentes.titulo("Actividad reciente", 18));
            p.add(Box.createVerticalStrut(10));

            // Tabla con las ventas reales.
            p.add(tablaVentas(ventas));

            JScrollPane scroll = new JScrollPane(p);
            scroll.setBorder(null);
            scroll.getViewport().setOpaque(false);
            scroll.setOpaque(false);

            contenido.add(scroll, BorderLayout.CENTER);

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar el resumen general desde MySQL.\n"
                    + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        contenido.revalidate();
        contenido.repaint();
    }
    
    private JPanel tarjeta(String titulo, String valor, String detalle) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 234, 240)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setForeground(Estilo.GRIS);
        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 25));
        v.setForeground(Estilo.AZUL);
        JLabel d = new JLabel(detalle);
        d.setForeground(Estilo.GRIS);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(t);
        p.add(Box.createVerticalStrut(12));
        p.add(v);
        p.add(Box.createVerticalStrut(8));
        p.add(d);
        return p;
    }

    private JScrollPane tablaVentas(List<Venta> ventas) {

        String[] columnas = {
            "ID VENTA",
            "FECHA",
            "TOTAL",
            "ESTADO"
        };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Ordenar ventas desde la más reciente.
        ventas.sort(
                Comparator.comparing(Venta::getFecha).reversed()
        );

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Mostrar como máximo las últimas 8 ventas.
        int limite = Math.min(ventas.size(), 8);

        for (int i = 0; i < limite; i++) {

            Venta venta = ventas.get(i);

            modelo.addRow(new Object[]{
                venta.getId(),
                venta.getFecha().format(formato),
                String.format("$%,.2f", venta.getTotal()),
                venta.getEstado()
            });
        }

        JTable tabla = new JTable(modelo);

        tabla.setRowHeight(36);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        tabla.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        tabla.getTableHeader().setBackground(Color.WHITE);
        tabla.getTableHeader().setForeground(Estilo.TEXTO);

        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 239, 243));

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setPreferredSize(new Dimension(700, 340));

        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(228, 234, 240)
        ));

        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    
    private void mostrarTransacciones() {

        contenido.removeAll();

        TransaccionesPanel panel = new TransaccionesPanel();

        contenido.add(panel, BorderLayout.CENTER);

        contenido.revalidate();
        contenido.repaint();
    }

    private void mostrarAviso(String titulo, String mensaje) {
        contenido.removeAll();
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JPanel caja = new JPanel();
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.add(Componentes.titulo(titulo, 24));
        caja.add(Box.createVerticalStrut(12));
        JLabel l = new JLabel(mensaje);
        l.setForeground(Estilo.GRIS);
        caja.add(l);
        p.add(caja);
        contenido.add(p, BorderLayout.CENTER);
        contenido.revalidate();
        contenido.repaint();
    }

    private void mostrarPerfil() {
        JOptionPane.showMessageDialog(this,
                "Nombre: " + usuario.getNombreCompleto()
                + "\nUsuario: " + usuario.getUsuario()
                + "\nCorreo: " + usuario.getCorreo()
                + "\nRol: " + usuario.getRol(),
                "Perfil de usuario", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas cerrar la sesión?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            new LoginFrame(servicio).setVisible(true);
            dispose();
        }
    }
    
    private void mostrarResumenPagos() {

        contenido.removeAll();

        ResumenPagosPanel panel = new ResumenPagosPanel();

        contenido.add(panel, BorderLayout.CENTER);

        contenido.revalidate();
        contenido.repaint();
    }

}
