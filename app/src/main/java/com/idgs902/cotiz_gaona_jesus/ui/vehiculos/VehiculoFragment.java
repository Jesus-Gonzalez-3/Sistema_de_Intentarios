package com.idgs902.cotiz_gaona_jesus.ui.vehiculos;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentVehiculoBinding;
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

public class VehiculoFragment extends Fragment {

    private Button btnAgregarV, btnBuscarV, btnGenerarV;
    private TextInputEditText etBusquedaV;
    private TextView txtV;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private FragmentVehiculoBinding binding;
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Vehiculos.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Vehiculo> lista = new ArrayList<Vehiculo>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentVehiculoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarV = root.findViewById(R.id.btnAgregarV);
        btnBuscarV = root.findViewById(R.id.btnBuscarV);
        btnGenerarV = root.findViewById(R.id.btnReporteV);
        etBusquedaV = root.findViewById(R.id.etBusquedaV);
        txtV = root.findViewById(R.id.txtV);

        try {
            db = getActivity().openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM vehiculo;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Vehiculo v = new Vehiculo();
                    v.setClave(c.getString(0));
                    v.setNombre(c.getString(1));
                    v.setMarca(c.getString(2));
                    v.setModelo(c.getString(3));
                    v.setCosto(c.getString(4));
                    lista.add(v);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtV.setText(cadena);

            }
        } catch (
                Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        btnAgregarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), VehiculosActivity.class);
                startActivity(i);
            }
        });

        btnGenerarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                } catch (
                        Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnBuscarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etBusquedaV.getText() != null && etBusquedaV.getText().toString() != " ") {
                        Intent i = new Intent(getContext(), VehiculosActivity2.class);

                        i.putExtra(DATOS, etBusquedaV.getText().toString());
                        startActivity(i);
                    } else {
                        showMessage("Error", "Tiene que ingresar una clave para busqueda");
                    }

                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
        return root;
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

            // Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Cotizaciones Jesus Gaona"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Desarrollo Para Dispositivos Inteligentes \t\t\t  "), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA, "", 28, Color.BLACK);
            // Añadimos un titulo con la fuente por defecto.
            Paragraph p = new Paragraph("Empleados", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Clave");
            tabla.addCell("Nombre");
            tabla.addCell("Marca");
            tabla.addCell("Modelo");
            tabla.addCell("Costo");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).nombre);
                tabla.addCell(lista.get(i).marca);
                tabla.addCell(lista.get(i).modelo);
                tabla.addCell(lista.get(i).costo);
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
                    showMessage("Info", String.valueOf(creado));
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

    public class Vehiculo {
        String clave;
        String nombre;
        String modelo;
        String marca;
        String costo;

        public Vehiculo() {
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public String getMarca() {
            return marca;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

        public String getCosto() {
            return costo;
        }

        public void setCosto(String costo) {
            this.costo = costo;
        }
    }
}