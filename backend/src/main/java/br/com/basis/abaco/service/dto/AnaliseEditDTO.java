package br.com.basis.abaco.service.dto;

import br.com.basis.abaco.domain.Compartilhada;
import br.com.basis.abaco.domain.Contrato;
import br.com.basis.abaco.domain.EsforcoFase;
import br.com.basis.abaco.domain.FatorAjuste;
import br.com.basis.abaco.domain.Manual;
import br.com.basis.abaco.domain.Status;
import br.com.basis.dynamicexports.pojo.ReportObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AnaliseEditDTO extends AnaliseBaseDTO implements ReportObject {

    private boolean bloqueiaAnalise;
    private boolean clonadaParaEquipe;
    private Set<UserAnaliseDTO> users = new HashSet<>();
    private String escopo;
    private String fronteiras;
    private String documentacao;
    private String propositoContagem;
    private String observacoes;
    private Boolean baselineImediatamente;
    private Timestamp dataHomologacao;
    private boolean enviarBaseline;
    private Contrato contrato;
    private FatorAjuste fatorAjuste;
    private Set<EsforcoFase> esforcoFases;
    private Manual manual;
    private Set<Compartilhada> compartilhadas;
    private Status status;
}
