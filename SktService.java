package go.pajak.pbb.app.registrasi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import go.pajak.pbb.app.registrasi.dtomodel.*;
import go.pajak.pbb.app.registrasi.utility.NopValidation;
import go.pajak.pbb.app.registrasi.utility.Utility;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class SktService implements ISktService {

    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Autowired
    ILhptService lhptService;

    @Autowired
    IReferensiService referensiService;
    @Autowired
    IReportService reportService;
    @Autowired
    SigningService signingService;
    @Autowired
    private HcpService hcpService;
    @Autowired
    private NopValidation nopValidation;

    @Override
    public ResponseModel getSkt(String idSkt, UserSikkaModel user) {
        Mono<ResponseModel> result = regWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/skt")
                        .queryParam("idSkt", idSkt)
                        .queryParam("kdKpp", user.getKodeKpp())
                        .queryParam("kdKanwil", user.getKodeKanwil())
                        .build())
                .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                .retrieve()
                .bodyToMono(ResponseModel.class);
        ResponseModel res = result.block();

        if (res.getKodeResponse() == 1) {
            ObjectMapper mapper = new ObjectMapper();
            DtoSkt objSkt = mapper.convertValue(res.getObjResponse(), DtoSkt.class);

            return new ResponseModelBuilder().success().withData(objSkt).build();

        } else {
            return new ResponseModelBuilder().failed().withMessage("Gagal").build();
        }
    }

    @Override
    public ResponseModel simpanSkt(DtoSimpanSkt dtoSimpanSkt, UserSikkaModel user) {
        try {
            ResponseModel rmLhpt = lhptService.getDataLhpt(dtoSimpanSkt.getIdLhpt(), user.getKodeKpp(), user.getKodeKanwil());
            /**
             * 1. Cek kode nop dan sektor
             * 2. Cek NPWP, harus lokal
             */
            if (Objects.isNull(dtoSimpanSkt.getNop())) {
                return new ResponseModelBuilder()
                        .failed().withMessage("NOP tidak ada").build();
            }
            if (!dtoSimpanSkt.getNop().substring(7, 10).equals(user.getKodeKpp())) {
                return new ResponseModelBuilder()
                        .failed().withMessage("Kode KPP pada NOP tidak sesuai").build();
            }


            if (rmLhpt.getKodeResponse() == 1) {
                ResponseModel rmKpp = referensiService.getKantorByKdKpp(user.getKodeKpp(), user.getKodeKanwil());
                if (rmKpp.getKodeResponse() == 1) {
                    ObjectMapper mapper = new ObjectMapper();
                    DtoKantorModel kantor = mapper.convertValue(rmKpp.getObjResponse(), DtoKantorModel.class);
                    DtoSktModel skt = null;
                    switch (rmLhpt.getMessage()) {
                        case "3": //initial model
                            LhptInitialModel lim = mapper.convertValue(rmLhpt.getObjResponse(), LhptInitialModel.class);
                            if (!nopValidation.validateKodeWilayah(lim.getNop(), lim.getSektor(), lim.getSubSektor())
                                    || !nopValidation.validateKodeKecamatan(lim.getNop(), lim.getSektor())
                                    || !nopValidation.validateKodeBlok(lim.getNop(), lim.getSektor())) {
                                return new ResponseModelBuilder()
                                        .failed().withMessage("Struktur NOP tidak sesuai").build();
                            }
                            skt = buildDtoSktModelFromInitial(dtoSimpanSkt, lim, kantor, user);
                            break;
                    }
                    if (Objects.nonNull(skt)) {
                        Mono<ResponseModel> result = regWebClient.post()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/skt")
                                        .build())
                                .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .body(Mono.just(skt), DtoSktModel.class)
                                .retrieve()
                                .bodyToMono(ResponseModel.class);
                        return result.block();
                    } else {
                        return new ResponseModelBuilder()
                                .failed().withMessage("Simpan Data Tdk Berhasil").build();
                    }
                } else {
                    return new ResponseModelBuilder()
                            .failed().withMessage("Invalid Unit Kantor").build();
                }

            } else {
                return new ResponseModelBuilder()
                        .failed().withMessage("LHPt is not found").build();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseModel signSkt(DtoSignSkt dtoSignSkt, UserSikkaModel user, String isPjsPelayanan) {
        try {
            /**
             * 1.update data skt
             * 2. generate pdf SKT
             * 3. sign SKT
             */
            ResponseModel rmKpp = referensiService.getKantorByKdKpp(user.getKodeKpp(), user.getKodeKanwil());
            if (rmKpp.getKodeResponse() == 1) {
                ObjectMapper mapper = new ObjectMapper();
                DtoKantorModel kantor = mapper.convertValue(rmKpp.getObjResponse(), DtoKantorModel.class);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                DtoUpdateSkt updateSkt = new DtoUpdateSkt();
                updateSkt.setIdSkt(dtoSignSkt.getIdSkt());
                updateSkt.setNipTtd(user.getNipBaru());
                updateSkt.setUserId(user.getNip());
                updateSkt.setTglTtd(sdf.format(new Date()));
                updateSkt.setNamaTtd(user.getNamaPegawai());
                updateSkt.setKota(kantor.getKotaKpp());
                updateSkt.setIsPjs(isPjsPelayanan);
                String docId = UUID.randomUUID().toString();
                updateSkt.setDocId(docId);

                Mono<ResponseModel> result = regWebClient.put()
                        .uri(uriBuilder -> uriBuilder
                                .path("/skt")
                                .build())
                        .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(updateSkt), DtoUpdateSkt.class)
                        .retrieve()
                        .bodyToMono(ResponseModel.class);
                ResponseModel rmUpdate = result.block();
                if (rmKpp.getKodeResponse() == 1 && rmUpdate.getKodeResponse() == 1) {
                    //berhasil update data skt
                    ByteArrayInputStream bis = reportService.getSktReport(dtoSignSkt.getIdSkt(), user);
                    SigningRequestModel srm = new SigningRequestModel();
                    srm.setAppName("PBBREG");
                    srm.setDocId(docId);
                    srm.setDocumentType("SKT");
                    srm.setNomor(dtoSignSkt.getDocumentNumber());
                    srm.setTujuan(dtoSignSkt.getNamaWp());
                    srm.setPerihal("SURAT KETERANGAN TERDAFTAR");
                    srm.setInfo("BIASA");
                    srm.setTampilan("invisible");
                    srm.setNpwpTujuan(dtoSignSkt.getNpwp());
                    srm.setSignerIdType("1");
                    srm.setSignerId(dtoSignSkt.getNik());
                    srm.setSignerPassphrase(dtoSignSkt.getPass());
                    srm.setNipPenandatangan(user.getNipBaru());
                    srm.setSignerName(user.getNamaPegawai());
                    srm.setNamaJabatan(user.getNamaJabatan());
                    srm.setNamaUnitJabatan(user.getNamaUnitOrg());
                    srm.setSigningProvider("1");
                    srm.setAppUser(user.getNip());
                    srm.setKdKanwil(user.getKodeKanwil());
                    srm.setKdKpp(user.getKodeKpp());
//                    List<Map<String,Object>> lsSumData = new ArrayList<>();
//                    Map<String, Object> map1 = Stream.of(new Object[][] {
//                            { "no_skt", dtoSignSkt.getDocumentNumber()}
//                    }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));
//                    Map<String, Object> map2 = Stream.of(new Object[][] {
//                            { "no_skt", dtoSignSkt.getDocumentNumber()}
//                    }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));
//                    lsSumData.add(map1);
//                    lsSumData.add(map2);

                    ResponseModel rmSign;
                    byte[] pdfDoc = new byte[bis.available()];
                    bis.read(pdfDoc);
                    rmSign = signingService.signDocument(pdfDoc, srm);
                    if (rmSign.getKodeResponse() == 1) {
                        return new ResponseModelBuilder().success().withMessage("Proses penandatanganan dokumen berlangsung, status penandatanganan dapat dilihat pada menu pencetakan").build();
                    } else {
                        return new ResponseModelBuilder().failed().withMessage("proses signing tidak berhasil").build();
                    }
                } else {
                    //tidak berhasil update data skt
                    return new ResponseModelBuilder().failed().withMessage("Proses Signing Dokumen Gagal, Mungkin Server sangat sibuk").build();
                }
            } else {
                return new ResponseModelBuilder().failed().withMessage("illegal proses").build();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();
        }
    }

    @Override
    public ResponseModel getWp(String pnpwp, UserSikkaModel user) {
        Mono<ResponseModel> result = regWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/wp")
                        .queryParam("npwp", pnpwp)
                        .queryParam("kdKpp", user.getKodeKpp())
                        .build())
                .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                .retrieve()
                .bodyToMono(ResponseModel.class);
        ResponseModel res = result.block();

        if (res.getKodeResponse() == 1) {
            ObjectMapper mapper = new ObjectMapper();
            DtoMfwp objWp = mapper.convertValue(res.getObjResponse(), DtoMfwp.class);

            return new ResponseModelBuilder().success().withData(objWp).build();

        } else {
            return new ResponseModelBuilder().failed().withMessage("Gagal").build();
        }
    }

    @Override
    public ResponseModel printSkt(String idSkt, UserSikkaModel user) {
        try {
            /**
             * 1. get data skt by idskt, kdkanwil and kd kpp
             * 2. evaluate if file_location isnull
             * 3. if file_location isnull:
             *      a. cek to signing agent by doc_id and app name
             *      b. if cek result return 9, get doc from signing agent, then save to obj_storage pbb,then update file_location. return id_skt
             *      c. if cek result return 1, return message still process
             *      d. if cek result return 0, return message from resut
             * 4. if file_location notnull, return id_skt
             */
            ResponseModel rmSkt = getSkt(idSkt, user);
            if (rmSkt.getKodeResponse() == 1) {
                //skt exist
                DtoSkt objSkt = (DtoSkt) rmSkt.getObjResponse();
                if (Objects.isNull(objSkt.getFileLocation())) {
                    //signed document belum didownload
                    ResponseModel rmCekDoc = signingService.checkDocument(objSkt.getSignedDocId());
                    switch (rmCekDoc.getKodeResponse()) {
                        case 9:
                            //signed
                            /**
                             * 1. download signed document dari ObStore Signing Agent
                             * 2. simpan signed document ke obstore pbb
                             * 3. update lokasi file di obstore pbb
                             */
                            byte[] signedFile = signingService.getSignedDocument(objSkt.getSignedDocId());
                            if (Objects.nonNull(signedFile)) {
                                String objectName = new StringBuilder("signed_skt/")
                                        .append(objSkt.getNpwp())
                                        .append("_")
                                        .append(objSkt.getNop())
                                        .append(".pdf")
                                        .toString();
                                HttpResponse res1 = hcpService.delete(objectName);
                                HttpResponse res2 = hcpService.writeHcpFromByteArray(objectName, signedFile);
                                if (res2.getStatusLine().getStatusCode() == 201) {
                                    //berhasil nulis ke HCP
                                    DtoUpdatePrintSkt updatePrintSkt = new DtoUpdatePrintSkt();
                                    updatePrintSkt.setIdSkt(objSkt.getIdSkt());
                                    updatePrintSkt.setFileLocation(objectName);
                                    updatePrintSkt.setDocId(objSkt.getSignedDocId());
                                    updatePrintSkt.setStatus("2");
                                    ResponseModel rmUpdate = updateSktSigned(updatePrintSkt);
                                    if (rmUpdate.getKodeResponse() == 1) {
                                        return new ResponseModelBuilder()
                                                .success()
                                                .withMessage("Cetak Berhasil")
                                                .withData(objSkt.getIdSkt())
                                                .build();
                                    } else {
                                        return new ResponseModelBuilder()
                                                .failed()
                                                .withMessage("Update SKT print Gagal").build();
                                    }

                                } else {
                                    //tidak berhasil nulis ke HCP
                                    return new ResponseModelBuilder()
                                            .failed()
                                            .withMessage("Fail on write to HCP").build();
                                }

                            } else {
                                return new ResponseModelBuilder()
                                        .failed()
                                        .withMessage("Dokumen SKT Tidak ada").build();
                            }
                        case 8:
                            //deleted
                            return new ResponseModelBuilder()
                                    .failed()
                                    .withMessage("Dokumen SKT Tidak Berlaku karena dihapus").build();

                        case 0:
                            //signing fail
                            /**
                             * update data skt (status dikembalikan menjadi 0
                             */
                            DtoUpdatePrintSkt updatePrintSkt = new DtoUpdatePrintSkt();
                            updatePrintSkt.setIdSkt(objSkt.getIdSkt());
                            updatePrintSkt.setFileLocation(null);
                            updatePrintSkt.setDocId(objSkt.getSignedDocId());
                            updatePrintSkt.setStatus("0");
                            ResponseModel rmUpdate2 = updateSktSigned(updatePrintSkt);
                            if (rmUpdate2.getKodeResponse() == 1) {
                                return new ResponseModelBuilder()
                                        .failed()
                                        .withMessage(new StringBuilder("Signing Gagal, ")
                                                .append(rmCekDoc.getMessage())
                                                .append(", Dokumen dikembalikan untuk ditandatangai ulang")
                                                .toString())
                                        .build();
                            } else {
                                return new ResponseModelBuilder()
                                        .failed()
                                        .withMessage("update to re-signing gagal")
                                        .build();
                            }

                        default:
                            //on process
                            return new ResponseModelBuilder()
                                    .failed()
                                    .withMessage(new StringBuilder("Signing is on process, ")
                                            .append(rmCekDoc.getMessage())
                                            .toString())
                                    .build();


                    }
                } else {
                    //signed SKT sudah didownload
                    return new ResponseModelBuilder()
                            .success()
                            .withData(objSkt.getIdSkt()).build();
                }
            } else {
                //skt doesn't exist
                return new ResponseModelBuilder()
                        .failed()
                        .withMessage("SKT tidak ditemukan").build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();
        }
    }

    @Override
    public DataTableResponse getDaftarSkt(int draw, int start, int length, String kodeKpp, String kodeKanwil, String[] inStatus, String search) {
        try {
            Mono<DataTableResponse> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/skt/list")
                            .queryParam("draw", draw)
                            .queryParam("start", start)
                            .queryParam("length", length)
                            .queryParam("kdKpp", kodeKpp)
                            .queryParam("kdKanwil", kodeKanwil)
                            .queryParam("status", inStatus)
                            .queryParam("search", search)
                            .build())
                    .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                    .retrieve()
                    .bodyToMono(DataTableResponse.class);

            return result.block();


        } catch (Exception e) {
            return null;
        }
    }

    private ResponseModel updateSktSigned(DtoUpdatePrintSkt updatePrintSkt) {
        Mono<ResponseModel> result = regWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/skt/print")
                        .build())
                .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatePrintSkt), DtoUpdatePrintSkt.class)
                .retrieve()
                .bodyToMono(ResponseModel.class);
        return result.block();
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.getEncoder().encode(creds.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Tidak bisa convert");
        }
    }

    private DtoSktModel buildDtoSktModelFromInitial(DtoSimpanSkt dtoSimpanSkt, LhptInitialModel lim, DtoKantorModel kantor, UserSikkaModel user) {
        if (Objects.nonNull(lim)) {
            DtoSktModel skt = new DtoSktModel();
            skt.setKodeKanwil(lim.getKodeKanwil());
            skt.setNamaKanwil(kantor.getNamaKanwil());
            skt.setKodeKpp(lim.getKodeKpp());
            skt.setNamaKpp(kantor.getNamaKpp());
            skt.setNoSkt(dtoSimpanSkt.getNoSkt());
            skt.setNamaObjekPajak(lim.getNamaObjekPajak());
            skt.setNop(dtoSimpanSkt.getNop());
            skt.setNamaSektor(Utility.getNamaSektorByKode(lim.getSektor()));
            skt.setKodeSektor(lim.getSektor());
            skt.setKodeSubSektor(lim.getSubSektor());
            skt.setNamaSubSektor(Utility.getNamaSubSektorByKode(lim.getSektor(), lim.getSubSektor()));
            skt.setLokNamaJalan(lim.getObjekPajak().getLokJalan());
            skt.setLokDesa(lim.getObjekPajak().getLokDesa());
            skt.setLokKecamatan(lim.getObjekPajak().getLokKecamatan());
            skt.setLokKabupaten(lim.getObjekPajak().getLokKabupaten());
            skt.setLokProvinsi(lim.getObjekPajak().getLokProvinsi());
            skt.setLokKodePos(lim.getObjekPajak().getLokKodePos());
            skt.setNamaWp(dtoSimpanSkt.getNamaWp());
            skt.setNpwp(dtoSimpanSkt.getNpwp());
            skt.setAlamatWP(dtoSimpanSkt.getAlamatWp());
            skt.setJenisWP(dtoSimpanSkt.getJnsWp());
            skt.setEmail(dtoSimpanSkt.getEmail());
            skt.setNoTelp(dtoSimpanSkt.getTelp());
            //skt.setTempatTtd(kantor.getKotaKpp());
            //skt.setNamaTtd();
            //skt.setNipTtd();
            skt.setIsPjs(user.getIsPjs()?"1":"0");
            //skt.setTanggalTtd();
            skt.setTanggalMulai(dtoSimpanSkt.getTglSejak());
            String[] arTgl = dtoSimpanSkt.getTglSejak().split("-");
            Integer thAwal = Integer.valueOf(arTgl[2]) + 1;
            skt.setTahunAwal(thAwal.toString());
            skt.setTahunAkhir("9999");
            skt.setStatus("1");
            skt.setUserId(user.getNip());
            //skt.setFileLocation();
            skt.setIdPendaftaran(lim.getIdPendaftaran());
            return skt;
        } else {
            return null;
        }

    }
}
