package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.service.PegawaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("/api/pegawai")
public class PegawaiController {

    @Autowired
    PegawaiService pegawaiService;

    public PegawaiController(PegawaiService pegawaiService) {
        this.pegawaiService = pegawaiService;
    }

    @GetMapping()
    public ResponseEntity<ResponseModel> getDataPegawai(@RequestParam("nip") String idPegawai, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = pegawaiService.getPegawaiByNip(idPegawai);
            if(Objects.nonNull(user)){
                ResponseModel rm = new ResponseModel();
                rm.setKodeResponse(1);
                rm.setMessage("ok");
                rm.setObjResponse(user);
                return new ResponseEntity<>(rm,headers, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("atasan")
    public ResponseEntity<ResponseModel> getDataAtasanPegawai(@RequestParam("nip") String idPegawai, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = pegawaiService.getAtasanPegawaiByNip(idPegawai);
            if(Objects.nonNull(user)){
                ResponseModel rm = new ResponseModel();
                rm.setKodeResponse(1);
                rm.setMessage("ok");
                rm.setObjResponse(user);
                return new ResponseEntity<>(rm,headers, HttpStatus.OK);
            }else{
                ResponseModel rm = new ResponseModel();
                rm.setKodeResponse(0);
                rm.setMessage("not ok");
                rm.setObjResponse(null);
                return new ResponseEntity<>(rm, headers, HttpStatus.OK);
            }
        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
}
