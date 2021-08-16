package com.idgs902.cotiz_gaona_jesus.ui.compras;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.databinding.ComprasFragmentBinding;

import android.app.AlertDialog.Builder;
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

import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.cotizacion.ProductosActivity2;
import com.idgs902.cotiz_gaona_jesus.ui.cotizacion.ProductosFragment;
import com.idgs902.cotiz_gaona_jesus.ui.empleados.VendedoresFragment;
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

public class ComprasFragment extends Fragment {

    public ComprasFragmentBinding binding;
    private Button btnAgregarCom, btnBuscarCom, btnGenerarCom;
    private TextInputEditText etBusquedaCom;
    private TextView txtCom;

    private SQLiteDatabase db;

    static final String DATOS = "Datos";
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Compras.pdf";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private List<Compras> lista = new ArrayList<Compras>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ComprasFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAgregarCom = root.findViewById(R.id.btnAgregarCom);
        btnBuscarCom = root.findViewById(R.id.btnBuscarCom);
        btnGenerarCom = root.findViewById(R.id.btnReporteCom);
        etBusquedaCom = root.findViewById(R.id.etBusquedaCom);
        txtCom = root.findViewById(R.id.txtCom);

        try {
            db = getActivity().openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM compras;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Compras com = new Compras();
                    com.setId(c.getString(0));
                    com.setClave(c.getString(1));
                    com.setClave_p(c.getString(2));
                    com.setNombre_p(c.getString(3));
                    com.setCalle_p(c.getString(4));
                    com.setFecha(c.getString(5));
                    com.setTotal_pares(c.getString(6));
                    com.setSubtotal(c.getString(7));
                    com.setIva(c.getString(8));
                    com.setTotal(c.getString(9));
                    com.setTipo_recibo(c.getString(10));
                    com.setClave_recibo(c.getString(11));
                    lista.add(com);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtCom.setText(cadena);

            }
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(getActivity(), ComprasActivity.class);
                    startActivityForResult(i, 1, new Bundle());
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGenerarCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarPdf();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnBuscarCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                 buscarCompra();
                }catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        return root;
    }

    public void buscarCompra(){
        if(etBusquedaCom.getText().toString().trim().length()!=0){
            Intent i = new Intent(getContext(), ComprasActivity2.class);

            i.putExtra(DATOS, etBusquedaCom.getText().toString());
            startActivityForResult(i,1,new Bundle());
        }else {
            showMessage("Info", "Debe Ingresar una clave a buscar");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        txtCom.setText("");
        lista.clear();
        try {
            Cursor c = db.rawQuery("SELECT * FROM compras;", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();

                for (int i = 0; i < c.getCount(); i++) {

                    Compras com = new Compras();
                    com.setId(c.getString(0));
                    com.setClave(c.getString(1));
                    com.setClave_p(c.getString(2));
                    com.setNombre_p(c.getString(3));
                    com.setCalle_p(c.getString(4));
                    com.setFecha(c.getString(5));
                    com.setTotal_pares(c.getString(6));
                    com.setSubtotal(c.getString(7));
                    com.setIva(c.getString(8));
                    com.setTotal(c.getString(9));
                    com.setTipo_recibo(c.getString(10));
                    com.setClave_recibo(c.getString(11));
                    lista.add(com);
                    for (int j = 0; j < c.getColumnCount(); j++) {
                        cadena.append(c.getColumnName(j) + ": " + c.getString(j) + "\t \t");
                    }
                    cadena.append('\n');
                    cadena.append('\n');
                    c.moveToNext();
                }
                txtCom.setText(cadena);
            }
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(getContext());
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
            Paragraph p = new Paragraph("Compras A Proveedores\n\n", font);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(9);
            tabla.addCell("Clave");
            tabla.addCell("Clave Proveedor");
            tabla.addCell("Nombre Proveedor");
            tabla.addCell("Direccion");
            tabla.addCell("Fecha Emisión");
            tabla.addCell("Total Pares");
            tabla.addCell("Total $");
            tabla.addCell("Tipo Recibo");
            tabla.addCell("Clave Recibo");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).clave);
                tabla.addCell(lista.get(i).clave_p);
                tabla.addCell(lista.get(i).nombre_p);
                tabla.addCell(lista.get(i).calle_p);
                tabla.addCell(lista.get(i).fecha);
                tabla.addCell(lista.get(i).total_pares);
                tabla.addCell(lista.get(i).total);
                tabla.addCell(lista.get(i).tipo_recibo);
                tabla.addCell(lista.get(i).clave_recibo);
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

    public class Compras {
        String id, clave, clave_p, nombre_p, calle_p, fecha, total_pares, subtotal, iva, total, tipo_recibo, clave_recibo;

        public String getClave_recibo() {
            return clave_recibo;
        }

        public void setClave_recibo(String clave_recibo) {
            this.clave_recibo = clave_recibo;
        }

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

        public String getClave_p() {
            return clave_p;
        }

        public void setClave_p(String clave_p) {
            this.clave_p = clave_p;
        }

        public String getNombre_p() {
            return nombre_p;
        }

        public void setNombre_p(String nombre_p) {
            this.nombre_p = nombre_p;
        }

        public String getCalle_p() {
            return calle_p;
        }

        public void setCalle_p(String calle_p) {
            this.calle_p = calle_p;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getTotal_pares() {
            return total_pares;
        }

        public void setTotal_pares(String total_pares) {
            this.total_pares = total_pares;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(String subtotal) {
            this.subtotal = subtotal;
        }

        public String getIva() {
            return iva;
        }

        public void setIva(String iva) {
            this.iva = iva;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTipo_recibo() {
            return tipo_recibo;
        }

        public void setTipo_recibo(String tipo_recibo) {
            this.tipo_recibo = tipo_recibo;
        }
    }

}