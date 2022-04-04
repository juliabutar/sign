package go.pajak.pbb.app.registrasi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.xmp.impl.Base64;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModelBuilder;
import go.pajak.pbb.app.registrasi.dtomodel.SigningRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
public class SigningService {
    @Autowired
    @Qualifier("SigningServiceWebClient")
    WebClient signingWebClient;
    @Value("${endpoint.svc.signing.userid}")
    private String signingSvcClientId;
    @Value("${endpoint.svc.signing.usersecret}")
    private String signingSvcClientSecret;

    public ResponseModel signDocument(byte[] pdfDoc, SigningRequestModel signingModel) {
        try {
            File filepdf = File.createTempFile("temp", ".pdf");
            FileOutputStream os = new FileOutputStream(filepdf);
            os.write(pdfDoc);

            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(signingModel);

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(filepdf));
            builder.part("jsondata", jsonData);

            MultiValueMap<String, HttpEntity<?>> parts = builder.build();
            ResponseModel result = new ResponseModel();
            Mono<ResponseModel> res = signingWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/signpdf2")
                            .build())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", getAuthorizationHeader(signingSvcClientId, signingSvcClientSecret))
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(ResponseModel.class);
            result = res.block();
            filepdf.delete();

            return result;

        } catch (Exception ex) {
            return new ResponseModelBuilder().failed().withMessage(ex.getMessage()).build();

        }

    }

    public ResponseModel checkDocument(String docId) {
        try {
            Mono<ResponseModel> res = signingWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/dokumen/status")
                            .queryParam("docid", docId)
                            .queryParam("appname", "PBBREG")
                            .build())
                    .header("Authorization", getAuthorizationHeader(signingSvcClientId, signingSvcClientSecret))
                    .retrieve()
                    .bodyToMono(ResponseModel.class);
            return res.block();

        } catch (Exception ex) {
            return new ResponseModelBuilder()
                    .failed()
                    .withMessage(ex.getMessage()).build();

        }
    }

    public byte[] getSignedDocument(String docId) {
        try {
            Mono<byte[]> result = signingWebClient.get()
                    .uri(builder -> builder
                            .path("/dokumen")
                            .queryParam("appname", "PBBREG")
                            .queryParam("objectname", docId)
                            .build())
                    .header("Authorization", getAuthorizationHeader(signingSvcClientId, signingSvcClientSecret))
                    .retrieve()
                    .bodyToMono(byte[].class);
            byte[] res = result.block();
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private String getAuthorizationHeader(String user, String pwd) {
        String creds = String.format("%s:%s", user, pwd);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

}
