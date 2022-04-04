package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.OAuthModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.service.AuthService;
import go.pajak.pbb.app.registrasi.service.CustomRemoteTokenService;
import go.pajak.pbb.app.registrasi.service.PegawaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequestMapping("callback")
public class CallbackController {

    @Autowired
    private AuthService authService;

    @Autowired
    PegawaiService pegawaiService;

    @Autowired
    private CustomRemoteTokenService remoteTokenService;

    @GetMapping(value = "/auth")
    public ModelAndView cek(@RequestParam(required = false) String code,
                            HttpServletRequest req
    ) throws IOException {
        if (!code.isEmpty()) {
            OAuthModel oauthModel = authService.getToken(code);
            if (null != oauthModel) {
                OAuth2Authentication auth2Authentication = remoteTokenService.loadAuthentication(oauthModel.getAccess_token());
                if (null != auth2Authentication){
                    SecurityContext sc = SecurityContextHolder.getContext();
                    sc.setAuthentication(auth2Authentication);
                    HttpSession session = req.getSession(true);
                    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                    session.setAttribute("OAuthModel",oauthModel);
                    UserSikkaModel userLoggedIn = pegawaiService.getPegawaiByNip(auth2Authentication.getPrincipal().toString());
                    session.setAttribute("user",userLoggedIn);
                    return new ModelAndView("redirect:/home?access_token="+ oauthModel.getAccess_token());
                }else{
                    return new ModelAndView("redirect:auth/login");
                }
            } else {
                return new ModelAndView("redirect:auth/login");
            }
        } else {
            return new ModelAndView("redirect:auth/login");
        }
    }
}
