package br.com.basis.abaco.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
public class RlrDTO implements Comparable {

    private Long id;

    private String nome;

    private Integer valor;

    private FuncaoDadosSaveDTO funcaoDados;

    private Integer numeracao;

    @Override
    public int compareTo(@NotNull Object o) {
        RlrDTO rlr = (RlrDTO) o;
        if(rlr.getNumeracao() != null && numeracao != null){
            return numeracao - rlr.getNumeracao();
        }
        else{
            return 1;
        }
    }
}
