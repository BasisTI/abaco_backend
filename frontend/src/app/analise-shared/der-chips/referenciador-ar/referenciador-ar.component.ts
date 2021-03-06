import {DerService} from './../../../der/der.service';
import {Component, EventEmitter, OnDestroy, OnInit, Output,} from '@angular/core';

import {AnaliseSharedDataService} from '../../../shared/analise-shared-data.service';
import {AnaliseService} from './../../../analise/analise.service';
import {Der} from '../../../der/der.model';
import {ResponseWrapper} from '../../../shared';
import { Subscription } from 'rxjs';
import { BaselineService } from 'src/app/baseline';
import { FuncaoDados } from 'src/app/funcao-dados';
import { FuncaoDadosService } from 'src/app/funcao-dados/funcao-dados.service';
import { BlockUIModule } from 'primeng';
import { BlockUiService } from '@nuvem/angular-base';

@Component({
    selector: 'app-analise-referenciador-ar',
    templateUrl: './referenciador-ar.component.html'
})
export class ReferenciadorArComponent implements OnInit, OnDestroy {


    @Output()
    dersReferenciadosEvent: EventEmitter<Der[]> = new EventEmitter<Der[]>();

    @Output()
    funcaoDadosReferenciadaEvent: EventEmitter<string> = new EventEmitter<string>();

    private subscriptionAnaliseCarregada: Subscription;

    funcoesDados: FuncaoDados[] = [];

    funcoesDadosCache: FuncaoDados[] = [];

    ders: Der[] = [];

    derMsg: Der = new Der(1, 'Mensagem');
    derAcao: Der = new Der(2, 'Ação');

    idAnalise: number;

    mostrarDialog = false;

    funcaoDadosSelecionada: any;

    dersReferenciados: Der[] = [];

    valorVariavel: string;

    constructor(
        private analiseSharedDataService: AnaliseSharedDataService,
        private analiseService: AnaliseService,
        private baselineService: BaselineService,
        private funcaoDadosService: FuncaoDadosService,
        private derService: DerService,
        private blockUiService: BlockUiService,
    ) {
    }

    getLabel(label) {
        return label;
    }

    ngOnInit() {
    }

    private getFuncoesDados() {
        this.blockUiService.show();
        this.funcoesDados = [];
        this.funcaoDadosService.dropDownPEAnalitico(this.analiseSharedDataService.analise.sistema.id).subscribe(res => {
            this.blockUiService.show();
            this.funcoesDados = this.funcoesDados.concat(res.map((item) => {
                const fd = new FuncaoDados();
                fd.id = item.id;
                fd.name = item.name;
                return fd;
            }));
            this.funcoesDados.sort((t1, t2) => {
                const name1 = t1.name.toLowerCase();
                const name2 = t2.name.toLowerCase();
                if (name1 > name2) { return 1; }
                if (name1 < name2) { return -1; }
                return 0;
              });
              this.blockUiService.hide();
        });
    }

    private subscribeAnaliseCarregada() {
        this.blockUiService.show();
        this.subscriptionAnaliseCarregada = this.analiseSharedDataService.getLoadSubject().subscribe(() => {
            this.baselineService.analiticosFuncaoDados(
                this.analiseSharedDataService.analise.sistema.id).subscribe((res: ResponseWrapper) => {
                    this.blockUiService.show();
                    this.funcoesDados = res.json;
                    this.funcoesDados.concat(this.funcoesDadosCache);
                    if (this.funcoesDados && this.funcoesDados.length !== 0 && this.funcoesDadosCache) {
                        for (const funcoes of this.funcoesDadosCache) {
                            if (this.funcoesDados.indexOf(funcoes) === -1) {
                                this.funcoesDados.push(funcoes);
                            }
                        }
                    } else {
                        this.funcoesDados = this.funcoesDadosCache;
                    }
                    this.funcoesDados.sort((t1, t2) => {
                        const name1 = t1.name.toLowerCase();
                        const name2 = t2.name.toLowerCase();
                        if (name1 > name2) { return 1; }
                        if (name1 < name2) { return -1; }
                        return 0;
                    });
                    this.blockUiService.hide();
            });
        });
    }


    findIndexToUpdate(newItem) {
        return newItem.id === this;
    }

    abrirDialog() {
        this.getFuncoesDados();
        //if (this.habilitarBotaoAbrirDialog()) {
        this.subscribeAnaliseCarregada();
        this.mostrarDialog = true;
        //}
    }

    habilitarBotaoAbrirDialog(): boolean {
        /*if (!this.funcoesDados) {
            return false;
        }
        return this.funcoesDados.length > 0;
        */
        return true;
    }

    funcoesDadosDropdownPlaceholder(): string {
        return this.getLabel('Selecione uma Função de Dados');
    }

    funcaoDadosSelected(fd: FuncaoDados) {
        this.funcaoDadosSelecionada = fd;
        this.derService.dropDownByFuncaoDadosId(fd.id).subscribe(res => {
            this.ders = res;
            if (!this.ders.some(der => (der.nome === 'Mensagem' || der.nome === 'Ação'))) {
                this.ders.push(this.derMsg, this.derAcao);
            }
        });
    }

    dersMultiSelectedPlaceholder(): string {
        if (!this.funcaoDadosSelecionada) {
            return this.getLabel('DERs - Selecione uma Função de Dados para selecionar quais DERs referenciar');
        }
        return this.getLabel('Selecione quais DERs deseja referenciar');

    }

    relacionar() {
        if (this.dersReferenciados) {
            this.dersReferenciados.forEach(der => {
                der.id = undefined;
            });
        }
        this.dersReferenciadosEvent.emit(this.dersReferenciados);
        // XXX vai precisar relacionar qual funcao de dados foi relacionada?
        this.funcaoDadosReferenciadaEvent.emit(this.funcaoDadosSelecionada.name);
        this.fecharDialog();
    }

    fecharDialog() {
        this.resetarCampos();
        this.mostrarDialog = false;
    }

    private resetarCampos() {
        this.funcaoDadosSelecionada = undefined;
        this.ders = [];
        this.dersReferenciados = [];
    }

    ngOnDestroy() {

    }

}
