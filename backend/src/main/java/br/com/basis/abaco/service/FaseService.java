package br.com.basis.abaco.service;

import br.com.basis.abaco.domain.novo.Fase;
import br.com.basis.abaco.repository.FaseRepository;
import br.com.basis.abaco.service.dto.novo.DropdownDTO;
import br.com.basis.abaco.service.dto.FaseDTO;
import br.com.basis.abaco.service.dto.filter.FaseFiltroDTO;
import br.com.basis.abaco.service.mapper.FaseMapper;
import br.com.basis.abaco.service.relatorio.RelatorioFaseColunas;
import br.com.basis.abaco.utils.RelatorioUtil;
import br.com.basis.abaco.web.rest.errors.CustomParameterizedException;
import br.com.basis.abaco.web.rest.errors.ErrorConstants;
import br.com.basis.dynamicexports.service.DynamicExportsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FaseService {

    private final Logger log = LoggerFactory.getLogger(FaseService.class);

    private final FaseRepository repository;

    private final DynamicExportsService dynamicExportsService;

    private final EsforcoFaseService esforcoFaseService;

    private final FaseMapper mapper;

    public void save(FaseDTO faseDTO) {
        validaNome(faseDTO);
        repository.save(mapper.toEntity(faseDTO));
    }
    public void save(Fase fase) {
        validaNome(fase);
        repository.save(fase);
    }

    private void validaNome(FaseDTO faseDTO) {
        if (repository.existsByNome(faseDTO.getNome())) {
            throw new CustomParameterizedException(ErrorConstants.FASE_CADASTRADA);
        }
    }

    private void validaNome(Fase fase) {
        if ((fase.getId() == null || fase.getId() == 0)  && repository.existsByNome(fase.getNome())) {
            throw new CustomParameterizedException(ErrorConstants.FASE_CADASTRADA);
        } else if(fase.getId() != null && fase.getId() > 0  && repository.existsByNomeAndIdNot(fase.getNome(), fase.getId())){
            throw new CustomParameterizedException(ErrorConstants.FASE_CADASTRADA);
        }
    }

    public void delete(Long id) {
        validaRemocao(id);
        repository.delete(id);
    }

    private void validaRemocao(Long id) {
        if (esforcoFaseService.existFase(id)) {
            throw new CustomParameterizedException(ErrorConstants.FASE_EM_USO);
        }
    }

    public Page<FaseDTO> getPage(FaseFiltroDTO filter, Pageable page) {
        return repository.findPage(filter, page);
    }

    public Page<Fase> list(String nome,Pageable page) {
        if(nome!= null && !(nome.isEmpty())){
            FaseFiltroDTO dto = new FaseFiltroDTO();
            dto.setNome(nome);
            return repository.findByNomeContainsIlike(dto, page);
        }else{
            return repository.findAll(page);
        }
    }

    public ByteArrayOutputStream getRelatorioBAOS(String tipoRelatorio, FaseFiltroDTO filter, Pageable pageable) {
        Page<FaseDTO> fasePage = repository.findPage(filter, pageable);
        return RelatorioUtil.getRelatorioBAOS(tipoRelatorio, fasePage, dynamicExportsService, new RelatorioFaseColunas());
    }

    public Fase findOne(Long id) {
        return repository.findOne(id);
    }

    public List<DropdownDTO> getDropdown() {
        return repository.getDropdown();
    }
}
