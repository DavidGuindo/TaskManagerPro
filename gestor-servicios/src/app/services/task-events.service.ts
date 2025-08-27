import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TaskEventsService {

  constructor() { }


  private refreshTaskSource = new Subject<void>();
  refreshTasks$ = this.refreshTaskSource.asObservable();

  emitRechargeTasks(){
    this.refreshTaskSource.next();  
  }

}
