package lib.sunger.org.httpclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.sunger.lib.http.asyn.AsyncHttpCliect;
import org.sunger.lib.http.asyn.AsyncHttpResponseHandler;
import org.sunger.lib.http.client.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncHttpCliect cliect = new AsyncHttpCliect();
        Map<String, String> map = new HashMap<String, String>();
        map.put("ip", "63.223.108.42");
        cliect.post("http://ip.taobao.com/service/getIpInfo.php", map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(HttpResponse response) {
                StringBuffer out = new StringBuffer();
                byte[] b = new byte[4096];
                try {
                    for (int n; (n = response.getPayload().read(b)) != -1; ) {
                        out.append(new String(b, 0, n));
                    }
                    Log.d("sunger",out.toString());
                    Toast.makeText(MainActivity.this,out.toString(),Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }


}
