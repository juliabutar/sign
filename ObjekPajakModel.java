package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Setter
@Getter
public class ObjekPajakModel implements Serializable {
    private String nop;
    private String namaObjekPajak;
    private String kodeSektor;
    private String sektor;
    private String kodeSubSektor;
    private String subSektor;
}
