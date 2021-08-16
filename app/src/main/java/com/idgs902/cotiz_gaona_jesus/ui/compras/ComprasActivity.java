package com.idgs902.cotiz_gaona_jesus.ui.compras;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.lowagie.text.Table;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComprasActivity extends AppCompatActivity {

    private TextInputEditText etClaveCompra, txtClaveProv, etNombreProv, etCalleProv, etFechaCompra,
            txtClaveRecibo, txtClaveProducto, txtCantidadProducto, txtPrecioProducto;
    private Button btnAgregarProducto, btnGuardarCompra;
    private TableLayout tableLayout;
    private TextView txtSuma, txtIva, txtTotal, txtTotalPares;
    private Spinner spnTipo;
    private SQLiteDatabase db;
    private TableDynamic tableDynamic;
    private String[] headers = {"Clave", "Descripción", "Unidad", "Linea", "Cantidad", "Costo", "Importe"};
    private ArrayList<String[]> rows = new ArrayList<>();
    private List<DetallesCompras> lista = new ArrayList<DetallesCompras>();
    private String[] Recibos = {"Remision", "Factura"};

    private int TotalParesTabla = 0;
    private double SubtotalTabla = 0;
    private double IvaTabla = 0;
    private double TotalFinalTabla = 0;
    DecimalFormat format = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);

        etClaveCompra = this.findViewById(R.id.etClaveCompra);
        txtClaveProv = this.findViewById(R.id.txtClaveProv);
        etNombreProv = this.findViewById(R.id.etNombreProv);
        etCalleProv = this.findViewById(R.id.etCalleProv);
        etFechaCompra = this.findViewById(R.id.etFechaCompra);
        txtClaveProducto = this.findViewById(R.id.txtClaveProducto);
        txtClaveRecibo = this.findViewById(R.id.txtClaveRecibo);
        txtCantidadProducto = this.findViewById(R.id.txtCantidadProducto);
        txtPrecioProducto = this.findViewById(R.id.txtPrecioProducto);
        btnAgregarProducto = this.findViewById(R.id.btnAgregarProducto);
        btnGuardarCompra = this.findViewById(R.id.btnGuardarCompra);
        spnTipo = this.findViewById(R.id.spnTipo);
        txtSuma = this.findViewById(R.id.tvSuma);
        txtIva = this.findViewById(R.id.tvIVA);
        txtTotal = this.findViewById(R.id.tvTotal);
        txtTotalPares = this.findViewById(R.id.tvTotalPares);
        tableLayout = (TableLayout) this.findViewById(R.id.table);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        try {
            String fecha = dateFormat.format(new Date());
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            spnTipo.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Recibos));
            etFechaCompra.setText(fecha);

            tableDynamic = new TableDynamic(tableLayout, getApplicationContext());
            tableDynamic.addHeader(headers);
            tableDynamic.addData(new ArrayList<String[]>());

            Cursor c = db.rawQuery("SELECT * FROM compras;", null);
            if (c.getCount() != 0) {
                c.moveToLast();
                int id = c.getInt(0);
                etClaveCompra.setText("COM" + String.valueOf(id + 1));
            } else {
                int id = 1;
                etClaveCompra.setText("COM" + String.valueOf(id));
            }

            etClaveCompra.setEnabled(false);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        txtClaveProv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Cursor c = db.rawQuery("Select * from proveedor where id='" + txtClaveProv.getText().toString() + "';", null);
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    etNombreProv.setText(c.getString(1));
                    etCalleProv.setText(c.getString(2) + " " + c.getString(3));
                    etNombreProv.setEnabled(false);
                    etCalleProv.setEnabled(false);
                } else {
                    showMessage("Info", "No existe un proveedor con esa clave");
                }
            }
        });

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addProducto();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGuardarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    guardarCompra();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    private void guardarCompra() {
        if (txtClaveProv.getText().toString().trim().length() != 0
                && etNombreProv.getText().toString().trim().length() != 0
                && etCalleProv.getText().toString().trim().length() != 0
                && etFechaCompra.getText().toString().trim().length() != 0
                && txtClaveRecibo.getText().toString().trim().length() != 0
        ) {
            if (lista.size() != 0) {
                try {
                    Log.e("TotalPares", txtTotalPares.getText().toString().trim().split(":")[1].toString());
                    db.execSQL("INSERT INTO compras(clave, clave_p, nombre_p, calle_p, fecha, total_pares, " +
                            "subtotal, iva, total,  tipo_recibo, clave_recibo)" +
                            "values('"+etClaveCompra.getText().toString().trim()+"' , '"+
                            txtClaveProv.getText().toString().trim()+"' , '"+
                            etNombreProv.getText().toString().trim()+"' , '"+
                            etCalleProv.getText().toString().trim()+"' , '"+
                            etFechaCompra.getText().toString().trim()+"' , '"+
                            txtTotalPares.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtSuma.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtIva.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtTotal.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            spnTipo.getSelectedItem().toString()+"' , '"+
                            txtClaveRecibo.getText().toString().trim()+"');");
                    Cursor c = db.rawQuery("select Max(id) from compras;", null);
                    Log.e("Cursor", c.toString());
                    c.moveToFirst();
                    int compra_id = c.getInt(0);

                    for (DetallesCompras item : lista) {
                        try {
                            db.execSQL("INSERT INTO compras_detalle(id_compra, clave_pro,nombre_pro,unidad," +
                                    " linea, cantidad_pro, costo_pro, importe)" +
                                    "VALUES ('"+compra_id+
                                    "', '"+item.clave+
                                    "', '"+item.descripcion+
                                    "', '"+item.unidad+
                                    "', '"+item.linea+
                                    "', '"+item.cantidad+
                                    "', '"+item.costo+
                                    "', '"+item.importe+"');");
                            Cursor c1 = db.rawQuery("SELECT * FROM producto where id=" + item.id, null);
                            Log.e("guardarCompra: ", String.valueOf(c1.getColumnCount()));
                            if (c1.getCount() != 0) {
                                c1.moveToFirst();
                                if (c1.getInt(4) != 0
                                        && c1.getDouble(5) != 0
                                        && c1.getDouble(6) != 0
                                        && c1.getDouble(7) != 0
                                        && c1.getDouble(8) != 0) {
                                    int existenciaAnt = c1.getInt(4);
                                    double costoAnt = c1.getDouble(5);

                                    int TotalExistencia  = existenciaAnt+Integer.parseInt(item.cantidad);
                                    double TotalImporte = (costoAnt*existenciaAnt) + (Double.parseDouble(item.costo)*Integer.parseInt(item.cantidad));
                                    double costoPromedio = TotalImporte/TotalExistencia;

                                    db.execSQL("UPDATE producto SET existencia='"+TotalExistencia+
                                            "', Pcosto='"+ item.costo+"', PCpromedio='"+costoPromedio+
                                            "', PMenudeo='"+format.format(Double.parseDouble(item.costo) * 1.40)+
                                            "', PMayoreo='"+format.format(Double.parseDouble(item.costo)*1.28)+
                                            "' Where id ="+item.id+";");
                                }else{
                                    db.execSQL("UPDATE producto SET existencia='"+item.cantidad+
                                            "', Pcosto='"+ item.costo+"', PCpromedio='"+item.costo+
                                            "', PMenudeo='"+format.format(Double.parseDouble(item.costo) * 1.40)+
                                            "', PMayoreo='"+format.format(Double.parseDouble(item.costo)*1.28)+
                                            "' Where id ="+item.id+";");
                                }
                            }
                        } catch (Exception ex) {
                            showMessage("Error", ex.toString());
                            return;
                        }

                    }


                } catch (Exception ex) {
                    showMessage("Error", ex.toString());
                }
                showMessage("Success", "La Compra se ha registrado con exito");
                finish();
            } else {
                showMessage("Info", "No ha agregado productos a la compra intente de nuevo");
                return;
            }

        } else {
            showMessage("Info", "No ha agregado todos los campos de la compra, revise por favor!");
            return;
        }
    }

    public void addProducto() {
        if (txtClaveProducto.getText().toString().trim().length() != 0
                && txtCantidadProducto.getText().toString().trim().length() != 0
                && txtPrecioProducto.getText().toString().trim().length() != 0) {

            try {
                Cursor c = db.rawQuery("SELECT * FROM producto where clave='" + txtClaveProducto.getText().toString() + "';", null);
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    float subtotal = Float.parseFloat(txtCantidadProducto.getText().toString()) * Float.parseFloat(txtPrecioProducto.getText().toString());
                    String[] item = new String[]{txtClaveProducto.getText().toString(), c.getString(2), "Par", c.getString(3), txtCantidadProducto.getText().toString(), txtPrecioProducto.getText().toString(), String.valueOf(subtotal)};
                    tableDynamic.addItems(item);
                    DetallesCompras det = new DetallesCompras();
                    det.setClave(txtClaveProducto.getText().toString());
                    det.setDescripcion(c.getString(2));
                    det.setUnidad("Par");
                    det.setId(c.getString(0));
                    det.setLinea(c.getString(3));
                    det.setCantidad(txtCantidadProducto.getText().toString());
                    det.setCosto(txtPrecioProducto.getText().toString());
                    det.setImporte(String.valueOf(subtotal));
                    lista.add(det);
                    TotalParesTabla += Integer.parseInt(txtCantidadProducto.getText().toString());
                    SubtotalTabla += subtotal;
                    IvaTabla = (SubtotalTabla * 0.16);
                    TotalFinalTabla = SubtotalTabla + IvaTabla;


                    txtTotalPares.setText("TOTAL DE PARES : " + format.format(TotalParesTabla).toString());
                    txtIva.setText("I.V.A : $" + format.format(IvaTabla).toString());
                    txtSuma.setText("SUMA : $" + format.format(SubtotalTabla).toString());
                    txtTotal.setText("TOTAL : $" + format.format(TotalFinalTabla).toString());

                } else {
                    showMessage("Info", "No existe un producto con esa clave , intente de nuevo");
                }
            } catch (Exception ex) {
                Log.e("ErrorAñadir", ex.toString());
                showMessage("Error", ex.getMessage());
            }

        } else {
            showMessage("Info", "Debe completar todos los campos , ya que son requeridos");
        }

    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public class DetallesCompras {
        String id, clave, descripcion, unidad, linea, cantidad, costo, importe;

        public DetallesCompras() {
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getUnidad() {
            return unidad;
        }

        public void setUnidad(String unidad) {
            this.unidad = unidad;
        }

        public String getLinea() {
            return linea;
        }

        public void setLinea(String linea) {
            this.linea = linea;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }

        public String getCosto() {
            return costo;
        }

        public void setCosto(String costo) {
            this.costo = costo;
        }

        public String getImporte() {
            return importe;
        }

        public void setImporte(String importe) {
            this.importe = importe;
        }
    }
}