/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.innovacion.ui;

import sv.innovacion.service.ResumenPagoService;
import sv.innovacion.service.ResumenPagoService.ResumenMetodo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
/**
 *
 * @author Frank
 */
public class ResumenPagosPanel extends JPanel {

    private final ResumenPagoService servicio = new ResumenPagoService();

    private final DefaultTableModel modelo =
            new DefaultTableModel(
                    new Object[]{
                        "MÉTODO DE PAGO",
                        "TOTAL OPERACIONES",
                        "APROBADAS",
                        "RECHAZADAS",
                        "PENDIENTES",
                        "IMPORTE APROBADO"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modelo);

    private final JLabel etiquetaTotalOperaciones =
            new JLabel("Total de operaciones: 0");

    private final JLabel etiquetaImporteAprobado =
            new JLabel("Importe aprobado: $0.00");

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(Locale.US);

    public ResumenPagosPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // ENCABEZADO
        JLabel titulo = new JLabel("Resumen de pagos");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitulo = new JLabel(
                "Cantidad de operaciones e importes por método de pago"
        );
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(subtitulo);

        // BOTÓN ACTUALIZAR
        JButton botonActualizar = new JButton("ACTUALIZAR REPORTE");
        botonActualizar.addActionListener(e -> cargarResumen());

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.add(encabezado, BorderLayout.WEST);
        barraSuperior.add(botonActualizar, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        // TABLA
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // TOTALES GENERALES
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 10));
        panelTotales.setOpaque(false);

        etiquetaTotalOperaciones.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        etiquetaImporteAprobado.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        panelTotales.add(etiquetaTotalOperaciones);
        panelTotales.add(etiquetaImporteAprobado);

        add(panelTotales, BorderLayout.SOUTH);

        cargarResumen();
    }

    private void cargarResumen() {

        try {

            List<ResumenMetodo> datos = servicio.obtenerResumen();

            modelo.setRowCount(0);

            long totalOperaciones = 0;
            long totalAprobadas = 0;
            long totalRechazadas = 0;
            long totalPendientes = 0;

            BigDecimal importeTotalAprobado = BigDecimal.ZERO;

            for (ResumenMetodo dato : datos) {

                modelo.addRow(new Object[]{
                    dato.getMetodo(),
                    dato.getTotalOperaciones(),
                    dato.getAprobadas(),
                    dato.getRechazadas(),
                    dato.getPendientes(),
                    formatoMoneda.format(dato.getImporteAprobado())
                });

                totalOperaciones += dato.getTotalOperaciones();
                totalAprobadas += dato.getAprobadas();
                totalRechazadas += dato.getRechazadas();
                totalPendientes += dato.getPendientes();

                importeTotalAprobado =
                        importeTotalAprobado.add(dato.getImporteAprobado());
            }

            // FILA DE TOTALES
            modelo.addRow(new Object[]{
                "TOTAL GENERAL",
                totalOperaciones,
                totalAprobadas,
                totalRechazadas,
                totalPendientes,
                formatoMoneda.format(importeTotalAprobado)
            });

            etiquetaTotalOperaciones.setText(
                    "Total de operaciones: " + totalOperaciones
            );

            etiquetaImporteAprobado.setText(
                    "Importe aprobado: "
                    + formatoMoneda.format(importeTotalAprobado)
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo generar el resumen de pagos:\n"
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}