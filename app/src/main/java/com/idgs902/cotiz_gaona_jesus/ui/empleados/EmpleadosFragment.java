package com.idgs902.cotiz_gaona_jesus.ui.empleados;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentEmpleadosBinding;
import com.idgs902.cotiz_gaona_jesus.ui.cliente.ClienteFragment;
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

public class EmpleadosFragment extends Fragment {

    private FragmentEmpleadosBinding binding;
    private Button btnAgregarE, btnBuscarE, btnGenerarE;
    private TextInputEditText etBusquedaE;
    private TextView txtE;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Empleados.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Empleado> lista = new ArrayList<Empleado>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmpleadosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarE = root.findViewById(R.id.btnAgregarE);
        btnBuscarE = root.findViewById(R.id.btnBuscarE);
        etBusquedaE = root.findViewById(R.id.etBusquedaE);
        txtE = root.findViewById(R.id.txtE);
        btnGenerarE = root.findViewById(R.id.btnReporteE);

        try {
            db = getActivity().openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM empleado;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Empleado em = new Empleado();
                    em.setClave(c.getString(0));
                    em.setNombre(c.getString(1));
                    em.setPuesto(c.getString(2));
                    em.setFecha(c.getString(3));

                    lista.add(em);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtE.setText(cadena);

            }
        } catch (
                Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        btnAgregarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EmpleadosActivity.class);
                startActivity(i);
            }
        });

        btnBuscarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etBusquedaE.getText() != null && etBusquedaE.getText().toString() != " ") {
                        Intent i = new Intent(getContext(), EmpleadosActivity2.class);

                        i.putExtra(DATOS, etBusquedaE.getText().toString());
                        startActivity(i);
                    } else {
                        showMessage("Error", "Tiene que ingresar una clave para busqueda");
                    }

                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGenerarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                }catch (Exception ex) {
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
            tabla.addCell("Puesto");
            tabla.addCell("Fecha Ingreso");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).nombre);
                tabla.addCell(lista.get(i).puesto);
                tabla.addCell(lista.get(i).fecha);
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

    public class Empleado {
        String clave;
        String nombre;
        String puesto;
        String fecha;

        public Empleado() {
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

        public String getPuesto() {
            return puesto;
        }

        public void setPuesto(String puesto) {
            this.puesto = puesto;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }
}