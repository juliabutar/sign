package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class SigningRequestModel {
    //document
    private String fileUri;
    //@NotBlank(message="Application name cannot be null")
    private String documentType;
    private String nomor;
    private String tujuan;
    private String perihal;
    private String info;
    private String tampilan;
    private String docId;
    private String originalFileName;
    private String objectLocation;
    private String signingStatus;

    //document for
    private String npwpTujuan;
    private String sendByEmail;
    private String emailTujuan;

    //signer
    //@NotBlank(message="Signer Id cannot be null")
    private String signerIdType;
    private String signerId;
    private String signerPassphrase;
    private String signerName;
    private String nipPenandatangan;
    private String namaJabatan;
    private String namaUnitJabatan;
    private String cert;

    //requester
    private String appName;
    //@NotBlank(message="Document Type cannot be null")
    private String appUser;
    private String kdKanwil;
    private String kdKpp;

    //signer provider
    private String signingProvider;

    //meta data
    private List<HashMap<String, Object>> sumdata;
}
