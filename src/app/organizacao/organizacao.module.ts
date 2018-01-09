import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatatableModule } from '@basis/angular-components';
import { MemoryDataTableModule } from '../memory-datatable/memory-datatable.module';
import {
  ButtonModule,
  InputTextModule,
  SpinnerModule,
  CalendarModule,
  DropdownModule,
  RadioButtonModule,
  ConfirmDialogModule,
  ConfirmationService,
  DataTableModule,
  DialogModule,
  CheckboxModule
} from 'primeng/primeng';

import {
  OrganizacaoService,
  OrganizacaoComponent,
  OrganizacaoDetailComponent,
  OrganizacaoFormComponent,
  organizacaoRoute
} from './';

@NgModule({
  imports: [
    CommonModule,
    HttpModule,
    FormsModule,
    RouterModule.forRoot(organizacaoRoute, { useHash: true }),
    DatatableModule,
    ButtonModule,
    SpinnerModule,
    CalendarModule,
    DropdownModule,
    RadioButtonModule,
    InputTextModule,
    ConfirmDialogModule,
    DataTableModule,
    DialogModule,
    MemoryDataTableModule,
    CheckboxModule
  ],
  declarations: [
    OrganizacaoComponent,
    OrganizacaoDetailComponent,
    OrganizacaoFormComponent
  ],
  providers: [
    OrganizacaoService,
    ConfirmationService
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AbacoOrganizacaoModule {}
