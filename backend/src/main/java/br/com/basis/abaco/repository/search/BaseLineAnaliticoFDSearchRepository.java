package br.com.basis.abaco.repository.search;

import br.com.basis.abaco.domain.BaseLineAnaliticoFD;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Set;

public interface BaseLineAnaliticoFDSearchRepository extends ElasticsearchRepository<BaseLineAnaliticoFD, Long> {

    List<BaseLineAnaliticoFD> findByIdsistemaOrderByNameAsc(Long idsistema);

    Set<BaseLineAnaliticoFD> findAll();

    void deleteAllByIdsistemaAndEquipeResponsavelId(Long id, Long idEquipe);

    List<BaseLineAnaliticoFD> save(List<BaseLineAnaliticoFD> baseLineAnaliticoFDs);
}
