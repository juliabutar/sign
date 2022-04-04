package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;

public interface IReferensiService {
    ResponseModel getKantorByKdKpp(String kdKpp,String kdKanwil);
}
