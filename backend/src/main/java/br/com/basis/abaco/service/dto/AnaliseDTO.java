package br.com.basis.abaco.service.dto;

import br.com.basis.abaco.domain.Analise;
import br.com.basis.abaco.domain.Compartilhada;
import br.com.basis.abaco.domain.enumeration.MetodoContagem;
import br.com.basis.abaco.domain.enumeration.TipoAnalise;
import br.com.basis.dynamicexports.pojo.ReportObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AnaliseDTO implements ReportObject, Serializable {

    private Long id;
    private String identificadorAnalise;
    private String numeroOs;
    private OrganizacaoAnaliseDTO organizacao;
    private TipoEquipeAnaliseDTO equipeResponsavel;
    private SistemaAnaliseDTO sistema;
    private MetodoContagem metodoContagem;
    private String pfTotal;
    private String adjustPFTotal;
    private Timestamp dataCriacaoOrdemServico;
    private TipoAnalise tipoAnalise;
    private Boolean isDivergence;
    private boolean bloqueiaAnalise;
    private boolean clonadaParaEquipe;
    private Set<UserAnaliseDTO> users = new HashSet<>();
    private Set<Compartilhada> compartilhadas = new HashSet<>();
    private StatusDTO status;
    private AnaliseDivergenceDTO analiseDivergence;
    private Set<AnaliseDivergenceDTO> analisesComparadas = new HashSet<>();

    @JsonIgnoreProperties("analiseClonadaParaEquipe")
    private AnaliseDTO analiseClonadaParaEquipe;
    private Boolean analiseClonou;

    public void setDataCriacaoOrdemServico(Timestamp dataCriacaoOrdemServico) {
        this.dataCriacaoOrdemServico = dataCriacaoOrdemServico == null ? null : new Timestamp(dataCriacaoOrdemServico.getTime());
    }

    public Timestamp getDataCriacaoOrdemServico() {
        return dataCriacaoOrdemServico == null ? null : new Timestamp(this.dataCriacaoOrdemServico.getTime());
    }

}
