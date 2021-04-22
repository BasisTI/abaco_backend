package br.com.basis.abaco.config;

import br.com.basis.abaco.domain.*;
import br.com.basis.abaco.repository.search.*;
import br.com.basis.abaco.service.EntityMapper;
import br.com.basis.abaco.service.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import br.com.basis.abaco.domain.enumeration.IndexadoresUtil;
import br.com.basis.abaco.repository.AlrRepository;
import br.com.basis.abaco.repository.AnaliseRepository;
import br.com.basis.abaco.repository.BaseLineAnaliticoFDRepository;
import br.com.basis.abaco.repository.BaseLineAnaliticoFTRepository;
import br.com.basis.abaco.repository.BaseLineSinteticoRepository;
import br.com.basis.abaco.repository.ContratoRepository;
import br.com.basis.abaco.repository.DerRepository;
import br.com.basis.abaco.repository.EsforcoFaseRepository;
import br.com.basis.abaco.repository.FatorAjusteRepository;
import br.com.basis.abaco.repository.FuncaoDadosRepository;
import br.com.basis.abaco.repository.FuncaoTransacaoRepository;
import br.com.basis.abaco.repository.FuncionalidadeRepository;
import br.com.basis.abaco.repository.ManualContratoRepository;
import br.com.basis.abaco.repository.ManualRepository;
import br.com.basis.abaco.repository.ModuloRepository;
import br.com.basis.abaco.repository.NomenclaturaRepository;
import br.com.basis.abaco.repository.OrganizacaoRepository;
import br.com.basis.abaco.repository.PerfilRepository;
import br.com.basis.abaco.repository.RlrRepository;
import br.com.basis.abaco.repository.SistemaRepository;
import br.com.basis.abaco.repository.StatusRepository;
import br.com.basis.abaco.repository.TipoEquipeRepository;
import br.com.basis.abaco.repository.UserRepository;
import br.com.basis.abaco.service.Indexador;
import br.com.basis.abaco.service.IndexadorComMapper;
import br.com.basis.abaco.service.IndexadorSemMapper;
import br.com.basis.abaco.service.dto.AnaliseDTO;
import br.com.basis.abaco.service.dto.SistemaListDTO;
import br.com.basis.abaco.service.dto.TipoEquipeDTO;
import br.com.basis.abaco.service.dto.UserEditDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class IndexadorConfiguration {

    private ElasticsearchTemplate elasticsearchTemplate;

    private AnaliseRepository analiseRepository;
    private AlrRepository alrRepository;
    private UserRepository userRepository;
    private SistemaRepository sistemaRepository;
    private ContratoRepository contratoRepository;
    private DerRepository derRepository;
    private EsforcoFaseRepository esforcoFaseRepository;
    private FatorAjusteRepository fatorAjusteRepository;
    private FuncaoDadosRepository funcaoDadosRepository;
    private FuncaoTransacaoRepository funcaoTransacaoRepository;
    private FuncionalidadeRepository funcionalidadeRepository;
    private ManualRepository manualRepository;
    private ManualContratoRepository manualContratoRepository;
    private ModuloRepository moduloRepository;
    private OrganizacaoRepository organizacaoRepository;
    private RlrRepository rlrRepository;
    private TipoEquipeRepository tipoEquipeRepository;
    private BaseLineAnaliticoFDRepository baseLineAnaliticoFDRepository;
    private BaseLineAnaliticoFTRepository baseLineAnaliticoFTRepository;
    private BaseLineSinteticoRepository baseLineSinteticoRepository;
    private StatusRepository statusRepository;
    private NomenclaturaRepository nomenclaturaRepository;
    private PerfilRepository perfilRepository;

    private AnaliseSearchRepository analiseSearchRepository;
    private AlrSearchRepository alrSearchRepository;
    private VwAlrSearchRepository vwAlrSearchRepository;
    private UserSearchRepository userSearchRepository;
    private SistemaSearchRepository sistemaSearchRepository;
    private ContratoSearchRepository contratoSearchRepository;
    private DerSearchRepository derSearchRepository;
    private VwDerSearchRepository vwDerSearchRepository;
    private EsforcoFaseSearchRepository esforcoFaseSearchRepository;
    private FatorAjusteSearchRepository fatorAjusteSearchRepository;
    private FuncaoDadosSearchRepository funcaoDadosSearchRepository;
    private FuncaoTransacaoSearchRepository funcaoTransacaoSearchRepository;
    private FuncionalidadeSearchRepository funcionalidadeSearchRepository;
    private ManualSearchRepository manualSearchRepository;
    private ManualContratoSearchRepository manualContratoSearchRepository;
    private ModuloSearchRepository moduloSearchRepository;
    private OrganizacaoSearchRepository organizacaoSearchRepository;
    private RlrSearchRepository rlrSearchRepository;
    private VwRlrSearchRepository vwRlrSearchRepository;
    private TipoEquipeSearchRepository tipoEquipeSearchRepository;
    private BaseLineAnaliticoFDSearchRepository baseLineAnaliticoFDSearchRepository;
    private BaseLineAnaliticoFTSearchRepository baseLineAnaliticoFTSearchRepository;
    private BaseLineSinteticoSearchRepository baseLineSinteticoSearchRepository;
    private StatusSearchRepository statusSearchRepository;
    private NomenclaturaSearchRepository nomenclaturaSearchRepository;
    private PerfilSearchRepository perfilSearchRepository;

    @Bean
    public Indexador indexadorUser() {
        UserElasticSearchMapper userElasticSearchMapper = new UserElasticSearchMapper();
        IndexadorComMapper<User, User, Long, UserEditDTO> indexador = new IndexadorComMapper<>(
            userRepository,
            userSearchRepository,
            userElasticSearchMapper,
            elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.USER.name());
        indexador.setDescricao(IndexadoresUtil.USER.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorSistema() {

        SistemaElasticSearchMapper sistemaElasticSearchMapper = new SistemaElasticSearchMapper();
        IndexadorComMapper<Sistema, Sistema, Long, SistemaListDTO> indexador = new IndexadorComMapper<>(
            sistemaRepository,
            sistemaSearchRepository,
            sistemaElasticSearchMapper,
            elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.SISTEMA.name());
        indexador.setDescricao(IndexadoresUtil.SISTEMA.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorAlr() {
        AlrMapper alrMapper = new AlrMapper(alrRepository);
        IndexadorComMapper<VwAlr, VwAlr, Long, Alr> indexador = new IndexadorComMapper(alrRepository,
            vwAlrSearchRepository,
            alrMapper,
            elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.ALR.name());
        indexador.setDescricao(IndexadoresUtil.ALR.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorAnalise() {
        AnaliseMapper analiseMapper = new AnaliseMapper();
        IndexadorComMapper<Analise, Analise, Long, AnaliseDTO> indexador = new IndexadorComMapper<>(analiseRepository
            , analiseSearchRepository
            , analiseMapper
            , elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.ANALISE.name());
        indexador.setDescricao(IndexadoresUtil.ANALISE.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorContrato() {
        IndexadorSemMapper<Contrato, Long> indexador = new IndexadorSemMapper<>(contratoRepository,
            contratoSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.CONTRATO.name());
        indexador.setDescricao(IndexadoresUtil.CONTRATO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorDer() {
        DerMapper derMapper = new DerMapper(derRepository);
        IndexadorComMapper<VwDer, VwDer, Long, Der> indexador = new IndexadorComMapper(derRepository
            , vwDerSearchRepository
            , derMapper
            , elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.DER.name());
        indexador.setDescricao(IndexadoresUtil.DER.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorEsforcoFase() {
        IndexadorSemMapper<EsforcoFase, Long> indexador = new IndexadorSemMapper<>(esforcoFaseRepository,
            esforcoFaseSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.ESFORCO_FASE.name());
        indexador.setDescricao(IndexadoresUtil.ESFORCO_FASE.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorFatorAjuste() {
        IndexadorSemMapper<FatorAjuste, Long> indexador = new IndexadorSemMapper<>(fatorAjusteRepository,
            fatorAjusteSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.FATOR_AJUSTE.name());
        indexador.setDescricao(IndexadoresUtil.FATOR_AJUSTE.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorFuncaoDados() {
        IndexadorSemMapper<FuncaoDados, Long> indexador = new IndexadorSemMapper<>(funcaoDadosRepository,
            funcaoDadosSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.FUNCAO_DADOS.name());
        indexador.setDescricao(IndexadoresUtil.FUNCAO_DADOS.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorFuncaoTransacao() {
        IndexadorSemMapper<FuncaoTransacao, Long> indexador = new IndexadorSemMapper<>(funcaoTransacaoRepository,
            funcaoTransacaoSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.FUNCAO_TRANSACAO.name());
        indexador.setDescricao(IndexadoresUtil.FUNCAO_TRANSACAO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorFuncionalidade() {
        IndexadorSemMapper<Funcionalidade, Long> indexador = new IndexadorSemMapper<>(funcionalidadeRepository,
            funcionalidadeSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.FUNCIONALIDADE.name());
        indexador.setDescricao(IndexadoresUtil.FUNCIONALIDADE.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorManual() {
        IndexadorSemMapper<Manual, Long> indexador = new IndexadorSemMapper<>(manualRepository,
            manualSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.MANUAL.name());
        indexador.setDescricao(IndexadoresUtil.MANUAL.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorManualContrato() {
        IndexadorSemMapper<ManualContrato, Long> indexador = new IndexadorSemMapper<>(manualContratoRepository,
            manualContratoSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.MANUAL_CONTRATO.name());
        indexador.setDescricao(IndexadoresUtil.MANUAL_CONTRATO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorModulo() {
        IndexadorSemMapper<Modulo, Long> indexador = new IndexadorSemMapper<>(moduloRepository,
            moduloSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.MODULO.name());
        indexador.setDescricao(IndexadoresUtil.MODULO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorOrganizacao() {
        IndexadorSemMapper<Organizacao, Long> indexador = new IndexadorSemMapper<>(organizacaoRepository,
            organizacaoSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.ORGANIZACAO.name());
        indexador.setDescricao(IndexadoresUtil.ORGANIZACAO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorPerfil() {
        IndexadorSemMapper<Perfil, Long> indexador = new IndexadorSemMapper<>(perfilRepository,
            perfilSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.PERFIL.name());
        indexador.setDescricao(IndexadoresUtil.PERFIL.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorRlr() {
        RlrMapper rlrMapper = new RlrMapper(rlrRepository);
        IndexadorComMapper<VwRlr, VwRlr, Long, Rlr> indexador = new IndexadorComMapper(rlrRepository,
            vwRlrSearchRepository,
            rlrMapper,
            elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.RLR.name());
        indexador.setDescricao(IndexadoresUtil.RLR.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorTipoEquipe() {
        TipoEquipeMapper tipoEquipeMapper = new TipoEquipeMapper();
        IndexadorComMapper<TipoEquipe, TipoEquipe, Long, TipoEquipeDTO> indexador = new IndexadorComMapper<>(
            tipoEquipeRepository,
            tipoEquipeSearchRepository,
            tipoEquipeMapper,
            elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.TIPO_EQUIPE.name());
        indexador.setDescricao(IndexadoresUtil.TIPO_EQUIPE.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorBaseLineAnaliticoFD() {
        IndexadorSemMapper<BaseLineAnaliticoFD, Long> indexador = new IndexadorSemMapper<>(baseLineAnaliticoFDRepository,
            baseLineAnaliticoFDSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.BASE_LINE_ANALITICO_FD.name());
        indexador.setDescricao(IndexadoresUtil.BASE_LINE_ANALITICO_FD.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorBaseLineAnaliticoFT() {
        IndexadorSemMapper<BaseLineAnaliticoFT, Long> indexador = new IndexadorSemMapper<>(baseLineAnaliticoFTRepository,
            baseLineAnaliticoFTSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.BASE_LINE_ANALITICO_FT.name());
        indexador.setDescricao(IndexadoresUtil.BASE_LINE_ANALITICO_FT.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorBaseLineSintetico() {
        IndexadorSemMapper<BaseLineSintetico, Long> indexador = new IndexadorSemMapper<>(baseLineSinteticoRepository,
            baseLineSinteticoSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.BASE_LINE_SINTETICO.name());
        indexador.setDescricao(IndexadoresUtil.BASE_LINE_SINTETICO.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorStatus() {
        IndexadorSemMapper<Status, Long> indexador = new IndexadorSemMapper<>(statusRepository,
            statusSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.STATUS.name());
        indexador.setDescricao(IndexadoresUtil.STATUS.label);
        return indexador;
    }

    @Bean
    public Indexador indexadorNomenclatura() {
        IndexadorSemMapper<Nomenclatura, Long> indexador = new IndexadorSemMapper<>(nomenclaturaRepository,
            nomenclaturaSearchRepository, elasticsearchTemplate);
        indexador.setCodigo(IndexadoresUtil.NOMENCLATURA.name());
        indexador.setDescricao(IndexadoresUtil.NOMENCLATURA.label);
        return indexador;
    }
}
