import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpService, SecurityModule, AuthService } from '@basis/angular-components';
import {
    ButtonModule,
    InputTextModule,
    SpinnerModule,
    CalendarModule,
    DropdownModule,
    RadioButtonModule,
    ConfirmDialogModule,
    ConfirmationService,
    PasswordModule
} from 'primeng/primeng';

import { LoginComponent, loginRoute, LoginService } from './';

@NgModule({
    imports: [
        CommonModule,
        HttpModule,
        FormsModule,
        RouterModule.forRoot(loginRoute, { useHash: true }),
        ButtonModule,
        ButtonModule,
        SpinnerModule,
        CalendarModule,
        DropdownModule,
        RadioButtonModule,
        InputTextModule,
        ConfirmDialogModule,
        PasswordModule,
        SecurityModule.forRoot()
    ],
    declarations: [
        LoginComponent
    ],
    providers: [
        LoginService,
        AuthService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})

export class LoginModule {

}
