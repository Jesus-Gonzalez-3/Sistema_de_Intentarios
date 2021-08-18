package com.idgs902.cotiz_gaona_jesus.ui.reporte_venta_producto;

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
import com.idgs902.cotiz_gaona_jesus.databinding.ReporteProductoFragmentBinding;
import com.idgs902.cotiz_gaona_jesus.ui.vehiculos.ProveedorActivity2;
import com.idgs902.cotiz_gaona_jesus.ui.ventas.VentaFragment;
import com.idgs902.cotiz_gaona_jesus.ui.ventas.VentasActivity;
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

public class ReporteProductoFragment extends Fragment {

    private ReporteProductoFragmentBinding binding;
    private Button btnGenerar;
    private TextInputEditText txtFechaI, txtFechaF;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Reporte_venta_producto.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";
    private static boolean creado = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding =  ReporteProductoFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        btnGenerar = root.findViewById(R.id.btnGenerarReporte);
        txtFechaI = root.findViewById(R.id.txtFechaInicio);
        txtFechaF = root.findViewById(R.id.txtFechaFin);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (txtFechaI.getText() != null && txtFechaI.getText().toString() != " ") {
                        if (txtFechaF.getText() != null && txtFechaF.getText().toString() != " "){
                            generarPdf();
                        }else {
                            showMessage("Error", "Tiene que ingresar la fecha fin. ");
                        }
                    } else {
                        showMessage("Error", "Tiene que ingresar la fecha inicio. ");
                    }
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        return root;
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
            Paragraph p = new Paragraph("Reporte de productos\n\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Clave");
            tabla.addCell("Nombre");
            tabla.addCell("Línea");
            tabla.addCell("Unidades");
            Cursor c = db.rawQuery("SELECT  producto.clave, producto.nombre, producto.linea, SUM(ventas_detalle.cantidad_pro)  AS cantidad FROM ventas_detalle INNER JOIN producto ON ventas_detalle.clave_pro = producto.clave INNER JOIN ventas ON ventas.id = ventas_detalle.id_venta WHERE ventas.fecha BETWEEN '"+ txtFechaI.getText().toString() + "' AND  '" + txtFechaF.getText().toString()  + "' GROUP BY producto.clave;", null);
            if(c.getCount()!=0) {
                c.moveToFirst();
                //StringBuilder cadena = new StringBuilder();
                for (int i = 0; i < c.getCount(); i++) {
                    tabla.addCell(c.getString(0));
                    tabla.addCell(c.getString(1));
                    tabla.addCell(c.getString(2));
                    tabla.addCell(c.getString(3));
                    c.moveToNext();
                }
            }
            else{
                tabla.addCell("No");
                tabla.addCell("Hay");
                tabla.addCell("registros");
                tabla.addCell("en esas fechas");
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

}