package spartanmobileapps.com.myfinance;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentAdd extends AppCompatActivity implements View.OnClickListener {
    TextView tvType, tvSum,tvData;
    EditText etType,etSum,etData;
    Button btnAdd,btnUpdate;

    LinearLayout lnSpend;
    DB dbHelper;

    String dateMonth, dateDay, dateForPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_add);



        tvType=(TextView)findViewById(R.id.tvType);
        tvSum=(TextView)findViewById(R.id.tvSum);
        tvData=(TextView)findViewById(R.id.tvData);

        etSum=(EditText)findViewById(R.id.etSum);
        etType=(EditText)findViewById(R.id.etType);


        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        dbHelper=new DB(this);

        Date date=new Date();

        SimpleDateFormat formatMonth=new SimpleDateFormat("MM.yyyy");
        SimpleDateFormat formatDay=new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatDayForPayment=new SimpleDateFormat("dd.MM.");

        dateForPayment=formatDayForPayment.format(date);
        dateMonth = formatMonth.format(date);
        dateDay = formatDay.format(date);

        Log.d("mLog","dateDay= "+dateDay);
        Log.d("mLog","dateMonth= "+dateMonth);

        tvData.setText(dateDay);
    }

    @Override
    public void onClick(View v) {

        String type="Payment";
        String sum=etSum.getText().toString();

        SQLiteDatabase database=dbHelper.getWritableDatabase();
        SQLiteDatabase readDatabase=dbHelper.getReadableDatabase();

        ContentValues contentValues=new ContentValues();

        switch (v.getId()){
            case R.id.btnAdd:


                contentValues.put(DB.KEY_TYPE,type);
                contentValues.put(DB.KEY_SUM,sum);
                contentValues.put(DB.KEY_DATE1,dateMonth);
                contentValues.put(DB.KEY_DATE2,dateDay);
                contentValues.put(DB.KEY_DATE3,dateForPayment);



                /*if (etData.getText().toString().equals("")){
                    contentValues.put(DBHelper.KEY_DATE1,dateMonth);
                }else {
                    contentValues.put(DBHelper.KEY_DATE1,etDate);
                }*/

                Cursor c=null;
                c=readDatabase.rawQuery("SELECT * FROM tPayment where dateDay =?", new String[]{dateDay});

                Log.d("mLog","Cursor count= "+c.getCount());

                if (c.getCount()!=0){
                    Log.d("mPayment","Payment Toast");
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.textToast, Toast.LENGTH_SHORT);
                    toast.show();
                }else {

                database.insert(DB.TABLE_NAME3,null,contentValues);
                Log.d("mLog","Add rows"+type+", "+sum);
                 }
                break;

        }
        dbHelper.close();


        this.finish();

    }
}
