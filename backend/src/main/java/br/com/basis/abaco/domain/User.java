package br.com.basis.abaco.domain;

import br.com.basis.abaco.config.Constants;
import br.com.basis.dynamicexports.pojo.ReportObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractAuditingEntity implements Serializable, ReportObject {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    @Column(length = 100, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String lastName;

    @Transient
    @JsonSerialize
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String nome;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = true;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date")
    private ZonedDateTime resetDate = null;


    @ManyToMany
    @JoinTable(name = "user_perfil", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "perfil_id", referencedColumnName = "id")})
    private Set<Perfil> perfils = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerfilOrganizacao> perfilOrganizacoes = new ArrayList<>();

    @Field(type = FieldType.Nested)
    @ManyToMany
    @JoinTable(name = "user_tipo_equipe", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tipo_equipe_id"))
    private Set<TipoEquipe> tipoEquipes = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<Analise> analises = new HashSet<>();


    @Field(type = FieldType.Nested)
    @ManyToMany
    @JoinTable(name = "user_organizacao", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "organizacao_id", referencedColumnName = "id"))
    private Set<Organizacao> organizacoes = new HashSet<>();



    public String getEquipes(){
        String ponto = ". ";
        String nomeEquipe = "";

        if (tipoEquipes != null) {
            for (TipoEquipe equipe : tipoEquipes) {
                nomeEquipe = nomeEquipe.concat(equipe.getNome()).concat(ponto);
            }
        }
        return nomeEquipe;
    }
    public String getIsActivated(){
        return activated ? "Ativo" : "Inativo";
    }

    public String getNomePerfil(){
        String ponto = " | ";
        String nomePerfil = "";

        if (perfils != null) {
            for (Perfil perfil : perfils) {
                nomePerfil = nomePerfil.concat(perfil.getNome()).concat(ponto);
            }
        }

        return nomePerfil;
    }

    public String getNomeOrg(){
        String ponto = ". ";
        String nomeOrg = "";

        if (organizacoes != null) {
            for (Organizacao org : organizacoes) {
                nomeOrg = nomeOrg.concat(org.getNome()).concat(ponto);
            }
        }

        return nomeOrg;
    }

    public String getNome() {
        return this.firstName.toLowerCase() + " " + this.lastName.toLowerCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public ZonedDateTime getResetDate() {
        return resetDate;
    }

    public void setResetDate(ZonedDateTime resetDate) {
        this.resetDate = resetDate;
    }

    public Set<TipoEquipe> getTipoEquipes() {
        return unmodifiableSet(tipoEquipes);
    }

    public void setTipoEquipes(Set<TipoEquipe> tipoEquipes) {
        this.tipoEquipes = unmodifiableSet(tipoEquipes);
    }

    public Set<Analise> getAnalises() {
        return unmodifiableSet(analises);
    }

    public void setAnalises(Set<Analise> analises) {
        this.analises = unmodifiableSet(analises);
    }

    public Set<Organizacao> getOrganizacoes() {
        return unmodifiableSet(organizacoes);
    }

    public void setOrganizacoes(Set<Organizacao> organizacoes) {
        this.organizacoes = unmodifiableSet(organizacoes);
    }

    public Set<Perfil> getPerfils() {
        return Optional.ofNullable(this.perfils)
            .map(lista -> new LinkedHashSet<Perfil>(lista))
            .orElse(new LinkedHashSet<Perfil>());
    }

    public void setPerfils(Set<Perfil> perfils) {
        this.perfils = Optional.ofNullable(perfils)
            .map(lista -> new LinkedHashSet<Perfil>(lista))
            .orElse(new LinkedHashSet<Perfil>());
    }

    public List<PerfilOrganizacao> getPerfilOrganizacoes() {
        return Optional.ofNullable(this.perfilOrganizacoes)
            .map(lista -> new ArrayList<PerfilOrganizacao>(lista))
            .orElse(new ArrayList<PerfilOrganizacao>());
    }

    public void setPerfilOrganizacoes(List<PerfilOrganizacao> perfilOrganizacoes) {
        this.perfilOrganizacoes = Optional.ofNullable(perfilOrganizacoes)
            .map(lista -> new ArrayList<PerfilOrganizacao>(lista))
            .orElse(new ArrayList<PerfilOrganizacao>());
    }
}
