package com.idgs902.cotiz_gaona_jesus.ui.ventas;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.idgs902.cotiz_gaona_jesus.databinding.VentaFragmentBinding;
import com.idgs902.cotiz_gaona_jesus.ui.compras.ComprasActivity;
import com.idgs902.cotiz_gaona_jesus.ui.compras.ComprasActivity2;
import com.idgs902.cotiz_gaona_jesus.ui.compras.ComprasFragment;
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

public class VentaFragment extends Fragment {

    private VentaFragmentBinding binding;
    private Button btnAgregarVenta, btnBuscarVenta, btnGenerarVenta;
    private TextInputEditText etBusquedaVenta;
    private TextView txtVenta;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Ventas.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Venta> lista = new ArrayList<Venta>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = VentaFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAgregarVenta = root.findViewById(R.id.btnAgregarVenta);
        btnBuscarVenta = root.findViewById(R.id.btnBuscarVent);
        btnGenerarVenta = root.findViewById(R.id.btnReporteVent);
        etBusquedaVenta = root.findViewById(R.id.etBusquedaVent);
        txtVenta = root.findViewById(R.id.txtVent);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM ventas;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Venta v = new Venta();
                    v.setId(c.getString(0));
                    v.setClave_cliente(c.getString(0));
                    v.setNombre_cliente(c.getString(0));
                    v.setCalle_cliente(c.getString(1));
                    v.setClave_vendedor(c.getString(1));
                    v.setNombre_vendedor(c.getString(1));
                    v.setFecha(c.getString(2));
                    v.setComision(c.getString(1));
                    v.setTipo_recibo(c.getString(2));
                    v.setClave_recibo(c.getString(3));
                    v.setTotal_productos(c.getString(3));
                    v.setSuma(c.getString(2));
                    v.setIva(c.getString(2));
                    v.setTotal_venta(c.getString(2));
                    v.setComis_vendedor(c.getString(3));
                    lista.add(v);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtVenta.setText(cadena);

            }
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(getActivity(), VentasActivity.class);
                    startActivityForResult(i, 1, new Bundle());
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGenerarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnBuscarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buscarVenta();
                }catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        return root;
    }

    public void buscarVenta(){
        if(etBusquedaVenta.getText().toString().trim().length()!=0){
            Intent i = new Intent(getContext(), VentasActivity2.class);

            i.putExtra(DATOS, etBusquedaVenta.getText().toString());
            startActivityForResult(i,1,new Bundle());
        }else {
            showMessage("Info", "Debe ingresar una clave a buscar");
        }

    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
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
            Paragraph p = new Paragraph("Ventas\n\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(15);
            tabla.addCell("Clave");
            tabla.addCell("Clave Cliente");
            tabla.addCell("Nombre Cliente");
            tabla.addCell("Calle Cliente");
            tabla.addCell("Clave Vendedor");
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
            for (int i = 0; i < lista.size(); i++) {

                tabla.addCell(lista.get(i).id);
                tabla.addCell(lista.get(i).clave_cliente);
                tabla.addCell(lista.get(i).nombre_cliente);
                tabla.addCell(lista.get(i).calle_cliente);
                tabla.addCell(lista.get(i).clave_vendedor);
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

    public class Venta {
        String id, clave_cliente, nombre_cliente, calle_cliente, clave_vendedor, nombre_vendedor, fecha, comision, tipo_recibo,  clave_recibo, total_productos, suma, iva, total_venta, comis_vendedor;

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

}