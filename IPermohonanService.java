package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.DataTableResponse;
import go.pajak.pbb.app.registrasi.dtomodel.DtoPendaftaran;
import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;
import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;

public interface IPermohonanService {
    ResponseModel setDataPermohonan (DtoPendaftaran dtoPendaftaran, UserSikkaModel user);
    ResponseModel getDataPermohonan (String idPendaftaran, String kdKanwil, String kdKpp);
    DataTableResponse getDataPermohonan (int draw, int start, int length, String kodeKpp, String kodeKanwil,String[] inStatus,String search);
}
