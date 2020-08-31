package com.piglet.controller;

import com.piglet.domain.User;
import com.piglet.service.MenuService;
import com.piglet.service.UserService;
import com.piglet.util.Result;
import com.piglet.util.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.Session;
import static com.piglet.util.ShiroUtils.getUser;

@Controller
public class LoginController {

    @Autowired
    MenuService menuService;

    @Autowired
    UserService userService;

    @RequestMapping({"","login"})
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) throws UnknownHostException {
        User user = ShiroUtils.getUser();
        Map<String,Object> loginLog = new HashMap<String,Object>();
        loginLog.put("userId",user.getUserId());
        loginLog.put("ipAddress",getIpAddr(request));
        loginLog.put("loginTime",new Date());
        userService.loginLog(loginLog);
        int icount = userService.icount(user.getUserId());
        Session session = ShiroUtils.getSession();
        session.setAttribute("icount", icount);
        return "index";
    }

    @RequestMapping("/welcome")
    public String welcome(Model model){
        User user=getUser();
        model.addAttribute("user",user);
        return "welcome";
    }

    @PostMapping("/checklogin")
    @ResponseBody
    Result checklogin(String username, String password, HttpServletRequest request) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return Result.ok();
        } catch (AuthenticationException e) {
            return Result.error(e.getMessage());
        }
    }

    //精确获取本机IP地址
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }
}

