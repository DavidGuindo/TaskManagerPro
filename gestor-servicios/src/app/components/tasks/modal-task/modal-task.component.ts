import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Task } from '../../../models/task';
import { Process } from '../../../models/process';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { TaskEventsService } from '../../../services/task-events.service';

@Component({
  selector: 'app-modal-task',
  imports:[
    CommonModule, ReactiveFormsModule
  ],
  templateUrl: './modal-task.component.html',
  styleUrl: './modal-task.component.scss'
})
export class ModalTaskComponent implements OnInit{
  
  @Input() taskToUpdate?: Task;
  @Output() cerrarModal = new EventEmitter<void>();
  
  today: Date = new Date;

  listUsers: any[] = []; listStates: any[] = []; listDpt: any[] = []; listProcess: Process[] = [];
  seeNewProcess: boolean = false;
  seeButtonNewProcess: boolean = true;
  expandedProcesses: boolean[] = [];
  
  constructor(private api: ApiService, private authService: AuthService, private router: Router, private tasksEvent: TaskEventsService){};


  /** 
   * Al arrancar el modulo obtenemos los usuarios, estados y departamentos que existen
   * Tambien seteamos el formulario con los datos por defecto que tengamos en la tarea en el caso de que estemos abriendo una tarea existente
   * 
   */
  ngOnInit(): void {
    // Obtenemos los usuarios, estados y departamentos TODO: Hacerlo con un servicio
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
    
    // ¿Estamos abriendo una tarea?
    if(this.taskToUpdate) {
       // Asignamos los valores de la tarea que nos han pasado al formulario
      this.formTask.patchValue({
        user: String(this.taskToUpdate?.ownerID),
        state: String(this.taskToUpdate?.stateID),
        dpt: String(this.taskToUpdate?.dptID),
        description: this.taskToUpdate?.description
      });

      // Deshabilitamos formulario si:
      //  - La tarea tiene estado finalizado o anulado
      //  - El usuario no es administrador (1) y no es una tarea asignada a él
      if( (this.taskToUpdate?.stateID == 4 || this.taskToUpdate?.stateID == 5) || (Number(this.authService.getRolId()) != 1 && this.taskToUpdate?.ownerID != Number(this.authService.getUserId())) ){
        this.seeButtonNewProcess = false;
        this.formTask.disable();
      }

      // Inicializar el array de procesos expandidos
      this.expandedProcesses = new Array(this.taskToUpdate?.processDtos?.length).fill(false);
    }
    console.log(this.taskToUpdate);

  }

  // Formulario para guardar la tarea
  formTask = new FormGroup({
    user: new FormControl('', [Validators.required]),
    state: new FormControl('', [Validators.required]),
    dpt: new FormControl('', [Validators.required]),
    description: new FormControl(''),
    newProcess: new FormControl('') 

  });

  /** Método para alternar la expansión de un proceso */
  toggleProcessExpansion(index: number): void {
    if (this.expandedProcesses[index] !== undefined) {
      this.expandedProcesses[index] = !this.expandedProcesses[index];
    }
  }

 /**  Metodo que obtiene la informacion del formulario y llama a la API para crear o actualizar la tarea */
  onSubmit(){
    if ( this.formTask.valid ){

      // Si tenemos una tarea, la actualizamos
      if(this.taskToUpdate){

        //Actualizamos los datos de la tarea antes de pasarla a la API
        this.taskToUpdate.description = String(this.formTask.value.description);
        this.taskToUpdate.dptID = Number(this.formTask.value.dpt);
        this.taskToUpdate.ownerID = Number(this.formTask.value.user);
        this.taskToUpdate.stateID = Number(this.formTask.value.state);
        this.taskToUpdate.processDtos = [];

        // Si el tenemos un nuevo proceso se lo pasamos, en caso contrario se pasará la lista de procesos vacia
        if(this.formTask.value.newProcess != ''){
          const newProcess: Process = {
            id: null,
            authorID: Number(this.authService.getUserId()),
            authorName: null,
            date: null,
            description: this.formTask.value.newProcess!
          };
          this.taskToUpdate.processDtos = [newProcess];
        }


        //Actualizamos la tarea
        this.api.updateTask(this.taskToUpdate).subscribe({
          next: (res) => {
            console.log("Tarea actualizada correctamente");
            this.cerrar();
          },
          error: (err) => {
            console.log("Error al actualizar tarea");
            console.log(err);

          }
        })

      
      } else { // Sino hay tarea previa, es que tenemos que crear una nueva

        // Creamos el nuevo proceso, si lo hay
        if(this.formTask.value.newProcess != ''){
          const newProcess: Process = {
            id: null,
            authorID: null,
            authorName: null,
            date: null,
            description: this.formTask.value.newProcess!
          };
          this.listProcess.push(newProcess);
        }

        // Creamos la tarea, y se la pasamos a la api.
        const taskNew: Task = {
          id: null,
          stateID: Number(this.formTask.value.state)!,
          stateName: '',
          authorID: Number(this.authService.getUserId()),
          authorName: '',
          ownerID: Number(this.formTask.value.user!),
          ownerName: '',
          dptID: Number(this.formTask.value.dpt!),
          dptName:'',
          dateIni: null,
          dateEnd: null,
          description: this.formTask.value.description!,
          processDtos: this.listProcess
        };

        this.api.newTask(taskNew).subscribe({
          next: (res) => {
            console.log("Tarea creada");
            this.cerrar();
          },
          error: (err) => { console.log(err); }
        });
      }

    } else {
      console.log("Debe rellenar los campos minimos para la tarea");
    }
  }


  /** Metodo que muestra la opcion para crear un nuevo proceso */
  openNewProcess(){
    this.seeNewProcess = !this.seeNewProcess;
  }
  
  /** Funcion que emite una señal para cerrar el modal y recarga la lista de tareas*/
  cerrar(){
    this.cerrarModal.emit();
    this.tasksEvent.emitRechargeTasks();
  }




}
