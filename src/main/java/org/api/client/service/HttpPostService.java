package org.api.client.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpPostService {

    public String httpPost(String requestUrl, String requestJson) throws IOException {
        try(CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost httpPost = new HttpPost(requestUrl);
            StringEntity entity = new StringEntity(requestJson, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
            try(CloseableHttpResponse response = client.execute(httpPost);){
                String result = EntityUtils.toString(response.getEntity());
                return result;
            } catch (Exception e){
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
