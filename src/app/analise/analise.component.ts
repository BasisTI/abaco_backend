import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {DatatableClickEvent, DatatableComponent} from '@basis/angular-components';
import {TranslateService} from '@ngx-translate/core';
import {BlockUI, NgBlockUI} from 'ng-block-ui';
import {ConfirmationService} from 'primeng/primeng';
import {Subscription} from 'rxjs/Subscription';
import {PageNotificationService, ResponseWrapper} from '../shared';
import {Analise, AnaliseService, AnaliseShareEquipe, GrupoService} from './';
import {Organizacao, OrganizacaoService} from './../organizacao';
import {Sistema, SistemaService} from './../sistema';
import {TipoEquipe, TipoEquipeService} from './../tipo-equipe';
import {SearchGroup} from './grupo/grupo.model';
import {User, UserService} from '../user';

@Component({
    selector: 'jhi-analise',
    templateUrl: './analise.component.html',
    providers: [GrupoService]
})
export class AnaliseComponent implements OnInit, OnDestroy {

    @ViewChild(DatatableComponent) datatable: DatatableComponent;
    @BlockUI() blockUI: NgBlockUI;

    searchUrl: string = this.grupoService.grupoUrl;

    userAnaliseUrl: string = this.searchUrl;

    rowsPerPageOptions: number[] = [5, 10, 20, 50, 100];

    analiseSelecionada: any = new Analise();
    searchGroup: SearchGroup = new SearchGroup();
    nomeSistemas: Array<Sistema>;
    usuariosOptions: Array<User>;
    organizations: Array<Organizacao>;
    teams: Array<TipoEquipe>;
    equipeShare;
    analiseShared: Array<AnaliseShareEquipe> = [];
    selectedEquipes: Array<AnaliseShareEquipe>;
    selectedToDelete: AnaliseShareEquipe;
    analiseTemp: Analise = new Analise();
    tipoEquipesLoggedUser: TipoEquipe[] = [];
    query: String;
    usuarios: String[] = [];

    translateSusbscriptions: Subscription[] = [];

    metsContagens = [
        {label: undefined, value: undefined},
        {label: 'DETALHADA', value: 'DETALHADA'},
        {label: 'INDICATIVA', value: 'INDICATIVA'},
        {label: 'ESTIMADA', value: 'ESTIMADA'}
    ];

    blocked;
    inicial: boolean;
    mostrarDialog = false;

    constructor(
        private router: Router,
        private confirmationService: ConfirmationService,
        private sistemaService: SistemaService,
        private analiseService: AnaliseService,
        private tipoEquipeService: TipoEquipeService,
        private organizacaoService: OrganizacaoService,
        private pageNotificationService: PageNotificationService,
        private translate: TranslateService,
        private grupoService: GrupoService,
        private equipeService: TipoEquipeService,
        private usuarioService: UserService,
    ) {
    }

    public ngOnInit() {
        this.blockUI.stop();
        this.userAnaliseUrl = this.changeUrl();
        this.estadoInicial();
        this.traduzirmetsContagens();
    }

    getLabel(label) {
        let str: any;
        this.translateSusbscriptions.push(this.translate.get(label).subscribe((res: string) => {
            str = res;
        }));
        return str;
    }

    estadoInicial() {
        this.getEquipesFromActiveLoggedUser();
        this.recuperarOrganizacoes();
        this.recuperarEquipe();
        this.recuperarSistema();
        this.recuperarUsuarios();
        this.inicial = false;

        this.datatable.pDatatableComponent.onRowSelect.subscribe((event) => {
            this.analiseSelecionada = event.data;
            this.blocked = event.data.bloqueado;
            this.inicial = true;
        });
        this.datatable.pDatatableComponent.onRowUnselect.subscribe((event) => {
            this.analiseSelecionada = undefined;
        });
    }

    getEquipesFromActiveLoggedUser() {
        this.equipeService.getEquipesActiveLoggedUser().subscribe(res => {
            this.tipoEquipesLoggedUser = res.json;
        });
    }

    traduzirmetsContagens() {
        this.translate.stream(['Analise.Analise.metsContagens.DETALHADA', 'Analise.Analise.metsContagens.ESTIMADA',
            'Analise.Analise.metsContagens.INDICATIVA']).subscribe((traducao) => {
                this.metsContagens = [
                    {label: traducao['Analise.Analise.metsContagens.DETALHADA'], value: 'DETALHADA'},
                    {label: traducao['Analise.Analise.metsContagens.ESTIMADA'], value: 'ESTIMADA'},
                    {label: traducao['Analise.Analise.metsContagens.INDICATIVA'], value: 'INDICATIVA'}
                ];
            }
        );
    }

    public carregarDataTable() {
        this.grupoService.all().subscribe((res: ResponseWrapper) => {
            this.datatable.value = res.json;
        });
    }

    clonarTooltip() {
        if (!this.analiseSelecionada.id) {
            return this.getLabel('Analise.Analise.Mensagens.msgRegistroClonar');
        }
        return this.getLabel('Analise.Analise.Clonar');
    }

    compartilharTooltip() {
        if (!this.analiseSelecionada.id) {
            return this.getLabel('Analise.Analise.Mensagens.msgResgistroCompartilhar');
        }
        return this.getLabel('Analise.Analise.CompartilharAnalise');
    }

    relatorioTooltip() {
        if (!this.analiseSelecionada.id) {
            return this.getLabel('Analise.Analise.Mensagens.msgRegistroGerarRelatorio');
        }
        return this.getLabel('Analise.Analise.RelatorioDetalhado');
    }

    relatorioExcelTooltip() {
        if (!this.analiseSelecionada.id) {
            return this.getLabel('Analise.Analise.Mensagens.msgRegistroGerarRelatorio');
        }
        return this.getLabel('Analise.Analise.RelatorioExcel');
    }

    relatorioContagemTooltip() {
        if (!this.analiseSelecionada.id) {
            return this.getLabel('Analise.Analise.Mensagens.msgRegistroGerarRelatorio');
        }
        return this.getLabel('Analise.Analise.RelatorioFundamentacao');
    }

    recuperarOrganizacoes() {
        this.organizacaoService.dropDown().subscribe(response => {
            this.organizations = response.json;
            const emptyOrg = new Organizacao();
            this.organizations.unshift(emptyOrg);
        });
    }

    recuperarSistema() {
        this.sistemaService.dropDown().subscribe(response => {
            this.nomeSistemas = response.json;
            const emptySystem = new Sistema();
            this.nomeSistemas.unshift(emptySystem);
        });
    }

    recuperarUsuarios() {
        this.usuarioService.dropDown().subscribe(response => {
            this.usuariosOptions = response.json;
        });
    }

    recuperarEquipe() {
        this.tipoEquipeService.dropDown().subscribe(response => {
            this.teams = response.json;
            const emptyTeam = new TipoEquipe();
            this.teams.unshift(emptyTeam);
        });
    }

    public datatableClick(event: DatatableClickEvent) {
        if (!event.selection) {
            return;
        }
        switch (event.button) {
            case 'edit':
                if (event.selection.bloqueado) {
                    this.pageNotificationService.addErrorMsg(
                        this.getLabel('Analise.Analise.Mensagens.msgEDITAR_ANALISE_BLOQUEADA'));
                    return;
                }
                this.router.navigate(['/analise', event.selection.id, 'edit']);
                break;
            case 'view':
                this.router.navigate(['/analise', event.selection.id, 'view']);
                break;
            case 'delete':
                this.confirmDelete(event.selection);
                break;
            case 'relatorioBrowser':
                this.geraRelatorioPdfBrowser(event.selection);
                break;
            case 'relatorioArquivo':
                this.gerarRelatorioPdfArquivo(event.selection);
                break;
            case 'relatorioBrowserDetalhado':
                this.geraRelatorioPdfDetalhadoBrowser(event.selection);
                break;
            case 'relatorioExcelDetalhado':
                this.gerarRelatorioExcel(event.selection);
                break;
            case 'clone':
                this.clonar(event.selection.id);
                break;
            case 'geraBaselinePdfBrowser':
                this.geraBaselinePdfBrowser();
                break;
            case 'compartilhar':
                if (this.checkUserAnaliseEquipes()) {
                    this.openCompartilharDialog();
                } else {
                    this.pageNotificationService.addErrorMsg(
                        this.getLabel('Analise.Analise.Mensagens.msgSomenteEquipeCompartilharAnalise')
                    );
                }
                break;
            case 'relatorioAnaliseContagem':
                this.gerarRelatorioContagem(event.selection);
                break;
        }
    }

    checkUserAnaliseEquipes() {
        let retorno = false;
        return this.analiseService.find(this.analiseSelecionada.id).subscribe((res: any) => {
            this.analiseTemp = res;
            if (this.tipoEquipesLoggedUser) {
                this.tipoEquipesLoggedUser.forEach(equipe => {
                    if (equipe.id === this.analiseTemp.equipeResponsavel.id) {
                        retorno = true;
                    }
                });
            }
            return retorno;
        });

    }

    checkIfUserCanEdit() {
        let retorno = false;
        if (this.tipoEquipesLoggedUser) {
            this.tipoEquipesLoggedUser.forEach(equipe => {
                if (this.analiseSelecionada.compartilhadas) {
                    this.analiseSelecionada.compartilhadas.forEach(compartilhada => {
                        if (equipe.id === compartilhada.equipeId) {
                            if (!compartilhada.viewOnly) {
                                retorno = true;
                            }
                        }
                    });
                }
            });
        }
        return retorno;
    }

    public onRowDblclick(event) {

        if (event.target.nodeName === 'TD') {
            this.abrirEditar();
        } else if (event.target.parentNode.nodeName === 'TD') {
            this.abrirEditar();
        }
    }

    abrirEditar() {
        this.router.navigate(['/analise', this.analiseSelecionada.id, 'edit']);
    }

    public clonar(id: number) {
        this.confirmationService.confirm({
            message: this.getLabel('Analise.Analise.Mensagens.msgCONFIRMAR_CLONE')
                .concat(this.analiseSelecionada.identificadorAnalise).concat('?'),
            accept: () => {
                this.analiseService.find(id).subscribe((res: any) => {
                    const analiseClonada = res.clone();

                    analiseClonada.id = undefined;
                    analiseClonada.identificadorAnalise += this.getLabel('Analise.Analise.Mensagens.msgCONCAT_COPIA');
                    analiseClonada.bloqueiaAnalise = false;
                    analiseClonada.compartilhadas = undefined;

                    if (analiseClonada.funcaoDados) {
                        analiseClonada.funcaoDados.forEach(FuncaoDados => {
                            FuncaoDados.id = undefined;
                            if (FuncaoDados.ders) {
                                FuncaoDados.ders.forEach(Ders => {
                                    Ders.id = undefined;
                                });
                            }
                            if (FuncaoDados.rlrs) {
                                FuncaoDados.rlrs.forEach(rlrs => {
                                    rlrs.id = undefined;
                                });
                            }
                        });
                    }

                    if (analiseClonada.funcaoTransacaos) {
                        analiseClonada.funcaoTransacaos.forEach(funcaoTransacaos => {
                            funcaoTransacaos.id = undefined;
                            if (funcaoTransacaos.ders) {
                                funcaoTransacaos.ders.forEach(ders => {
                                    ders.id = undefined;
                                });
                            }
                            if (funcaoTransacaos.alrs) {
                                funcaoTransacaos.alrs.forEach(alrs => {
                                    alrs.id = undefined;
                                });
                            }
                        });
                    }

                    this.analiseService.create(analiseClonada).subscribe((response: any) => {
                        const menssagem: string = this.getLabel('Analise.Analise.Analise')
                                                        .concat(' ').concat(this.analiseSelecionada.identificadorAnalise)
                                                        .concat(this.getLabel('Analise.Analise.Mensagens.msgCLONAGEM_SUCESSO'));

                        this.pageNotificationService.addSuccessMsg(menssagem);
                        this.recarregarDataTable();
                        this.router.navigate(['/analise', response.id, 'edit']);
                    });
                });
            }
        });

    }

    public confirmDelete(analise: Analise) {
        if (this.analiseSelecionada.bloqueado) {
            this.pageNotificationService.addErrorMsg(this.getLabel('Analise.Analise.Mensagens.msgERRO_EXCLUSAO_ANALISE_BLOQUEADA'));
            return;
        }

        this.confirmationService.confirm({
            message: this.getLabel('Analise.Analise.Mensagens.msgCertezaExcluirRegistro').concat(analise.identificadorAnalise).concat('?'),
            accept: () => {
                this.blockUI.start(this.getLabel('Global.Mensagens.EXCLUINDO_REGISTRO'));
                this.analiseService.delete(analise.id).subscribe(() => {
                    this.recarregarDataTable();
                    this.blockUI.stop();
                    this.pageNotificationService.addDeleteMsgWithName(analise.identificadorAnalise);
                });
            }
        });
    }

    public limparPesquisa() {
        this.searchGroup.organizacao = undefined;
        this.searchGroup.identificadorAnalise = undefined;
        this.searchGroup.sistema = undefined;
        this.searchGroup.metodoContagem = undefined;
        this.searchGroup.equipe = undefined;
        this.searchGroup.usuario = undefined;
        this.userAnaliseUrl = this.changeUrl();
        this.recarregarDataTable();
    }

    public recarregarDataTable() {
        this.datatable.url = this.userAnaliseUrl;
        this.datatable.reset();
    }

    public gerarRelatorioPdfArquivo(analise: Analise) {
        this.analiseService.gerarRelatorioPdfArquivo(analise.id);
    }

    public geraRelatorioPdfBrowser(analise: Analise) {
        this.analiseService.geraRelatorioPdfBrowser(analise.id);
    }

    public geraRelatorioPdfDetalhadoBrowser(analise: Analise) {
        this.analiseService.geraRelatorioPdfDetalhadoBrowser(analise.id);
    }

    public gerarRelatorioExcel(analise: Analise) {
        this.analiseService.gerarRelatorioExcel(analise.id);
    }

    public geraBaselinePdfBrowser() {
        this.analiseService.geraBaselinePdfBrowser();
    }

    public gerarRelatorioContagem(analise: Analise) {
        this.analiseService.gerarRelatorioContagem(analise.id);
    }

    public changeUrl() {

        let querySearch = '?identificador=';
        querySearch = querySearch.concat((this.searchGroup.identificadorAnalise) ? `*${this.searchGroup.identificadorAnalise}*&` : '&');

        querySearch = querySearch.concat((this.searchGroup.sistema && this.searchGroup.sistema.id) ?
            `sistema=${this.searchGroup.sistema.id}&` : '');

        querySearch = querySearch.concat((this.searchGroup.metodoContagem) ? `metodo=${this.searchGroup.metodoContagem}&` : '');

        querySearch = querySearch.concat((this.searchGroup.organizacao && this.searchGroup.organizacao.id) ?
            `organizacao=${this.searchGroup.organizacao.id}&` : '');

        querySearch = querySearch.concat((this.searchGroup.equipe && this.searchGroup.equipe.id) ?
            `equipe=${this.searchGroup.equipe.id}&` : '');

        querySearch = querySearch.concat((this.searchGroup.usuario && this.searchGroup.usuario.id) ?
            `usuario=${this.searchGroup.usuario.id}&` : '');

        querySearch = (querySearch === '?') ? '' : querySearch;

        querySearch = (querySearch.endsWith('&')) ? querySearch.slice(0, -1) : querySearch;

        this.recuperarQuery(this.searchGroup);
        return this.grupoService.grupoUrl + querySearch;
    }

    recuperarQuery(searchGroup: SearchGroup) {
        this.query = '';
        this.query = this.query.concat(
            (this.searchGroup.identificadorAnalise) ? `identificadorAnalise:*${this.searchGroup.identificadorAnalise}*` : '');
        if (this.searchGroup.sistema && this.searchGroup.sistema.nome !== undefined && this.query === '') {
            this.query = `${this.query}sistema.nome:*${this.searchGroup.sistema.nome}*`;
        } else if (this.searchGroup.sistema && this.searchGroup.sistema.nome !== undefined && this.query !== '') {
            this.query = `${this.query} AND sistema.nome:*${this.searchGroup.sistema.nome}*`;
        }

        if (this.searchGroup.metodoContagem !== undefined && this.query === '') {
            this.query = `metodoContagem:*${this.searchGroup.metodoContagem}*`;
        } else if (this.searchGroup.metodoContagem !== undefined && this.query !== '') {
            this.query = `${this.query} AND metodoContagem:*${this.searchGroup.metodoContagem}*`;
        }

        if (this.searchGroup.organizacao && this.searchGroup.organizacao.nome !== undefined && this.query === '') {
            this.query = `organizacao.nome:*${this.searchGroup.organizacao.nome}*`;
        } else if (this.searchGroup.organizacao && this.searchGroup.organizacao.nome !== undefined && this.query !== '') {
            this.query = `${this.query} AND organizacao.nome:*${this.searchGroup.organizacao.nome}*`;
        }

        if (this.searchGroup.equipe && this.searchGroup.equipe.nome !== undefined && this.query === '') {
            this.query = `equipeResponsavel.nome:*${this.searchGroup.equipe.nome}*`;
        } else if (this.searchGroup.equipe && this.searchGroup.equipe.nome !== undefined && this.query !== '') {
            this.query = `${this.query} AND equipeResponsavel.nome:*${this.searchGroup.equipe.nome}*`;
        }

        if (this.searchGroup.usuario && this.searchGroup.usuario.id !== undefined && this.query === '') {
            this.query = `usuario.id: ${this.searchGroup.usuario.id}`;
        } else if (this.searchGroup.equipe && this.searchGroup.equipe.nome !== undefined && this.query !== '') {
            this.query = `${this.query} AND equipeResponsavel.nome:*${this.searchGroup.equipe.nome}*`;
        }

    }

    public performSearch() {
        this.userAnaliseUrl = this.changeUrl();
        this.recarregarDataTable();
    }

    /**
     * Desabilita botão relatório
     */
    public desabilitarBotaoRelatorio(): boolean {
        return !this.analiseSelecionada;
    }

    /**
     * Bloquear Análise
     */
    public bloqueiaAnalise(bloquear: boolean) {
        this.analiseService.find(this.analiseSelecionada.id).subscribe((res: any) => {
            this.analiseTemp = res;
            if (!this.analiseTemp.dataHomologacao && !bloquear) {
                this.pageNotificationService.addInfoMsg(this.getLabel('Analise.Analise.Mensagens.msgINFORME_DATA_HOMOLOGACAO'));
            } else {
                if (this.checkUserAnaliseEquipes()) {
                    this.confirmationService.confirm({
                        message: this.mensagemDialogBloquear(bloquear),
                        accept: () => {
                            const copy = this.analiseTemp.toJSONState();
                            this.analiseService.block(copy).subscribe(() => {
                                const nome = this.analiseTemp.identificadorAnalise;
                                const bloqueado = this.analiseTemp.bloqueiaAnalise;
                                this.mensagemAnaliseBloqueada(bloqueado, nome);
                                this.recarregarDataTable();
                            });
                        }
                    });
                } else {
                    this.pageNotificationService.addErrorMsg(this.getLabel('Analise.Analise.Mensagens.msgSomenteEquipeBloquearAnalise'));
                }
            }
        });
    }

    private mensagemDialogBloquear(retorno: boolean) {
        if (retorno) {
            return this.getLabel('Analise.Analise.Mensagens.msgCONFIRMAR_DESBLOQUEIO').concat('?');
        } else {
            return this.getLabel('Analise.Analise.Mensagens.msgCONFIRMAR_BLOQUEIO').concat('?');
        }
    }

    private mensagemAnaliseBloqueada(retorno: boolean, nome: string) {
        if (retorno) {
            this.pageNotificationService.addUnblockMsgWithName(nome);
        } else {
            this.pageNotificationService.addBlockMsgWithName(nome);
        }
    }


    public openCompartilharDialog() {
        this.equipeShare = [];
        this.analiseService.find(this.analiseSelecionada.id).subscribe((res: any) => {
            this.analiseTemp = res;
            this.tipoEquipeService.findAllCompartilhaveis(
                this.analiseTemp.organizacao.id,
                this.analiseSelecionada.id,
                this.analiseTemp.equipeResponsavel.id)
                .subscribe((equipes) => {
                    if (equipes.json) {
                        equipes.json.forEach((equipe) => {
                            const entity: AnaliseShareEquipe = Object.assign(new AnaliseShareEquipe(),
                                {
                                    id: undefined,
                                    equipeId: equipe.id,
                                    analiseId: this.analiseSelecionada.id,
                                    viewOnly: false, nomeEquipe: equipe.nome
                                });
                            this.equipeShare.push(entity);
                        });
                    }
                    this.blockUI.stop();
                });
        });

        this.analiseService.findAllCompartilhadaByAnalise(this.analiseSelecionada.id).subscribe((shared) => {
            this.analiseShared = shared.json;
        });
        this.mostrarDialog = true;
    }

    public salvarCompartilhar() {
        if (this.selectedEquipes && this.selectedEquipes.length !== 0) {
            this.analiseService.salvarCompartilhar(this.selectedEquipes).subscribe((res) => {
                this.mostrarDialog = false;
                this.pageNotificationService.addSuccessMsg(this.getLabel('Analise.Analise.Mensagens.msgAnaliseCompartilhadaSucesso'));
                this.limparSelecaoCompartilhar();
            });
        } else {
            this.pageNotificationService.addInfoMsg(this.getLabel('Analise.Analise.Mensagens.msgSelecioneRegistroAdicionarCliqueSair'));
        }
    }

    public deletarCompartilhar() {
        if (this.selectedToDelete && this.selectedToDelete !== null) {
            this.analiseService.deletarCompartilhar(this.selectedToDelete.id).subscribe((res) => {
                this.mostrarDialog = false;
                this.pageNotificationService.addSuccessMsg(this.getLabel('Analise.Analise.Mensagens.msgCompartilhamentoRemovidoSucesso'));
                this.limparSelecaoCompartilhar();
            });
        } else {
            this.pageNotificationService.addInfoMsg(this.getLabel('Analise.Analise.Mensagens.msgSelecioneRegistroRemoverCliqueSair'));
        }
    }

    public limparSelecaoCompartilhar() {
        this.recarregarDataTable();
        this.selectedEquipes = undefined;
        this.selectedToDelete = undefined;
    }

    public updateViewOnly() {
        setTimeout(() => {
            this.analiseService.atualizarCompartilhar(this.selectedToDelete).subscribe((res) => {
                this.pageNotificationService.addSuccessMsg(this.getLabel('Analise.Analise.Mensagens.msgRegistroAtualizadoSucesso'));
            });
        }, 250);
    }

    ngOnDestroy() {
        this.translateSusbscriptions.forEach(subscription => subscription.unsubscribe());
    }
}
