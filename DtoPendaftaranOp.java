package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

import java.util.List;

@Data
public class DtoPendaftaranOp {
    private String id;
    private String channel;
    private String noBps;
    private String sumber;
    private String kodeKanwil;
    private String kodeKpp;
    private String jenisPendaftaran;
    private String tipePendaftaran;
    private String namaPendaftar;
    private String tanggalPendaftaran;
    private String tempatPendaftaran;
    private String statusPermohonan;
    private String namaObjekPajak;
    private String userId;
    private DtoDataWp wajibPajak;
    private DtoDataObjek objekPajak;
    private List<DtoDataPendukung> dataPendukungs;
    private String nipPetugas;
}
