package com.idgs902.cotiz_gaona_jesus.ui.compras;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

import android.app.AlertDialog.Builder;
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
import java.util.List;

public class ComprasActivity2 extends AppCompatActivity {

    private TextInputEditText etClaveCompraM, txtClaveProvM, etNombreProvM, etCalleProvM, etFechaCompraM,
            txtClaveReciboM, txtClaveProductoM, txtCantidadProductoM, txtPrecioProductoM;
    private Button btnEliminarComp;
    private TableLayout tableLayout;
    private TextView txtSumaM, txtIvaM, txtTotalM, txtTotalParesM;
    private Spinner spnTipoM;
    private SQLiteDatabase db;
    private TableDynamic tableDynamic;
    private String[] headers = {"Clave", "Descripción", "Unidad", "Linea", "Cantidad", "Costo", "Importe"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private List<ComprasActivity.DetallesCompras> lista = new ArrayList<ComprasActivity.DetallesCompras>();
    private String[] Recibos = {"Remision", "Factura"};

    private int TotalParesTabla = 0;
    private double SubtotalTabla = 0;
    private double IvaTabla = 0;
    private double TotalFinalTabla = 0;

    private int id;
    DecimalFormat format = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras2);

        etClaveCompraM = this.findViewById(R.id.etClaveCompraM);
        txtClaveProvM = this.findViewById(R.id.txtClaveProvM);
        etNombreProvM = this.findViewById(R.id.etNombreProvM);
        etCalleProvM = this.findViewById(R.id.etCalleProvM);
        etFechaCompraM = this.findViewById(R.id.etFechaCompraM);
        txtClaveReciboM = this.findViewById(R.id.txtClaveReciboM);
        btnEliminarComp = this.findViewById(R.id.btnEliminarComp);
        spnTipoM = this.findViewById(R.id.spnTipoM);
        txtSumaM = this.findViewById(R.id.tvSumaM);
        txtIvaM = this.findViewById(R.id.tvIVAM);
        txtTotalM = this.findViewById(R.id.tvTotalM);
        txtTotalParesM = this.findViewById(R.id.tvTotalParesM);
        tableLayout = (TableLayout) this.findViewById(R.id.tableM);

        Intent i = getIntent();
        String clave = i.getStringExtra(ComprasFragment.DATOS);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            spnTipoM.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Recibos));
            tableDynamic = new TableDynamic(tableLayout, getApplicationContext());
            tableDynamic.addHeader(headers);
            Cursor c = db.rawQuery("SELECT * FROM compras WHERE id =" + clave + ";", null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                id = c.getInt(0);
                etClaveCompraM.setText(c.getString(1));
                txtClaveProvM.setText(c.getString(2));
                etNombreProvM.setText(c.getString(3));
                etCalleProvM.setText(c.getString(4));
                etFechaCompraM.setText(c.getString(5));
                txtTotalParesM.setText("TOTAL PARES: " + c.getString(6));
                txtSumaM.setText("SUMA : $" + c.getString(7));
                txtIvaM.setText("I.V.A. : $" + c.getString(8));
                txtTotalM.setText("TOTAL: $" + c.getString(9));
                Log.e("Ctipo", c.getString(10));
                switch (c.getString(10)){
                    case "Remision":
                        spnTipoM.setSelection(0);
                        break;
                    case "Factura":
                        spnTipoM.setSelection(1);
                        break;
                    default:
                        spnTipoM.setSelection(0);
                        break;
                }
                txtClaveReciboM.setText(c.getString(11));

                Cursor c1 = db.rawQuery("SELECT * FROM compras_detalle WHERE id_compra=" + id + ";", null);
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
            txtClaveProvM.setEnabled(false);
            etFechaCompraM.setEnabled(false);
            txtClaveReciboM.setEnabled(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        btnEliminarComp.setOnClickListener(new View.OnClickListener() {
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

    public void eliminar(){
        try {
            db.execSQL("DELETE FROM compras WHERE id ="+id+";");
            showMessage("Success", "Se ha borrado con Éxito");
            finish();
        }catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}