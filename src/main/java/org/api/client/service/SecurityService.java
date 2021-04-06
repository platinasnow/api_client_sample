package org.api.client.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.api.client.model.Members;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class SecurityService {

    @Value("spring.jwt.secret")
    private String tokenSecretKey;

    @PostConstruct
    protected void init() {
        tokenSecretKey = Base64.getEncoder().encodeToString(tokenSecretKey.getBytes());
    }

    public boolean loginByJwtLoginToken(HttpServletRequest req, String loginToken){
        String primaryKey = this.getUserPrimaryKey(loginToken);
        if(primaryKey != null){
            Members members = new Members();
            members.setId(primaryKey);
            this.securityLoginWithoutLoginForm(req, members);
            return true;
        } else{
            return false;
        }
    }

    private void securityLoginWithoutLoginForm(HttpServletRequest req, Object user) {
        //로그인 세션에 들어갈 권한을 설정합니다.
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));

        SecurityContext sc = SecurityContextHolder.getContext();
        //아이디, 패스워드, 권한을 설정합니다.
        sc.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, list));
        HttpSession session = req.getSession(true);

        //위에서 설정한 값을 Spring security에서 사용할 수 있도록 세션에 설정해줍니다.
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
    }

    public String getUserPrimaryKey(String token) {
        String primaryKey = null;
        try {
            primaryKey = JWT.require(Algorithm.HMAC256(tokenSecretKey)).build().verify(token).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return primaryKey;
    }

}
