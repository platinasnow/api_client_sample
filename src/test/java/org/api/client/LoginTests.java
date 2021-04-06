package org.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.api.client.model.Members;
import org.api.client.service.HttpPostService;
import org.api.client.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@SpringBootTest
public class LoginTests {

    @Autowired
    private HttpPostService httpPostService;
    @Autowired
    private SecurityService securityService;
    private final static String _LOGIN_URL = "http://localhost:9000/api/login";
    private Logger logger = LoggerFactory.getLogger(LoginTests.class);
    private static String loginToken = "";
    private HttpServletRequest mockReq = new MockHttpServletRequest();

    @Test
    public void loginTest(){
        Members members = new Members();
        members.setId("test");
        members.setPwd("test123");
        try{
            ObjectMapper mapper = new ObjectMapper();
            String membersJson = mapper.writeValueAsString(members);
            String postResult = httpPostService.httpPost(_LOGIN_URL, membersJson);
            Map<String, String> resultMap = mapper.readValue(postResult, Map.class);
            logger.info("result ===> {}", resultMap);

            assert ("SUCCESS".equals(resultMap.get("msg")));
            assert (resultMap.containsKey("data"));
            loginToken = resultMap.get("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLoginTokenBody(){
        String primaryKey = securityService.getUserPrimaryKey(loginToken);
        assert (primaryKey != null);
        assert ("test".equals(primaryKey));
    }

    @Test
    public void getAuthenticate(){
        assert (securityService.loginByJwtLoginToken(mockReq, loginToken));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("authenticate principal ===> {}", authentication.getPrincipal());
        Members members = (Members)authentication.getPrincipal();
        assert("test".equals(members.getId()));
    }

}
