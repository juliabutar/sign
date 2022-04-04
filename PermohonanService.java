package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.*;
import go.pajak.pbb.app.registrasi.utility.Utility;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class PermohonanService implements IPermohonanService {

    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Override
    public ResponseModel setDataPermohonan(DtoPendaftaran dtoPendaftaran, UserSikkaModel user) {
        try {
            DtoPendaftaranOp dtoPendaftaranOp = new DtoPendaftaranOp();
            dtoPendaftaranOp.setId(dtoPendaftaran.getIdPermohonan());
            dtoPendaftaranOp.setNipPetugas(user.getNip());
            dtoPendaftaranOp.setUserId(user.getNip());
            dtoPendaftaranOp.setChannel(dtoPendaftaran.getChannel());
            dtoPendaftaranOp.setNoBps(dtoPendaftaran.getNoBps());
            dtoPendaftaranOp.setKodeKpp(user.getKodeKpp());
            dtoPendaftaranOp.setKodeKanwil(user.getKodeKanwil());
            dtoPendaftaranOp.setJenisPendaftaran(dtoPendaftaran.getJenisPendaftaran());
            dtoPendaftaranOp.setTipePendaftaran(dtoPendaftaran.getTipePendaftaran());
            dtoPendaftaranOp.setNamaPendaftar(dtoPendaftaran.getNamaPendaftar());
            dtoPendaftaranOp.setTanggalPendaftaran(dtoPendaftaran.getTanggalPendaftaran());
            dtoPendaftaranOp.setTempatPendaftaran(dtoPendaftaran.getTempatPendaftaran());
            dtoPendaftaranOp.setStatusPermohonan("1");
            dtoPendaftaranOp.setNamaObjekPajak(dtoPendaftaran.getNamaObjekPajak());

            DtoDataWp dataWp = new DtoDataWp();
            dataWp.setNamaWp(dtoPendaftaran.getNamaWp());
            dataWp.setJenisWp(dtoPendaftaran.getJenisWp());
            dataWp.setNoAkta(dtoPendaftaran.getNoAkta());
            dataWp.setNik(dtoPendaftaran.getNik());
            dataWp.setNpwp(dtoPendaftaran.getNpwp().replace(".", "").replace("-", ""));
            dataWp.setStatusWp(dtoPendaftaran.getStatusWp());
            dataWp.setAlamatWp(dtoPendaftaran.getAlamatWp());
            dataWp.setEmail(dtoPendaftaran.getEmail());
            dataWp.setNoTelp(dtoPendaftaran.getNoTelp());

            dtoPendaftaranOp.setWajibPajak(dataWp);

            DtoDataObjek objekPajak = new DtoDataObjek();
            objekPajak.setNop(Objects.nonNull(dtoPendaftaran.getNop())?dtoPendaftaran.getNop().replace(".", "").replace("-", ""):"");
            objekPajak.setNamaObjekPajak(dtoPendaftaran.getNamaObjekPajak());
            objekPajak.setKodeSektor(dtoPendaftaran.getSektor());
            objekPajak.setSektor(getNamaSektor(dtoPendaftaran.getSektor()));
            objekPajak.setKodeSubSektor(dtoPendaftaran.getSubSektor());
            objekPajak.setSubSektor(Utility.getNamaSubSektorByKode(dtoPendaftaran.getSektor(), dtoPendaftaran.getSubSektor()));
            objekPajak.setStatusKegiatan(dtoPendaftaran.getStatusKegiatan());
            objekPajak.setLokJalan(dtoPendaftaran.getLokJalan());
            objekPajak.setLokDesa(dtoPendaftaran.getLokDesa());
            objekPajak.setLokKecamatan(dtoPendaftaran.getLokKecamatan());
            objekPajak.setLokKabupaten(dtoPendaftaran.getLokKabupaten());
            objekPajak.setLokProvinsi(dtoPendaftaran.getLokProvinsi());
            objekPajak.setLokKodePos(dtoPendaftaran.getLokKodePos());

            dtoPendaftaranOp.setObjekPajak(objekPajak);

            dtoPendaftaranOp.setDataPendukungs(dtoPendaftaran.getDataPendukungs());

            // jenis transaksi form (0 for new data , 1 for update data)
            if (dtoPendaftaran.getJnsTrx().equals("0")){
                Mono<ResponseModel> result = regWebClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/pendaftaran")
                                .build())
                        .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(dtoPendaftaranOp), DtoPendaftaranOp.class)
                        .retrieve()
                        .bodyToMono(ResponseModel.class);

                return result.block();
            } else {
                Mono<ResponseModel> result = regWebClient.put()
                        .uri(uriBuilder -> uriBuilder
                                .path("/pendaftaran")
                                .build())
                        .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(dtoPendaftaranOp), DtoPendaftaranOp.class)
                        .retrieve()
                        .bodyToMono(ResponseModel.class);

                return result.block();
            }





        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(e.getMessage()).build();
        }
    }

    @Override
    public ResponseModel getDataPermohonan(String idPendaftaran, String kdKanwil, String kdKpp) {
        try {
            Mono<ResponseModel> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pendaftaran")
                            .queryParam("idPendaftaran", idPendaftaran)
                            .queryParam("kdKpp", kdKpp)
                            .queryParam("kdKanwil", kdKanwil)
                            .build())
                    .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                    .retrieve()
                    .bodyToMono(ResponseModel.class);

            return result.block();

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();
        }
    }


    @Override
    public DataTableResponse getDataPermohonan(int draw, int start, int length, String kodeKpp, String kodeKanwil, String[] inStatus,String search) {
        try {
            Mono<DataTableResponse> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pendaftaran/list")
                            .queryParam("draw", draw)
                            .queryParam("start", start)
                            .queryParam("length", length)
                            .queryParam("kdKpp", kodeKpp)
                            .queryParam("kdKanwil", kodeKanwil)
                            .queryParam("search", search)
                            .queryParam("status", inStatus)
                            .build())
                    .header("Authorization", getAuthorizationHeader("admin", "admin123"))
                    .retrieve()
                    .bodyToMono(DataTableResponse.class);

            return result.block();


        } catch (Exception e) {
            return null;
        }

    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.getEncoder().encode(creds.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Tidak bisa convert");
        }
    }

    private String getNamaSektor(String kodeSektor) {
        if (Objects.nonNull(kodeSektor)) {
            String namaSektor = "";
            switch (kodeSektor) {
                case "1":
                    namaSektor = "PERKEBUNAN";
                    break;
                case "2":
                    namaSektor = "PERHUTANAN";
                    break;
                case "3":
                    namaSektor = "PERTAMBANGAN MINYAK DAN GAS BUMI";
                    break;
                case "4":
                    namaSektor = "PERTAMBANGAN UNTUK PENGUSAHAAN PANAS BUMI";
                    break;
                case "5":
                    namaSektor = "PERTAMBANGAN MINERAL ATAU BATUBARA";
                    break;
                case "6":
                    namaSektor = "SEKTOR LAINNYA";
                    break;
            }
            return namaSektor;
        } else {
            return "";
        }
    }

    private String getNamaSubSektor(String kodeSektor, String kodeSubSektor) {
        if (Objects.nonNull(kodeSubSektor)) {
            String namaSubSektor = "";
            switch (kodeSektor) {
                case "1":
                    namaSubSektor = "Perkebunan";
                    break;
                case "2":
                    if (kodeSubSektor.equals("A")) {
                        namaSubSektor = "Hutan Alam";
                    } else {
                        namaSubSektor = "Hutan Tanaman";
                    }
                    break;
                case "3":
                case "4":
                case "5":
                    switch (kodeSubSektor) {
                        case "A":
                            namaSubSektor = "Onshore";
                            break;
                        case "B":
                            namaSubSektor = "Offshore";
                            break;
                        case "C":
                            namaSubSektor = "Tubuh Bumi";
                            break;
                    }
                case "6":
                    switch (kodeSubSektor) {
                        case "A":
                            namaSubSektor = "Perikanan Tangkap";
                            break;
                        case "B":
                            namaSubSektor = "Pembudidayaan Ikan";
                            break;
                        case "C":
                            namaSubSektor = "Jaringan Pipa";
                            break;
                        case "D":
                            namaSubSektor = "Jaringan Kabel";
                            break;
                        case "E":
                            namaSubSektor = "Ruas Jalan Tol";
                            break;
                        case "F":
                            namaSubSektor = "Fasilitas Penyimpanan dan Pengolahan";
                            break;
                    }
                /*case "A":
                    if (getNamaSektor("2").equals("2")){
                        namaSubSektor = "HUTAN ALAM";
                    } else if (getNamaSektor("3").equals("3") || getNamaSektor("4")=="4" || getNamaSektor("5")=="5"){
                        namaSubSektor = "ONSHORE";
                    } else if (getNamaSektor("6")=="6"){
                        namaSubSektor = "PERIKANAN TANGKAP";
                    }
                    break;
                case "B":
                    if (getNamaSektor("2")=="2"){
                        namaSubSektor = "HUTAN TANAMAN";
                    } else if (getNamaSektor("3")=="3" || getNamaSektor("4")=="4" || getNamaSektor("5")=="5"){
                        namaSubSektor = "OFFSHORE";
                    } else if (getNamaSektor("6")=="6"){
                        namaSubSektor = "PEMBUDIDAYAAN IKAN";
                    }
                    break;
                case "C":
                    if (getNamaSektor("3")=="3" || getNamaSektor("4")=="4" || getNamaSektor("5")=="5"){
                        namaSubSektor = "TUBUH BUMI";
                    } else if (getNamaSektor("6")=="6"){
                        namaSubSektor = "JARINGAN PIPA";
                    }
                    break;
                case "D":
                    namaSubSektor = "JARINGAN KABEL";
                    break;
                case "E":
                    namaSubSektor = "RUAS JALAN TOL";
                    break;
                case "F":
                    namaSubSektor = "FASILITAS PENYIMPANAN DAN PENGOLAHAN";
                    break;*/
            }
            return namaSubSektor;
        } else {
            return "";
        }
    }
}
