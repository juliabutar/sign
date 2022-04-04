package go.pajak.pbb.app.registrasi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import go.pajak.pbb.app.registrasi.dtomodel.*;
import go.pajak.pbb.app.registrasi.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LhptService implements ILhptService {

    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Autowired
    IReferensiService referensiService;


    @Override
    public ResponseModel setDataLhptInitial(DtoLhptInit dtoLhptInit, UserSikkaModel user) {
        try {
            ResponseModel rmKpp = referensiService.getKantorByKdKpp(user.getKodeKpp(), user.getKodeKanwil());
            if(rmKpp.getKodeResponse()== 1){
                ObjectMapper mapper = new ObjectMapper();
                DtoKantorModel kantor = mapper.convertValue(rmKpp.getObjResponse(),DtoKantorModel.class);

                LhptInitialModel lim = new LhptInitialModel();
                //General 18 Data
//            DtoLhpt lh = new DtoLhpt();
//            lim.setIdLhpt();
                lim.setIdPendaftaran(dtoLhptInit.getIdPendaftaran());
                lim.setJenisLhpp("3");
                lim.setNoLhpp(dtoLhptInit.getNoLhpt());
                lim.setTanggalLhpp(dtoLhptInit.getTanggalLhpt());
                lim.setNop(Objects.nonNull(dtoLhptInit.getNop())?dtoLhptInit.getNop().replace(".", "").replace("-", ""):"");
                lim.setNamaObjekPajak(dtoLhptInit.getNamaOp());
                lim.setNpwp(Objects.nonNull(dtoLhptInit.getNpwp())?dtoLhptInit.getNpwp().replace(".", "").replace("-", ""):"");
                lim.setNamaWp(dtoLhptInit.getNamaWp());
                lim.setStatus("1");
                lim.setSektor(dtoLhptInit.getKodeSektor());
                lim.setSubSektor(dtoLhptInit.getKodeSubSektor());
                lim.setKodeKpp(user.getKodeKpp());
                lim.setKodeKanwil(user.getKodeKanwil());
                lim.setUserId(user.getNip());
                dtoLhptInit.setUserId(user.getNip());
                lim.setNamaKanwil(kantor.getNamaKanwil());
                lim.setNamaKpp(user.getNamaKantor());
                lim.setNoLhpt(dtoLhptInit.getNoLhpt());


                ObjekPajakLhppModel op = new ObjekPajakLhppModel();
                //Objek Pajak 12 Data

                op.setNop(Objects.nonNull(dtoLhptInit.getNop())?dtoLhptInit.getNop().replace(".", "").replace("-", ""):"");
                op.setNamaObjekPajak(dtoLhptInit.getNamaOp());
                op.setKodeSektor(dtoLhptInit.getKodeSektor());
                op.setSektor(Objects.nonNull(dtoLhptInit.getKodeSektor())?Utility.getNamaSektorByKode(dtoLhptInit.getKodeSektor()):"");
                op.setKodeSubSektor(dtoLhptInit.getKodeSubSektor());
                op.setSubSektor(Objects.nonNull(dtoLhptInit.getKodeSektor())?Utility.getNamaSubSektorByKode(dtoLhptInit.getKodeSektor(), dtoLhptInit.getKodeSubSektor()):"");
                op.setLokJalan(dtoLhptInit.getLokJalan());
                op.setLokDesa(dtoLhptInit.getLokDesa());
                op.setLokKecamatan(dtoLhptInit.getLokKecamatan());
                op.setLokKabupaten(dtoLhptInit.getLokKabupaten());
                op.setLokProvinsi(dtoLhptInit.getLokProvinsi());
                op.setLokKodePos(dtoLhptInit.getKodePos());

                lim.setObjekPajak(op);

                //Wajib Pajak 6 Data
                WajibPajakModel wp = new WajibPajakModel();
                wp.setNpwp(Objects.nonNull(dtoLhptInit.getNpwp())?dtoLhptInit.getNpwp().replace(".", "").replace("-", ""):"");
                wp.setNamaWp(dtoLhptInit.getNamaWp());
                wp.setJenisWp(dtoLhptInit.getJenisWp());
                wp.setAlamatWp(dtoLhptInit.getAlamatWp());
                wp.setEmail(dtoLhptInit.getEmail());
                wp.setNoTelp(dtoLhptInit.getNoTelp());

                lim.setWajibPajak(wp);

                //Dok Pendukung 8 Data
                lim.setTanggalPenelitian(dtoLhptInit.getTanggalPenelitian());
                lim.setJnsDokumen(dtoLhptInit.getJnsDok());
                lim.setNamaDokumen(dtoLhptInit.getNamaDok());
                lim.setTanggalDokumen(dtoLhptInit.getTanggalDok());
                lim.setExpiredDate(dtoLhptInit.getExpiredDate());
                lim.setIsBerlaku(dtoLhptInit.getIsBerlaku());
                lim.setIsSyaratSubjektif(dtoLhptInit.getIsMemenuhiSyaratSubjektif());
                lim.setKetSyaratSubjektif(dtoLhptInit.getKetSyaratSubjektif());

                //Kesimpulan 6 Data
                KesimpulanLhptModel klm = new KesimpulanLhptModel();
                klm.setIsEligibleSkt(dtoLhptInit.getIsEligible());
                klm.setNop(Objects.nonNull(dtoLhptInit.getNop())?dtoLhptInit.getNop().replace(".", "").replace("-", ""):"");
                klm.setIsTerbitNpwpJabatan(dtoLhptInit.getIsTerbitNpwpJabatan());
                klm.setNpwp(Objects.nonNull(dtoLhptInit.getNpwpJabatan())?dtoLhptInit.getNpwpJabatan().replace(".", "").replace("-", ""):"");
                klm.setSaran(dtoLhptInit.getSaran());

                lim.setKesimpulan(klm);

                //LegalLhpt 8 Data
                LegalLhptModel llm = new LegalLhptModel();
                llm.setTempatLhpt(dtoLhptInit.getTempatLhpt());
                llm.setTanggalLhpt(dtoLhptInit.getTanggalLhpt());
                llm.setNamaSeksi(dtoLhptInit.getNamaSeksi());
                llm.setNamaKasi(dtoLhptInit.getNamaAtasanPembuatLhpt());
                llm.setNipKasi(dtoLhptInit.getNipAtasanPembuatLhpt());
                llm.setJabatanPembuatLhpt(dtoLhptInit.getJabatanPembuatLhpt());
                llm.setNamaPejabatPembuatLhpt(dtoLhptInit.getNamaPembuatLhpt());
                llm.setNipPejabatPembuatLhpt(dtoLhptInit.getNipPembuatLhpt());

                lim.setLegalLhpt(llm);

                Mono<ResponseModel> result = regWebClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/lhpp/initial")
                                .build())
                        .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(lim), LhptInitialModel.class)
                        .retrieve()
                        .bodyToMono(ResponseModel.class);
                return result.block();

            }else{
                return new ResponseModelBuilder().failed().withMessage("Invalid Kantor").build();
            }


        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(e.getMessage()).build();
        }
    }

    @Override
    public ResponseModel updateStatus (String idLhpp, UserSikkaModel userSikkaModel) {
        try {
            Map<String, String> param = new HashMap<String,String>(){{
                put("id",idLhpp);
                put("kdKpp",userSikkaModel.getKodeKpp());
                put("kdKanwil",userSikkaModel.getKodeKanwil());
                put("idUser",userSikkaModel.getNip());
            }};

            Mono<ResponseModel> result = regWebClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lhpp")
                            .build())
                    .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(param), HashMap.class)
                    .retrieve()
                    .bodyToMono(ResponseModel.class);
            return result.block();

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(e.getMessage()).build();
        }
    }

    @Override
    public DataTableResponse getDataLhpt(int draw, int start, int length, String kodeKpp, String kodeKanwil,String search, String[] inStatus) {
        try {
            Mono<DataTableResponse> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lhpp/list")
                            .queryParam("draw", draw)
                            .queryParam("start", start)
                            .queryParam("length", length)
                            .queryParam("kdKpp", kodeKpp)
                            .queryParam("kdKanwil", kodeKanwil)
                            .queryParam("status", inStatus)
                            .queryParam("search", search)
                            .build())
                    .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                    .retrieve()
                    .bodyToMono(DataTableResponse.class);
            return result.block();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseModel getDataLhpt(String idLhpt, String kdKpp, String kdKanwil) {
       try {
           Mono<ResponseModel> result = regWebClient.get()
                   .uri(uriBuilder -> uriBuilder
                           .path("/lhpp")
                           .queryParam("idlhpp", idLhpt)
                           .queryParam("kdKanwil", kdKanwil)
                           .queryParam("kdKpp", kdKpp)
                           .build())
                   .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                   .retrieve()
                   .bodyToMono(ResponseModel.class);
           return result.block();
       }catch (Exception ex){
           ex.printStackTrace();
           return new ResponseModelBuilder()
                   .failed()
                   .withMessage(ex.getMessage())
                   .build();
       }
    }

}
