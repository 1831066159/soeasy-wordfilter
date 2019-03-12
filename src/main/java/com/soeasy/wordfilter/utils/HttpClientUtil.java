package com.soeasy.wordfilter.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author
 * @create 2019/3/8
 **/

public class HttpClientUtil {


    /**
     * 请求url,返回content
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        HttpEntity content = HttpClientUtil.getContextByUrl(url, new HashMap<>());
        try {
            return EntityUtils.toString(content, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送get请求 带参数
     *
     * @param url
     * @param params
     * @return
     */
    public static HttpEntity getContextByUrl(String url, Map<String, String> params) {
        HttpEntity entity = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String value = String.valueOf(params.get(name));
            nvps.add(new BasicNameValuePair(name, value));
        }
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            HttpGet httpClient = new HttpGet(url + "?" + str);
            httpClient.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            response = client.execute(httpClient);
            entity = response.getEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 发送get请求 不带参数
     *
     * @param url
     * @return
     */
    public static HttpEntity getContextByUrl(String url) {
        HttpEntity entity = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        HttpGet httpClient = new HttpGet(url);
        httpClient.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        try {
            response = client.execute(httpClient);
            entity = response.getEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 使用post方式发送http请求
     *
     * @param url
     * @param params
     * @return
     */
    public static HttpEntity postContextByUrl(String url, Map<String, String> params) {
        HttpEntity entity = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse response = client.execute(httpPost);
            entity = response.getEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;

    }
}
