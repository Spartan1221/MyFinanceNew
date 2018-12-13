package spartanmobileapps.com.myfinance;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfitAdd extends AppCompatActivity implements View.OnClickListener {

    TextView tvType, tvSum,tvData;
    EditText etType,etSum,etData;
    Button btnAdd,btnUpdate;

    LinearLayout lnSpend;
    DB dbHelper;

    String dateMonth, dateDay, dateForPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_add);




        tvType=(TextView)findViewById(R.id.tvType);
        tvSum=(TextView)findViewById(R.id.tvSum);

        etSum=(EditText)findViewById(R.id.etSum);
        etType=(EditText)findViewById(R.id.etType);

        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        dbHelper=new DB(this);



    }

    @Override
    public void onClick(View v) {

        String type=etType.getText().toString();
        String sum=etSum.getText().toString();

        SQLiteDatabase database=dbHelper.getWritableDatabase();

        ContentValues contentValues=new ContentValues();

        switch (v.getId()){
            case R.id.btnAdd:

                Date date=new Date();

                SimpleDateFormat formatMonth=new SimpleDateFormat("MM.yyyy");
                SimpleDateFormat formatDay=new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat formatDayForPayment=new SimpleDateFormat("dd.MM.");

                dateForPayment=formatDayForPayment.format(date);
                dateMonth = formatMonth.format(date);
                dateDay = formatDay.format(date);

                Log.d("mLog","dateNow "+dateMonth);

                contentValues.put(DB.KEY_TYPE,type);
                contentValues.put(DB.KEY_SUM,sum);
                contentValues.put(DB.KEY_DATE1,dateMonth);
                contentValues.put(DB.KEY_DATE2,dateDay);
                contentValues.put(DB.KEY_DATE3,dateForPayment);

                database.insert(DB.TABLE_NAME1,null,contentValues);
                Log.d("mLog","Add rows "+type+", "+sum+", "+dateMonth+", "+dateDay);
                break;
            /*case R.id.btnUpdate:
                //поставить условие на проверку пустых полей
                if (data.equalsIgnoreCase("")){
                    break;
                }
                contentValues.put(DBHelper.KEY_TYPE,type);
                contentValues.put(DBHelper.KEY_SUM,sum);

                int updCount=database.update(DBHelper.TABLE_NAME1,contentValues,DBHelper.KEY_DATA+"= ?",new String[]{data});

                Log.d("mLog","update ="+updCount);
                break;*/
        }
        dbHelper.close();
        this.finish();
    }
}
