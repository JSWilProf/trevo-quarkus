package br.senai.sp.informatica.trevobk.propostas.components;

import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPageMulti<T> {
    private int page;
    private int size;
    private Uni<List<T>> items;
}