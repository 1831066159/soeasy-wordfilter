package com.soeasy.wordfilter.service;

import com.soeasy.wordfilter.mapper.UserMapper;
import com.soeasy.wordfilter.model.User;
import com.soeasy.wordfilter.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author
 * @create 2019/4/2
 **/
@Service
public class IndexService {

    private static Logger logger = LoggerFactory.getLogger(ProcessorService.class);


    @Autowired
    private UserMapper userMapper;


    /**
     * 用户登录检查
     *
     * @param name
     * @param pwd
     * @return
     */
    public boolean checkUser(String name, String pwd) {
        logger.info("登录用户检查:{}", name);
        try {
            Assert.hasLength(name, "用户名不能为空");
            Assert.hasLength(pwd, "密码不能为空");
            User user = userMapper.selectByName(name);
            if (user != null && !StringUtils.isEmpty(user.getPassword())) {
                // 密码MD5验证
                if (user.getPassword().equals(MD5Util.md5(pwd))) {
                    logger.info("登录用户检查,验证通过");
                    return true;
                }
            }

        } catch (Exception e) {
            logger.info("登录用户检查失败", e);
        }
        logger.info("登录用户检查,验证失败");
        return false;
    }

}
