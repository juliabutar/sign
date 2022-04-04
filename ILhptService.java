package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.DataTableResponse;
import go.pajak.pbb.app.registrasi.dtomodel.DtoLhptInit;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;

public interface ILhptService {
    ResponseModel setDataLhptInitial(DtoLhptInit dtoLhptInit, UserSikkaModel user);
    ResponseModel updateStatus (String idLhpp, UserSikkaModel userSikkaModel);
    DataTableResponse getDataLhpt (int draw, int start, int length, String kodeKpp, String kodeKanwil,String search, String[] inStatus);
    ResponseModel getDataLhpt(String idLhpt, String kdKpp, String kdKanwil);

}
