package br.com.basis.abaco.web.rest;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import br.com.basis.abaco.service.PerfilService;
import br.com.basis.dynamicexports.service.DynamicExportsService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import br.com.basis.abaco.domain.Analise;
import br.com.basis.abaco.domain.FuncaoDados;
import br.com.basis.abaco.domain.FuncaoDadosVersionavel;
import br.com.basis.abaco.domain.Modulo;
import br.com.basis.abaco.domain.Organizacao;
import br.com.basis.abaco.domain.Sistema;
import br.com.basis.abaco.repository.AnaliseRepository;
import br.com.basis.abaco.repository.FuncaoDadosRepository;
import br.com.basis.abaco.repository.FuncaoDadosVersionavelRepository;
import br.com.basis.abaco.repository.SistemaRepository;
import br.com.basis.abaco.repository.search.SistemaSearchRepository;
import br.com.basis.abaco.service.SistemaService;
import br.com.basis.abaco.service.dto.SistemaDropdownDTO;
import br.com.basis.abaco.service.dto.filter.SistemaFilterDTO;
import br.com.basis.abaco.service.exception.RelatorioException;
import br.com.basis.abaco.utils.PageUtils;
import br.com.basis.abaco.web.rest.util.HeaderUtil;
import br.com.basis.abaco.web.rest.util.PaginationUtil;
import br.com.basis.dynamicexports.util.DynamicExporter;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class SistemaResource {

    private final Logger log = LoggerFactory.getLogger(SistemaResource.class);
    private static final String ENTITY_NAME = "sistema";
    private static final String DBG_MSG_SIS = "REST request to search Sistemas for query {}";
    private final SistemaRepository sistemaRepository;
    private final SistemaSearchRepository sistemaSearchRepository;
    private final FuncaoDadosVersionavelRepository funcaoDadosVersionavelRepository;
    private final FuncaoDadosRepository funcaoDadosRepository;
    private final SistemaService sistemaService;
    private final AnaliseRepository analiseRepository;
    private final PerfilService perfilService;
    private final DynamicExportsService dynamicExportsService;
    private static final String PAGE = "page";

    public SistemaResource(
        SistemaRepository sistemaRepository,
        SistemaSearchRepository sistemaSearchRepository,
        FuncaoDadosVersionavelRepository funcaoDadosVersionavelRepository,
        FuncaoDadosRepository funcaoDadosRepository,
        SistemaService sistemaService,
        AnaliseRepository analiseRepository, PerfilService perfilService, DynamicExportsService dynamicExportsService) {

        this.sistemaRepository = sistemaRepository;
        this.sistemaSearchRepository = sistemaSearchRepository;
        this.funcaoDadosVersionavelRepository = funcaoDadosVersionavelRepository;
        this.funcaoDadosRepository = funcaoDadosRepository;
        this.sistemaService = sistemaService;
        this.analiseRepository = analiseRepository;
        this.perfilService = perfilService;
        this.dynamicExportsService = dynamicExportsService;
    }

    @PostMapping("/sistemas")
    @Timed
    @Secured("ROLE_ABACO_SISTEMA_CADASTRAR")
    public ResponseEntity<Sistema> createSistema(@Valid @RequestBody Sistema sistema) throws URISyntaxException {
        log.debug("REST request to save Sistema : {}", sistema);
        if (sistema.getId() != null) {
            return ResponseEntity.badRequest().headers(
                HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new sistema cannot already have an ID"))
                .body(null);
        }
        Sistema linkedSistema = linkSistemaToModuleToFunctionalities(sistema);
        Sistema result = sistemaService.saveSistema(linkedSistema);
        return ResponseEntity.created(new URI("/api/sistemas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    private Sistema linkSistemaToModuleToFunctionalities(Sistema sistema) {
        Sistema linkedSistema = copySistema(sistema);
        Set<Modulo> modulos = linkedSistema.getModulos();
        Optional.ofNullable(modulos).orElse(Collections.emptySet())
            .forEach(m -> {
                m.setSistema(linkedSistema);
                Optional.ofNullable(m.getFuncionalidades())
                    .orElse(Collections.emptySet())
                    .parallelStream().forEach(f -> f.setModulo(m));
            });
        return linkedSistema;
    }

    private Sistema copySistema(Sistema sistema) {
        Sistema copy = new Sistema();
        copy.setId(sistema.getId());
        copy.setSigla(sistema.getSigla());
        copy.setNome(sistema.getNome());
        copy.setTipoSistema(sistema.getTipoSistema());
        copy.setNumeroOcorrencia(sistema.getNumeroOcorrencia());
        copy.setOrganizacao(sistema.getOrganizacao());
        copy.setModulos(Optional.ofNullable(sistema.getModulos())
            .map((lista) -> new HashSet<>(lista))
            .orElse(new HashSet<>()));
        return copy;
    }

    @PutMapping("/sistemas")
    @Timed
    @Secured("ROLE_ABACO_SISTEMA_EDITAR")
    public ResponseEntity<Sistema> updateSistema(@Valid @RequestBody Sistema sistema) throws URISyntaxException {
        log.debug("REST request to update Sistema : {}", sistema);
        if (sistema.getId() == null) {
            return createSistema(sistema);
        }
        Sistema linkedSistema = linkSistemaToModuleToFunctionalities(sistema);
        Sistema result = sistemaService.saveSistema(linkedSistema);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, sistema.getId().toString())).body(result);
    }

    @PostMapping("/sistemas/organizations")
    @Timed
    public List<Sistema> getAllSistemasByOrganization(@Valid @RequestBody Organizacao organization) {
        log.debug("REST request to get all Sistemas");
        return sistemaRepository.findAllByOrganizacao(organization);
    }

    @GetMapping("/sistemas/organizacao/{idOrganizacao}")
    @Timed
    @Transactional
    public Set<Sistema> findAllSystemOrg(@PathVariable Long idOrganizacao) {
        log.debug("REST request to get all Sistemas by Organizacao");
        return sistemaRepository.findAllByOrganizacaoId(idOrganizacao);
    }

    @GetMapping("/sistemas/drop-down")
    @Timed
    public List<SistemaDropdownDTO> getSistemaDropdown() {
        log.debug("REST request to get dropdown Sistemas");
        return sistemaService.getSistemaDropdown();
    }

    @GetMapping("/sistemas/{id}")
    @Timed
    @Secured({"ROLE_ABACO_SISTEMA_CONSULTAR", "ROLE_ABACO_SISTEMA_EDITAR"})
    public ResponseEntity<Sistema> getSistema(@PathVariable Long id) {
        log.debug("REST request to get Sistema : {}", id);
        Sistema sistema = sistemaRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(sistema));
    }

    // TODO essa ou nova rota para retornar somente o nome das funcoes
    @GetMapping("/sistemas/{id}/funcao-dados")
    public Set<FuncaoDadosVersionavel> getFuncoesDeDadosVersionaveisBySistema(@PathVariable Long id) {
        return funcaoDadosVersionavelRepository.findAllBySistemaId(id);
    }

    @GetMapping("/sistemas/{id}/funcao-dados-versionavel/{nome}")
    public FuncaoDados recuperarFuncaoDadosPorIdNome(@PathVariable Long id, @PathVariable String nome) {

        Optional<FuncaoDadosVersionavel> funcaoDadosVersionavelOptional = funcaoDadosVersionavelRepository
            .findOneByNomeIgnoreCaseAndSistemaId(nome, id);

        if (funcaoDadosVersionavelOptional.isPresent()) {
            FuncaoDadosVersionavel fdv = funcaoDadosVersionavelOptional.get();
            return funcaoDadosRepository.findFirstByFuncaoDadosVersionavelIdOrderByAuditUpdatedOnDesc(fdv.getId()).get();
        }

        return null;
    }

    @DeleteMapping("/sistemas/{id}")
    @Timed
    @Secured("ROLE_ABACO_SISTEMA_EXCLUIR")
    public ResponseEntity<Void> deleteSistema(@PathVariable Long id) {
        Sistema sistema = sistemaRepository.findOne(id);
        List<Analise> analises = analiseRepository.findAllBySistema(sistema);
        if (sistema == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "not_found_system", "This system can not found for delete.")).body(null);
        } else if (analises.size() > 0) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "analise_exists", "This System can not be deleted")).body(null);
        } else {
            sistemaSearchRepository.delete(id);
            sistemaRepository.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        }
    }

    @GetMapping("/_search/sistemas")
    @Timed
    @Secured({"ROLE_ABACO_SISTEMA_PESQUISAR", "ROLE_ABACO_SISTEMA_ACESSAR"})
    public ResponseEntity<List<Sistema>> searchSistemas(@RequestParam(required = false) String sigla,
                                                        @RequestParam(required = false) String nome,
                                                        @RequestParam(required = false) String numeroOcorrencia,
                                                        @RequestParam(required = false) Long[] organizacao,
                                                        @RequestParam(defaultValue = "ASC", required = false) String order,
                                                        @RequestParam(name = PAGE) int pageNumber, @RequestParam int size,
                                                        @RequestParam(defaultValue = "id") String sort) throws URISyntaxException {
        log.debug(DBG_MSG_SIS);
        Sort.Direction sortOrder = PageUtils.getSortDirection(order);
        Pageable pageable = new PageRequest(pageNumber, size, sortOrder, sort);
        FieldSortBuilder sortBuilder = new FieldSortBuilder(sort).order(SortOrder.ASC);
        BoolQueryBuilder qb = sistemaService.bindFilterSearch(nome, sigla, numeroOcorrencia, organizacao);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(dynamicExportsService.obterPageableMaximoExportacao()).withQuery(qb).withSort(sortBuilder).build();
        Page<Sistema> page = sistemaSearchRepository.search(searchQuery);

        Page<Sistema> pageNew = perfilService.validarPerfilSistema(page, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(pageNew, "/api/_search/sistemas");
        return new ResponseEntity<>(pageNew.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/sistema/exportacao/{tipoRelatorio}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    @Secured("ROLE_ABACO_SISTEMA_EXPORTAR")
    public ResponseEntity<InputStreamResource> gerarRelatorioExportacao(@PathVariable String tipoRelatorio, @RequestBody SistemaFilterDTO filtro) throws RelatorioException {
        ByteArrayOutputStream byteArrayOutputStream = sistemaService.gerarRelatorio(filtro, tipoRelatorio);
        return DynamicExporter.output(byteArrayOutputStream,
            "relatorio." + tipoRelatorio);
    }

    @PostMapping(value = "/sistema/exportacao-arquivo", produces = MediaType.APPLICATION_PDF_VALUE)
    @Timed
    @Secured("ROLE_ABACO_SISTEMA_EXPORTAR")
    public ResponseEntity<byte[]> gerarRelatorioImprimir(@RequestBody SistemaFilterDTO filtro) throws RelatorioException {
        ByteArrayOutputStream byteArrayOutputStream = sistemaService.gerarRelatorio(filtro, "pdf");
        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), HttpStatus.OK);
    }
}
