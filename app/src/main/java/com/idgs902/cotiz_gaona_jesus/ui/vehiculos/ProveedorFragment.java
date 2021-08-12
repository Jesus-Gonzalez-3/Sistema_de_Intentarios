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
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentProveedorBinding;
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

public class ProveedorFragment extends Fragment {

    private Button btnAgregarP, btnBuscarP, btnGenerarP;
    private TextInputEditText etBusquedaP;
    private TextView txtP;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private FragmentProveedorBinding binding;
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Proveedor.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Proveedor> lista = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProveedorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarP = root.findViewById(R.id.btnAgregarPr);
        btnBuscarP = root.findViewById(R.id.btnBuscarPr);
        btnGenerarP = root.findViewById(R.id.btnReportePr);
        etBusquedaP = root.findViewById(R.id.etBusquedaPr);
        txtP = root.findViewById(R.id.txtPr);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM proveedor;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Proveedor prv = new Proveedor();
                    prv.setClave(c.getString(0));
                    prv.setNombre(c.getString(1));
                    prv.setCalle(c.getString(2));
                    prv.setColonia(c.getString(3));
                    prv.setCiudad(c.getString(4));
                    prv.setRfc(c.getString(5));
                    prv.setTelefono(c.getString(6));
                    prv.setEmail(c.getString(7));
                    prv.setSaldo(c.getString(8));

                    lista.add(prv);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtP.setText(cadena);

            }
        } catch (
                Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        btnAgregarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProveedorActivity.class);
                startActivity(i);
            }
        });

        btnGenerarP.setOnClickListener(new View.OnClickListener() {
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

        btnBuscarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etBusquedaP.getText() != null && etBusquedaP.getText().toString() != " ") {
                        Intent i = new Intent(getContext(), ProveedorActivity2.class);

                        i.putExtra(DATOS, etBusquedaP.getText().toString());
                        startActivity(i);
                    } else {
                        showMessage("Error", "Tiene que ingresar una No. para busqueda");
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
                    "Sistema de Inventarios Zapateria Jessi"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Desarrollado por: \t Jessica Anahi Muñoz\t Jesus Guadalupe Gaona\t  "), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA, "", 28, Color.BLACK);
            // Añadimos un titulo con la fuente por defecto.
            Paragraph p = new Paragraph("Proveedores", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(9);
            tabla.addCell("No");
            tabla.addCell("Nombre");
            tabla.addCell("Calle");
            tabla.addCell("Colonia");
            tabla.addCell("Ciudad");
            tabla.addCell("RFC");
            tabla.addCell("Teléfono");
            tabla.addCell("Correo electrónico");
            tabla.addCell("Saldo");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).nombre);
                tabla.addCell(lista.get(i).calle);
                tabla.addCell(lista.get(i).colonia);
                tabla.addCell(lista.get(i).ciudad);
                tabla.addCell(lista.get(i).rfc);
                tabla.addCell(lista.get(i).telefono);
                tabla.addCell(lista.get(i).email);
                tabla.addCell(lista.get(i).saldo);
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


    public class Proveedor {
        String clave;
        String nombre;
        String calle;
        String colonia;
        String ciudad;
        String rfc;
        String telefono;
        String email;
        String saldo;

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

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getColonia() {
            return colonia;
        }

        public void setColonia(String colonia) {
            this.colonia = colonia;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getRfc() {
            return rfc;
        }

        public void setRfc(String rfc) {
            this.rfc = rfc;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSaldo() {
            return saldo;
        }

        public void setSaldo(String saldo) {
            this.saldo = saldo;
        }

        public Proveedor() {
        }
    }
}