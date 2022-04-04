package go.pajak.pbb.app.registrasi.service;

import go.pajak.pbb.app.registrasi.dtomodel.ResponseModel;

public interface IMonitorService {
    ResponseModel getDataMonitor (String kdKanwil, String kdKpp);
}
