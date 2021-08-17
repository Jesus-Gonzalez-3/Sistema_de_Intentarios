package com.idgs902.cotiz_gaona_jesus.ui.ventas;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.compras.ComprasFragment;
import com.idgs902.cotiz_gaona_jesus.ui.compras.TableDynamic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VentasActivity2 extends AppCompatActivity {

    private TextInputEditText etClaveCliente, etNombreCliente, etCalleCliente,
            etClaveVendedor, etNombreVendedor, etFechaVenta,
            etComision,  etClaveRecibo, etClaveVenta;
    private Button btnEliminar;
    private TableLayout tableLayout;
    private TextView txtSuma, txtIva, txtTotal, txtTotalPares, txtComisionV;
    private Spinner spnTipo;
    private SQLiteDatabase db;
    private TableDynamic tableDynamic;
    private String[] headers = {"Clave", "Descripción", "Unidad", "Linea", "Cantidad", "PVenta", "Importe"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private String[] Recibos = {"Remision", "Factura"};

    private int TotalParesTabla = 0;
    private double SubtotalTabla = 0;
    private double IvaTabla = 0;
    private double TotalFinalTabla = 0;
    private double TotalComisiones = 0;
    DecimalFormat format = new DecimalFormat("#.##");
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas2);

        etClaveVenta = this.findViewById(R.id.txtClaveVenta);
        etClaveCliente = this.findViewById(R.id.txtClaveCliente);
        etNombreCliente = this.findViewById(R.id.etNombreCliente);
        etCalleCliente = this.findViewById(R.id.etCalleCliente);
        etClaveVendedor = this.findViewById(R.id.txtClaveVendedor);
        etNombreVendedor = this.findViewById(R.id.etNombreVendedor);
        etFechaVenta = this.findViewById(R.id.etFechaVenta);
        etComision = this.findViewById(R.id.txtComisionVend);
        spnTipo = this.findViewById(R.id.spnTipo);
        etClaveRecibo = this.findViewById(R.id.txtClaveRecibo);
        btnEliminar = this.findViewById(R.id.btnEliminarVenta);

        txtSuma = this.findViewById(R.id.tvSumaV);
        txtIva = this.findViewById(R.id.tvIVAV);
        txtTotal = this.findViewById(R.id.tvTotalV);
        txtTotalPares = this.findViewById(R.id.tvTotalPares);
        txtComisionV = this.findViewById(R.id.txtComisionVendedor);

        tableLayout = (TableLayout) this.findViewById(R.id.table);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Intent i = getIntent();
        String clave = i.getStringExtra(VentaFragment.DATOS);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            spnTipo.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Recibos));
            tableDynamic = new TableDynamic(tableLayout, getApplicationContext());
            tableDynamic.addHeader(headers);
            Cursor c = db.rawQuery("SELECT * FROM ventas WHERE id =" + clave + ";", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                id = c.getInt(0);
                etClaveVenta.setText(c.getString(0));
                etClaveCliente.setText(c.getString(1));
                etNombreCliente.setText(c.getString(2));
                etCalleCliente.setText(c.getString(3));
                etClaveVendedor.setText(c.getString(4));
                etNombreVendedor.setText(c.getString(5));
                etFechaVenta.setText(c.getString(6));
                etComision.setText(c.getString(7));
                etClaveRecibo.setText(c.getString(9));
                txtTotalPares.setText("TOTAL PARES: " + c.getString(10));
                txtSuma.setText("SUMA : $" + c.getString(11));
                txtIva.setText("I.V.A. : $" + c.getString(12));
                txtTotal.setText("TOTAL: $" + c.getString(13));
                txtComisionV.setText("Comis Vendedor: $" + c.getString(14));
                //Log.e("Ctipo", c.getString(10));
                switch (c.getString(8)){
                    case "Remision":
                        spnTipo.setSelection(0);
                        break;
                    case "Factura":
                        spnTipo.setSelection(1);
                        break;
                    default:
                        spnTipo.setSelection(0);
                        break;
                }

                Cursor c1 = db.rawQuery("SELECT * FROM ventas_detalle WHERE id_venta=" + id + ";", null);
                if (c1.getCount() != 0) {
                    c1.moveToFirst();
                    for (int m = 0; m < c1.getCount(); m++) {
                        Log.e("TotalColumnas", String.valueOf(c1.getColumnCount()));
                        String[] arreglo = new String[c1.getColumnCount()];
                        int contador = 0;
                        for (int j = 2; j < c1.getColumnCount(); j++) {
                            arreglo[contador] = c1.getString(j);
                            contador++;
                        }
                        rows.add(arreglo);
                        if (!c1.isLast()) c1.moveToNext();
                    }
                }
            } else {
                showMessage("Info", "No existe información con la clave: " + clave);
            }
            tableDynamic.addData(rows);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                }catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void eliminar(){
        try {

            /*Cursor c1 = db.rawQuery("SELECT * FROM cliente where id='" + etClaveCliente.getText().toString() + "'", null);
            c1.moveToFirst();
            int cliente_id = c1.getInt(0);
            double saldo_cliente = Double.parseDouble(c1.getString(8)) - Double.parseDouble(txtTotal.getText().toString());
            db.execSQL("UPDATE cliente SET saldo='"+ saldo_cliente +
                    "' Where id ='"+ cliente_id+"';");
            Cursor c2 = db.rawQuery("SELECT * FROM vendedor where id='" + etClaveVendedor.getText().toString() + "'", null);
            c2.moveToFirst();
            int vendedor_id = c2.getInt(0);
            double comisiones_vendedor = Double.parseDouble(c2.getString(6)) - Double.parseDouble(txtComisionV.getText().toString());
            db.execSQL("UPDATE vendedor SET comisiones='"+ comisiones_vendedor +
                    "' Where id ='"+ vendedor_id+"';");*/
            db.execSQL("DELETE FROM ventas WHERE id ="+id+";");
            db.execSQL("DELETE FROM ventas_detalle WHERE id_venta ="+id+";");
            showMessage("Success", "Se ha borrado con Éxito");
            finish();
        }catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

    }
}

