package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.OAuthModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.service.PegawaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class WebController {

    @Value("${app.uri}")
    private String apphost;


    private void setupDefaultPageData(HttpServletRequest request, ModelMap model) {
        OAuthModel oAuthModel = (OAuthModel) request.getSession().getAttribute("OAuthModel");
        if (Objects.nonNull(oAuthModel)){
            UserSikkaModel userLoggedIn = (UserSikkaModel) request.getSession().getAttribute("user");
            model.addAttribute("token", "?access_token=" + oAuthModel.getAccess_token());
            model.addAttribute("ac", oAuthModel.getAccess_token());
            model.addAttribute("user", userLoggedIn);
        }else{
            getIndex();
        }

    }

    @GetMapping({"/"})
    public String getIndex() {
        return "redirect:/auth/login";
    }

    @Secured({"ROLE_USERDJP"})
    @GetMapping({"/home"})
    public String getHome(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "home";
    }

    @Secured({"ROLE_PBBREG_AR"})
    @GetMapping("/permohonan/entry")
    public String getFormPermohonan(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "PendaftaranOP/rekampermohonan";
    }

    @Secured({"ROLE_PBBREG_AR"})
    @GetMapping("/lhpt/entry")
    public String getFormLhpt(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "PendaftaranOP/lhptrekam";
    }

    @Secured({"ROLE_PELAKSANA_PELAYANAN"})
    @GetMapping("/skt/cetak")
    public String getFormCetak(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "PendaftaranOP/cetakoutput";
    }

    @Secured({"ROLE_PELAKSANA_PELAYANAN"})
    @GetMapping("/skt/proses")
    public String getFormProsesSkt(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "PendaftaranOP/prosesskt";
    }

    @Secured({"ROLE_KASI_PELAYANAN", "ROLE_PJS_KASI_PELAYANAN"})
    @GetMapping("/skt/sign")
    public String getFormSignSkt(HttpServletRequest request, ModelMap model) {
        setupDefaultPageData(request, model);
        return "PendaftaranOP/signskt";
    }


    @GetMapping("/unauthorized")
    public String error(HttpServletRequest request, ModelMap model) {
        model.addAttribute("host", apphost);

        return "403";
    }

    @GetMapping("/testbranch")
    public String branch(HttpServletRequest request, ModelMap model) {
        model.addAttribute("host", apphost);

        return "500";
    }
}
