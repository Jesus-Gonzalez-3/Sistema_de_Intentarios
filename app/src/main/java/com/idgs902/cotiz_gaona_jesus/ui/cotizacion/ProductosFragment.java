package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

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
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentProductosBinding;
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

public class ProductosFragment extends Fragment {

    private FragmentProductosBinding binding;
    private Button btnAgregarCot, btnBuscarCot, btnGenerarCot;
    private TextInputEditText etBusquedaCot;
    private TextView txtCot;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Productos.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Producto> lista = new ArrayList<Producto>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarCot = root.findViewById(R.id.btnAgregarP);
        btnBuscarCot = root.findViewById(R.id.btnBuscarP);
        etBusquedaCot = root.findViewById(R.id.etBusquedaP);
        btnGenerarCot = root.findViewById(R.id.btnReporteP);
        txtCot = root.findViewById(R.id.txtP);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM producto;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Producto cot = new Producto();
                    cot.setId(c.getString(0));
                    cot.setClave(c.getString(1));
                    cot.setNombre(c.getString(2));
                    cot.setLinea(c.getString(3));
                    cot.setExistencia(c.getString(4));
                    cot.setPcosto(c.getString(5));
                    cot.setPCpromedio(c.getString(6));
                    cot.setPMenudeo(c.getString(7));
                    cot.setPMayoreo(c.getString(8));
                    lista.add(cot);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtCot.setText(cadena);
            }
        } catch (
                Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnBuscarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etBusquedaCot.getText() != null && etBusquedaCot.getText().toString() != " ") {
                        Intent i = new Intent(getContext(), ProductosActivity2.class);

                        i.putExtra(DATOS, etBusquedaCot.getText().toString());
                        startActivityForResult(i,1,new Bundle());
                    } else {
                        showMessage("Error", "Tiene que ingresar una clave para busqueda");
                    }

                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGenerarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnAgregarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProductosActivity.class);
                startActivityForResult(i,1,new Bundle());
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
    public void onDestroy() {
        super.onDestroy();
        binding = null;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM producto;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Producto cot = new Producto();
                    cot.setId(c.getString(0));
                    cot.setClave(c.getString(1));
                    cot.setNombre(c.getString(2));
                    cot.setLinea(c.getString(3));
                    cot.setExistencia(c.getString(4));
                    cot.setPcosto(c.getString(5));
                    cot.setPCpromedio(c.getString(6));
                    cot.setPMenudeo(c.getString(7));
                    cot.setPMayoreo(c.getString(8));
                    lista.add(cot);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtCot.setText(cadena);
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
            Paragraph p = new Paragraph("Cotizaciones", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(9);
            tabla.addCell("ID");
            tabla.addCell("Clave");
            tabla.addCell("Nombre");
            tabla.addCell("Linea");
            tabla.addCell("Existencia");
            tabla.addCell("Costo Producto");
            tabla.addCell("Costo Pormedio Producto");
            tabla.addCell("Costo Menudeo");
            tabla.addCell("Costo Mayoreo");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).Id);
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).nombre);
                tabla.addCell(lista.get(i).linea);
                tabla.addCell(lista.get(i).existencia);
                tabla.addCell(lista.get(i).Pcosto);
                tabla.addCell(lista.get(i).PCpromedio);
                tabla.addCell(lista.get(i).PMenudeo);
                tabla.addCell(lista.get(i).PMayoreo);
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

    public class Producto {
        String Id, clave, nombre, linea, existencia, Pcosto , PCpromedio, PMenudeo , PMayoreo;

        public Producto() {
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
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

        public String getLinea() {
            return linea;
        }

        public void setLinea(String linea) {
            this.linea = linea;
        }

        public String getExistencia() {
            return existencia;
        }

        public void setExistencia(String existencia) {
            this.existencia = existencia;
        }

        public String getPcosto() {
            return Pcosto;
        }

        public void setPcosto(String pcosto) {
            Pcosto = pcosto;
        }

        public String getPCpromedio() {
            return PCpromedio;
        }

        public void setPCpromedio(String PCpromedio) {
            this.PCpromedio = PCpromedio;
        }

        public String getPMenudeo() {
            return PMenudeo;
        }

        public void setPMenudeo(String PMenudeo) {
            this.PMenudeo = PMenudeo;
        }

        public String getPMayoreo() {
            return PMayoreo;
        }

        public void setPMayoreo(String PMayoreo) {
            this.PMayoreo = PMayoreo;
        }
    }
}