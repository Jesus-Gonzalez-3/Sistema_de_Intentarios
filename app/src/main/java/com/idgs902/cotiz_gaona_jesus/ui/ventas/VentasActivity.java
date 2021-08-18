package com.idgs902.cotiz_gaona_jesus.ui.ventas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.compras.ComprasActivity;
import com.idgs902.cotiz_gaona_jesus.ui.compras.TableDynamic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentasActivity extends AppCompatActivity {

    private TextInputEditText etClaveCliente, etNombreCliente, etCalleCliente,
            etClaveVendedor, etNombreVendedor, etFechaVenta,
            etComision, txtClaveProducto, txtCantidadProducto, etClaveRecibo;
    private Button btnAgregarProducto, btnGuardarVenta;
    private TableLayout tableLayout;
    private TextView txtSuma, txtIva, txtTotal, txtTotalPares, txtComisionV;
    private Spinner spnTipo;
    private SQLiteDatabase db;
    private TableDynamic tableDynamic;
    private String[] headers = {"Clave", "Descripción", "Unidad", "Linea", "Cantidad", "PVenta", "Importe"};
    private ArrayList<String[]> rows = new ArrayList<>();
    //private List<ComprasActivity.DetallesCompras> lista = new ArrayList<ComprasActivity.DetallesCompras>();
    private String[] Recibos = {"Remision", "Factura"};

    private List<DetalleVentas> lista = new ArrayList<DetalleVentas>();

    private int TotalParesTabla = 0;
    private double SubtotalTabla = 0;
    private double IvaTabla = 0;
    private double TotalFinalTabla = 0;
    private double TotalComisiones = 0;
    DecimalFormat format = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        etClaveCliente = this.findViewById(R.id.txtClaveCliente);
        etNombreCliente = this.findViewById(R.id.etNombreCliente);
        etCalleCliente = this.findViewById(R.id.etCalleCliente);
        etClaveVendedor = this.findViewById(R.id.txtClaveVendedor);
        etNombreVendedor = this.findViewById(R.id.etNombreVendedor);
        etFechaVenta = this.findViewById(R.id.etFechaVenta);
        etComision = this.findViewById(R.id.txtComisionVend);
        spnTipo = this.findViewById(R.id.spnTipo);
        etClaveRecibo = this.findViewById(R.id.txtClaveRecibo);

        txtClaveProducto = this.findViewById(R.id.txtClaveProductoV);
        txtCantidadProducto = this.findViewById(R.id.txtCantidadProductoV);

        btnAgregarProducto = this.findViewById(R.id.btnAgregarProductoV);
        btnGuardarVenta = this.findViewById(R.id.btnGuardarVenta);

        txtSuma = this.findViewById(R.id.tvSumaV);
        txtIva = this.findViewById(R.id.tvIVAV);
        txtTotal = this.findViewById(R.id.tvTotalV);
        txtTotalPares = this.findViewById(R.id.tvTotalPares);
        txtComisionV = this.findViewById(R.id.txtComisionVendedor);

        tableLayout = (TableLayout) this.findViewById(R.id.table);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String fecha = dateFormat.format(new Date());
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            spnTipo.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Recibos));
            etFechaVenta.setText(fecha);

            tableDynamic = new TableDynamic(tableLayout, getApplicationContext());
            tableDynamic.addHeader(headers);
            tableDynamic.addData(new ArrayList<String[]>());

            /*db.execSQL("UPDATE producto SET existencia='30"+
                    "', Pcosto='10', PCpromedio='10"+
                    "', PMenudeo='"+format.format(10 * 1.40)+
                    "', PMayoreo='"+format.format(10*1.28)+
                    "' Where id ='1';");*/

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        etClaveCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Cursor c = db.rawQuery("Select * from cliente where id='" + etClaveCliente.getText().toString() + "';", null);
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    etNombreCliente.setText(c.getString(1));
                    etCalleCliente.setText(c.getString(2) + ", " + c.getString(3) + ", " + c.getString(4));
                    //etNombreProv.setEnabled(false);
                    //etCalleProv.setEnabled(false);
                } else {
                    showMessage("Info", "No existe un cliente con esa clave");
                }
            }
        });

        etClaveVendedor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Cursor c = db.rawQuery("Select * from vendedor where id='" + etClaveVendedor.getText().toString() + "';", null);
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    etNombreVendedor.setText(c.getString(1));
                } else {
                    showMessage("Info", "No existe un vendedor con esa clave");
                }
            }
        });

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //showMessage("Chale", "No existe un cliente con esa clave");
                    addProducto();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnGuardarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //showMessage("Chale", "No existe un cliente con esa clave");
                    guardarVenta();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void addProducto() {
        if (txtClaveProducto.getText().toString().trim().length() != 0
                && txtCantidadProducto.getText().toString().trim().length() != 0) {

            try {
                Cursor c = db.rawQuery("SELECT * FROM producto where clave='" + txtClaveProducto.getText().toString() + "';", null);
                if (c.getCount() != 0) {
                    c.moveToFirst();

                    int can = Integer.parseInt(c.getString(5));
                    int can_sol = Integer.parseInt(txtCantidadProducto.getText().toString());
                    if (can_sol > can){
                        showMessage("Info", "No hay existencia para surtir la cantidad del producto.");
                    }else{
                        float subtotal = Float.parseFloat(txtCantidadProducto.getText().toString()) * Float.parseFloat(c.getString(6));
                        String[] item = new String[]{txtClaveProducto.getText().toString(), c.getString(2), "Par", c.getString(3), txtCantidadProducto.getText().toString(), c.getString(6), String.valueOf(subtotal)};
                        tableDynamic.addItems(item);

                        DetalleVentas det = new DetalleVentas ();

                        det.setClave(txtClaveProducto.getText().toString());
                        det.setDescripcion(c.getString(2));
                        det.setUnidad("Par");
                        det.setId(c.getString(0));
                        det.setLinea(c.getString(3));
                        det.setCantidad(txtCantidadProducto.getText().toString());
                        det.setPventa(c.getString(6));
                        det.setImporte(String.valueOf(subtotal));
                        lista.add(det);

                        TotalParesTabla += Integer.parseInt(txtCantidadProducto.getText().toString());
                        SubtotalTabla += subtotal;
                        IvaTabla = (SubtotalTabla * 0.16);
                        TotalFinalTabla = SubtotalTabla + IvaTabla;
                        TotalComisiones = TotalFinalTabla * Integer.parseInt(etComision.getText().toString()) / 100;

                        txtTotalPares.setText("TOTAL DE PARES : " + format.format(TotalParesTabla).toString());
                        txtIva.setText("I.V.A : $" + format.format(IvaTabla).toString());
                        txtSuma.setText("SUMA : $" + format.format(SubtotalTabla).toString());
                        txtTotal.setText("TOTAL : $" + format.format(TotalFinalTabla).toString());
                        txtComisionV.setText("Comis Vendedor : $" + format.format(TotalComisiones).toString());
                    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void guardarVenta() {
        if (etClaveCliente.getText().toString().trim().length() != 0
                && etNombreCliente.getText().toString().trim().length() != 0
                && etCalleCliente.getText().toString().trim().length() != 0
                && etClaveVendedor.getText().toString().trim().length() != 0
                && etNombreVendedor.getText().toString().trim().length() != 0
                && etFechaVenta.getText().toString().trim().length() != 0
                && etComision.getText().toString().trim().length() != 0
        ) {
            if (lista.size() != 0) {
                try {
                    Log.e("TotalPares", txtTotalPares.getText().toString().trim().split(":")[1].toString());
                    db.execSQL("INSERT INTO ventas(clave_cliente, nombre_cliente, calle_cliente, clave_vendedor, nombre_vendedor, fecha, " +
                            "comision, tipo_recibo, clave_recibo, total_productos, suma, iva, total_venta, comision_vendedor)" +
                            "values('"+etClaveCliente.getText().toString().trim()+"' , '"+
                            etNombreCliente.getText().toString().trim()+"' , '"+
                            etCalleCliente.getText().toString().trim()+"' , '"+
                            etClaveVendedor.getText().toString().trim()+"' , '"+
                            etNombreVendedor.getText().toString().trim()+"' , '"+
                            etFechaVenta.getText().toString().trim()+"' , '"+
                            etComision.getText().toString().trim()+"' , '"+
                            spnTipo.getSelectedItem().toString()+"' , '"+
                            etClaveRecibo.getText().toString().trim()+"' , '"+
                            txtTotalPares.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtSuma.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtIva.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtTotal.getText().toString().trim().split(":")[1].toString()+"' , '"+
                            txtComisionV.getText().toString().trim()+"');");
                    Cursor c = db.rawQuery("select Max(id) from ventas;", null);
                    Log.e("Cursor", c.toString());
                    c.moveToFirst();
                    int venta_id = c.getInt(0);

                    for (DetalleVentas item : lista) {
                        try {
                            db.execSQL("INSERT INTO ventas_detalle (id_venta, clave_pro, nombre_pro," +
                                    " unidad,linea, cantidad_pro, pre_venta, importe)" +
                                    "VALUES ('"+venta_id+
                                    "', '"+item.clave+
                                    "', '"+item.descripcion+
                                    "', '"+item.unidad+
                                    "', '"+item.linea+
                                    "', '"+item.cantidad+
                                    "', '"+item.pventa+
                                    "', '"+item.importe+"');");
                            Cursor c1 = db.rawQuery("SELECT * FROM producto where id=" + item.id, null);
                            Log.e("guardarCompra: ", String.valueOf(c1.getColumnCount()));
                            if (c1.getCount() != 0) {
                                c1.moveToFirst();
                                if (c1.getInt(4) != 0) {
                                    int existenciaAnt = c1.getInt(4);
                                    //double costoAnt = c1.getDouble(5);

                                    int TotalExistencia  = existenciaAnt-Integer.parseInt(item.cantidad);

                                    db.execSQL("UPDATE producto SET existencia='"+TotalExistencia+
                                            "' Where id ="+item.id+";");
                                }
                            }

                            Cursor c2 = db.rawQuery("SELECT * FROM cliente where id='" + etClaveCliente.getText().toString() + "'", null);
                            if (c2.getCount() != 0) {
                                c2.moveToFirst();
                                int cliente_id = c2.getInt(0);
                                Log.i("Cliente id ", "" + cliente_id);
                                double saldo_cliente = Double.parseDouble(c2.getString(8)) + TotalFinalTabla;
                                Log.i("Saldo id ", "" + saldo_cliente);
                                db.execSQL("UPDATE cliente SET saldo='"+saldo_cliente+
                                        "' Where id ="+cliente_id+";");
                            }

                            Cursor c3 = db.rawQuery("SELECT * FROM vendedor where id='" + etClaveVendedor.getText().toString() + "'", null);
                            c3.moveToFirst();
                            int vendedor_id = c3.getInt(0);
                            double comisiones_vendedor = Double.parseDouble(c3.getString(6)) + TotalComisiones;
                            Log.i("Vendedor id ", "" + vendedor_id);
                            Log.i("Saldo id ", "" + TotalComisiones);
                            db.execSQL("UPDATE vendedor SET comisiones='"+comisiones_vendedor+
                                    "' Where id ="+vendedor_id+";");
                        } catch (Exception ex) {
                            showMessage("Error", ex.toString());
                            return;
                        }

                    }
                } catch (Exception ex) {
                    showMessage("Error", ex.toString());
                }
                showMessage("Success", "La venta se ha registrado con exito");
                finish();
            } else {
                showMessage("Info", "No ha agregado productos a la venta intente de nuevo");
                return;
            }

        } else {
            showMessage("Info", "No ha agregado todos los campos de la venta, revise por favor!");
            return;
        }
    }

    public class DetalleVentas {
        String id, clave, descripcion, unidad, linea, cantidad, pventa, importe;

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

        public String getPventa() {
            return pventa;
        }

        public void setPventa(String pventa) {
            this.pventa = pventa;
        }

        public String getImporte() {
            return importe;
        }

        public void setImporte(String importe) {
            this.importe = importe;
        }

        public DetalleVentas() {
        }
    }
}