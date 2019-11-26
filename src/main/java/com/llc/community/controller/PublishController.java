package com.llc.community.controller;

import com.llc.community.entity.Question;
import com.llc.community.entity.User;
import com.llc.community.mapper.QuestionMapper;
import com.llc.community.mapper.UserMapper;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(Question question,
                            Model model,
                            HttpServletRequest request){

        model.addAttribute("title",question.getTitle());
        model.addAttribute("descrition",question.getDescrition());
        model.addAttribute("tag",question.getTag());

        if(StringUtils.isNullOrEmpty(question.getTitle())){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(StringUtils.isNullOrEmpty(question.getDescrition())){
            model.addAttribute("error","内容不能为空");
            return "publish";
        }
        if(StringUtils.isNullOrEmpty(question.getTag())){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        Cookie[] cookies = request.getCookies();
        User user = null;
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    user = userMapper.findByUser(token);
                    request.getSession().setAttribute("user",user);
                    break;
                }
            }
        }

        if(user == null || user.getId() == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(System.currentTimeMillis());
        questionMapper.insert(question);
        return "redirect:/";
    }
}
