package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.DtoPendaftaran;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.dtomodel.paging.PagingRequest;
import go.pajak.pbb.app.registrasi.service.IPermohonanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("/api/permohonan")

public class PermohonanController {

    @Autowired
    IPermohonanService permohonanService;


    @Secured({"ROLE_PBBREG_AR"})
    @GetMapping("/listinit")
    public ResponseEntity<?> getDaftarPendaftaran(PagingRequest pageRequest,
                                                  HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            String[] inStatus = new String[]{"0","1", "3"};
            return new ResponseEntity<>(permohonanService
                    .getDataPermohonan(pageRequest.getDraw(), pageRequest.getStart(), pageRequest.getLength(),
                            kodeKpp, kodeKanwil, inStatus, pageRequest.getSearch().getValue()), headers, HttpStatus.OK);
            //return new ResponseEntity<>(permohonanService.getDataPermohonan(draw, start, length, kodeKpp, kodeKanwil, inStatus), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured({"ROLE_PBBREG_AR"})
    @GetMapping("/listupdated")
    public ResponseEntity<?> getDaftarPendaftaranUpdated(PagingRequest pageRequest,
                                                  HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            String[] inStatus = new String[]{"1","3"};
            return new ResponseEntity<>(permohonanService
                    .getDataPermohonan(pageRequest.getDraw(), pageRequest.getStart(), pageRequest.getLength(),
                            kodeKpp, kodeKanwil, inStatus, pageRequest.getSearch().getValue()), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured({"ROLE_PBBREG_AR"})
    @PostMapping("/pendaftaran")
    public ResponseEntity<ResponseModel> setDataPendaftaran(@RequestBody DtoPendaftaran dtoPendaftaran, HttpServletRequest request){

        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(permohonanService.setDataPermohonan(dtoPendaftaran, user), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }



    }

    @Secured({"ROLE_PBBREG_AR"})
    @GetMapping()
    public ResponseEntity<ResponseModel> getDataPendaftaran(@RequestParam("id") String idPermohonan, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            return new ResponseEntity<>(permohonanService.getDataPermohonan(idPermohonan,kodeKanwil,kodeKpp),headers,HttpStatus.OK);

        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
}
