import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { AuthorizationService } from '@nuvem/angular-base';
import { PageNotificationService } from '@nuvem/primeng-components';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class SenhaService {

    private changeUrl = environment.apiUrl + '/account/change_password';

    constructor(private http: HttpClient, private authService: AuthorizationService,
        private pageNotificationService: PageNotificationService) { }

 
getLabel(label) {
    return label;
}

changePassword(newPassword: string): Observable < any > {
    return this.http.post(this.changeUrl, newPassword).pipe(catchError((error: any) => {
            if (error.status === 403) {
                this.pageNotificationService.addErrorMessage(this.getLabel('Você não possui permissão!'));
                return Observable.throw(new Error(error.status));
            }
        }));
}

getLogin(): Observable <any> {
    return this.http.request('GET', 'api/authenticate', {responseType: 'text'}).pipe(catchError((error: any) => {
        if (error.status === 403) {
            this.pageNotificationService.addErrorMessage(this.getLabel('Você não possui permissão!'));
            return Observable.throw(new Error(error.status));
        }
    }));
}
}
