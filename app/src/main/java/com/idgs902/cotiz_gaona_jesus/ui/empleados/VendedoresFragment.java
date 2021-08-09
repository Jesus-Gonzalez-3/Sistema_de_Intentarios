package com.idgs902.cotiz_gaona_jesus.ui.empleados;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentVendedoresBinding;
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

public class VendedoresFragment extends Fragment {

    private FragmentVendedoresBinding binding;
    private Button btnAgregarE, btnBuscarE, btnGenerarE;
    private TextInputEditText etBusquedaE;
    private TextView txtE;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Vendedores.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Vendedor> lista = new ArrayList<Vendedor>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVendedoresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarE = root.findViewById(R.id.btnAgregarE);
        btnBuscarE = root.findViewById(R.id.btnBuscarE);
        etBusquedaE = root.findViewById(R.id.etBusquedaE);
        txtE = root.findViewById(R.id.txtE);
        btnGenerarE = root.findViewById(R.id.btnReporteE);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM vendedor;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Vendedor ven = new Vendedor();
                    ven.setId(String.valueOf(c.getInt(0)));
                    ven.setNombre(c.getString(1));
                    ven.setCalle(c.getString(2));
                    ven.setColonia(c.getString(3));
                    ven.setTelefono(c.getString(4));
                    ven.setEmail(c.getString(5));
                    ven.setComisiones(c.getString(6));

                    lista.add(ven);
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
                Intent i = new Intent(getContext(), VendedoresActivity.class);
                startActivityForResult(i,1, new Bundle());
            }
        });

        btnBuscarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etBusquedaE.getText() != null && etBusquedaE.getText().toString() != " ") {
                        Intent i = new Intent(getContext(), VendedoresActivity2.class);

                        i.putExtra(DATOS, etBusquedaE.getText().toString());
                        startActivityForResult(i, 1, new Bundle());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        txtE.setText("");
        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM vendedor;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Vendedor ven = new Vendedor();
                    ven.setId(String.valueOf(c.getInt(0)));
                    ven.setNombre(c.getString(1));
                    ven.setCalle(c.getString(2));
                    ven.setColonia(c.getString(3));
                    ven.setTelefono(c.getString(4));
                    ven.setEmail(c.getString(5));
                    ven.setComisiones(c.getString(6));

                    lista.add(ven);
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
            Paragraph p = new Paragraph("Vendedores\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            Paragraph p2 = new Paragraph(" ", font);
            p2.setAlignment(Element.ALIGN_CENTER);
            documento.add(p2);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(7);
            tabla.addCell("ID");
            tabla.addCell("Nombre");
            tabla.addCell("Calle");
            tabla.addCell("Colonia");
            tabla.addCell("Teléfono");
            tabla.addCell("Email");
            tabla.addCell("Comisiones");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).Id);
                tabla.addCell(lista.get(i).nombre);
                tabla.addCell(lista.get(i).calle);
                tabla.addCell(lista.get(i).colonia);
                tabla.addCell(lista.get(i).telefono);
                tabla.addCell(lista.get(i).email);
                tabla.addCell(lista.get(i).comisiones);
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

    public class Vendedor {
        String Id;
        String nombre;
        String calle;
        String colonia;
        String telefono;
        String email;
        String comisiones;

        public Vendedor() {
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
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

        public String getComisiones() {
            return comisiones;
        }

        public void setComisiones(String comisiones) {
            this.comisiones = comisiones;
        }
    }
}