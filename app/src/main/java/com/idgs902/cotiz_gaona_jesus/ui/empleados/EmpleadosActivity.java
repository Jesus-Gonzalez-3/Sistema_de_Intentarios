package com.idgs902.cotiz_gaona_jesus.ui.empleados;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EmpleadosActivity extends AppCompatActivity {
    private Button btnAgregarE;
    private TextInputEditText etClaveE, etNombreE, etFechaIngreso;
    private Spinner spnPuesto;
    private SQLiteDatabase db;

    private String[] Puesto = {"Seleccione", "Gerente de Ventas", "Asesor de ventas", "Gerente General", "RRHH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados);

        etClaveE = this.findViewById(R.id.etClaveE);
        etNombreE = this.findViewById(R.id.etNombreE);
        etFechaIngreso = this.findViewById(R.id.etFechaIngreso);
        spnPuesto = this.findViewById(R.id.spnPuesto);
        btnAgregarE = this.findViewById(R.id.btnGuardarE);

        spnPuesto.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Puesto));
        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        etFechaIngreso.setText(fecha.format(date).toString());
        etFechaIngreso.setEnabled(false);

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    agregar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void agregar() {
        if (etClaveE.getText().toString().trim().length() != 0
                && etNombreE.getText().toString().trim().length() != 0
                && spnPuesto.getSelectedItem().toString().length() != 0
                && etFechaIngreso.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO empleado VALUES('" + etClaveE.getText() + "','"
                        + etNombreE.getText() + "','" + spnPuesto.getSelectedItem().toString() + "','" +
                        etFechaIngreso.getText() + "');");
                showMessage("Exito!", "Empleado agregado");
                finish();
            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
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