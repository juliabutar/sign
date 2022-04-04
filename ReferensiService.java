package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.DataTableResponse;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModelBuilder;
import go.pajak.pbb.app.registrasi.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ReferensiService implements IReferensiService{
    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Override
    public ResponseModel getKantorByKdKpp(String kdKpp, String kdKanwil) {
        try {
            Mono<ResponseModel> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/referensi/kantor")
                            .queryParam("kdKpp", kdKpp)
                            .queryParam("kdKanwil", kdKanwil)
                            .build())
                    .header("Authorization", Utility.getAuthorizationHeader("admin", "admin123"))
                    .retrieve()
                    .bodyToMono(ResponseModel.class);
            return result.block();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseModelBuilder().failed().withMessage(e.getMessage()).build();
        }
    }
}
