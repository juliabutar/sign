package go.pajak.pbb.app.registrasi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PegawaiService {

    @Autowired
    @Qualifier("PegawaiServiceWebClient")
    WebClient pegawaiWebClient;

    public UserSikkaModel getPegawaiByNip(String nip) {
        try {
            Mono<ResponseModel> result = pegawaiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/pegawai")
                            .queryParam("nip", nip).build())
                    .retrieve()
                    .bodyToMono(ResponseModel.class);

            ResponseModel rm = result.block();
            if (rm.getKodeResponse() == 1) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserSikkaModel user = objectMapper.convertValue(rm.getObjResponse(), UserSikkaModel.class);
                return user;
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public UserSikkaModel getAtasanPegawaiByNip(String nip) {
        try {
            Mono<ResponseModel> result = pegawaiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/atasan")
                            .queryParam("nip", nip).build())
                    .retrieve()
                    .bodyToMono(ResponseModel.class);

            ResponseModel rm = result.block();
            if (rm.getKodeResponse() == 1) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserSikkaModel user = objectMapper.convertValue(rm.getObjResponse(), UserSikkaModel.class);
                return user;
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
