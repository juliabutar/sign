package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LegalLhptModel implements Serializable {
    private String tempatLhpt;
    private String tanggalLhpt;
    private String namaSeksi;
    private String namaKasi;
    private String nipKasi;
    private String jabatanPembuatLhpt;
    private String namaPejabatPembuatLhpt;
    private String nipPejabatPembuatLhpt;
}
