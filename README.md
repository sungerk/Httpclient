# Httpclient
android 6.0 移除了 apache httpclient  <br>  
为了做兼容，我们必须考虑这个问题，下面推荐几个解决方案<br>  

1.使用[okhttp](https://github.com/square/okhttp)、[volley](https://github.com/mcxiaoke/android-volley)<br>  
2.官网下载[apache](http://hc.apache.org/) httpclient jar包<br>  
3.使用HttpURLConnection访问网络。<br>  

##这个库是基于HttpURLConnection封装的同步和异步网络请求库。

##<a name="code"/>异步请求
```Java
 AsyncHttpCliect cliect = new AsyncHttpCliect();
        Map<String, String> map = new HashMap<String, String>();
        map.put("ip", "63.223.108.42");
        cliect.post("http://ip.taobao.com/service/getIpInfo.php", map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(HttpResponse response) {


            }
        });
        cliect.get("http://www.baidu.com", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(HttpResponse response) {

            }
        });
```


同步访问如下：
##<a name="code"/>同步请求
```Java
 HttpClient httpClient=new HttpClient();
        try {
            httpClient.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
            httpClient.get("http://www.baidu.com").addParam("name","sunger").addParam("age","22").execute();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
```
##<a name="code"/>文件下载，支持断点续传
```Java
 DownloadDelegate   downloadDelegate = new DownloadDelegate(this, "http://dldir1.qq.com/weixin/android/weixin625android620.apk", "/sdcard/a/", new DownloadHandler() {
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
        //开始下载
        downloadDelegate.start();
         //暂停下载
        downloadDelegate.pause();
```

##<a name="code"/>Https请求导入证书
```Java
  AsyncHttpCliect cliect = new AsyncHttpCliect();
        try {
            cliect.setCertificates(getAssets().open("srca.cer"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cliect.get("https://kyfw.12306.cn/otn/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d("-----HttpResponse--", response.getStatusCode() + "");
            }

            @Override
            public void onFailure(Throwable error) {
                Log.d("-----Error--", error.getMessage());
            }
        });
```

下一次更新将实现[HttpResponseCache](http://developer.android.com/reference/android/net/http/HttpResponseCache.html)Http缓存功能
