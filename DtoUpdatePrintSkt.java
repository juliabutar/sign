package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUpdatePrintSkt {
    private String idSkt;
    private String status;
    private String docId;
    private String fileLocation;
}
