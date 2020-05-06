package e.yunus.kasapp;

import androidx.appcompat.app.AppCompatActivity;
import e.yunus.kasapp.helper.CurrentDate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FilterActivity extends AppCompatActivity {

    EditText edit_dari, edit_ke;
    RippleView rip_filter;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        edit_dari = (EditText) findViewById(R.id.edit_dari);
        edit_ke   = (EditText) findViewById(R.id.edit_ke);
        rip_filter = (RippleView)findViewById(R.id.rip_filter);

        edit_dari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");    // biar 2
                        MainActivity.tgl_dari = year + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth) ;

                        Log.e("_tanggaldari", MainActivity.tgl_dari);
                        edit_dari.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month + 1) + "/" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);   // funsi agar meposiiskan tangal sekarang date picker dialog || helper
                datePickerDialog.show();

            }
        });

        edit_ke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");    // biar 2
                        MainActivity.tgl_ke = year + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth) ;

                        Log.e("_tanggalke", MainActivity.tgl_ke);
                        edit_ke.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month + 1) + "/" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);   // funsi agar meposiiskan tangal sekarang date picker dialog || helper
                datePickerDialog.show();

            }
        });

        rip_filter.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (edit_dari.getText().toString().equals("") || edit_ke.getText().toString().equals("")){
                    Toast.makeText(getApplication(), "isi data dengan benar", Toast.LENGTH_SHORT).show();
                }else {
                    MainActivity.filter = true;
                    MainActivity.text_filter.setText(edit_dari.getText().toString()+ " " + edit_ke.getText().toString());
                    MainActivity.text_filter.setVisibility(View.VISIBLE);
                    finish();

                }
            }
        });

        getSupportActionBar().setTitle("Filter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp(){
        //jika kembali menutup halaman yang sudah terbuka
        finish();
        return true;
    }
}
