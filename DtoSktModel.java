package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DtoSktModel implements Serializable {
    private String idSkt;
    private String kodeKanwil;
    private String namaKanwil;
    private String kodeKpp;
    private String namaKpp;
    private String noSkt;
    private String namaObjekPajak;
    private String nop;
    private String namaSektor;
    private String kodeSektor;
    private String namaSubSektor;
    private String kodeSubSektor;
    private String lokNamaJalan;
    private String lokDesa;
    private String lokKecamatan;
    private String lokKabupaten;
    private String lokProvinsi;
    private String lokKodePos;
    private String namaWp;
    private String npwp;
    private String alamatWP;
    private String jenisWP;
    private String email;
    private String noTelp;
    private String tempatTtd;
    private String namaTtd;
    private String nipTtd;
    private String isPjs;
    private String tanggalTtd;
    private String tanggalMulai;
    private String tahunAwal;
    private String tahunAkhir;
    private String status;
    private String userId;
    private String fileLocation;
    private String idPendaftaran;
    private String signedDocId;

}
