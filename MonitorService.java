package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Service
public class MonitorService implements IMonitorService {

    @Autowired
    @Qualifier("RegServiceWebClient")
    WebClient regWebClient;

    @Override
    public ResponseModel getDataMonitor(String kdKanwil, String kdKpp) {
        try {
            Mono<ResponseModel> result = regWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/monitoring")
                            .queryParam("kdKanwil", kdKanwil)
                            .queryParam("kdKpp", kdKpp)
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

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.getEncoder().encode(creds.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Tidak bisa convert");
        }
    }
}
