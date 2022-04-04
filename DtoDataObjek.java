package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

@Data
public class DtoDataObjek {

    private String nop;
    private String namaObjekPajak;
    private String kodeSektor;
    private String sektor;
    private String kodeSubSektor;
    private String subSektor;
    private String statusKegiatan;
    private String lokJalan;
    private String lokDesa;
    private String lokKecamatan;
    private String lokKabupaten;
    private String lokProvinsi;
    private String lokKodePos;
}
