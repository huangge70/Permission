package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.MD5Util;
import com.mmall.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话号码已被占用");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已经被占用");
        }
        String password= PasswordUtil.randomPassword();
        //TODO
        password="123456";
        //对密码进行加密
        String encryptedPassword= MD5Util.encrypt(password);
        SysUser user=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system");
        user.setOperateTime(new Date());
        user.setOperateIp("127.0.0.1");
        //TODO:sendEmail
        sysUserMapper.insertSelective(user);
    }
    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话号码已被占用");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已经被占用");
        }
        //取出用户更新之前的信息
        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after=SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail()).password(before.getPassword()).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        sysUserMapper.updateByPrimaryKeySelective(after);
    }
    public boolean checkEmailExist(String mail,Integer userId){
        return false;
    }
    public boolean checkTelephoneExist(String telephone,Integer userId){
        return false;
    }
}
