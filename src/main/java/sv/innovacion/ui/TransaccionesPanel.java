/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.ui;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import sv.innovacion.model.Estado;
import sv.innovacion.service.TransaccionService;
import sv.innovacion.util.Estilo;
import sv.innovacion.util.Transaccion;
/**
 *
 * @author Frank
 */
public class TransaccionesPanel extends JPanel {

    private final TransaccionService servicio;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JTextField campoId;
    private JTextField campoVentaId;

    private JComboBox<String> comboMetodo;
    private JComboBox<String> comboEstado;

    private JSpinner fechaInicio;
    private JSpinner fechaFin;

    public TransaccionesPanel() {

        servicio = new TransaccionService();

        setLayout(new BorderLayout(0, 5));
        setBackground(Estilo.FONDO);

        construirInterfaz();

        // Al abrir el módulo, cargar todos los registros.
        cargarTodas();
    }

    private void construirInterfaz() {

        // ENCABEZADO
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(
            new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );

        encabezado.add(Componentes.titulo(
            "Historial de transacciones", 25
        ));

        encabezado.add(Box.createVerticalStrut(6));

        JLabel descripcion = new JLabel(
            "Consulta, busca y filtra las operaciones de pago registradas."
        );

        descripcion.setForeground(Estilo.GRIS);
        encabezado.add(descripcion);

        // FILTROS
        JPanel filtros = new JPanel();
        filtros.setOpaque(false);
        filtros.setLayout(new BoxLayout(filtros, BoxLayout.Y_AXIS));

        filtros.add(crearFiltroIdentificador());
        filtros.add(Box.createVerticalStrut(5));

        filtros.add(crearFiltroMetodoEstado());
        filtros.add(Box.createVerticalStrut(5));

        filtros.add(crearFiltroFechas());
        filtros.add(Box.createVerticalStrut(5));

        filtros.add(crearFiltroVenta());

        // TABLA
        String[] columnas = {
            "ID TRANSACCIÓN",
            "ID VENTA",
            "FECHA",
            "MONTO",
            "MÉTODO",
            "ESTADO"
        };

        modelo = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);

        tabla.setRowHeight(38);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(Estilo.TEXTO);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 239, 243));

        tabla.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 11)
        );

        tabla.getTableHeader().setBackground(Color.WHITE);
        tabla.getTableHeader().setForeground(Estilo.TEXTO);

        tabla.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(
            new Color(228, 234, 240)
        ));
        scroll.getViewport().setBackground(Color.WHITE);

        // CONTENEDOR PRINCIPAL
        JPanel superior = new JPanel(new BorderLayout(0, 5));
        superior.setOpaque(false);
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(filtros, BorderLayout.CENTER);

        add(superior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // FILTRO 4.1: IDENTIFICADOR DE TRANSACCIÓN
    private JPanel crearFiltroIdentificador() {
        JPanel panel = crearPanelFiltro();

        campoId = new JTextField(8);
        campoVentaId = new JTextField(8);

        JButton buscar = crearBoton("BUSCAR ID");
        JButton buscarVenta = crearBoton("BUSCAR VENTA");
        JButton todas = crearBoton("MOSTRAR TODAS");

        buscar.addActionListener(e -> buscarPorId());
        buscarVenta.addActionListener(e -> buscarPorVenta());

        todas.addActionListener(e -> {
            limpiarFiltros();
            cargarTodas();
        });

        panel.add(new JLabel("ID transacción:"));
        panel.add(campoId);
        panel.add(buscar);

        panel.add(Box.createHorizontalStrut(12));

        panel.add(new JLabel("ID venta:"));
        panel.add(campoVentaId);
        panel.add(buscarVenta);

        panel.add(Box.createHorizontalStrut(12));

        panel.add(todas);

        return panel;
    }

    // FILTROS 4.2 Y 4.3: MÉTODO Y ESTADO
    private JPanel crearFiltroMetodoEstado() {

        JPanel panel = crearPanelFiltro();

        comboMetodo = new JComboBox<>(new String[]{
            "Todos",
            "Efectivo",
            "Tarjeta",
            "Transferencia",
            "Pago electrónico"
        });

        comboEstado = new JComboBox<>(new String[]{
            "Todos",
            "PENDIENTE",
            "PAGADA",
            "RECHAZADA"
        });

        JButton buscarMetodo = crearBoton("FILTRAR MÉTODO");
        JButton buscarEstado = crearBoton("FILTRAR ESTADO");

        buscarMetodo.addActionListener(e -> buscarPorMetodo());
        buscarEstado.addActionListener(e -> buscarPorEstado());

        panel.add(new JLabel("Método:"));
        panel.add(comboMetodo);
        panel.add(buscarMetodo);

        panel.add(Box.createHorizontalStrut(12));

        panel.add(new JLabel("Estado:"));
        panel.add(comboEstado);
        panel.add(buscarEstado);

        return panel;
    }

    // FILTRO 4.4: PERÍODO DE FECHAS
    private JPanel crearFiltroFechas() {

        JPanel panel = crearPanelFiltro();

        fechaInicio = crearSelectorFecha();
        fechaFin = crearSelectorFecha();

        JButton buscar = crearBoton("BUSCAR POR FECHA");

        buscar.addActionListener(e -> buscarPorFecha());

        panel.add(new JLabel("Desde:"));
        panel.add(fechaInicio);

        panel.add(new JLabel("Hasta:"));
        panel.add(fechaFin);

        panel.add(buscar);

        return panel;
    }

    // FILTRO 4.5: VENTA ASOCIADA
    private JPanel crearFiltroVenta() {
        return crearPanelFiltro();
    }

    private JPanel crearPanelFiltro() {

        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(228, 234, 240)
            ),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        return panel;
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(new Font("Segoe UI", Font.BOLD, 10));
        boton.setForeground(Color.WHITE);
        boton.setBackground(Estilo.AZUL);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(
            9, 12, 9, 12
        ));

        boton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return boton;
    }

    private JSpinner crearSelectorFecha() {

        JSpinner spinner = new JSpinner(
            new SpinnerDateModel(
                new Date(),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
            )
        );

        spinner.setEditor(
            new JSpinner.DateEditor(spinner, "dd/MM/yyyy")
        );

        spinner.setPreferredSize(new Dimension(115, 28));

        return spinner;
    }

    // CONSULTAR TODAS
   private void cargarTodas() {
        ejecutarConsulta(servicio::listarTodas);
    }

    // 4.1. BUSCAR POR ID
    private void buscarPorId() {

        try {

            String texto = campoId.getText().trim();

            if (texto.isEmpty()) {
                throw new IllegalArgumentException(
                    "Escribe el ID de la transacción."
                );
            }

            int id = Integer.parseInt(texto);

            if (id <= 0) {
                throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero."
                );
            }

            Transaccion transaccion = servicio.buscarPorId(id);

            modelo.setRowCount(0);

            if (transaccion != null) {
                agregarFila(transaccion);
            } else {
                mostrarSinResultados();
            }

        } catch (NumberFormatException e) {

            mostrarError("El ID debe ser un número entero.");

        } catch (IllegalArgumentException e) {

            mostrarError(e.getMessage());

        } catch (SQLException e) {

            mostrarErrorSQL(e);
        }
    }

    // 4.2. BUSCAR POR MÉTODO
    private void buscarPorMetodo() {

        String metodo = comboMetodo.getSelectedItem().toString();

        if (metodo.equals("Todos")) {
            cargarTodas();
            return;
        }

        ejecutarConsulta(
            () -> servicio.buscarPorMetodo(metodo)
        );
    }

    // 4.3. BUSCAR POR ESTADO
    private void buscarPorEstado() {

        String estado = comboEstado.getSelectedItem().toString();

        if (estado.equals("Todos")) {
            cargarTodas();
            return;
        }

        try {

            Estado estadoSeleccionado = Estado.valueOf(estado);

            ejecutarConsulta(
                () -> servicio.buscarPorEstado(estadoSeleccionado)
            );

        } catch (IllegalArgumentException e) {

            mostrarError("El estado seleccionado no es válido.");
        }
    }

    // 4.4. BUSCAR POR FECHAS
    private void buscarPorFecha() {

        try {

            LocalDate inicio = convertirFecha(fechaInicio);
            LocalDate fin = convertirFecha(fechaFin);

            ejecutarConsulta(
                () -> servicio.buscarPorFecha(inicio, fin)
            );

        } catch (IllegalArgumentException e) {

            mostrarError(e.getMessage());
        }
    }

    private LocalDate convertirFecha(JSpinner spinner) {

        Date fecha = (Date) spinner.getValue();

        return fecha.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    // 4.5. BUSCAR POR VENTA
    private void buscarPorVenta() {

        try {

            String texto = campoVentaId.getText().trim();

            if (texto.isEmpty()) {
                throw new IllegalArgumentException(
                    "Escribe el ID de la venta."
                );
            }

            int id = Integer.parseInt(texto);

            if (id <= 0) {
                throw new IllegalArgumentException(
                    "El ID de venta debe ser mayor que cero."
                );
            }

            ejecutarConsulta(
                () -> servicio.buscarPorVenta(id)
            );

        } catch (NumberFormatException e) {

            mostrarError("El ID de venta debe ser un número entero.");

        } catch (IllegalArgumentException e) {

            mostrarError(e.getMessage());
        }
    }

    // EJECUTAR CONSULTAS QUE DEVUELVEN LISTAS
    @FunctionalInterface
    private interface ConsultaTransacciones {
        List<Transaccion> ejecutar() throws SQLException;
    }

    private void ejecutarConsulta(ConsultaTransacciones consulta) {

        try {

            List<Transaccion> transacciones = consulta.ejecutar();

            modelo.setRowCount(0);

            for (Transaccion transaccion : transacciones) {
                agregarFila(transaccion);
            }

            if (transacciones.isEmpty()) {
                mostrarSinResultados();
            }

        } catch (SQLException e) {

            mostrarErrorSQL(e);
        }
    }

    // AGREGAR REGISTRO A LA TABLA
    private void agregarFila(Transaccion transaccion) {

        String idMostrar = transaccion.getId() == 0
            ? "Sin pago"
            : String.valueOf(transaccion.getId());

        modelo.addRow(new Object[]{
            idMostrar,
            transaccion.getVentaId(),
            transaccion.getFecha(),
            String.format("$%.2f", transaccion.getMonto()),
            transaccion.getMetodo(),
            transaccion.getEstado()
        });
    }

    private void mostrarSinResultados() {

        JOptionPane.showMessageDialog(
            this,
            "No se encontraron transacciones con los criterios indicados.",
            "Sin resultados",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void limpiarFiltros() {

        campoId.setText("");
        campoVentaId.setText("");

        comboMetodo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);

        fechaInicio.setValue(new Date());
        fechaFin.setValue(new Date());
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Consulta de transacciones",
            JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarErrorSQL(SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            "No se pudo realizar la consulta en MySQL:\n"
            + e.getMessage(),
            "Error de base de datos",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

