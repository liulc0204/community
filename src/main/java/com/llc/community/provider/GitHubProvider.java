package com.llc.community.provider;

import com.alibaba.fastjson.JSON;
import com.llc.community.dto.AccessTokenDto;
import com.llc.community.dto.GitHubUser;
import org.springframework.stereotype.Component;
import okhttp3.*;

import java.io.IOException;

@Component
public class GitHubProvider {

    public String getAccessToken(AccessTokenDto accessTokenDto){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDto));

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String accessToken = string.split("&")[0].split("=")[1];
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public GitHubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token=" + accessToken)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String string = response.body().string();
                GitHubUser gitHubUser = JSON.parseObject(string, GitHubUser.class);
                return gitHubUser;
            } catch (IOException e) {
            }

        return null;
    }
}
