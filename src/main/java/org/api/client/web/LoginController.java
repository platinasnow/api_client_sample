package org.api.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.api.client.model.Members;
import org.api.client.service.HttpPostService;
import org.api.client.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final HttpPostService httpPostService;
    private final SecurityService securityService;
    private final static String _LOGIN_URL = "http://localhost:9000/api/login";
    private Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/loginProc")
    public String loginProc(HttpServletRequest request, Members members){
        if(!StringUtils.hasText(members.getId()) || !StringUtils.hasText(members.getPwd())){
            return "redirect:/login";
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            String membersJson = mapper.writeValueAsString(members);
            String postResult = httpPostService.httpPost(_LOGIN_URL, membersJson);
            Map<String, String> resultMap = mapper.readValue(postResult, Map.class);
            logger.debug("resultMap ===> {}", resultMap);
            if("0".equals(resultMap.get("code"))){
                String loginToken = resultMap.get("data");
                if(securityService.loginByJwtLoginToken(request, loginToken)){
                    return "redirect:/";
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/login";
    }


}
