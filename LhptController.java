package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.*;
import go.pajak.pbb.app.registrasi.dtomodel.paging.PagingRequest;
import go.pajak.pbb.app.registrasi.service.ILhptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("/api/lhpp")
public class LhptController {

    @Autowired
    final ILhptService lhptService;

    public LhptController(ILhptService lhptService) {
        this.lhptService = lhptService;
    }


    @PostMapping("/initial")
    public ResponseEntity<ResponseModel> setDataRekam(@RequestBody DtoLhptInit dtoLhptInit, HttpServletRequest request){

        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(lhptService.setDataLhptInitial(dtoLhptInit,user), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }



    }

    @PostMapping("/arsip")
    public ResponseEntity<ResponseModel> updateStatus (@RequestBody String idLhpp, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(lhptService.updateStatus(idLhpp,user), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> getDaftarPendaftaranUpdated(PagingRequest pageRequest,
                                                         HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            String[] inStatus = new String[]{"1"};
            return new ResponseEntity<>(lhptService.getDataLhpt(pageRequest.getDraw(), pageRequest.getStart(),
                    pageRequest.getLength(), kodeKpp, kodeKanwil,pageRequest.getSearch().getValue(), inStatus), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("")
    public ResponseEntity<ResponseModel> getLhptGeneric (@RequestParam ("id") String idLhpt, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            return new ResponseEntity<>(lhptService.getDataLhpt(idLhpt, user.getKodeKpp(), user.getKodeKanwil()), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


};
