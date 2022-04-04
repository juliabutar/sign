package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

@Data
public class UserSikkaModel {
    private String nip;
    private String nipBaru;
    private String namaPegawai;
    private String email;
    private String kodeKanwil;
    private String kodeKpp;
    private String kodeKantor;
    private String namaKantor;
    private String kodeJenisKantor;
    private String namaJenisKantor;
    private String kodeUnitOrg;
    private String namaUnitOrg;
    private String kodeJabatan;
    private String jenisJabatan;
    private String namaJabatan;
    private String namaPangkat;
    private String urlPhoto;
    private Boolean isPelaksanaKi;
    private Boolean isAdministratorSistem;
    private Boolean isDefinitive;
    private Boolean isUnitOrgStrategis;
    private Boolean isPpmPkm;
    private Boolean isPjs;
    private String kdKppPjs;
    private String kdKanwilPjs;
    private String kdKantorPjs;
    private String nmKantorPjs;
    private String kdUnitOrgPjs;
    private String nmUnitOrgPjs;
    private String kdJabatanPjs;
    private String nmJabatanPjs;
}
