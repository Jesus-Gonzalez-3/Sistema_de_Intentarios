package com.idgs902.cotiz_gaona_jesus.ui.empleados;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.cliente.ClienteFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EmpleadosActivity2 extends AppCompatActivity {

    private Button btnModificarE, btnEliminarE;
    private TextInputEditText etClaveEM, etNombreEM, etFechaIngresoM;
    private Spinner spnPuestoM;
    private SQLiteDatabase db;

    private String[] Puesto = {"Seleccione", "Gerente de Ventas", "Asesor de ventas", "Gerente General", "RRHH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados2);

        etClaveEM = this.findViewById(R.id.etClaveEM);
        etNombreEM = this.findViewById(R.id.etNombreEM);
        etFechaIngresoM = this.findViewById(R.id.etFechaIngresoM);
        spnPuestoM = this.findViewById(R.id.spnPuestoM);
        btnEliminarE = this.findViewById(R.id.btnEliminarE);
        btnModificarE = this.findViewById(R.id.btnModificarE);


        spnPuestoM.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Puesto));
        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        etFechaIngresoM.setText(fecha.format(date).toString());
        etFechaIngresoM.setEnabled(false);

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(EmpleadosFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM empleado WHERE clave ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveEM.setText(c.getString(0));
                    etNombreEM.setText(c.getString(1));
                    etFechaIngresoM.setText(c.getString(3));
                    etClaveEM.setEnabled(false);
                    etFechaIngresoM.setEnabled(false);
                    switch (c.getString(2)) {
                        case "Gerente de Ventas":
                            spnPuestoM.setSelection(1, true);
                            break;
                        case "Asesor de ventas":
                            spnPuestoM.setSelection(2, true);
                            break;
                        case "Gerente General":
                            spnPuestoM.setSelection(3, true);
                            break;
                        case "RRHH":
                            spnPuestoM.setSelection(4, true);
                            break;
                    }
                }
            } else {
                showMessage("Info", "No existe un Registro con esa clave");
            }

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnModificarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    modificar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnEliminarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void modificar() {
        if (etNombreEM.getText().toString().trim().length() != 0 && spnPuestoM.getSelectedItem().toString().length() != 0) {
            try {
                db.execSQL("UPDATE empleado SET nombre ='"
                        + etNombreEM.getText() + "', puesto='" + spnPuestoM.getSelectedItem().toString() + "' WHERE clave = '" + etClaveEM.getText() + "';");
                showMessage("Exito!", "Empleado Modificado");
            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }

            finish();
        } else {
            showMessage("Info", "Debe Llenar todos los datos");
        }
    }

    public void eliminar() {
        try {
            db.execSQL("DELETE FROM empleado WHERE clave ='" +
                    etClaveEM.getText() + "' AND  nombre ='" + etNombreEM.getText() + "';");
            showMessage("Exito!", "Empleado Eliminado");
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
}