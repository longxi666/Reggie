package com.dragon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dragon.reggie.common.R;
import com.dragon.reggie.entity.User;
import com.dragon.reggie.service.UserService;
import com.dragon.reggie.utils.SMSUtils;
import com.dragon.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * TODO 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码——————》{}", code);

            //调用阿里云提供的短信服务API 完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            //保存验证码到Session -- 旧
            //session.setAttribute(phone, code);

            //将生成的验证码保存到Redis 中，并设置五分钟有效期
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            R.success("手机验证码短信发生成功！");
        }
        return R.error("短信发送失败");
    }

    /**
     * TODO 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session 中获取保存的验证码 -- 旧
        //Object codeInSession = session.getAttribute(phone);

        //从Redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码比对
        if(codeInSession != null && codeInSession.equals(code)){
            //比对成功则登录成功

            //判断是否为新用户，
            //新用户自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //登录成功则删除验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败,！");
    }
}
