import { Component, OnDestroy, OnInit } from '@angular/core';
import { ListTasksComponent } from './list-tasks/list-tasks.component';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { ModalTaskComponent } from "./modal-task/modal-task.component";
import { Router } from '@angular/router';
import { Subscribable, Subscription } from 'rxjs';
import { TaskEventsService } from '../../services/task-events.service';
import { animate, style, transition, trigger } from '@angular/animations';
import { ModalFilterComponent } from "./modal-filter/modal-filter.component";
import { ModalMaintenanceComponent } from "../maintenance/modal-maintenance.component";

@Component({
  selector: 'app-tasks',
  imports: [
    ListTasksComponent,
    CommonModule,
    ModalTaskComponent,
    ModalFilterComponent,
    ModalMaintenanceComponent
],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.scss',
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

export class TasksComponent implements OnInit{

  userId!: number;
  rolId!: number;
  seeModalNewTask: boolean = false;
  seeModalFilter: boolean = false;
  menuAbierto = false;
  seeModalMantenimientos: boolean = false;
  tasksActive: any[] = []; tasksProcess: any[] = []; tasksPaused: any[] = []; users: any[] = [];

  constructor(private api: ApiService, private authService: AuthService, private router: Router, private taskEvent: TaskEventsService){};

  /** Al arrancar la pagina, obtendremos todas las tareas que tiene el cliente y nos subscrbimos para al evento para refrescar tareas porque lo usarán otros componentes */
  ngOnInit(): void {
    this.rolId = Number(this.authService.getRolId());

    this.getTaskPending();

    this.taskEvent.refreshTasks$.subscribe(() => {
      this.getTaskPending();
    });

  

  }

  // Función para abrir el menú en resposive
  toggleMenu() {
    this.menuAbierto = !this.menuAbierto;
  }

/** Metodo para obtener las tareas del usuarios pendientes (Activas, En proceso y Pausadas)  */
  getTaskPending(){
    this.userId = Number(this.authService.getUserId());

    // Obtenemos las tareas del usuario
    this.api.getTasksByUserPending(this.userId).subscribe({
      next: (res) => {
        this.tasksActive = res.active;
        this.tasksProcess = res.process;
        this.tasksPaused = res.paused;

      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  /** Metodo para ocultar modal de nueva tarea, cuando se oculta se recargan las tareas */
  closeModal(){
    this.seeModalNewTask = false;
    this.seeModalFilter = false;
    this.seeModalMantenimientos = false;
    this.getTaskPending();
  }

  /** Metodo para abrir el modal de nueva tarea */
  openModal(){
    this.seeModalNewTask = true;
  }

  /** Metodo para abrir el modal de filtros */
  openFilter(){
    this.seeModalFilter = true;
  }

  /** Metodo para cerrar sesión */
  closeSession(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  openMantenimientos(){
    this.seeModalMantenimientos = true;
  }


 



}
