package ai.remifx.openapi.demo.base;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.api.client.util.Charsets;
import org.apache.curator.shaded.com.google.common.hash.Hashing;

import java.util.LinkedHashMap;
import java.util.List;

public class BaseTests {
    private String baseUrl = "https://api.remifx-test.ai/openapi/";
    private String secretKey = "HruFdwUTvn5h8bq9hYWjkaVB1iryJ0n6ig9kVwtavBMrQ6tPV2xZHetSiE3BiKpm";
    private String apiKey = "NBjQ0qp4C3p289yFg9KlkYmS6vsQ5iC5c8eiQYHumwyzYMdN9aXgkcfT3YBjROoh";
    /**
     * 签名
     * @param secretKey
     * @return
     */
    private   String sign(String secretKey,LinkedHashMap<String,String> map){
        StringBuffer paramBuffer = new StringBuffer();
        return  Hashing.hmacSha256(secretKey.getBytes()).hashString(getOriginStr(map), Charsets.UTF_8).toString();
    }
    private  String getOriginStr(LinkedHashMap<String,String> map){
        List<String> paramList = Lists.newArrayList();
        for (String key:map.keySet()){
            paramList.add(key + "=" + map.get(key));
        }
        return String.join("&", paramList);
    }

    private static String getUrlParamStr(LinkedHashMap<String,String> map){
        List<String> paramList = Lists.newArrayList();
        for (String key:map.keySet()){
            paramList.add(key + "=" + map.get(key));
        }
        return String.join("&", paramList);
    }

    protected String bizRequest(ActionEnum actionEnum, LinkedHashMap<String,String> param){
        String method = actionEnum.getMethod().toLowerCase();
        String url = baseUrl + actionEnum.getAction();
        String resone  ="";
        switch (method){
            case "post":
                if (actionEnum.isSign()){
                    String sign  = sign(secretKey,param);
                    param.put("signature",sign);
                }
                System.out.println("requestUrl="+url);
                System.out.println("request="+ JSONObject.toJSONString(param));
                resone =  OkHttpUtils.post(url,param,header());
                break;
            case "get":
                if (param!=null && param.size()>0){
                    url =url+"?"+ getUrlParamStr(param);
                }
                if (actionEnum.isSign()){
                    String sign  = sign(secretKey,param);
                    url=url+"&signature="+sign;
                }
                System.out.println("requestUrl="+url);
                resone =   OkHttpUtils.get(url,header());
            break;
            case "delete":
                if (param!=null && param.size()>0){
                    url =url+"?"+ getUrlParamStr(param);
                }
                if (actionEnum.isSign()){
                    String sign  = sign(secretKey,param);
                    url=url+"&signature="+sign;
                }
                System.out.println("requestUrl="+url);
                System.out.println("request del="+ JSONObject.toJSONString(param));
                resone =  OkHttpUtils.delete(url,param,header());
                break;
        }
        System.out.println("url = "+url);
        System.out.println("resone = "+resone);
        return resone;
    }

    private LinkedHashMap<String,String> header(){
        LinkedHashMap<String,String>  header= new LinkedHashMap<>();
        header.put("X-BH-APIKEY", apiKey);
        return header ;
    }


    public static  void main(String[] array){
        System.out.println("123456");

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("KEY1","VAL1");
        map.put("KEY2","VAL2");
        map.put("KEY3","VAL3");
        map.put("KEY4","VAL4");
        map.put("KEY5","VAL5");

        System.out.println(getUrlParamStr(map));



    }


}
