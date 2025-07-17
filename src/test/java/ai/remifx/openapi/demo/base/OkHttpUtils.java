package ai.remifx.openapi.demo.base;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {


    private static OkHttpClient createUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final OkHttpClient okHttpClient = createUnsafeOkHttpClient();


    public static  String  get(String url, LinkedHashMap<String,String> header){
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        if (header!=null&&header.size()>0){
            for (String key : header.keySet()){
                builder.addHeader(key,header.get(key));
            }
        }
        try (Response response = okHttpClient.newCall(builder.build()).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new RuntimeException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String post(String url, LinkedHashMap<String,String> param,LinkedHashMap<String,String> header){
        okhttp3.FormBody.Builder formBuilder = new okhttp3.FormBody.Builder();
        if (param!=null&&param.size()>0){
            for (String key: param.keySet()){
                formBuilder.add(key,param.get(key));
            }
        }
        okhttp3.RequestBody body = formBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (header!=null&&header.size()>0){
            for (String key : header.keySet()){
                builder.addHeader(key,header.get(key));
            }
        }
        try (Response response = okHttpClient.newCall(builder.build()).execute()) {
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new RuntimeException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String delete(String url, LinkedHashMap<String,String> param,LinkedHashMap<String,String> header){
//        okhttp3.FormBody.Builder formBuilder = new okhttp3.FormBody.Builder();
//        if (param!=null&&param.size()>0){
//            for (String key: param.keySet()){
//                formBuilder.add(key,param.get(key));
//            }
//        }
//        okhttp3.RequestBody body = formBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();
        if (header!=null&&header.size()>0){
            for (String key : header.keySet()){
                builder.addHeader(key,header.get(key));
            }
        }

        try (Response response = okHttpClient.newCall(builder.build()).execute()) {

            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new RuntimeException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
