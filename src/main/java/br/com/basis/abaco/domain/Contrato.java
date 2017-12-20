package br.com.basis.abaco.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

/**
 * A Contrato.
 */
@Entity
@Table(name = "contrato")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "contrato")
public class Contrato implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
	@SequenceGenerator(name = "sequenceGenerator")
	private Long id;

	@Column(name = "numero_contrato")
	private String numeroContrato;

	@Column(name = "data_inicio_vigencia")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataInicioVigencia;

	@Column(name = "data_fim_vigencia")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate dataFimVigencia;

	@ManyToOne
	private Manual manual;

	@ManyToOne
	@JsonBackReference
	private Organizacao organization;

	@Column(name = "ativo")
	private boolean ativo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public Contrato numeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
		return this;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public LocalDate getDataInicioVigencia() {
		return dataInicioVigencia;
	}

	public Contrato dataInicioVigencia(LocalDate dataInicioVigencia) {
		this.dataInicioVigencia = dataInicioVigencia;
		return this;
	}

	public void setDataInicioVigencia(LocalDate dataInicioVigencia) {
		this.dataInicioVigencia = dataInicioVigencia;
	}

	public LocalDate getDataFimVigencia() {
		return dataFimVigencia;
	}

	public Contrato dataFimVigencia(LocalDate dataFimVigencia) {
		this.dataFimVigencia = dataFimVigencia;
		return this;
	}

	public void setDataFimVigencia(LocalDate dataFimVigencia) {
		this.dataFimVigencia = dataFimVigencia;
	}

	public Manual getManual() {
		return manual;
	}

	public Contrato manual(Manual manual) {
		this.manual = manual;
		return this;
	}

	public void setManual(Manual manual) {
		this.manual = manual;
	}

	public Organizacao getOrganization() {
		return organization;
	}

	public void setOrganization(Organizacao organization) {
		this.organization = organization;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Contrato contrato = (Contrato) o;
		if (contrato.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, contrato.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Contrato{" + "id=" + id + ", numeroContrato='" + numeroContrato + "'" + ", dataInicioVigencia='"
				+ dataInicioVigencia + "'" + ", dataFimVigencia='" + dataFimVigencia + "'" + '}';
	}
}
