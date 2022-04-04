package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

@Data
public class ObjekPajakLhppModel extends ObjekPajakModel {
    private String lokJalan;
    private String lokDesa;
    private String lokKecamatan;
    private String lokKabupaten;
    private String lokProvinsi;
    private String lokKodePos;
}
