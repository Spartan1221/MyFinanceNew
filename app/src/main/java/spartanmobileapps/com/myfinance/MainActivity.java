package spartanmobileapps.com.myfinance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private AdView mAdView;

    private TextView mTextMessage;

    public static final String TABLE_NAME1="tProfit";
    public static final String TABLE_NAME2="tSpend";
    public static final String TABLE_NAME3="tPayment";

    LinearLayout lnBalance, lnProfit, lnSpend,lnPayment;
    TextView tvMonth,tvDay, tvMonthBalance,tvDayProfit,tvDaySpend;
    TextView tvMonthRes,tvDayRes,tvMonthBalanceRes,tvDayProfitRes,tvDaySpendRes;


    BottomNavigationView bottomNavigationView;

    Intent intent;

    TextView tvType,tvSum,tvData;

    Toast mToast;

    int[] colors=new int[2];

    DB dbHelper;
    SQLiteDatabase db;
    LayoutInflater ltInflater;

    String columnsProfit="_id, dateMonth, dateDay, dateForPayment, type, sum ";
    String columnsSpend="_id, dateMonth, dateDay, dateForPayment, type, sum ";
    String columnsPayment="_id, dateMonth, dateDay, dateForPayment, type, sum ";

    String dateMonth,dateDay,dateForPayment;

    String oneProfit="1";
    String twoSpend="2";
    String threePayment="3";

    String sumProfit=null;
    String sumSpend=null;
    String sumPayment=null;

    Integer dayOfMonth;
    Integer minId1=0;
    Integer daySumPayment=0;
    Integer dayBalance=0;
    Integer budjetForPayment=0;
    Integer monthBudjet=0;

    AlertDialog ad;

    SpeedDialView speedDialView;

    String actionName;

    SharedPreferences sPref;

    ListView lvData, lvDataPayment;
    SimpleCursorAdapter scAdapter;

    String tableName=" ";

    private static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sPref=getSharedPreferences("MyPref",MODE_PRIVATE);
        String passwordSet1= sPref.getString("check", "1");
        Log.d("mPassword","passwordSet= "+passwordSet1);

        if (Integer.parseInt(passwordSet1)>0){
        Intent intent = new Intent(getApplicationContext(), EnterPinActivity.class);

        int resulteCode = 0;
        startActivityForResult(intent, REQUEST_CODE);
        onActivityResult(REQUEST_CODE,resulteCode,intent);

        }

        //Balance
        lnBalance=(LinearLayout)findViewById(R.id.lnBalance);

        tvDay=(TextView)findViewById(R.id.tvDay);
        tvDayRes=(TextView)findViewById(R.id.tvDayRes);
        tvDayProfit=(TextView)findViewById(R.id.tvDayProfit);
        tvDayProfitRes=(TextView)findViewById(R.id.tvDayProfitRes);
        tvDaySpend=(TextView)findViewById(R.id.tvDaySpend);
        tvDaySpendRes=(TextView)findViewById(R.id.tvDaySpendRes);
        tvMonth=(TextView)findViewById(R.id.tvMonth);
        tvMonthBalance=(TextView)findViewById(R.id.tvMonthBalance);
        tvMonthBalanceRes=(TextView)findViewById(R.id.tvMonthBalanceRes);
        tvMonthRes=(TextView)findViewById(R.id.tvMonthRes);

        //Отрытие и считывание базы
        dbHelper=new DB(this);
        db=dbHelper.getWritableDatabase();

        ltInflater=getLayoutInflater();
        lvData=(ListView)findViewById(R.id.lvData);

        lvDataPayment=(ListView)findViewById(R.id.lvDataPayment);
        /*                                               */
        mTextMessage = (TextView) findViewById(R.id.message);
        /*                                               */

        lnBalance.setVisibility(View.VISIBLE);
        lvData.setVisibility(View.GONE);
        lvDataPayment.setVisibility(View.GONE);



        bottomNavigationView=(BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_balance:
                                mTextMessage.setText(R.string.tvMessageBal);
                                lnBalance.setVisibility(View.VISIBLE);

                                lvData.setVisibility(View.GONE);
                                lvDataPayment.setVisibility(View.GONE);
                                speedDialView.setVisibility(View.GONE);



                                valueRead(TABLE_NAME3,columnsPayment,R.layout.item);
                                calculateValue();

                                return true;
                            case R.id.navigation_profit:
                                mTextMessage.setText(R.string.tvMessageProf);
                                lnBalance.setVisibility(View.GONE);

                                lvData.setVisibility(View.VISIBLE);
                                lvDataPayment.setVisibility(View.GONE);
                                speedDialView.setVisibility(View.VISIBLE);

                                tableName="tProfit";

                                actionName="spartanmobileappps.intent.action.profit";
                                valueRead(TABLE_NAME1,columnsProfit,R.layout.item1);

                                return true;
                            case R.id.navigation_spend:
                                mTextMessage.setText(R.string.tvMessageBuy);
                                lnBalance.setVisibility(View.GONE);

                                lvData.setVisibility(View.VISIBLE);
                                lvDataPayment.setVisibility(View.GONE);
                                speedDialView.setVisibility(View.VISIBLE);

                                tableName="tSpend";

                                actionName="spartanmobileappps.intent.action.spend";
                                valueRead(TABLE_NAME2,columnsSpend,R.layout.item1);

                                return true;

                            case R.id.navigation_payment:
                                mTextMessage.setText(R.string.tvMessagePay);
                                lnBalance.setVisibility(View.GONE);

                                lvData.setVisibility(View.GONE);
                                lvDataPayment.setVisibility(View.VISIBLE);
                                speedDialView.setVisibility(View.VISIBLE);

                                tableName="tPayment";

                                actionName="spartanmobileappps.intent.action.payment";
                                valueRead(TABLE_NAME3,columnsPayment,R.layout.item);
                                return true;
                        }
                        return false;
                    }
                });

        Date date=new Date();

        SimpleDateFormat formatMonth=new SimpleDateFormat("MM.yyyy");
        SimpleDateFormat formatDay=new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatDayForPayment=new SimpleDateFormat("dd.MM");


        dateMonth = formatMonth.format(date);
        dateDay=formatDay.format(date);
        dateForPayment=formatDayForPayment.format(date);

        speedDialView = findViewById(R.id.speedDial);
        initSpeedDial(savedInstanceState == null);

        //Получение количества дней в месяце
        Calendar calendar = Calendar.getInstance();
        dayOfMonth=Integer.parseInt(String.valueOf(calendar.getActualMaximum(calendar.DAY_OF_MONTH)));
        Log.d("mLog","DAY_OF_MONTH= "+dayOfMonth);

        valueRead(TABLE_NAME3,columnsPayment,R.layout.item);
        calculateValue();


        /*MobileAds.initialize(this, "ca-app-pub-3017783383508898~8739579819");*/
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == EnterPinActivity.RESULT_BACK_PRESSED) {
                   //закрытие приложения при нажатии кнопки назад
                   finish();
                }
                break;
        }
    }


    private void initSpeedDial(boolean addActionItems) {

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_new_count, R.drawable.if_add2)
                        .create()
        );

       /* speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_del_count, R.drawable.if_delete)
                        .create()
        );*/

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_password, R.drawable.if_lock)
                        .create()
        );


        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {

                    //удаление записи
                    case R.id.fab_del_count:
                       /* if (actionName=="spartanmobileappps.intent.action.payment"){
                            alertDialog(TABLE_NAME3, columnsPayment, lnPayment, R.layout.item);
                        }
                        if (actionName=="spartanmobileappps.intent.action.spend"){
                            alertDialog(TABLE_NAME2, columnsSpend, lnSpend, R.layout.item1);
                        }
                        if (actionName=="spartanmobileappps.intent.action.profit"){
                            alertDialog(TABLE_NAME1, columnsProfit, lnProfit, R.layout.item1);
                        }*/
                        return false;

                    //добавление записи
                    case R.id.fab_new_count:
                        intent=new Intent(actionName);
                        int requestCode = 0;
                        startActivity(intent);
                        return false;

                    //установка пароля
                    case R.id.fab_password:
                        passwordDialog();
                        return false;
                    default:
                        return false;

                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        //обновление таблиц

        if (tableName=="tProfit"){
            valueRead(TABLE_NAME1,columnsProfit,R.layout.item1);
        }
        if (tableName=="tSpend"){
            valueRead(TABLE_NAME2,columnsSpend,R.layout.item1);
        }
        if (tableName=="tPayment"){
            valueRead(TABLE_NAME3,columnsPayment,R.layout.item);
        }

    }

    public void alertDialog(final String table, final String columns, final LinearLayout layout, final int takeItem, final long id){
        ad=new AlertDialog.Builder(MainActivity.this).setMessage(R.string.messageDialog)
                .setPositiveButton(R.string.dialogYes,new  DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delCount(table,id);

                        Log.d("mDelete","DeleteChoose");
                        valueRead(table,columns,takeItem);

                        Toast.makeText(MainActivity.this,"Post deleted ",Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(R.string.dialogNo,new  DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.cancel();
                    }
                } ).create();
        ad.show();
    }

   public void passwordDialog(){
        ad=new AlertDialog.Builder(MainActivity.this).setMessage(R.string.passwordDialog)
                .setPositiveButton(R.string.dialogYes,new  DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //set password
                        Intent intent = EnterPinActivity.getIntent(MainActivity.this, true);
                        startActivity(intent);

                        sPref=getSharedPreferences("MyPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sPref.edit();

                        editor.putString("check","1");
                        editor.commit();
                        Log.d("mPassword","Password set");

                    }
                }).setNegativeButton(R.string.dialogDelete,new  DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sPref=getSharedPreferences("MyPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sPref.edit();

                        editor.putString("check","0");
                        editor.commit();
                        Log.d("mPassword","Password delete");
                    }
                } ).setNeutralButton(R.string.dialogNo,new  DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.cancel();
                    }
                } ).create();
        ad.show();
    }


    public void valueRead(String table1, String columns1, int takeItem){

        /*colors[0]=Color.parseColor("#559966CC");
        colors[1]=Color.parseColor("#55336699");*/

        //cursor
        Cursor c=null;
        Cursor cursorMin=null;

        cursorMin=db.rawQuery("SELECT min(_id) as minId FROM tPayment WHERE dateMonth="+dateMonth,new String[]{});
        c=db.rawQuery("SELECT "+columns1+" FROM "+ table1+" WHERE dateMonth= "+dateMonth,new String[]{});

        ArrayList<String> type=new ArrayList<String>();
        ArrayList<String> sum=new ArrayList<String>();
        ArrayList<String> dateMonth=new ArrayList<String>();
        ArrayList<String> dateDay=new ArrayList<String>();
        ArrayList<String> countId=new ArrayList<String>();

        ArrayList<String> dateDayForPayment=new ArrayList<String>();

        if(cursorMin!= null && cursorMin.moveToFirst()){
            minId1 = cursorMin.getInt( cursorMin.getColumnIndex("minId"));
            Log.d("mLog","minID = "+minId1);
            cursorMin.close();
        }

        while (c.moveToNext()){
            String lol1=c.getString(c.getColumnIndex("type"));
            type.add(lol1);
            String lol2=c.getString(c.getColumnIndex("sum"));
            sum.add(lol2);
            String lol3=c.getString(c.getColumnIndex("dateMonth"));
            dateMonth.add(lol3);
            String lol4=c.getString(c.getColumnIndex("dateDay"));
            dateDay.add(lol4);

            String lol5=c.getString(c.getColumnIndex("dateForPayment"));
            dateDayForPayment.add(lol5);

            String lol6=c.getString(c.getColumnIndex("_id"));
            countId.add(lol6);
        }
        c.close();

        //массив для Spend и Profit
        ArrayList<Map<String,Object>> data=new ArrayList<Map<String, Object>>(
                type.size());
        Map<String,Object>m;
        for (int i=0;i<type.size();i++){
            m=new HashMap<String, Object>();
            m.put("_id", countId.get(i));
            m.put("type", type.get(i));
            m.put("sum", sum.get(i));

            Log.d("mId","_id= "+countId.get(i)+"  type= "+type.get(i)+"  sum= "+sum.get(i));

            data.add(m);
        }
        Log.d("mId","--------------------------------------");

        //массив имен Spend, Profit
        String[]from={"_id","type","sum"};
        int[] to={R.id.tvCountId,R.id.tvType,R.id.tvSum};

        final SimpleAdapter simpleAdapter=new SimpleAdapter(this,data,R.layout.item1,from,to);
        simpleAdapter.hasStableIds();

        Log.d("mLog","count size= "+type.size());

        Cursor cursorSum=null;
        dayBalance=0;


            /*View item=ltInflater.inflate(takeItem,layout1,false);*/


            //Обработка Spend, Profit
        if (takeItem==R.layout.item1){

            lvData=(ListView)findViewById(R.id.lvData);
            lvData.setAdapter(simpleAdapter);
            lvData.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            LayoutInflater ltInflater = getLayoutInflater();
            View view = ltInflater.inflate(R.layout.item1, null, false);

            /*ImageView imageView=(ImageView)view.findViewById(R.id.imageButton);
            imageView.setClickable(true);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mLol","Lol");
                }
            });*/

           /* Button btnOne=(Button)view.findViewById(R.id.btnOne);
            btnOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mLol","Lol");
                }
            });*/


            lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        TextView countId=(TextView)view.findViewById(R.id.tvCountId);

                        Log.d("mId","Id= "+countId.getText().toString());

                        if (tableName=="tProfit"){
                            alertDialog(TABLE_NAME1, columnsProfit, lnProfit, R.layout.item1,Long.parseLong(countId.getText().toString()));

                        }
                        if (tableName=="tSpend"){
                            alertDialog(TABLE_NAME2, columnsSpend, lnSpend, R.layout.item1,Long.parseLong(countId.getText().toString()));

                        }

                    }
                });
            }

        ArrayList<String> budjetForPaymentM=new ArrayList<String>();
        ArrayList<String> dayBalanceM=new ArrayList<String>();

        //Обработка Payment
        if (takeItem==R.layout.item){

            for (int i=0;i<dateDayForPayment.size();i++){
                cursorSum=db.rawQuery("SELECT sum FROM tPayment WHERE _id= "+minId1,new String[]{});

                minId1=minId1+1;

                cursorSum.moveToFirst();
                if(cursorSum != null && cursorSum.moveToFirst()){

                    daySumPayment=cursorSum.getInt(cursorSum.getColumnIndex("sum"));
                    Log.d("mPayment","daySumPayment= "+daySumPayment);

                    allCalculate("tProfit",oneProfit);
                    allCalculate("tSpend",twoSpend);

                    Integer monthBudjet1=Integer.parseInt(sumProfit)-Integer.parseInt(sumSpend);
                    Log.d("mPayment","monthBudjet= "+monthBudjet1);
                    int dayBudjet1=monthBudjet1/dayOfMonth;

                    //вычисление дневного бюджета
                    budjetForPayment=dayBudjet1+dayBalance;

                    if (budjetForPayment<0){
                        budjetForPaymentM.add(String.valueOf(0));
                    }else {
                        budjetForPaymentM.add(String.valueOf(budjetForPayment));
                    }

                    //вычисление дневного баланса
                    dayBalance=dayBudjet1-daySumPayment+dayBalance;
                    Log.d("mPayment","dayBalance= "+dayBalance);

                    dayBalanceM.add(String.valueOf(dayBalance));
                    Log.d("mPayment","dayBalanceM= "+dayBalanceM.get(0));

                    cursorSum.close();
                }
            }

            //Payment
                ArrayList<Map<String,Object>> dataPayment=new ArrayList<Map<String, Object>>(
                        dateDayForPayment.size());
                Map<String,Object>m1;
                for (int i=0;i<dateDayForPayment.size();i++){
                    m1=new HashMap<String, Object>();
                    m1.put("_id", countId.get(i));
                    m1.put("date", dateDayForPayment.get(i));
                    m1.put("dayBudjet", budjetForPaymentM.get(i));
                    m1.put("sum", sum.get(i));
                    m1.put("daySum",  dayBalanceM.get(i));
                    dataPayment.add(m1);
                }

                //массив имен Payment
                String[]from1={"_id","date","dayBudjet","sum","daySum"};
                int[] to1={R.id.tvCountId,R.id.tvData12,R.id.tvDayBudget,R.id.tvSumPayment,R.id.tvDaySum};

                SimpleAdapter simpleAdapter1=new SimpleAdapter(this,dataPayment,R.layout.item,from1,to1);
                simpleAdapter.hasStableIds();
                lvDataPayment=(ListView)findViewById(R.id.lvDataPayment);
                lvDataPayment.setAdapter(simpleAdapter1);

                lvDataPayment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                lvDataPayment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        TextView countId=(TextView)view.findViewById(R.id.tvCountId);

                        alertDialog(TABLE_NAME3, columnsPayment, lnPayment, R.layout.item,Long.parseLong(countId.getText().toString()));
                       /* delCount(DB.TABLE_NAME3, Long.parseLong(countId.getText().toString()));
                        Log.d("mId","Id= "+countId.getText().toString());

                        Toast.makeText(MainActivity.this,"Post deleted ",Toast.LENGTH_SHORT).show();*/

                    }
                });


                long[] sba = lvData.getCheckedItemIds();
                for (int i = 0; i < sba.length; i++) {
                Log.d("mDelete","Lol");
                Log.d("mDelete","getCheckedItemsIds= "+sba[i]);
                /*db.delete(table, DB.KEY_ID + "=" + sba[i], null);*/

                }

        }

    }

    public void deleteChoose (String table) {
        long[] sba = lvData.getCheckedItemIds();
        for (int i = 0; i < sba.length; i++) {
                Log.d("mDelete","Lol");
                Log.d("mDelete","getCheckedItemsIds= "+sba[i]);
                /*db.delete(table, DB.KEY_ID + "=" + sba[i], null);*/
        }
    }

    public void delCount(String table, long id){
        /*db.delete(table, DB.KEY_ID + "= (SELECT MAX(_id) FROM "+ table+")", null);*/
        Log.d("mId","-----------------------------");
        Log.d("mId","Delete id= "+id);
        Log.d("mId","-----------------------------");
         db.delete(table, DB.KEY_ID + "= "+ id, null);
    }

    public void calculateValue(){

        allCalculate("tProfit",oneProfit);
        allCalculate("tSpend",twoSpend);
        allCalculate("tPayment",threePayment);

        Log.d("mLog","sumProfit= "+sumProfit);
        Log.d("mLog","sumSpend= "+sumSpend);
        Log.d("mLog","sumPayment= "+sumPayment);

        //Вычисление бюджета на месяц
        if(Integer.parseInt(sumProfit)==0||Integer.parseInt(sumSpend)>Integer.parseInt(sumProfit) ){
            monthBudjet=0;
        }else {
            monthBudjet = Integer.parseInt(sumProfit) - Integer.parseInt(sumSpend);
        }
        Log.d("mLog","monthBudjet= "+monthBudjet);
        tvMonthRes.setText(Integer.toString(monthBudjet)+" "+getString(R.string.currency));

        //Вычисление бюджета на день
        int dayBudjet=monthBudjet/dayOfMonth;
        Log.d("mLog","dayBudjet = "+dayBudjet);
        tvDayRes.setText(Integer.toString(dayBudjet)+" "+getString(R.string.currency));

        //Вычисление накоплений за месяц
        int bank=monthBudjet-Integer.parseInt(sumPayment);
        Log.d("mLog","bank= "+bank);
        tvMonthBalanceRes.setText(Integer.toString(bank)+" "+getString(R.string.currency));

        //Вычисление расхода в день
        int dayPayment=(Integer.parseInt(sumPayment)+Integer.parseInt(sumSpend))/dayOfMonth;
        Log.d("mLog","dayPayment= "+dayPayment);
        tvDaySpendRes.setText(Integer.toString(dayPayment)+" "+getString(R.string.currency));

        //Вычисление дохода в день
        int dayProfit=Integer.parseInt(sumProfit)/dayOfMonth;
        Log.d("mLog","dayProfit"+dayProfit);

        tvDayProfitRes.setText(Integer.toString(dayProfit)+" "+getString(R.string.currency));

    }

    public void allCalculate(String table, String value){

        Cursor c=null;
        c=db.rawQuery("SELECT sum(sum) as allSum FROM "+ table+" WHERE dateMonth = "+dateMonth,null);

        if(c != null && c.moveToFirst()){
            int allSum = c.getColumnIndex("allSum");

            if (c.getString(allSum)==null){
                if (value=="1"){
                    sumProfit="0";
                }
                if (value=="2"){
                    sumSpend="0";
                }
                if (value=="3"){
                    sumPayment="0";
                }
            }else {

                if (value=="1"){
                sumProfit= c.getString(allSum);
                }
                if (value=="2"){
                sumSpend= c.getString(allSum);
                }
                if (value=="3"){
                sumPayment= c.getString(allSum);
                }
            }
            c.close();
        }
    }
    protected void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbHelper.close();
    }

    @Override
    public void onClick(View v) {
        Log.d("mLol","Lol");
    }
}
