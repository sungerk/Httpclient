# Httpclient
android 6.0 移除了 apache httpclient 
为了做兼容，我们必须考虑这个问题，下面推荐几个解决方案

1.使用okhttp、volley
2.官网下载apache httpclient jar包
3.使用HttpURLConnection访问网络。

##这个库是基于HttpURLConnection封装的同步和异步网络请求库。

使用异步访问使用如下：
` ``java
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

同步访问如下：
` ``java
        HttpClient httpClient=new HttpClient();
        try {
            httpClient.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
            httpClient.get("http://www.baidu.com").addParam("name","sunger").addParam("age","22").execute();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
