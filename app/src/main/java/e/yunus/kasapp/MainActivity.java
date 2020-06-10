package e.yunus.kasapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.andexert.library.RippleView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import e.yunus.kasapp.helper.Config;
import e.yunus.kasapp.helper.SqliteHelper;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView list_kas;
    SwipeRefreshLayout swipe_refresh;

    ArrayList<HashMap<String, String>>aruskas;

    TextView text_masuk, text_keluar, text_total;
    SqliteHelper sqliteHelper;

    Cursor cursor; //untuk menemukan index komom keberapa

    public static String transaksi_id, tgl_dari, tgl_ke; // varibel onitemclicklistener || supaya bisa diakses lintas activity maka gunakan public
    public static boolean filter;
    public static TextView text_filter;
    String query_kas, query_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, AddActivity.class));
                Toast.makeText(MainActivity.this, "pindah", Toast.LENGTH_SHORT).show();
                Log.e("pinda", "pindah");

            }
        });

        text_masuk = (TextView) findViewById(R.id.text_masuk);
        text_keluar = (TextView)  findViewById(R.id.text_keluar);
        text_total = (TextView) findViewById(R.id.text_total);
        text_filter = (TextView) findViewById(R.id.text_filter);
        list_kas = (ListView) findViewById(R.id.list_kas);   // view list
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        aruskas = new ArrayList<>();
        sqliteHelper = new SqliteHelper(this);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_kas   = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tanggal FROM transaksi ORDER BY transaksi_id DESC";
                query_total = "SELECT SUM(jumlah) AS total," +
                        "(SELECT SUM (jumlah) FROM transaksi WHERE status='MASUK') AS masuk," +
                        "(SELECT SUM (jumlah) FROM transaksi WHERE status='KELUAR') AS keluar FROM transaksi";

                kasAdapter();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        query_kas   =
                "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
        query_total =
                "SELECT SUM(jumlah) AS total, " +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') AS masuk, " +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR') AS keluar " +
                        "FROM transaksi";

        if (filter) {

            query_kas   =
                    "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi  " +
                            "WHERE (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "') ORDER BY transaksi_id ASC ";
            query_total =
                    "SELECT SUM(jumlah) AS total, " +
                            "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK' AND (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "') ), " +
                            "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR' AND (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "')) " +
                            "FROM transaksi " +
                            "WHERE (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "') ";
//            filter = false;
        }

        //kasAdapter();
        selectMYSQL();

    }

    private void selectMYSQL(){
        AndroidNetworking.get(Config.HOST + "read.php") // diread tidak membutuhkan param
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        NumberFormat rupiahformat = NumberFormat.getInstance(Locale.GERMANY);
                        text_masuk.setText(rupiahformat.format(response.optDouble("masuk"))); // nilai yang diambil masuk | api php
                        text_keluar.setText(rupiahformat.format(response.optDouble("keluar")));
                        text_total.setText(rupiahformat.format(response.optDouble("saldo")));


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void kasAdapter(){

        aruskas.clear(); list_kas.setAdapter(null);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        cursor = db.rawQuery(query_kas , null);   // ASC=ascending dari awal | DESC= descending dari belakang
        cursor.moveToFirst();

        for (int i =0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            HashMap<String, String> map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
//            map.put("tanggal", cursor.getString(4));  // dikarenakan menggunakan tanggal
            map.put("tanggal", cursor.getString(5));
            aruskas.add(map);  // aray list aruskah

        }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, aruskas, R.layout.list_kas,   // aruskas disimoan disini
                    new String[] {"transaksi_id", "status", "jumlah", "keterangan", "tanggal"},  // ambil dari hashmap
                    new int[]{R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan,
                            R.id.text_tanggal});

            list_kas.setAdapter(simpleAdapter);

            //menambahi aksi ketika salah satu item diklik
            list_kas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    transaksi_id = ((TextView)view.findViewById(R.id.text_transaksi_id)).getText().toString();
                    Log.e("_transaksi", transaksi_id);

                    ListMenu();
                }
            });
            KasTotal();
    }

    private void KasTotal(){
        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);
        SQLiteDatabase db =  sqliteHelper.getReadableDatabase();
//
        //tulisan masuk harus sama dengan yang di add
        cursor = db.rawQuery(query_total, null);
        cursor.moveToFirst();
        text_masuk.setText(rupiah.format(cursor.getDouble(1)));
        text_keluar.setText(rupiah.format(cursor.getDouble(2)));
        text_total.setText(
                rupiah.format(cursor.getDouble(1) - cursor.getDouble(2))
        );

        swipe_refresh.setRefreshing(false); // untuk menghentikan refrress || ketika selesai memuat data sudah
        if (!filter){
            text_filter.setVisibility(View.GONE);
        }
        filter = false;

    }

    private void ListMenu(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_menu); // memenaggil lismenu.xml
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        //action
//        TextView text_hapus = dialog.findViewById(R.id.text_hapus);
//        TextView text_edit = dialog.findViewById(R.id.text_edit);
//
//        text_hapus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
////                hapus();
//            }
//        });
//
//        text_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//            }
//        });

        RippleView rip_hapus = dialog.findViewById(R.id.rip_hapus);
        RippleView rip_edit = dialog.findViewById(R.id.rip_edit);

        rip_hapus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                hapus();
            }
        });

        rip_edit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, EditActivity.class));

            }
        });

        dialog.show();
    }

    private void hapus(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("konfirmasi");
        builder.setMessage("Yakin untuk menghapus data ini ?");
        builder.setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM transaksi WHERE transaksi_id = '" + transaksi_id +"'");

                        Toast.makeText(getApplicationContext(), "data berhasil dihapus", Toast.LENGTH_SHORT).show();
                        kasAdapter();
                    }
                });

        builder.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    @Override // mengambil menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override //action
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            startActivity(new Intent(this, FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
