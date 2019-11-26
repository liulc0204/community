package com.llc.community.controller;


import com.llc.community.dto.AccessTokenDto;
import com.llc.community.dto.GitHubUser;
import com.llc.community.entity.User;
import com.llc.community.mapper.UserMapper;
import com.llc.community.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(redirectUri);
        accessTokenDto.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDto);
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);
        if(gitHubUser != null){
            User entityUser = new User();
            entityUser.setName(gitHubUser.getName());
            entityUser.setToken(UUID.randomUUID().toString());
            entityUser.setAccountId(String.valueOf(gitHubUser.getId()));
            entityUser.setGmtCreate(System.currentTimeMillis());
            entityUser.setGmtModified(entityUser.getGmtCreate());
            entityUser.setAvatarUrl(gitHubUser.getAvatar_url());
            userMapper.insert(entityUser);
            response.addCookie(new Cookie("token",entityUser.getToken()));
            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }

}
