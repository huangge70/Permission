package com.mmall.controller;

import com.mmall.common.JsonData;
import com.mmall.param.DeptParam;
import com.mmall.param.UserParam;
import com.mmall.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
@Controller
@RequestMapping("/sys/user")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(UserParam param){
        sysUserService.save(param);
        return  JsonData.success();
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(UserParam param){
        sysUserService.update(param);
        return  JsonData.success();
    }
}
