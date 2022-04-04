package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DtoKantorModel  implements Serializable {
    private String kodeKpp;
    private String namaKpp;
    private String kodeKanwil;
    private String namaKanwil;
    private String alamatBaris1;
    private String alamatBaris2;
    private String alamatBaris3;
    private String kotaKpp;
}
