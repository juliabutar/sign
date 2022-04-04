package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.*;

public interface ISktService {
    ResponseModel getSkt(String idSkt, UserSikkaModel user);
    ResponseModel simpanSkt(DtoSimpanSkt dtoSimpanSkt, UserSikkaModel user);
    ResponseModel signSkt(DtoSignSkt dtoSignSkt, UserSikkaModel user, String isPjsPelayanan);
    ResponseModel printSkt(String idSkt, UserSikkaModel user);
    ResponseModel getWp(String pnpwp, UserSikkaModel user);
    DataTableResponse getDaftarSkt (int draw, int start, int length, String kodeKpp, String kodeKanwil,String[] inStatus, String search);
}
