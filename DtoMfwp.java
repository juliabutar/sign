package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DtoMfwp {
    private String npwp;
    private String namaWp;
    private String alamat;
    private String jenisWp;
    private String email;
    private String noTelp;
}
