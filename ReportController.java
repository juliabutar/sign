package go.pajak.pbb.app.registrasi.controller;

import go.pajak.pbb.app.registrasi.dtomodel.DtoSkt;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import go.pajak.pbb.app.registrasi.service.IReportService;
import go.pajak.pbb.app.registrasi.service.SigningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/report")

public class ReportController {
    @Autowired
    private final IReportService reportService;
//    @Autowired
//    SigningService signingService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/skt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> draftSktReport(@RequestParam("id") String id,
                                                              HttpServletRequest request) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))) {
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            ByteArrayInputStream bis = reportService.getSktReport(id, user);
            headers.add("Content-Disposition", "inline; filename=draftSkt_" + id + ".pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));

        } else {
            return null;

        }
    }

    @GetMapping(value = "/signedskt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> sktReport(@RequestParam("id") String id,
                                                       HttpServletRequest request) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))) {
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            //ByteArrayInputStream bis = reportService.getSignedSktReport(id, user);
            //headers.add("Content-Disposition", "inline; filename=skt_" + id + ".pdf");
            //return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));

            //byte[] data = reportService.getSignedSktReportFromSigningAgent(id, user);
            byte[] data = reportService.getSignedSktReport(id,user);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    //.header("Content-type", "application/octet-stream")
                    //.header("Content-disposition", "attachment; filename=\"" + file + "\"")
                    .header("Content-type", "application/pdf")
                    .header("Content-disposition", "inline; filename=signedskt_" + id + ".pdf")
                    .body(resource);

        } else {
            return null;

        }
    }

    /*@GetMapping("/api/lhpp")
    public ResponseEntity<ResponseModel> getDataLhpt (@RequestParam("id") String idlhpp, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))){
            UserSikkaModel user = (UserSikkaModel) session.getAttribute("user");
            String kodeKpp = user.getKodeKpp();
            String kodeKanwil = user.getKodeKanwil();
            return new ResponseEntity<>(reportService.getDataDetail(idlhpp,kodeKanwil,kodeKpp),headers, HttpStatus.OK);

        }else{
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }*/

}
