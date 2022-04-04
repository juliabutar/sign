package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.service.MonitorService;

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
@RequestMapping("api/monitoring")
public class MonitoringController {
    @Autowired
    MonitorService monitorService;

    public MonitoringController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping()
    public ResponseEntity<ResponseModel> getDataMonitor(HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kdKpp = user.getKodeKpp();
            String kdKanwil = user.getKodeKanwil();
            return new ResponseEntity<>(monitorService.getDataMonitor(kdKanwil, kdKpp), headers,HttpStatus.OK);

        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
}
