package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

public class CotizacionesActivity2 extends AppCompatActivity {

    private Button btnModificarCot, btnEliminarCot, btnConsultarCot;
    private TextInputEditText etClaveCotM, etFechaCotM, etEngancheM;
    private Spinner spnEmpleadoM, spnClienteM, spnVehiculoM, spnPlazosM;
    private SQLiteDatabase db;

    private String[] Plazos = {"Seleccione", "12", "24", "36", "48", "60"};
    private String[] Cliente = null;
    private String[] Empleado = null;
    private String[] Vehiculo = null;
    private final static String NOMBRE_DIRECTORIO = "MiPdf";
    private final static String NOMBRE_DOCUMENTO = "Cotizaciones";
    private final static String ETIQUETA_ERROR = "ERROR";

    private static boolean creado = false;
    private String TasaInteres = "";
    private String TasaAnual = "";
    private String Enganche = "";
    private String Saldo = "";
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    int meses = 0;

    public List<CotizacionDetalle> lista = new ArrayList<CotizacionDetalle>();
    public List<CotizacionDetalle> lista2 = new ArrayList<CotizacionDetalle>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizaciones2);

        btnModificarCot = this.findViewById(R.id.btnModificarCot);
        btnEliminarCot = this.findViewById(R.id.btnEliminarCot);
        btnConsultarCot = this.findViewById(R.id.btnConsultarCot);
        etClaveCotM = this.findViewById(R.id.etClaveCotM);
        etFechaCotM = this.findViewById(R.id.etFechaCotM);
        etEngancheM = this.findViewById(R.id.etEngacheM);

        spnClienteM = this.findViewById(R.id.spnClienteM);
        spnEmpleadoM = this.findViewById(R.id.spnEmpleadoM);
        spnVehiculoM = this.findViewById(R.id.spnVehiculoM);
        spnPlazosM = this.findViewById(R.id.spnPlazosM);

        spnPlazosM.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Plazos));

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * FROM empleado where puesto = 'Asesor de ventas';", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                StringBuilder cadena = new StringBuilder();
                Empleado = new String[c.getCount() + 1];
                Empleado[0] = "Seleccione";
                for (int i = 0; i < c.getCount(); i++) {
                    Empleado[i + 1] = c.getString(1);
                    c.moveToNext();
                }
            }
            Cursor c2 = db.rawQuery("SELECT * FROM cliente;", null);
            if (c2.getCount() != 0) {
                c2.moveToFirst();
                StringBuilder cadena = new StringBuilder();
                Cliente = new String[c2.getCount() + 1];
                Cliente[0] = "Seleccione";
                for (int i = 0; i < c2.getCount(); i++) {
                    Cliente[i + 1] = c2.getString(1);
                    c2.moveToNext();
                }
            }
            Cursor c3 = db.rawQuery("SELECT nombre, costo FROM vehiculo;", null);
            if (c3.getCount() != 0) {
                c3.moveToFirst();
                StringBuilder cadena = new StringBuilder();
                Vehiculo = new String[c3.getCount() + 1];
                Vehiculo[0] = "Seleccione";
                for (int i = 0; i < c3.getCount(); i++) {
                    Vehiculo[i + 1] = c3.getString(0) + " ¬ " + c3.getString(1);
                    c3.moveToNext();
                }
            }
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        spnEmpleadoM.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Empleado));
        spnClienteM.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Cliente));
        spnVehiculoM.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Vehiculo));

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(CotizacionesFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM cotizacion WHERE clave ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveCotM.setText(c.getString(0));
                    etFechaCotM.setText(c.getString(1));
                    int pos = 0;
                    int pos1 = 0;
                    int pos2 = 0;
                    int pos3 = 0;

                    for (int j = 0; j < Empleado.length; j++) {
                        if (Empleado[j].equalsIgnoreCase(c.getString(2))) {
                            pos = j;
                        }
                    }
                    spnEmpleadoM.setSelection(pos, true);
                    for (int k = 0; k < Cliente.length; k++) {
                        if (Cliente[k].equalsIgnoreCase(c.getString(3))) {
                            pos1 = k;
                        }
                    }
                    spnClienteM.setSelection(pos1, true);
                    for (int m = 0; m < Vehiculo.length; m++) {
                        if (Vehiculo[m].startsWith(c.getString(4))) {
                            pos2 = m;
                        }
                    }
                    spnVehiculoM.setSelection(pos2);
                    etEngancheM.setText(c.getString(6));
                    for (int n = 0; n < Plazos.length; n++) {
                        if (Plazos[n].equalsIgnoreCase(c.getString(8))) {
                            pos3 = n;
                        }
                    }
                    spnPlazosM.setSelection(pos3);
                    etClaveCotM.setEnabled(false);
                    TasaInteres = c.getString(9);
                    TasaAnual = c.getString(10);
                    Enganche = c.getString(7);

                }
            } else {
                showMessage("Info", "No existe un Registro con esa clave");
            }

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnConsultarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    generarCalculos();

                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnEliminarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnModificarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modificar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });


    }

    private void generarCalculos() {
        try {
            double costoVehiculo = Double.parseDouble(spnVehiculoM.getSelectedItem().toString().split("¬")[1]);
            double enganchePorcentaje = Double.parseDouble(etEngancheM.getText().toString());
            double plazo = Double.parseDouble(spnPlazosM.getSelectedItem().toString());
            double tasaInteres = Double.parseDouble(TasaInteres);
            double saldo = (costoVehiculo - Double.parseDouble(Enganche));
            double AbonoCapital = saldo / plazo;
            double newSaldo = saldo;
            Saldo = String.valueOf(saldo);

            double totalCap = costoVehiculo;
            double totalInteres = 0;
            double totalPagosB = 0;
            CotizacionDetalle c = new CotizacionDetalle();
            DecimalFormat formato1 = new DecimalFormat("#.##");

            for (int i = 0; i < plazo; i++) {
                CotizacionDetalle cd = new CotizacionDetalle();
                cd.setNoPago(String.valueOf(i + 1));
                cd.setConcepto("Pago");
                cd.setCapital(String.valueOf(AbonoCapital));
                double interes = (newSaldo * (tasaInteres * 0.01));
                cd.setInteres(String.valueOf(formato1.format(interes)));
                totalInteres = totalInteres + interes;
                double pagoB = AbonoCapital + interes;
                cd.setPagosBanco(String.valueOf(formato1.format(pagoB)));
                totalPagosB = totalPagosB + pagoB;
                newSaldo = (newSaldo - pagoB) + interes;
                if (newSaldo <= 0) {
                    newSaldo = 0;
                }
                meses = i+1;
                try {
                    Date fecha = formatter.parse(etFechaCotM.getText().toString());
                    calendar.setTime(fecha);
                    calendar.add(Calendar.MONTH, meses);
                    Date calendar_fecha = calendar.getTime();
                    cd.setFechaP(String.valueOf(formatter.format(calendar_fecha)));
                }catch (Exception ex){
                    showMessage(ETIQUETA_ERROR, ex.getMessage());
                }
                cd.setSaldoBanco(String.valueOf(formato1.format(newSaldo)));
                lista.add(cd);
            }
            c.setTotalCapital(String.valueOf(formato1.format(totalCap)));
            c.setTotalInteres(String.valueOf(formato1.format(totalInteres)));
            c.setTotalPagosB(String.valueOf(formato1.format(totalPagosB)));
            lista2.add(c);
            generarPdf();
        } catch (Exception ex) {
            showMessage(ETIQUETA_ERROR, ex.getMessage());
        }
    }

    public void modificar() {
        if (spnClienteM.getSelectedItemPosition() != 0 ||
                spnVehiculoM.getSelectedItemPosition() != 0 ||
                spnEmpleadoM.getSelectedItemPosition() != 0 ||
                spnPlazosM.getSelectedItemPosition() != 0) {
            if (etClaveCotM.getText().toString().trim().length() != 0
                    && etEngancheM.getText().toString().trim().length() != 0
                    && etFechaCotM.getText().toString().trim().length() != 0) {
                String Tasa = "";
                double tasa = 0;
                double enganche = 0;
                double costo = Double.parseDouble(spnVehiculoM.getSelectedItem().toString().split("¬")[1]);
                switch (spnPlazosM.getSelectedItemPosition()) {
                    case 1:
                        Tasa = "1.5";
                        tasa = 0.015;
                        break;
                    case 2:
                        Tasa = "2.5";
                        tasa = 0.025;
                        break;
                    case 3:
                        Tasa = "3.0";
                        tasa = 0.03;
                        break;
                    case 4:
                        Tasa = "3.5";
                        tasa = 0.035;
                        break;
                    case 5:
                        Tasa = "4.0";
                        tasa = 0.04;
                        break;
                }

                enganche = costo * (Double.parseDouble(etEngancheM.getText().toString()) * 0.01);
                String TasaAnual = String.valueOf((Double.parseDouble(spnPlazosM.getSelectedItem().toString()) *
                        Double.parseDouble(Tasa)));
                String vehiculo = spnVehiculoM.getSelectedItem().toString().split("¬")[0];

                try {
                    db.execSQL("UPDATE cotizacion SET fecha ='" + etFechaCotM.getText() +
                            "', vendedor ='" + spnEmpleadoM.getSelectedItem().toString() +
                            "', cliente ='" + spnClienteM.getSelectedItem().toString() +
                            "', vehiculo ='" + vehiculo +
                            "', costo ='" + String.valueOf(costo) +
                            "', enganchePorcentaje ='" + etEngancheM.getText().toString() +
                            "', enganche ='" + String.valueOf(enganche) +
                            "', plazo ='" + spnPlazosM.getSelectedItem().toString() +
                            "', tasaInteres ='" + Tasa +
                            "', tasaAnual ='" + TasaAnual +
                            "' WHERE clave = '" + etClaveCotM.getText() + "';");
                    showMessage("Exito!", "Cotización Modificada");
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }


            finish();
        } else {
            showMessage("Info", "Debe Llenar todos los datos");
        }

    }

    public void eliminar() {
        try {
            db.execSQL("DELETE FROM cotizacion WHERE clave ='" +
                    etClaveCotM.getText() + "' AND  fecha ='" + etFechaCotM.getText() + "';");
            showMessage("Exito!", "Cotización Eliminada");
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        finish();
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
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

            // Incluimos el pie de pagina y una cabecera
            HeaderFooter cabecera = new HeaderFooter(new Phrase(
                    "Cotizaciones Jesus Gaona"), false);
            HeaderFooter pie = new HeaderFooter(new Phrase(
                    "Desarrollo Para Dispositivos Inteligentes \t\t\t  "), false);

            documento.setHeader(cabecera);
            documento.setFooter(pie);

            // Abrimos el documento.
            documento.open();

            Font font1 = FontFactory.getFont(FontFactory.HELVETICA, "", 28, Color.BLACK);
            Font font = FontFactory.getFont(FontFactory.HELVETICA, "", 12, Color.BLACK);
            // Añadimos un titulo con la fuente por defecto.
            Paragraph p = new Paragraph("Cotizacion del Credito Automotriz", font1);
            p.setAlignment(Element.ALIGN_CENTER);
            documento.add(p);

            Paragraph p2 = new Paragraph("Cotizacion Num: " + etClaveCotM.getText().toString(), font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p2);

            Paragraph p3 = new Paragraph("Fecha: " + etFechaCotM.getText().toString(), font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p3);
            Paragraph p4 = new Paragraph("Vendedor: " + spnEmpleadoM.getSelectedItem().toString(), font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p4);

            Paragraph p5 = new Paragraph("Cliente: " + spnClienteM.getSelectedItem().toString(), font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p5);

            Paragraph p6 = new Paragraph("Vehiculo: " + spnVehiculoM.getSelectedItem().toString().split("¬")[0], font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p6);
            Paragraph p7 = new Paragraph("Monto: " + spnVehiculoM.getSelectedItem().toString().split("¬")[1], font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p7);
            Paragraph p8 = new Paragraph("% Enganche: " + etEngancheM.getText().toString() + " %", font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p8);
            Paragraph p9 = new Paragraph("Enganche: " + Enganche, font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p9);
            Paragraph p10 = new Paragraph("Saldo: " + Saldo, font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p10);
            Paragraph p11 = new Paragraph("Plazo: " + spnPlazosM.getSelectedItem().toString() + " Meses", font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p11);
            Paragraph p12 = new Paragraph("Tasa de Interes: " + TasaInteres + " % \t\t\t Tasa Anual: " + TasaAnual + "%", font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p12);

            Paragraph p13 = new Paragraph(" ", font);
            p.setAlignment(Element.ALIGN_LEFT);
            documento.add(p13);
            documento.add(p13);

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(7);
            tabla.addCell("No Pagos");
            tabla.addCell("Fecha Pagos");
            tabla.addCell("Concepto");
            tabla.addCell("Capital");
            tabla.addCell("Interes");
            tabla.addCell("Pago Total a Banco");
            tabla.addCell("Saldo del Banco");
            for (int i = 0; i < lista.size(); i++) {
                tabla.addCell(lista.get(i).noPago);
                tabla.addCell(lista.get(i).fechaP);
                tabla.addCell(lista.get(i).concepto);
                tabla.addCell(lista.get(i).capital);
                tabla.addCell(lista.get(i).interes);
                tabla.addCell(lista.get(i).pagosBanco);
                tabla.addCell(lista.get(i).SaldoBanco);
            }
            documento.add(tabla);

            documento.add(p13);
            documento.add(p13);

            PdfPTable tabla2 = new PdfPTable(3);
            tabla2.addCell("Total Capital");
            tabla2.addCell("Total Interes");
            tabla2.addCell("Total Pagos a Banco");
            for (int i = 0; i < lista2.size(); i++) {
                tabla2.addCell(lista2.get(i).totalCapital);
                tabla2.addCell(lista2.get(i).totalInteres);
                tabla2.addCell(lista2.get(i).totalPagosB);
            }
            documento.add(tabla2);

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
        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yyyy");
        nombreFichero = etClaveCotM.getText().toString() + "_" + nombreFichero + fecha.format(date) + ".pdf";
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

    public class CotizacionDetalle {
        String noPago, fechaP, concepto, capital, interes, pagosBanco, SaldoBanco, totalCapital, totalInteres, totalPagosB;

        public CotizacionDetalle() {
        }

        public String getTotalCapital() {
            return totalCapital;
        }

        public void setTotalCapital(String totalCapital) {
            this.totalCapital = totalCapital;
        }

        public String getTotalInteres() {
            return totalInteres;
        }

        public void setTotalInteres(String totalInteres) {
            this.totalInteres = totalInteres;
        }

        public String getTotalPagosB() {
            return totalPagosB;
        }

        public void setTotalPagosB(String totalPagosB) {
            this.totalPagosB = totalPagosB;
        }

        public String getNoPago() {
            return noPago;
        }

        public void setNoPago(String noPago) {
            this.noPago = noPago;
        }

        public String getFechaP() {
            return fechaP;
        }

        public void setFechaP(String fechaP) {
            this.fechaP = fechaP;
        }

        public String getConcepto() {
            return concepto;
        }

        public void setConcepto(String concepto) {
            this.concepto = concepto;
        }

        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public String getInteres() {
            return interes;
        }

        public void setInteres(String interes) {
            this.interes = interes;
        }

        public String getPagosBanco() {
            return pagosBanco;
        }

        public void setPagosBanco(String pagosBanco) {
            this.pagosBanco = pagosBanco;
        }

        public String getSaldoBanco() {
            return SaldoBanco;
        }

        public void setSaldoBanco(String saldoBanco) {
            SaldoBanco = saldoBanco;
        }
    }
}