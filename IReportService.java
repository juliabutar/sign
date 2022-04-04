package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.UserSikkaModel;

import java.io.ByteArrayInputStream;

public interface IReportService  {
    ByteArrayInputStream getSktReport(String idSkt, UserSikkaModel user);
    byte[] getSignedSktReport(String idSkt, UserSikkaModel user);
    byte[] getSignedSktReportFromSigningAgent(String idSkt, UserSikkaModel user);


}
