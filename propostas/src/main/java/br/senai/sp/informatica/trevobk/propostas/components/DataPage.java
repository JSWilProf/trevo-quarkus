package br.senai.sp.informatica.trevobk.propostas.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataPage<T> {
    private int page;
    private int size;
    private int total;
    private List<T> items;

    @JsonGetter
    public int getRecords() { return items.size();}
}
