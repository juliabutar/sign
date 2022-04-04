package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.Data;

import java.util.List;

@Data
public class DataTableResponse <T>{
    private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}
