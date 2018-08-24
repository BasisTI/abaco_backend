package br.com.basis.abaco.web.rest;

import com.codahale.metrics.annotation.Timed;
import br.com.basis.abaco.domain.BaseLineAnalitico;

import br.com.basis.abaco.repository.BaseLineAnaliticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

/**
 * REST controller for managing BaseLineAnalitico.
 */
@RestController
@RequestMapping("/api")
public class BaseLineAnaliticoResource {

    private final Logger log = LoggerFactory.getLogger(BaseLineAnaliticoResource.class);

    private static final String ENTITY_NAME = "baseLineAnalitico";

    private final BaseLineAnaliticoRepository baseLineAnaliticoRepository;

    public BaseLineAnaliticoResource(BaseLineAnaliticoRepository baseLineAnaliticoRepository) {
        this.baseLineAnaliticoRepository = baseLineAnaliticoRepository;
    }


    /**
     * GET  /base-line-analiticos : get all the baseLineAnaliticos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of baseLineAnaliticos in body
     */
    @GetMapping("/baseline-analiticos")
    @Timed
    public List<BaseLineAnalitico> getAllBaseLineAnaliticos() {
        log.debug("REST request to get all BaseLineAnaliticos");
        return baseLineAnaliticoRepository.findAll();
    }


    @GetMapping("/baseline-analiticos/fd/{id}")
    @Timed
    public List<BaseLineAnalitico> getBaseLineAnaliticoFD(@PathVariable Long id) {
        log.debug("REST request to get FD BaseLineAnalitico : {}", id);
        return baseLineAnaliticoRepository.getAllAnaliticosFD(id);
    }

    @GetMapping("/baseline-analiticos/ft/{id}")
    @Timed
    public List<BaseLineAnalitico> getBaseLineAnaliticoFT(@PathVariable Long id) {
        log.debug("REST request to get FT BaseLineAnalitico : {}", id);
        return baseLineAnaliticoRepository.getAllAnaliticosFT(id);
    }

}
