package com.gui.royal.coolweather.util;


/**回调服务器的返回结果
 * Created by Jeremy on 2015/5/18.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError (Exception e);
}
