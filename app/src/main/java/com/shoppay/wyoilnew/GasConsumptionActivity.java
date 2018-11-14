package com.shoppay.wyoilnew;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.wyoilnew.bean.FastShopZhehMoney;
import com.shoppay.wyoilnew.bean.VipInfo;
import com.shoppay.wyoilnew.card.ReadCardOpt;
import com.shoppay.wyoilnew.http.InterfaceBack;
import com.shoppay.wyoilnew.tools.ActivityStack;
import com.shoppay.wyoilnew.tools.BluetoothUtil;
import com.shoppay.wyoilnew.tools.CommonUtils;
import com.shoppay.wyoilnew.tools.DialogUtil;
import com.shoppay.wyoilnew.tools.ESCUtil;
import com.shoppay.wyoilnew.tools.LogUtils;
import com.shoppay.wyoilnew.tools.MergeLinearArraysUtil;
import com.shoppay.wyoilnew.tools.PreferenceHelper;
import com.shoppay.wyoilnew.tools.StringUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/9/14.
 */

public class GasConsumptionActivity extends Activity {


    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.gas_et_cardnum)
    EditText gasEtCardnum;
    @Bind(R.id.gas_tv_vipname)
    TextView gasTvVipname;
    @Bind(R.id.gas_tv_cardtype)
    TextView gasTvCardtype;
    @Bind(R.id.gas_tv_carnum)
    TextView gasTvCarnum;
    @Bind(R.id.gas_tv_vipjifen)
    TextView gasTvVipjifen;
    @Bind(R.id.gas_tv_vipyue)
    TextView gasTvVipyue;
    @Bind(R.id.gas_tv_vipdj)
    TextView gasTvVipdj;
    @Bind(R.id.gas_rl_chose)
    RelativeLayout gasRlChose;
    @Bind(R.id.gas_tv_price)
    TextView gasTvPrice;
    @Bind(R.id.gas_et_xfmoney)
    EditText gasEtXfmoney;
    @Bind(R.id.gas_tv_zhmoney)
    TextView gasTvZhmoney;
    @Bind(R.id.gas_tv_num)
    TextView gasTvNum;
    @Bind(R.id.tv_yue)
    TextView tvYue;
    @Bind(R.id.rl_money)
    RelativeLayout rlMoney;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.rl_yue)
    RelativeLayout rlYue;
    @Bind(R.id.gas_tv_hasjf)
    TextView gasTvHasjf;
    @Bind(R.id.tv_jiesuan)
    TextView tvJiesuan;
    @Bind(R.id.gas_rl_jiesuan)
    RelativeLayout gasRlJiesuan;
    private Activity ac;
    private String editString;
    private boolean isFcard=false;
    private boolean isMoney=false;
    private List<GasType> list;
    private GasType dengji;
    private boolean isVipOk=false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    isVipOk=true;
                    VipInfo info = (VipInfo) msg.obj;
                    if(info.IsMustSwingCard){
                        if(PreferenceHelper.readBoolean(ac,"shoppay","isCard", false)){
                            if (info.MemState == 0) {
                                gasTvVipname.setText(info.MemName);
                                String ty="";
                                switch (info.CardType){
                                    case 0:
                                        ty="父卡";
                                        isFcard=true;
                                        break;
                                    case 1:
                                        ty="子卡";
                                        isFcard=false;
                                        break;
                                    case 2:
                                        ty="个人";
                                        isFcard=false;
                                        break;
                                }
                                gasTvCardtype.setText(ty);
                                gasTvCarnum.setText(info.MemCardNumber);
                                gasTvVipjifen.setText(info.MemPoint);
                                gasTvVipyue.setText(info.MemMoney);
                                switch (info.PayType){
                                    case 0:
                                        isMoney=false;
//                                        rlYue.setBackgroundColor(getResources().getColor(R.color.theme_red));
//                                        tvYue.setTextColor(getResources().getColor(R.color.white));
//                                        rlMoney.setBackgroundColor(getResources().getColor(R.color.white));
//                                        tvMoney.setTextColor(getResources().getColor(R.color.text_30));
                                        tvMoney.setText("余额");
                                        break;
                                    case 1:
                                        isMoney=true;
//                                        rlYue.setBackgroundColor(getResources().getColor(R.color.white));
//                                        tvYue.setTextColor(getResources().getColor(R.color.text_30));
//                                        rlMoney.setBackgroundColor(getResources().getColor(R.color.theme_red));
//                                        tvMoney.setTextColor(getResources().getColor(R.color.white));
                                        tvMoney.setText("现金");
                                        break;
                                }
                                PreferenceHelper.write(MyApplication.context, "shoppay", "vipcar", gasEtCardnum.getText().toString());
                                PreferenceHelper.write(MyApplication.context, "shoppay", "memid", info.MemID + "");
                                PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", info.MemLevelID + "");
                                PreferenceHelper.write(MyApplication.context, "shoppay", "MemMoney", info.MemMoney);
                            } else if (info.MemState == 1) {
                                gasTvVipname.setText("");
                                gasTvCardtype.setText("");
                                gasTvCarnum.setText("");
                                gasTvVipjifen.setText("");
                                gasTvVipyue.setText("");
                                Toast.makeText(MyApplication.context, "此卡已锁定", Toast.LENGTH_LONG).show();
                                PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已锁定");
                            } else {
                                gasTvVipname.setText("");
                                gasTvCardtype.setText("");
                                gasTvCarnum.setText("");
                                gasTvVipjifen.setText("");
                                gasTvVipyue.setText("");
                                Toast.makeText(MyApplication.context, "此卡已挂失", Toast.LENGTH_LONG).show();
                                PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已挂失");
                            }
                        }else {
                            Toast.makeText(MyApplication.context, "必须刷卡消费", Toast.LENGTH_LONG).show();
                            gasEtCardnum.setText("");
                            gasTvVipname.setText("");
                            gasTvCardtype.setText("");
                            gasTvCarnum.setText("");
                            gasTvVipjifen.setText("");
                            gasTvVipyue.setText("");
                        }
                    }else {
                        if (info.MemState == 0) {
                            gasTvVipname.setText(info.MemName);
                            String ty="";
                            switch (info.CardType){
                                case 0:
                                    ty="父卡";
                                    isFcard=true;
                                    break;
                                case 1:
                                    ty="子卡";
                                    isFcard=false;
                                    break;
                                case 2:
                                    ty="个人";
                                    isFcard=false;
                                    break;
                            }
                            gasTvCardtype.setText(ty);
                            gasTvCarnum.setText(info.MemCardNumber);
                            gasTvVipjifen.setText(info.MemPoint);
                            gasTvVipyue.setText(info.MemMoney);
                            switch (info.PayType){
                                case 0:
                                    isMoney=false;
//                                    rlYue.setBackgroundColor(getResources().getColor(R.color.theme_red));
//                                    tvYue.setTextColor(getResources().getColor(R.color.white));
//                                    rlMoney.setBackgroundColor(getResources().getColor(R.color.white));
//                                    tvMoney.setTextColor(getResources().getColor(R.color.text_30));
                                    tvMoney.setText("余额");
                                    break;
                                case 1:
                                    isMoney=true;
//                                    rlYue.setBackgroundColor(getResources().getColor(R.color.white));
//                                    tvYue.setTextColor(getResources().getColor(R.color.text_30));
//                                    rlMoney.setBackgroundColor(getResources().getColor(R.color.theme_red));
//                                    tvMoney.setTextColor(getResources().getColor(R.color.white));
                                    tvMoney.setText("现金");
                                    break;
                            }
                            PreferenceHelper.write(MyApplication.context, "shoppay", "vipcar", gasEtCardnum.getText().toString());
                            PreferenceHelper.write(MyApplication.context, "shoppay", "memid", info.MemID + "");
                            PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", info.MemLevelID + "");
                            PreferenceHelper.write(MyApplication.context, "shoppay", "MemMoney", info.MemMoney);
                        } else if (info.MemState == 1) {
                            gasTvVipname.setText("");
                            gasTvCardtype.setText("");
                            gasTvCarnum.setText("");
                            gasTvVipjifen.setText("");
                            gasTvVipyue.setText("");
                            Toast.makeText(MyApplication.context, "此卡已锁定", Toast.LENGTH_LONG).show();
                            PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已锁定");
                        } else {
                            gasTvVipname.setText("");
                            gasTvCardtype.setText("");
                            gasTvCarnum.setText("");
                            gasTvVipjifen.setText("");
                            gasTvVipyue.setText("");
                            Toast.makeText(MyApplication.context, "此卡已挂失", Toast.LENGTH_LONG).show();
                            PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "此卡已挂失");
                        }
                    }
                    break;
                case 2:
                    isVipOk=false;
                    gasTvVipname.setText("");
                    gasTvCardtype.setText("");
                    gasTvCarnum.setText("");
                    gasTvVipjifen.setText("");
                    gasTvVipyue.setText("");
                    break;


                case 3:
                    FastShopZhehMoney zh = (FastShopZhehMoney) msg.obj;
                    gasTvZhmoney.setText(StringUtil.twoNum(zh.Money));
                    gasTvHasjf.setText(Integer.parseInt(zh.Point)+"");
                    if(gasTvPrice.getText().toString()==null||gasTvPrice.getText().toString().equals("")){

                    }else {
                        gasTvNum.setText(StringUtil.twoNum(CommonUtils.div(Double.parseDouble(gasEtXfmoney.getText().toString()), Double.parseDouble(dengji.GasPrice), 2) + ""));
                    }
                    break;
                case 4:
                    gasTvZhmoney.setText("");
                    gasTvHasjf.setText("");
                    gasTvNum.setText("");
                    break;

            }
        }
    };
    private String xfmoney;
  private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasconsumption);
        ButterKnife.bind(this);
        ac=this;
        PreferenceHelper.write(ac,"shoppay","isCard", false);
        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
        PreferenceHelper.write(MyApplication.context, "shoppay", "jifenpercent", "123");
        PreferenceHelper.write(MyApplication.context,"shoppay","viptoast","未查询到会员");
        dialog = DialogUtil.loadingDialog(ac, 1);
        tvTitle.setText("天然气消费");
        ActivityStack.create().addActivity(ac);
        vipDengjiList("no");
          gasEtCardnum.setOnKeyListener(onKey);
        gasEtXfmoney.setOnKeyListener(onKey);
        gasEtCardnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = editable.toString();
//                PreferenceHelper.write(ac,"shoppay","isCard", false);
                Log.d("xxs",editString);
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 800);
            }
        });
        gasEtXfmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {

                } else {
                    if (PreferenceHelper.readString(MyApplication.context, "shoppay", "vipdengjiid", "123").equals("123")) {
                        Toast.makeText(MyApplication.context,PreferenceHelper.readString(MyApplication.context,"shoppay","viptoast","未查询到会员"),Toast.LENGTH_SHORT).show();
                        gasEtXfmoney.setText("");
                    } else {
                        if (moneyrun != null) {
                            //每次editText有变化的时候，则移除上次发出的延迟线程
                            handler.removeCallbacks(moneyrun);
                        }
                        xfmoney = editable.toString();

                        //延迟800ms，如果不再输入字符，则执行该线程的run方法
                        handler.postDelayed(moneyrun, 800);
                    }
                }
            }
        });
    }
    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable moneyrun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainZhehMoney();
        }
    };


    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainVipInfo();
        }
    };

    private void obtainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memCard", editString);
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=AppGetMem_Oil", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxVipinfoS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<VipInfo>>() {
                        }.getType();
                        List<VipInfo> list = gson.fromJson(jso.getString("data"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
//                        Toast.makeText(MyApplication.context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                    PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.d("xxVipinfoE", error.getMessage());
                PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });

    }
    private void obtainZhehMoney() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("money", xfmoney);
        params.put("levelid", PreferenceHelper.readString(MyApplication.context, "shoppay", "vipdengjiid", "123"));
        params.put("memid",   PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=GetOilDiscountMoney", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxZhehMoneyS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<FastShopZhehMoney>>() {
                        }.getType();
                        List<FastShopZhehMoney> list = gson.fromJson(jso.getString("data"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = list.get(0);
                        handler.sendMessage(msg);
                    } else {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                        PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                        Message msg = handler.obtainMessage();
                        msg.what = 4;
                        handler.sendMessage(msg);
                        Toast.makeText(MyApplication.context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                    PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                    Message msg = handler.obtainMessage();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                PreferenceHelper.write(MyApplication.context, "shoppay", "memid", "123");
                PreferenceHelper.write(MyApplication.context, "shoppay", "vipdengjiid", "123");
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
            }
        });
    }
    @OnClick({R.id.rl_left, R.id.gas_rl_jiesuan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                ActivityStack.create().finishActivity(ac);
                break;
            case R.id.gas_rl_jiesuan:
                if (gasEtCardnum.getText().toString().equals("")
                        || gasEtCardnum.getText().toString() == null) {
                    Toast.makeText(MyApplication.context, "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                } else if (gasEtXfmoney.getText().toString().equals("")
                        || gasEtXfmoney.getText().toString() == null) {
                    Toast.makeText(MyApplication.context, "请输入消费金额",
                            Toast.LENGTH_SHORT).show();
                } else if (!isVipOk) {
                    Toast.makeText(MyApplication.context, "请重新输入会员卡号，获取会员信息",
                            Toast.LENGTH_SHORT).show();
                } else if (gasTvZhmoney.getText().toString().equals("")) {
                    Toast.makeText(MyApplication.context, "请重新输入消费金额，获取折后金额",
                            Toast.LENGTH_SHORT).show();
                }else if(gasTvPrice.getText().toString()==null||gasTvPrice.getText().toString().equals("")){
                    Toast.makeText(MyApplication.context, "请重新进入该界面，获取天然气单价",
                            Toast.LENGTH_SHORT).show();

                }else if(isFcard){
                    Toast.makeText(MyApplication.context, "此卡为父卡，不能消费",
                            Toast.LENGTH_SHORT).show();

                } else {
                    if (CommonUtils.checkNet(MyApplication.context)) {


                        if (isMoney) {
                            jiesuan();
                        } else {
                            if (PreferenceHelper.readBoolean(MyApplication.context, "shoppay", "IsChkPwd", false)) {
                                DialogUtil.pwdDialog("vip", ac, 1, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        jiesuan();
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {

                                    }
                                });
                            } else {
                                jiesuan();
                            }
                        }
                    }else{
                        Toast.makeText(MyApplication.context, "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();

                    }
                }
                break;
        }
    }

    private void vipDengjiList(final String type) {

        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        params.put("UserAcount", susername);
        client.post( PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=GetGasList", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try {
                    Log.d("xxLoginS",new String(responseBody,"UTF-8"));
                    JSONObject jso=new JSONObject(new String(responseBody,"UTF-8"));
                    if(jso.getBoolean("success")){
                        String data=jso.getString("data");
                        Gson gson=new Gson();
                        Type listType = new TypeToken<List<GasType>>(){}.getType();
                        list = gson.fromJson(data, listType);
                        if(type.equals("no")){
                              gasTvPrice.setText(list.get(0).GasPrice);
                            dengji=list.get(0);
                        }else {
                        }
                    }else{
                        if(type.equals("no")){
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    if(type.equals("no")){
                        Toast.makeText(ac, "获取天然气类型失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ac, "获取天然气类型失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                if(type.equals("no")){
                    Toast.makeText(ac, "获取天然气类型失败，请重新登录", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ac, "获取天然气类型失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new ReadCardOpt(gasEtCardnum);
    }

    @Override
    public void onStop() {
        try
        {
            new ReadCardOpt().overReadCard();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        super.onStop();
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
    }





    private void jiesuan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(MyApplication.context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("memID", PreferenceHelper.readString(MyApplication.context, "shoppay", "memid", "123"));
        params.put("Point", gasTvHasjf.getText().toString());
        params.put("Money", gasEtXfmoney.getText().toString());
        params.put("discountmoney", gasTvZhmoney.getText().toString());
        if(isMoney){
            params.put("bolIsCard", 0);//1：真 0：假
            params.put("bolIsCash",1);//1：真 0：假
        }else{
            params.put("bolIsCard", 1);//1：真 0：假
            params.put("bolIsCash", 0);//1：真 0：假
        }
        params.put("oilID",dengji.GasID);
        params.put("oilNum", gasTvNum.getText().toString());//1：真 0：假
        Log.d("xx",params.toString());
        client.post(PreferenceHelper.readString(MyApplication.context, "shoppay", "yuming", "123") + "/mobile/app/api/appAPI.ashx?Method=OilExpense", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getBoolean("success")) {
                        PreferenceHelper.write(MyApplication.context, "shoppay", "OrderAccount", jso.getJSONObject("data").getString("OrderAccount"));
                        Toast.makeText(MyApplication.context, "结算成功",
                                Toast.LENGTH_LONG).show();
                        if(!isMoney) {
                            if (PreferenceHelper.readBoolean(MyApplication.context, "shoppay", "IsPrint", false)) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(printReceipt_BlueTooth(), PreferenceHelper.readInt(MyApplication.context, "shoppay", "GoodsExpenesPrintNumber", 1));
                            }
                        }
                    finish();

                    } else {
                        Toast.makeText(MyApplication.context, jso.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                LogUtils.d("xxjiesuanE",error.getMessage());
                Toast.makeText(MyApplication.context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public byte[] printReceipt_BlueTooth()
    {
        String danhao = "消费单号:" + PreferenceHelper.readString(MyApplication.context, "shoppay","OrderAccount","");
        String huiyuankahao = "会员卡号:" + gasEtCardnum.getText().toString();
        String huiyuanming = "会员名称:" +gasTvVipname.getText().toString();

        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(MyApplication.context,"shoppay","PrintTitle","").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(MyApplication.context,"shoppay","PrintFootNote","").getBytes("gb2312");
            byte[] tickname = "天然气消费小票".getBytes("gb2312");
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus = "网 507".getBytes("gb2312");
            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
            byte[] left = ESCUtil.alignLeft();
            boldOn = ESCUtil.boldOn();
            byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
            boldOff = ESCUtil.boldOff();
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
            next2Line = ESCUtil.nextLine(2);
            byte[] nextLine = ESCUtil.nextLine(1);
            nextLine = ESCUtil.nextLine(1);
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();
            byte[][] mytitle = {nextLine, center, boldOn, title, boldOff, next2Line, left, tickname, nextLine, left, ordernum, nextLine, left,
                    vipcardnum, nextLine,
                    left, vipname,nextLine,xiahuaxian};

            byte[] headerBytes =ESCUtil. byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            byte[] xfmoney =( "消费金额:" +StringUtil.twoNum(gasEtXfmoney.getText().toString())).getBytes("gb2312");
            byte[] hasjifen =( "获得积分:" +gasTvHasjf.getText().toString()).getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, xfmoney,nextLine,left,hasjifen};
            bytesList.add(ESCUtil.byteMerger(mticket1));


            byte[][] mtickets = {nextLine,xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));

            byte[] yfmoney =( "应付金额:" +StringUtil.twoNum(gasTvZhmoney.getText().toString())).getBytes("gb2312");
            double xx=Double.parseDouble(gasEtXfmoney.getText().toString());
            double zh=Double.parseDouble(gasTvZhmoney.getText().toString());
            Log.d("xxx",xx+";"+zh);
                byte[] jinshengmoney = ("节省金额:" + StringUtil.twoNum(Double.toString(CommonUtils.del(xx, zh)))).getBytes("gb2312");

                byte[][] mticketsn = {nextLine, left, yfmoney, nextLine, left, jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
            if(isMoney){
                byte[] moneys=( "现金支付:" +StringUtil.twoNum(gasTvZhmoney.getText().toString())).getBytes("gb2312");
                byte[][] mticketsm= {nextLine,left,moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            else{
                byte[] yue=( "余额支付:" +StringUtil.twoNum(gasTvZhmoney.getText().toString())).getBytes("gb2312");
                byte[][] mticketyue= {nextLine,left,yue};
                bytesList.add(ESCUtil.byteMerger(mticketyue));
            }
            double syjf = Double.parseDouble(gasTvVipjifen.getText().toString()) + Double.parseDouble(gasTvHasjf.getText().toString());

            byte[] syjinfen=( "剩余积分:" +(int)syjf).getBytes("gb2312");
            byte[][] mticketsyjf= {nextLine,left,syjinfen};
            bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            double yuemoney=0;
            if(isMoney){
            }else{
                yuemoney=Double.parseDouble(gasTvZhmoney.getText().toString());
            }
            if(!isMoney) {
                double sy = CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(MyApplication.context, "shoppay", "MemMoney", "0")) , yuemoney);
                byte[] shengyu = ("卡内余额:" + StringUtil.twoNum(sy+"")).getBytes("gb2312");
                byte[][] mticketsy = {nextLine, left, shengyu};
                bytesList.add(ESCUtil.byteMerger(mticketsy));
            }
            byte[] ha=( "操作人员:"+PreferenceHelper.readString(MyApplication.context
                    ,"shoppay","UserName","")).trim().getBytes("gb2312");
            byte[] time=( "消费时间:"+getStringDate()).trim().getBytes("gb2312");
            byte[] qianming=( "客户签名:").getBytes("gb2312");

            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            Log.d("xx","UnsupportedEncodingException");
        }
        return null;
    }
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    View.OnKeyListener onKey=new View.OnKeyListener() {

        @Override

        public boolean onKey(View v, int keyCode, KeyEvent event) {

// TODO Auto-generated method stub

            if(keyCode == KeyEvent.KEYCODE_ENTER){

                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm.isActive()){

                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );

                }

                return true;

            }

            return false;

        }

    };
}
