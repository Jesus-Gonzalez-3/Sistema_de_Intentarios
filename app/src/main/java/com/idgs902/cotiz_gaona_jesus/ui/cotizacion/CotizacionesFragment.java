package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentCotizacionesBinding;
import com.idgs902.cotiz_gaona_jesus.databinding.FragmentEmpleadosBinding;
import com.idgs902.cotiz_gaona_jesus.ui.empleados.EmpleadosActivity;
import com.idgs902.cotiz_gaona_jesus.ui.empleados.EmpleadosActivity2;
import com.idgs902.cotiz_gaona_jesus.ui.vehiculos.VehiculoFragment;
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

public class CotizacionesFragment extends Fragment {

    private FragmentCotizacionesBinding binding;
    private Button btnAgregarCot, btnBuscarCot, btnGenerarCot;
    private TextInputEditText etBusquedaCot;
    private TextView txtCot;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Cotizaciones.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Cotizacion> lista = new ArrayList<Cotizacion>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCotizacionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarCot = root.findViewById(R.id.btnAgregarCot);
        btnBuscarCot = root.findViewById(R.id.btnBuscarCot);
        etBusquedaCot = root.findViewById(R.id.etBusquedaCot);
        btnGenerarCot = root.findViewById(R.id.btnReporteCot);
        txtCot = root.findViewById(R.id.txtCot);

        try {
            db = getActivity().openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);

            Cursor c = db.rawQuery("SELECT * FROM cotizacion;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {
                    Cotizacion cot = new Cotizacion();
                    cot.setClave(c.getString(0));
                    cot.setFecha(c.getString(1));
                    cot.setVendedor(c.getString(2));
                    cot.setCliente(c.getString(3));
                    cot.setVehiculo(c.getString(4));
                    cot.setCosto(c.getString(5));
                    cot.setEnganchePorcentaje(c.getString(6));
                    cot.setEncganche(c.getString(7));
                    cot.setPlazo(c.getString(8));
                    cot.setTasaInteres(c.getString(9));
                    cot.setTasaAnual(c.getString(10));
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
                        Intent i = new Intent(getContext(), CotizacionesActivity2.class);

                        i.putExtra(DATOS, etBusquedaCot.getText().toString());
                        startActivity(i);
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
                Intent i = new Intent(getContext(), CotizacionesActivity.class);
                startActivity(i);
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
            Paragraph p = new Paragraph("Cotizaciones", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(8);
            tabla.addCell("Clave");
            tabla.addCell("Fecha");
            tabla.addCell("Vendedor");
            tabla.addCell("Cliente");
            tabla.addCell("Vehiculo");
            tabla.addCell("Costo");
            tabla.addCell("Enganche");
            tabla.addCell("Plazos");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).fecha);
                tabla.addCell(lista.get(i).vendedor);
                tabla.addCell(lista.get(i).cliente);
                tabla.addCell(lista.get(i).vehiculo);
                tabla.addCell(lista.get(i).costo);
                tabla.addCell(lista.get(i).encganche);
                tabla.addCell(lista.get(i).plazo);
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

    public class Cotizacion {
        String clave, fecha, vendedor, cliente, vehiculo, costo, enganchePorcentaje, encganche, plazo, tasaInteres, TasaAnual;

        public Cotizacion() {
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getVendedor() {
            return vendedor;
        }

        public void setVendedor(String vendedor) {
            this.vendedor = vendedor;
        }

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getVehiculo() {
            return vehiculo;
        }

        public void setVehiculo(String vehiculo) {
            this.vehiculo = vehiculo;
        }

        public String getCosto() {
            return costo;
        }

        public void setCosto(String costo) {
            this.costo = costo;
        }

        public String getEnganchePorcentaje() {
            return enganchePorcentaje;
        }

        public void setEnganchePorcentaje(String enganchePorcentaje) {
            this.enganchePorcentaje = enganchePorcentaje;
        }

        public String getEncganche() {
            return encganche;
        }

        public void setEncganche(String encganche) {
            this.encganche = encganche;
        }

        public String getPlazo() {
            return plazo;
        }

        public void setPlazo(String plazo) {
            this.plazo = plazo;
        }

        public String getTasaInteres() {
            return tasaInteres;
        }

        public void setTasaInteres(String tasaInteres) {
            this.tasaInteres = tasaInteres;
        }

        public String getTasaAnual() {
            return TasaAnual;
        }

        public void setTasaAnual(String tasaAnual) {
            TasaAnual = tasaAnual;
        }
    }
}