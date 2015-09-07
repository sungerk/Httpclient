package lib.sunger.org.httpclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunger.lib.download.delegate.DownloadDelegate;
import com.sunger.lib.download.handler.DownloadHandler;

public class MainActivity extends AppCompatActivity {
    DownloadDelegate downloadDelegate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadDelegate = new DownloadDelegate(this, "http://dldir1.qq.com/weixin/android/weixin625android620.apk", "/sdcard/a/", new DownloadHandler() {
            @Override
            protected void onFinish(String msg) {

            }

            @Override
            public void onError(String msg) {

            }

            @Override
            protected void onSpeed(String speed) {

            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDelegate.start();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDelegate.pause();
            }
        });

//        AsyncHttpCliect cliect = new AsyncHttpCliect();
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("ip", "63.223.108.42");
//        cliect.post("http://ip.taobao.com/service/getIpInfo.php", map, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(HttpResponse response) {
//
//
//            }
//        });
//        cliect.get("http://www.baidu.com", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(HttpResponse response) {
//
//            }
//        });
//
//        HttpClient httpClient=new HttpClient();
//        try {
//            httpClient.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
//            httpClient.get("http://www.baidu.com").addParam("name","sunger").addParam("age","22").execute();
//        } catch (HttpClientException e) {
//            e.printStackTrace();
//        }


    }


}
