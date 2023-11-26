package com.example.springpractice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
public class LoginController {

    @GetMapping("/loginPage")
    public ModelAndView LoginPage(@RequestParam(value = "error", required = false)String error,@RequestParam(value = "exception",required = false)String exception){
        ModelAndView mv = new ModelAndView();

        mv.addObject("error",error);
        mv.addObject("exception",exception);
        mv.setViewName("/login");
        return mv;
    }

    @GetMapping("/admin-success")
    public ModelAndView adminPage(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/AdminLoginSuccess");
        return mv;
    }

    @GetMapping("/member-success")
    public ModelAndView memberPage(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/MemberSuccessPage");
        return mv;
    }
}
