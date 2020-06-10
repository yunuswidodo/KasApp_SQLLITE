package e.yunus.kasapp;

import androidx.appcompat.app.AppCompatActivity;
import e.yunus.kasapp.helper.Config;
import e.yunus.kasapp.helper.SqliteHelper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddActivity extends AppCompatActivity {

    RadioGroup radio_status;
    RadioButton radio_masuk, radio_keluar;
    EditText edit_jumlah, edit_keterangan;
    Button btn_simpan;
    RippleView rip_simpan;

    String status;

    SqliteHelper sqliteHelper; // memanggil sql helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        status = "";  // radion butoon/string masuk value default kosong

        sqliteHelper = new SqliteHelper(this);

        //mengganti judul danmenampilkan panah kembali mengguakan action bar
        getSupportActionBar().setTitle("tambah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radio_status = (RadioGroup) findViewById(R.id.radio_status);
        radio_masuk = (RadioButton) findViewById(R.id.radio_masuk);
        radio_keluar = (RadioButton) findViewById(R.id.radio_keluar);
        edit_jumlah = (EditText) findViewById(R.id.edit_jumlah);
        edit_keterangan = (EditText) findViewById(R.id.edit_keterangan);
        btn_simpan = (Button) findViewById(R.id.btn_simpan);
        rip_simpan = (RippleView) findViewById(R.id.rip_simpan);


        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_masuk:status = "MASUK";
                    break;
                    case R.id.radio_keluar:status = "KELUAR";
                    break;
                }
            }
        });
//        btn_simpan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //edit_jumlah.getText().toString(); mengambil nilai(bisa dimasukan kedalam toast melalui string)
//            }
//        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (status.equals("") || edit_jumlah.getText().toString().equals("") || edit_keterangan.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "isi data dengan benar", Toast.LENGTH_SHORT).show();
                }
                else if(status.equals("")){
                Toast.makeText(AddActivity.this, "nilai status tidak boleh kosong", Toast.LENGTH_SHORT).show();
                //functon fokus
                radio_status.requestFocus();
                }else if (edit_jumlah.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "nilai jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    edit_jumlah.requestFocus();
                }else if (edit_keterangan.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "nilai keterangan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    edit_keterangan.requestFocus();
                }
                else {
                    simpanData();
                }

            }
        });

    }

    private void insertMysql(){
        AndroidNetworking.get(Config.HOST +"create.php")
                .addQueryParameter("status", status)
                .addQueryParameter("jumlah", edit_jumlah.getText().toString())
                .addQueryParameter("keterangan", edit_keterangan.getText().toString())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optString("response").equals("success")){
                            Toast.makeText(getApplicationContext(), "transaksi berhasi disimpan", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "transaksi gagal disimpan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp(){
        //jika kembali menutup halaman yang sudah terbuka
        finish();
        return true;
    }



    private void simpanData(){
//        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
//        database.execSQL("INSERT INTO transaksi (status, jumlah, keterangan) VALUES ('"+status+
//                "', '"+ edit_jumlah.getText().toString() + "', '"+edit_keterangan.getText().toString()+ "')");
//
//
//        Toast.makeText(getApplicationContext(), "transaksi berhasil disimpan",Toast.LENGTH_LONG).show();
//
//       finish();
        insertMysql();

    }

}


