package br.com.basis.abaco.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VwResumoBase {
    @Id
    private Long rowNumber;
    @Column
    private Long analiseId;
    @Column
    private String pfAjustada;
    @Column
    private String pfTotal;
    @Column(name = "quantidade_tipo")
    private Long quantidadeTipo;
    @Column
    private Long sem;
    @Column
    private Long baixa;
    @Column
    private Long media;
    @Column
    private Long alta;
    @Column
    private Long inm;
    @Column
    private String tipo;
}
