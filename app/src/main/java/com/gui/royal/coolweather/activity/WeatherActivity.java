package com.gui.royal.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gui.royal.coolweather.R;
import com.gui.royal.coolweather.util.HttpCallbackListener;
import com.gui.royal.coolweather.util.HttpUtil;
import com.gui.royal.coolweather.util.Utility;

/**显示天气信息的活动
 * Created by Jeremy on 2015/5/19.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;
    /**
    *显示城市名
    */
    private TextView cityNameText;

    /**
     * 标题栏布局
     */
    private RelativeLayout titleLayout;

    /**
     * 显示发布时间的
     */
    private TextView publishText;

    /**
     * 显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 高温
     */
    private TextView temp1Text;
    /**
     * 低温
     */
    private TextView temp2Text;
    /**
     * 当前时间
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCtiy;

    /**
     * 刷新天气按钮
     */
    private Button refreshWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_weather);

        //初始化组件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.tv_city_name);
        titleLayout = (RelativeLayout) findViewById(R.id.rl_title);
        publishText = (TextView) findViewById(R.id.tv_publish);
        weatherDespText = (TextView) findViewById(R.id.tv_weather_desp);
        temp1Text = (TextView) findViewById(R.id.tv_temp1);
        temp2Text = (TextView) findViewById(R.id.tv_temp2);
        currentDateText = (TextView) findViewById(R.id.tv_current_date);
        switchCtiy = (Button) findViewById(R.id.btn_switch_city);
        refreshWeather = (Button) findViewById(R.id.btn_refresh_weather);
        titleLayout.getBackground().setAlpha(80);//设置标题栏的透明度

        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            //有县代号时就去查询天气
            publishText.setText("正在更新...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);//更新过程中设置不可见
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有县级代号时直接显示本地天气
            showWeather();
        }
        switchCtiy.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    /**
     * 查询城市代号对应城市的天气代号
     * @param countyCode  城市代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }


    /**
     * 根据传入的地址和类型从服务器上查询天气代号和天气信息
     * @param address 查询地址
     * @param type 城市类型
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final  String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)) {
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("更新失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreference文件中读取存储的天气信息，并显示
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString( "city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    /**
     * 查询天气代号对应的城市的天气
     * @param weatherCode 城市天气代号
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode +".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 设置按钮的点击事件
     * @param v View
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_city:
                Intent intent = new Intent (this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_refresh_weather:
                publishText.setText("更新中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weetherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weetherCode)) {
                    queryWeatherInfo(weetherCode);
                }
                break;
            default:
                break;
        }
    }
}
