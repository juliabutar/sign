package go.pajak.pbb.app.registrasi.controller;


import go.pajak.pbb.app.registrasi.dtomodel.OAuthModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequestMapping("auth")
public class AuthController {

    @Value("${auth.ClientId}")
    private String clientId;
    @Value("${auth.RedirectUri}")
    private String redirectUri;
    @Value("${auth.AuthEndpoint}")
    private String authorizationEndpoint;
    @Value("${auth.LogoutUri}")
    private String logoutUri;
    @Value("${app.uri}")
    private String apphost;


    @GetMapping("login")
    public String login(HttpServletRequest request) {

//        return "redirect:"
//                + authorizationEndpoint
//                + "?client_id="
//                + clientId
//                + "&response_type=code&redirect_uri="
//                + redirectUri;

        HttpSession session = request.getSession();
        SecurityContext sc = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        OAuthModel oAuthModel = (OAuthModel) request.getSession().getAttribute("OAuthModel");
        if (sc != null && oAuthModel != null) {
            return "redirect:/home?access_token="+ oAuthModel.getAccess_token();
        } else {
            return "redirect:"
                    + authorizationEndpoint
                    + "?client_id="
                    + clientId
                    + "&response_type=code&redirect_uri="
                    + redirectUri;
        }
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SecurityContext sc = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (sc != null) {
            //UserAuth user = (UserAuth) session.getAttribute("auth");
            session.invalidate();
            if (null != sc.getAuthentication().getPrincipal()) {
                return "redirect:" + logoutUri + "?username=" + sc.getAuthentication().getPrincipal().toString() + "&redirect=" + apphost;
            } else {
                return "redirect:/auth/login";
            }
        } else {
            return "redirect:/auth/login";

        }
    }

}
