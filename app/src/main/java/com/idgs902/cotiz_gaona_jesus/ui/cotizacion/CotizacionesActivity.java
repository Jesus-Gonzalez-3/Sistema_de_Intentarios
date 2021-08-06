package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.cliente.ClienteFragment;

public class CotizacionesActivity extends AppCompatActivity {

    private Button btnAgregarCot;
    private TextInputEditText etClaveCot, etFechaCot, etEnganche;
    private Spinner spnEmpleado, spnCliente, spnVehiculo, spnPlazos;
    private SQLiteDatabase db;

    private String[] Plazos = {"Seleccione", "12", "24", "36", "48", "60"};
    private String[] Cliente = null;
    private String[] Empleado = null;
    private String[] Vehiculo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizaciones);

        btnAgregarCot = this.findViewById(R.id.btnGuardarCot);
        etClaveCot = this.findViewById(R.id.etClaveCot);
        etFechaCot = this.findViewById(R.id.etFechaCot);
        etEnganche = this.findViewById(R.id.etEngache);

        spnCliente = this.findViewById(R.id.spnCliente);
        spnEmpleado = this.findViewById(R.id.spnEmpleado);
        spnVehiculo = this.findViewById(R.id.spnVehiculo);
        spnPlazos = this.findViewById(R.id.spnPlazos);

        spnPlazos.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Plazos));

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
        Log.d("Empleados", String.valueOf(Empleado.length));
        Log.d("Clientes", String.valueOf(Cliente.length));
        Log.d("Vehiculos", String.valueOf(Vehiculo.length));

        spnEmpleado.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Empleado));
        spnCliente.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Cliente));
        spnVehiculo.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, Vehiculo));

        btnAgregarCot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    guardar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    private void guardar() {
        if (spnCliente.getSelectedItemPosition() != 0 ||
                spnVehiculo.getSelectedItemPosition() != 0 ||
                spnEmpleado.getSelectedItemPosition() != 0) {
            if (etClaveCot.getText().toString().trim().length() != 0
                    && etEnganche.getText().toString().trim().length() != 0
                    && etFechaCot.getText().toString().trim().length() != 0) {
                String Tasa = "";
                double tasa = 0;
                double enganche = 0;
                double costo = Double.parseDouble(spnVehiculo.getSelectedItem().toString().split("¬")[1]);
                switch (spnPlazos.getSelectedItemPosition()) {
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

                enganche = costo * (Double.parseDouble(etEnganche.getText().toString())*0.01);
                String TasaAnual = String.valueOf((Double.parseDouble(spnPlazos.getSelectedItem().toString()) *
                        Double.parseDouble(Tasa)));
                String vehiculo = spnVehiculo.getSelectedItem().toString().split("¬")[0];


                db.execSQL("INSERT INTO cotizacion VALUES('" + etClaveCot.getText() + "','"
                        + etFechaCot.getText() + "','"
                        + spnEmpleado.getSelectedItem().toString() + "','"
                        + spnCliente.getSelectedItem().toString() + "','"
                        + vehiculo + "','"
                        + String.valueOf(costo) + "','"
                        + etEnganche.getText() + "','"
                        + String.valueOf(enganche) + "','"
                        + spnPlazos.getSelectedItem().toString() + "','"
                        + Tasa + "','"
                        + TasaAnual + "');");
                showMessage("Exito!", "Cotización agregada");
                finish();
            } else {
                showMessage("Error", "Debe llenar todos los campos");
            }
        } else {
            showMessage("Error", "Debe seleccionar todos los datos.");
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