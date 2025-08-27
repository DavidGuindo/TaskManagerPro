import { CommonModule } from '@angular/common';
import { Component, OnInit, Output } from '@angular/core';
import { ApiService } from '../../../../services/api.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Filter } from '../../../../models/filter';
import { EventEmitter } from '@angular/core';
import { Task } from '../../../../models/task';
@Component({
  selector: 'app-filter-tasks',
  imports:[CommonModule, ReactiveFormsModule],
  templateUrl: './filter-tasks.component.html',
  styleUrl: './filter-tasks.component.scss'
})
export class FilterTasksComponent implements OnInit{

  constructor(private api: ApiService){}

  listUsers: any[] = []; listStates: any[] = []; listDpt: any[] = [];
  @Output() emitResult = new EventEmitter<Task[]>();


 // Formulario para el filtro
  formFilter = new FormGroup({
    author: new FormControl(''),
    user: new FormControl(''),
    state: new FormControl(''),
    dpt: new FormControl(''),
    dateIniStart: new FormControl(''),
    dateIniEnd: new FormControl(''), 
    dateFinishStart: new FormControl(''), 
    dateFinishEnd: new FormControl('') 

  });

  onSumbitFilter(){
    //Creamos el objeto con los datos del filtro
    const filterData: Filter = {
      authorID : this.formFilter.value.author ? Number(this.formFilter.value.author) : null,
      ownerID: Number(this.formFilter.value.user) ? Number(this.formFilter.value.user) : null,
      stateID: Number(this.formFilter.value.state) ? Number(this.formFilter.value.state) : null,
      departmentID: Number(this.formFilter.value.dpt) ? Number(this.formFilter.value.dpt) : null,
      dateCreationIni: this.formFilter.value.dateIniStart ? new Date(this.formFilter.value.dateIniStart) : null,
      dateCreationEnd: this.formFilter.value.dateIniEnd ? new Date(this.formFilter.value.dateIniEnd) : null,
      dateEndingIni: this.formFilter.value.dateFinishStart ? new Date(this.formFilter.value.dateFinishStart) : null,
      dateEndingEnd: this.formFilter.value.dateFinishEnd ? new Date(this.formFilter.value.dateFinishEnd) : null
    };

    this.api.taskFilter(filterData).subscribe({
      next: (res) => {
        this.emitResult.emit(res);

      },
      error: (err) => {console.log(err)}
    });

  }

  sendResult(listTask:Task[]){
    this.emitResult.emit(listTask);
  }

  /** Al arrancar el modulo obtenemos los usuarios, estados y departamentos que existen */
  ngOnInit(): void {
    this.api.getUsers().subscribe({
      next: (res) => {this.listUsers = res;},
      error: (err) => {console.log(err);}
    });

    this.api.getStates().subscribe({
      next: (res) => {this.listStates = res;},
      error: (err) => {console.log(err);}
    })

    this.api.getDepartment().subscribe({
      next: (res) => {this.listDpt = res;},
      error: (err) => {console.log(err)}
    });
  

  }

}
