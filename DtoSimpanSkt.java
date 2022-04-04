package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DtoSimpanSkt implements Serializable {
    private String idLhpt;
    private String noSkt;
    private String tglSejak;
    private String nop;
    private String npwp;
    private String namaWp;
    private String alamatWp;
    private String email;
    private String telp;
    private String jnsWp;
}
