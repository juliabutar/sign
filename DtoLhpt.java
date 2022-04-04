package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class DtoLhpt implements Serializable {
    private String idLhpt;
    private String userId;
    private String idPendaftaran;
    private String jenisLhpp;
    private String noLhpp;
    private String tanggalLhpp;
    private String nop;
    private String namaObjekPajak;
    private String npwp;
    private String namaWp;
    private String status;
    private String sektor;
    private String subSektor;
    private String kodeKpp;
    private String kodeKanwil;
}
