import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { animate, style, transition, trigger } from '@angular/animations';
import { FilterTasksComponent } from "./filter-tasks/filter-tasks.component";
import { ResultFilterComponent } from "./result-filter/result-filter.component";
import { Task } from '../../../models/task';
import { ModalTaskComponent } from '../modal-task/modal-task.component';

@Component({
  selector: 'app-modal-filter',
  imports: [CommonModule, FilterTasksComponent, FilterTasksComponent, ResultFilterComponent, ModalTaskComponent],
  templateUrl: './modal-filter.component.html',
  styleUrl: './modal-filter.component.scss',
  animations: [
    trigger('fade', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('200ms ease-in', style({ opacity: 1 })),
      ]),
      transition(':leave', [
        animate('200ms ease-out', style({ opacity: 0 })),
      ]),
    ]),
  ],
})
export class ModalFilterComponent {

  @Output() cerrarModal = new EventEmitter<void>();
  @Output() taskSelectedFromFilter = new EventEmitter<Task>();
  
  results: Task[] = [];
  selectedTask?: Task;
  seeModalTask: boolean = false;

  constructor(){}

  sendResults(listTask:Task[]){
    this.results = listTask;
  }

  openModalTask(tarea: Task){
    this.selectedTask = tarea;
    this.seeModalTask = true;
    this.taskSelectedFromFilter.emit(tarea);
  }
  
  closeModalTask(){
    this.seeModalTask = false;
    this.selectedTask = undefined;
  }

  cerrar(){
    this.cerrarModal.emit();
  }

}
