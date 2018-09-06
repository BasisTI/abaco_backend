import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { ConfirmationService } from 'primeng/primeng';
import { DatatableComponent, DatatableClickEvent } from '@basis/angular-components';
import { environment } from '../../environments/environment';
import { Sistema } from './sistema.model';
import { SistemaService } from './sistema.service';
import { ElasticQuery, PageNotificationService } from '../shared';
import { Organizacao } from '../organizacao/organizacao.model';
import { OrganizacaoService } from '../organizacao/organizacao.service';
import { StringConcatService } from '../shared/string-concat.service';
import { BlockUI, NgBlockUI } from 'ng-block-ui';
import { MessageUtil } from '../util/message.util';
import { Response } from '@angular/http';


@Component({
  // tslint:disable-next-line:component-selector
  selector: 'jhi-sistema',
  templateUrl: './sistema.component.html'
})
export class SistemaComponent implements AfterViewInit {

  @BlockUI() blockUI: NgBlockUI;

  @ViewChild(DatatableComponent) datatable: DatatableComponent;

  searchUrl: string = this.sistemaService.searchUrl;

  sistemaSelecionado: Sistema;

  rowsPerPageOptions: number[] = [5, 10, 20];

  paginationParams = { contentIndex: null };
  elasticQuery: ElasticQuery = new ElasticQuery();
  organizations: Array<Organizacao>;
  searchParams: any = {
    sigla: undefined,
    nomeSistema: undefined,
    organizacao: {
      nome: undefined
    }
  };

  fieldName: string;

  constructor (
    private router: Router,
    private sistemaService: SistemaService,
    private confirmationService: ConfirmationService,
    private organizacaoService: OrganizacaoService,
    private stringConcatService: StringConcatService,
    private pageNotificationService: PageNotificationService
  ) {
    let emptyOrganization = new Organizacao();

    emptyOrganization.nome = '';
    this.organizacaoService.query().subscribe(response => {
      this.organizations = response.json;
      this.organizations.unshift(emptyOrganization);
    });
  }

  public ngOnInit(){
    this.datatable.pDatatableComponent.onRowSelect.subscribe((event) => {
      this.sistemaSelecionado = event.data;
    });
  this.datatable.pDatatableComponent.onRowUnselect.subscribe((event) => {
    this.sistemaSelecionado = undefined;
  });
  }

  /**
   *
   */
  public ngAfterViewInit() {
    this.recarregarDataTable();
    
  }

  /**
   *
   */
  public datatableClick(event: DatatableClickEvent) {
    if (!event.selection) {
      return;
    }
    switch (event.button) {
      case 'edit':
        this.router.navigate(['/sistema', event.selection.id, 'edit']);
        break;
      case 'delete':
        this.confirmDelete(event.selection.id);
        break;
      case 'view':
        this.router.navigate(['/sistema', event.selection.id]);
        break;
    }
  }

  public onRowDblclick(event) {
    
    if (event.target.nodeName === 'TD') {
      this.abrirEditar();
    }else if (event.target.parentNode.nodeName === 'TD') {
      this.abrirEditar();
    }
}

abrirEditar(){
  this.router.navigate(['/sistema', this.sistemaSelecionado.id, 'edit']);
}

  /**
   *
   * @param id
   */
  public confirmDelete(id: any) {
    this.confirmationService.confirm({
      message: MessageUtil.CONFIRMAR_EXCLUSAO,
      accept: () => {
        this.blockUI.start(MessageUtil.EXCLUINDO_REGISTRO);
        this.sistemaService.delete(id).subscribe(() => {
          this.limparPesquisa();
          this.pageNotificationService.addDeleteMsg();
          this.blockUI.stop();
        }, (error: Response) => {
            if (error.headers.toJSON()['x-abacoapp-error'][0] === 'error.analiseexists') {
              this.pageNotificationService.addErrorMsg('O sistema está vinculado a uma análise e não pode ser excluído!');
            }
          }
        );
      }
    });
  }

public switchUrlSigla() {

  if (((this.searchParams.sigla === undefined) || (this.searchParams.sigla === '')) && 
      ((this.searchParams.nomeSistema === undefined) || (this.searchParams.nomeSistema === '')) && 
      ((this.searchParams.organizacao.nome === undefined) || (this.searchParams.organizacao.nome === ''))) {

    this.searchUrl = this.sistemaService.fieldSearchSiglaUrl;
    } else {
      this.searchUrl = this.sistemaService.searchUrl;
    }

}

public switchUrlNomeSistema() {
  if (((this.searchParams.nomeSistema === undefined) || (this.searchParams.nomeSistema === '')) && 
      ((this.searchParams.sigla === undefined) || (this.searchParams.sigla === '')) && 
      ((this.searchParams.organizacao.nome === undefined) || (this.searchParams.organizacao.nome === ''))){
    
    this.searchUrl = this.sistemaService.fieldSearchSistemaUrl;

  } else {

         this.searchUrl = this.sistemaService.searchUrl;
        }

}

public switchUrlOrganizacao() {

  if (((this.searchParams.organizacao.nome === undefined) || (this.searchParams.organizacao.nome !== '')) && 
      ((this.searchParams.nomeSistema === undefined) || (this.searchParams.nomeSistema === '')) && 
      ((this.searchParams.sigla === undefined) || (this.searchParams.sigla === ''))) {

    this.searchUrl = this.sistemaService.fieldSearchOrganizacaoUrl;
    
  } else {
      this.searchUrl = this.sistemaService.searchUrl;
    }
 
}

// public switchSearchUrl() {
//   if (this.searchParams.sigla !== undefined && ((this.searchParams.nomeSistema !== undefined) || (this.searchParams.organizacao.nome !== undefined))) {
//     this.searchUrl = this.sistemaService.searchUrl;
//   }
// }

  /**
   *
   */
  private checkUndefinedParams() {
    (this.searchParams.sigla === '') ? (this.searchParams.sigla = undefined) : (this);
    (this.searchParams.nomeSistema === '') ? (this.searchParams.nomeSistema = undefined) : (this);
    (this.searchParams.organizacao.nome === '') ? (this.searchParams.organizacao.nome = undefined) : (this);
  }

  /**
   *
   */
  public performSearch() {
  
    this.searchUrl = this.sistemaService.searchUrl;
    this.checkUndefinedParams();
    this.elasticQuery.value = this.stringConcatService.concatResults(this.createStringParamsArray());
    this.recarregarDataTable();

  }
  
  /**
   *
   */
  private createStringParamsArray(): Array<string> {
    let stringParamsArray: Array<string> = [];

    (this.searchParams.sigla !== undefined) ? (stringParamsArray.push(this.searchParams.sigla)) : (this);
    (this.searchParams.nomeSistema !== undefined) ? (stringParamsArray.push(this.searchParams.nomeSistema)) : (this);
    (this.searchParams.organizacao.nome !== undefined) ? (stringParamsArray.push(this.searchParams.organizacao.nome)) : (this);

    return stringParamsArray;
  }

  /**
   * 
   */
  private checkFields(): void {
    
      if ((this.searchParams.nomeSistema === undefined) && ((this.searchParams.organizacao.nome === undefined) || (this.searchParams.organizacao.nome === ''))) {
        this.searchUrl = this.sistemaService.fieldSearchSiglaUrl;

      } else if ((this.searchParams.sigla === undefined) && ((this.searchParams.organizacao.nome === undefined) || (this.searchParams.organizacao.nome === ''))) {
        this.searchUrl = this.sistemaService.fieldSearchSistemaUrl;

      } else if ((this.searchParams.sigla === undefined) && (this.searchParams.nomeSistema === undefined)) {
        this.searchParams.organizacao.nome = this.sistemaService.searchUrl;
      }

      this.recarregarDataTable();
      
  }

  /**
   *
   */
  public limparPesquisa() {
    this.searchParams.sigla = '';
    this.searchParams.organizacao = '';
    this.searchParams.nomeSistema = '';
    this.elasticQuery.reset();
    this.searchUrl = this.sistemaService.searchUrl;
    this.recarregarDataTable();
    
  }

  /**
   *
   */
  public recarregarDataTable() {
    this.datatable.refresh(this.elasticQuery.query);
  }
}
