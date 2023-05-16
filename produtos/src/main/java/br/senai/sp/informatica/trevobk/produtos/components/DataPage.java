package br.senai.sp.informatica.trevobk.produtos.components;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataPage<T> {
    private int page;
    private int size;
    private int records;
    private List<T> items;
}
