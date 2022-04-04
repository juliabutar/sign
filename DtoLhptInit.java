package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

@Data
public class DtoLhptInit {
    String userId;
    String idPendaftaran;
    String noLhpt;
    String namaOp;
    String nop;
    String kodeSektor;
    String kodeSubSektor;
    String lokJalan;
    String lokDesa;
    String lokKecamatan;
    String lokKabupaten;
    String lokProvinsi;
    String kodePos;
    String namaWp;
    String npwp;
    String alamatWp;
    String jenisWp;
    String email;
    String noTelp;
    String tanggalPenelitian;
    String jnsDok;
    String namaDok;
    String tanggalDok;
    String expiredDate;
    Boolean isBerlaku;
    Boolean isMemenuhiSyaratSubjektif;
    String ketSyaratSubjektif;
    Boolean isEligible;
    Boolean isTerbitNpwpJabatan;
    String npwpJabatan;
    String saran;
    String tempatLhpt;
    String tanggalLhpt;
    String nipPembuatLhpt;
    String namaPembuatLhpt;
    String jabatanPembuatLhpt;
    String namaSeksi;
    String nipAtasanPembuatLhpt;
    String namaAtasanPembuatLhpt;
    String jabatanAtasanPembuatLhpt;

    String jnsLhpt;
}
