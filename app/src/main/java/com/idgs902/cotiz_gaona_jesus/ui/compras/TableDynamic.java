package com.idgs902.cotiz_gaona_jesus.ui.compras;

import android.content.Context;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TableDynamic {
    private TableLayout tableLayout;
    private Context context;
    private String[] header;
    private ArrayList<String[]> data;
    private TableRow tableRow;
    private TextView txtCel;
    private int indexC;
    private int indexR;

    public TableDynamic(TableLayout tableLayout, Context context) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    public void addHeader(String[] header) {
        this.header = header;
        createHeader();
    }

    public void addData(ArrayList<String[]> data) {
        this.data = data;
        createDataTable();
    }

    private void newRow() {
        tableRow = new TableRow(context);
    }

    private void newCell() {
        txtCel = new TextView(context);
        txtCel.setGravity(Gravity.CENTER);
        txtCel.setTextSize(25);
    }

    private void createHeader() {
        indexC = 0;
        newRow();
        while (indexC < header.length) {
            newCell();
            txtCel.setText(header[indexC++]);
            tableRow.addView(txtCel, newTableRowParams());
        }
        tableLayout.addView(tableRow);
    }

    private void createDataTable() {
        String info;
        if (data.size() != 0) {
            for (indexR = 1; indexR <= header.length; indexR++) {
                newRow();
                for (indexC = 0; indexC <= header.length; indexC++) {
                    newCell();
                    String[] row = data.get(indexR - 1);
                    info = (indexC < row.length) ? row[indexC] : "";
                    txtCel.setText(info);
                    tableRow.addView(txtCel, newTableRowParams());
                }
                tableLayout.addView(tableRow);
            }
        } else return;
    }

    public void addItems(String[] item) {
        String info;
        data.add(item);
        indexC = 0;
        newRow();
        while (indexC < header.length) {
            newCell();
            info = (indexC < item.length) ? item[indexC++] : "";
            txtCel.setText(info);
            tableRow.addView(txtCel, newTableRowParams());
        }
        tableLayout.addView(tableRow, data.size());
    }

    private TableRow.LayoutParams newTableRowParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1, 1, 1, 1);
        params.weight = 1;
        return params;
    }

}
