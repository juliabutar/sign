package go.pajak.pbb.app.registrasi.dtomodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class LhptInitialModel extends DtoLhpt {

    private String namaKanwil;
    private String namaKpp;
    private String noLhpt;
    private ObjekPajakLhppModel objekPajak;
    private WajibPajakModel wajibPajak;
    private String tanggalPenelitian;
    private String jnsDokumen;
    private String namaDokumen;
    private String tanggalDokumen;
    private String expiredDate;
    private Boolean isBerlaku;
    private Boolean isSyaratSubjektif;
    private String ketSyaratSubjektif;
    private KesimpulanLhptModel kesimpulan;
    private LegalLhptModel legalLhpt;

    public String transformToJsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
