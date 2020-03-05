package br.com.basis.abaco.service.dto;

import br.com.basis.abaco.domain.User;
import br.com.basis.abaco.domain.enumeration.MetodoContagem;
import br.com.basis.abaco.domain.enumeration.TipoAnalise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AnaliseDTO {

    private Long id;
    private String identificadorAnalise;
    private OrganizacaoAnaliseDTO organizacao;
    private TipoEquipeAnaliseDTO equipeResponsavel;
    private SistemaAnaliseDTO sistema;
    private MetodoContagem metodoContagem;
    private String pfTotal;
    private String adjustPFTotal;
    private Timestamp dataCriacaoOrdemServico;
    private TipoAnalise tipoAnalise;
    private boolean bloqueiaAnalise;
    private Set<UserAnaliseDTO> users = new HashSet<>();

    public void setDataCriacaoOrdemServico(Timestamp dataCriacaoOrdemServico) {
        this.dataCriacaoOrdemServico = new Timestamp(dataCriacaoOrdemServico.getTime());
    }
    public Timestamp getDataCriacaoOrdemServico() {
       return new Timestamp(this.dataCriacaoOrdemServico.getTime());
    }

}
