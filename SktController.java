package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.DtoSignSkt;
import go.pajak.pbb.app.registrasi.dtomodel.DtoSimpanSkt;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.dtomodel.paging.PagingRequest;
import go.pajak.pbb.app.registrasi.service.ISktService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("api/skt")
public class SktController {

    @Autowired
    ISktService sktService;

    @PostMapping("")
    public ResponseEntity<ResponseModel> setDataSkt(@RequestBody DtoSimpanSkt dtoSimpanSkt, HttpServletRequest request){

        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(sktService.simpanSkt(dtoSimpanSkt,user), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured({"ROLE_KASI_PELAYANAN", "ROLE_PJS_KASI_PELAYANAN"})
    @PostMapping("/sign")
    public ResponseEntity<ResponseModel> signSkt(@RequestBody DtoSignSkt dtoSignSkt, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            SecurityContext sc = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
            if (Objects.nonNull(sc)){
                String isPjsPelayanan="0";
                Authentication auth = sc.getAuthentication();
                SimpleGrantedAuthority rolePjsPelayanan = new SimpleGrantedAuthority("ROLE_PJS_KASI_PELAYANAN");
                if (auth.getAuthorities().contains(rolePjsPelayanan)){
                    isPjsPelayanan ="1";
                }
                return new ResponseEntity<>(sktService.signSkt(dtoSignSkt, user, isPjsPelayanan), headers, HttpStatus.OK);

            }else{
                return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
            }

        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getDaftarSkt(PagingRequest pageRequest,
                                                  HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            String[] inStatus = new String[]{"0","1"};
            return new ResponseEntity<>(sktService
                    .getDaftarSkt(pageRequest.getDraw(), pageRequest.getStart(), pageRequest.getLength(),
                            kodeKpp, kodeKanwil,inStatus, pageRequest.getSearch().getValue()), headers, HttpStatus.OK);
            }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/readyprint/list")
    public ResponseEntity<?> getDaftarSktReady(PagingRequest pageRequest,HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            String[] inStatus = new String[]{"2"};
            return new ResponseEntity<>(sktService
                    .getDaftarSkt(pageRequest.getDraw(), pageRequest.getStart(), pageRequest.getLength(),
                            kodeKpp, kodeKanwil,inStatus, pageRequest.getSearch().getValue()), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("print")
    public ResponseEntity<ResponseModel> getSktReport(@RequestParam ("idSkt") String idSkt, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(sktService.printSkt(idSkt, user), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/wp")
    public ResponseEntity<ResponseModel> getMfwp(@RequestParam ("npwp") String pnpwp, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(pnpwp)){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(sktService.getWp(pnpwp, user), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

}
