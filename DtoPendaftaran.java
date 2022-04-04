package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

import java.util.List;

@Data
public class DtoPendaftaran {
    private String idPermohonan;
    private String kodeKpp;
    private String kodeKanwil;
    private String channel;
    private String noBps;
    private String jenisPendaftaran;
    private String tipePendaftaran;
    private String noAkta;
    private String nik;
    private String npwp;
    private String namaWp;
    private String jenisWp;
    private String statusWp;
    private String alamatWp;
    private String email;
    private String noTelp;

    private String nop;
    private String namaObjekPajak;
    private String sektor;
    private String subSektor;
    private String statusKegiatan;
    private String lokJalan;
    private String lokDesa;
    private String lokKecamatan;
    private String lokKabupaten;
    private String lokProvinsi;
    private String lokKodePos;
    private List<DtoDataPendukung> dataPendukungs;

    private String namaPendaftar;
    private String tanggalPendaftaran;
    private String tempatPendaftaran;

    private String jnsTrx;



}
