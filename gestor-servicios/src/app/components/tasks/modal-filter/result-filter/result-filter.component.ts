import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Task } from '../../../../models/task';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-result-filter',
  imports: [CommonModule],
  templateUrl: './result-filter.component.html',
  styleUrl: './result-filter.component.scss'
})
export class ResultFilterComponent {

  @Input() listTask: Task[] = [];
  @Output() taskSelected = new EventEmitter<Task>();




  clickTarea(tarea: Task){
    this.taskSelected.emit(tarea);
  }


}
