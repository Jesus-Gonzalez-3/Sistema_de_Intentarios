package com.idgs902.cotiz_gaona_jesus.ui.ReporteVentasDia;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.databinding.ReporteVentasDiaFragmentBinding;
import com.idgs902.cotiz_gaona_jesus.ui.ventas.VentaFragment;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentReporteVentasDia extends Fragment {

    private ReporteVentasDiaFragmentBinding binding;
    private SQLiteDatabase db;

    private TextInputEditText etBusquedaVenta;
    private Button btnBuscarVenta, btnGenerarReporteVenta;
    private TextView txtVentasList;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "ReporteVentas.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Venta> lista = new ArrayList<Venta>();
    private List<Venta> lista2 = new ArrayList<Venta>();
    private List<DetalleVentas> lista3 = new ArrayList<DetalleVentas>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ReporteVentasDiaFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        etBusquedaVenta = root.findViewById(R.id.etBusquedaVenta);
        txtVentasList = root.findViewById(R.id.txtVentasList);
        btnBuscarVenta = root.findViewById(R.id.btnBuscarVenta);
        btnGenerarReporteVenta = root.findViewById(R.id.btnReporteVentasDia);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM ventas where fecha BETWEEN date('now') AND date('now','+1 day');", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Venta v = new Venta();
                    v.setId(c.getString(0));
                    v.setClave_cliente(c.getString(1));
                    v.setNombre_cliente(c.getString(2));
                    v.setCalle_cliente(c.getString(3));
                    v.setClave_vendedor(c.getString(4));
                    v.setNombre_vendedor(c.getString(5));
                    v.setFecha(c.getString(6));
                    v.setComision(c.getString(7));
                    v.setTipo_recibo(c.getString(8));
                    v.setClave_recibo(c.getString(9));
                    v.setTotal_productos(c.getString(10));
                    v.setSuma(c.getString(11));
                    v.setIva(c.getString(12));
                    v.setTotal_venta(c.getString(13));
                    v.setComis_vendedor(c.getString(14));
                    lista.add(v);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtVentasList.setText(cadena);

            } else {
                showMessage("Info", "No Hay ventas del dia de hoy que mostrar");
            }
        } catch (
                Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnGenerarReporteVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                } catch (Exception ex) {
                    showMessage(ETIQUETA_ERROR, ex.getMessage());
                }
            }
        });

        btnBuscarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buscarVenta();
                } catch (Exception ex) {
                    showMessage(ETIQUETA_ERROR, ex.getMessage());
                }
            }
        });

        return root;
    }

    private void buscarVenta() {
        if (etBusquedaVenta.getText().toString().trim().length() != 0) {

            Cursor c = db.rawQuery("Select * from ventas Where id =" + etBusquedaVenta.getText().toString(), null);
            String id = "";
            if (c.getCount() != 0) {
                c.moveToFirst();
                id = c.getString(0);
                Venta ven = new Venta();
                ven.setNombre_cliente(c.getString(2));
                ven.setCalle_cliente(c.getString(3));
                ven.setFecha(c.getString(6));
                ven.setNombre_vendedor(c.getString(5));
                ven.setTotal_productos(c.getString(10));
                ven.setSuma(c.getString(11));
                ven.setTotal_venta(c.getString(13));
                ven.setClave_recibo(c.getString(9));
                Cursor c1 = db.rawQuery("SELECT * FROM ventas_detalle Where id_venta = '" + id + "'", null);
                if (c1.getCount() != 0) {
                    c1.moveToFirst();
                    for (int i = 0; i < c1.getCount(); i++) {
                        DetalleVentas det = new DetalleVentas();
                        det.setClave(c1.getString(2));
                        det.setDescripcion(c1.getString(3));
                        det.setUnidad(c1.getString(4));
                        det.setLinea(c1.getString(5));
                        det.setCantidad(c1.getString(6));
                        det.setPventa(c1.getString(7));
                        det.setImporte(c1.getString(8));
                        lista3.add(det);
                        c1.moveToNext();
                    }
                    ven.setLista(lista3);
                }
                lista2.add(ven);
            }

            generarPdf2("Venta" + id+".pdf");

        } else {
            showMessage("INFO", "Debes de Proporcionar una clave a consultar");
        }
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void generarPdf2(String nombreDocumento) {
        // Creamos el documento.
        Document documento = new Document();
        try {
            File f = crearFichero(nombreDocumento);
            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());
            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);
            //Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Sistema de Inventarios Zapateria Jessi"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Desarrollado por: \t Jessica Anahi Muñoz\t Jesus Guadalupe Gaona\t  "), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA, "", 28, Color.BLACK);
            // Añadimos un titulo con la fuente por defecto.
            Paragraph p = new Paragraph("Zapateria Jessica\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, "", 15, Color.BLACK);
            Paragraph p2 = new Paragraph("El Mejor calzado para tu comodidad\n\nFactura de Venta\n", font2);
            p2.setAlignment(Element.ALIGN_CENTER);
            documento.add(p2);

            Paragraph p3 = new Paragraph("No.Factura: " + lista2.get(0).clave_recibo + "\nFecha: " + lista2.get(0).fecha + "\nCliente:" + lista2.get(0).nombre_cliente +
                    "\nAtendidoPor: " + lista2.get(0).nombre_vendedor + "\n---------------------------------------------------------------------------------------------------" +
                    "\n Detalles de la venta\n\n", font2);
            p3.setAlignment(Element.ALIGN_LEFT);
            documento.add(p3);


            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(7);
            tabla.addCell("Clave del Producto");
            tabla.addCell("Nombre del Porducto");
            tabla.addCell("Unidad");
            tabla.addCell("Linea");
            tabla.addCell("Cantidad de Producto");
            tabla.addCell("Precio Venta");
            tabla.addCell("Importe");

            if (lista2.get(0).lista.size() != 0) {
                for (int i = 0; i < lista2.get(0).lista.size(); i++) {
                    tabla.addCell(lista2.get(0).lista.get(i).clave);
                    tabla.addCell(lista2.get(0).lista.get(i).descripcion);
                    tabla.addCell(lista2.get(0).lista.get(i).unidad);
                    tabla.addCell(lista2.get(0).lista.get(i).linea);
                    tabla.addCell(lista2.get(0).lista.get(i).cantidad);
                    tabla.addCell(lista2.get(0).lista.get(i).pventa);
                    tabla.addCell(lista2.get(0).lista.get(i).importe);
                }
            } else {
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("Sin");
                tabla.addCell("Regis");
                tabla.addCell("tros");
                tabla.addCell("");
                tabla.addCell("");

            }
            documento.add(tabla);

            Font font4 = FontFactory.getFont(FontFactory.HELVETICA, "", 15, Color.BLACK);
            Paragraph p4 = new Paragraph("Articulos vendidos:" + lista2.get(0).total_productos +
                    "\nSubtotal: " + lista2.get(0).suma + "\nTotal a Pagar:" + lista2.get(0).total_venta + "\n", font4);
            p4.setAlignment(Element.ALIGN_RIGHT);
            documento.add(p4);

            showMessage("Success", "PDF Generado con Éxito!!");

        } catch (DocumentException e) {

            showMessage(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            showMessage(ETIQUETA_ERROR, e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
        }
    }


    public class Venta {
        String id, clave_cliente, nombre_cliente, calle_cliente, clave_vendedor, nombre_vendedor, fecha, comision, tipo_recibo, clave_recibo, total_productos, suma, iva, total_venta, comis_vendedor;
        List<DetalleVentas> lista;

        public List<DetalleVentas> getLista() {
            return lista;
        }

        public void setLista(List<DetalleVentas> lista) {
            this.lista = lista;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClave_cliente() {
            return clave_cliente;
        }

        public void setClave_cliente(String clave_cliente) {
            this.clave_cliente = clave_cliente;
        }

        public String getNombre_cliente() {
            return nombre_cliente;
        }

        public void setNombre_cliente(String nombre_cliente) {
            this.nombre_cliente = nombre_cliente;
        }

        public String getCalle_cliente() {
            return calle_cliente;
        }

        public void setCalle_cliente(String calle_cliente) {
            this.calle_cliente = calle_cliente;
        }

        public String getClave_vendedor() {
            return clave_vendedor;
        }

        public void setClave_vendedor(String clave_vendedor) {
            this.clave_vendedor = clave_vendedor;
        }

        public String getNombre_vendedor() {
            return nombre_vendedor;
        }

        public void setNombre_vendedor(String nombre_vendedor) {
            this.nombre_vendedor = nombre_vendedor;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getComision() {
            return comision;
        }

        public void setComision(String comision) {
            this.comision = comision;
        }

        public String getTipo_recibo() {
            return tipo_recibo;
        }

        public void setTipo_recibo(String tipo_recibo) {
            this.tipo_recibo = tipo_recibo;
        }

        public String getClave_recibo() {
            return clave_recibo;
        }

        public void setClave_recibo(String clave_recibo) {
            this.clave_recibo = clave_recibo;
        }

        public String getTotal_productos() {
            return total_productos;
        }

        public void setTotal_productos(String total_productos) {
            this.total_productos = total_productos;
        }

        public String getSuma() {
            return suma;
        }

        public void setSuma(String suma) {
            this.suma = suma;
        }

        public String getIva() {
            return iva;
        }

        public void setIva(String iva) {
            this.iva = iva;
        }

        public String getTotal_venta() {
            return total_venta;
        }

        public void setTotal_venta(String total_venta) {
            this.total_venta = total_venta;
        }

        public String getComis_vendedor() {
            return comis_vendedor;
        }

        public void setComis_vendedor(String comis_vendedor) {
            this.comis_vendedor = comis_vendedor;
        }

        public Venta() {
        }

    }

    public void generarPdf() {
        // Creamos el documento.
        Document documento = new Document();
        try {
            File f = crearFichero(NOMBRE_DOCUMENTO);
            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());
            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);
            //Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Sistema de Inventarios Zapateria Jessi"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Desarrollado por: \t Jessica Anahi Muñoz\t Jesus Guadalupe Gaona\t  "), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA, "", 28, Color.BLACK);
            // Añadimos un titulo con la fuente por defecto.
            Paragraph p = new Paragraph("Ventas del día\n\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(12);
            tabla.addCell("Nombre Cliente");
            tabla.addCell("Calle Cliente");
            tabla.addCell("Nombre Vendedor");
            tabla.addCell("Fecha");
            tabla.addCell("Comisión");
            tabla.addCell("Comisión del Vendedor");
            tabla.addCell("Tipo Recibo");
            tabla.addCell("Clave Recibo");
            tabla.addCell("Total de productos");
            tabla.addCell("Suma");
            tabla.addCell("IVA");
            tabla.addCell("Total $");

            if (lista.size() != 0) {
                for (int i = 0; i < lista.size(); i++) {
                    tabla.addCell(lista.get(i).nombre_cliente);
                    tabla.addCell(lista.get(i).calle_cliente);
                    tabla.addCell(lista.get(i).nombre_vendedor);
                    tabla.addCell(lista.get(i).fecha);
                    tabla.addCell(lista.get(i).comision);
                    tabla.addCell(lista.get(i).comis_vendedor);
                    tabla.addCell(lista.get(i).tipo_recibo);
                    tabla.addCell(lista.get(i).clave_recibo);
                    tabla.addCell(lista.get(i).total_productos);
                    tabla.addCell(lista.get(i).suma);
                    tabla.addCell(lista.get(i).iva);
                    tabla.addCell(lista.get(i).total_venta);
                }
            } else {
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("Sin");
                tabla.addCell("Regis");
                tabla.addCell("tros");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");
                tabla.addCell("");

            }
            documento.add(tabla);

            showMessage("Success", "PDF Generado con Éxito!!");

        } catch (DocumentException e) {

            showMessage(ETIQUETA_ERROR, e.getMessage());

        } catch (IOException e) {

            showMessage(ETIQUETA_ERROR, e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
        }
    }

    public File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
            if (!fichero.exists()) { // Si no existe, crea el archivo.
                try {
                    creado = fichero.createNewFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return fichero;
    }

    public File getRuta() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    }

    public class DetalleVentas {
        String id, clave, descripcion, unidad, linea, cantidad, pventa, importe;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getUnidad() {
            return unidad;
        }

        public void setUnidad(String unidad) {
            this.unidad = unidad;
        }

        public String getLinea() {
            return linea;
        }

        public void setLinea(String linea) {
            this.linea = linea;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }

        public String getPventa() {
            return pventa;
        }

        public void setPventa(String pventa) {
            this.pventa = pventa;
        }

        public String getImporte() {
            return importe;
        }

        public void setImporte(String importe) {
            this.importe = importe;
        }

        public DetalleVentas() {
        }
    }
}