import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { Task } from '../../../models/task';
import { CommonModule } from '@angular/common';
import { trigger, state, style, animate, transition } from '@angular/animations';
import { ModalTaskComponent } from '../modal-task/modal-task.component';


@Component({
  selector: 'app-list-tasks',
  imports:[
    CommonModule,
    ModalTaskComponent,
  ],
  templateUrl: './list-tasks.component.html',
  styleUrl: './list-tasks.component.scss',
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

export class ListTasksComponent implements OnInit{
  @Input() tasksActive: any[] = [];
  @Input() tasksProcess: any[] = [];
  @Input() tasksPaused: any[] = [];


  taskSelected?: Task;
  seeModalTask: boolean = false;
  columnasAbiertas = [false, false, false];

  ngOnInit(): void {
    console.log(this.tasksActive);
  }


  toggleColumna(idx: number) {
    this.columnasAbiertas[idx] = !this.columnasAbiertas[idx];
  }

 /** Metodo que abre el modal para ver los datos de la tarea seleccionada */
 clickTarea(tarea: Task){
    console.log('Has hecho click');
    this.seeModalTask = true;
    this.taskSelected = tarea;
  }
 
  /** Metodo cierra el modal de tarea */
  closeModal(){
    this.seeModalTask = false;
  }
}
    