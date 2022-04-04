package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class WajibPajakModel implements Serializable {
    private String npwp;
    private String namaWp;
    private String jenisWp;
    private String alamatWp;
    private String email;
    private String noTelp;
}
