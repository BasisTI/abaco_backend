package br.com.basis.abaco.repository;

import br.com.basis.abaco.domain.Alr;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Alr entity.
 */
@SuppressWarnings("unused")
public interface AlrRepository extends JpaRepository<Alr, Long> {

}
