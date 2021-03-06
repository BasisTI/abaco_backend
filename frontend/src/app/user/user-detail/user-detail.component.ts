import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import { UserService } from '../user.service';
import { User } from '../user.model';
import { Subscription } from 'rxjs';


@Component({
    selector: 'jhi-user-detail',
    templateUrl: './user-detail.component.html'
})
export class UserDetailComponent implements OnInit, OnDestroy {

    user: User;
    private subscription: Subscription;

    constructor(
        private userService: UserService,
        private route: ActivatedRoute,
    ) {
    }

    getLabel(label) {
        return label;
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
    }

    load(id) {
        this.userService.find(id).subscribe((user) => {
            this.user = user;
        });

    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
}
