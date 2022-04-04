package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class KesimpulanLhptModel implements Serializable {
    private Boolean isEligibleSkt;
    private String nop;
    private Boolean isTerbitNpwpJabatan;
    private String npwp;
    private String saran;
}
