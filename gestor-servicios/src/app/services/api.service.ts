import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthRequest } from '../models/authRequest';
import { Task } from '../models/task';
import { Filter } from '../models/filter';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService { 

  private url = "http://localhost:8080/api";

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Método para obtener headers con el token
  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  /** USUARIOS */
  
  /** Registrar usuario */
  register(user: AuthRequest): Observable<any> {
    return this.http.post(`${this.url}/user/register`,user, { responseType: 'text'});
  }

  /** Logear usuario */
  login(user: AuthRequest): Observable<any> {
    return this.http.post(`${this.url}/user/login`,user);
  }

  /** Obtener todos los usuarios de bd */
  getUsers(): Observable<any> {
    return this.http.get(`${this.url}/user/getAll`, { headers: this.getHeaders() });  
  }

  /** Actualizar usuario */
  updateUser(user: any): Observable<any> {
    return this.http.post(`${this.url}/user/update`, user, { responseType: 'text', headers: this.getHeaders() });
  }

  /** Borrar usuario */
  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.url}/user/delete/${id}`, { responseType: 'text', headers: this.getHeaders() });
  }

  /** Obtener todos los roles de bd */
  getAllRoles(): Observable<any> {
    return this.http.get(`${this.url}/user/getAllRoles`, { headers: this.getHeaders() });  
  }


  

  /** ESTADOS */

  /** Obtener todos los estados de bd */
  getStates(): Observable<any> {
    return this.http.get(`${this.url}/state/getAll`, { headers: this.getHeaders() });
  }

  /** Crear estado */
  createState(state: any): Observable<any> {
    return this.http.post(`${this.url}/state/new`, state,  { responseType: 'text', headers: this.getHeaders() });
  }

  /** Actualizar estado */
  updateState(state: any): Observable<any> {
    return this.http.put(`${this.url}/state/update`, state,  { responseType: 'text', headers: this.getHeaders() });
  }

  /** Borrar estado */
  deleteState(id: number): Observable<any> {
    return this.http.delete(`${this.url}/state/delete/${id}`,  { responseType: 'text', headers: this.getHeaders() });
  }

  /** DEPARTAMENTOS */

  /** Obtener todos los departamentos de bd */
  getDepartment(): Observable<any> {
    return this.http.get(`${this.url}/department/getAll`, { headers: this.getHeaders() });
  }

  /** Crear departamento */
  createDepartment(department: any): Observable<any> {
    return this.http.post(`${this.url}/department/new`, department,  { responseType: 'text', headers: this.getHeaders() });
  }

  /** Actualizar departamento */
  updateDepartment(department: any): Observable<any> {
    return this.http.post(`${this.url}/department/update`, department,  { responseType: 'text', headers: this.getHeaders() });
  }

  /** Borrar departamento */
  deleteDepartment(id: number): Observable<any> {
    return this.http.delete(`${this.url}/department/delete/${id}`,  { responseType: 'text', headers: this.getHeaders() });
  }


  /** TAREAS  */

  /** Obtener las tareas pendientes (Activas, En proceso y Pausadas) de un usuario */
  getTasksByUserPending(userId: number): Observable<any> {
    return this.http.get(`${this.url}/task/getAllByUserPending/${userId}`, { headers: this.getHeaders() });
  }

  /** Crear una nueva tarea */
  newTask(task: Task): Observable<any>{
    return this.http.post(`${this.url}/task/new`, task, { responseType: 'text', headers: this.getHeaders() });
  }

  /** Actualizar una tarea */
  updateTask(task: Task): Observable<any>{
    return this.http.post(`${this.url}/task/update`, task, {responseType: 'text', headers: this.getHeaders()});
  }

  /** Obtener tareas filtradas */
  taskFilter(filterData: Filter): Observable<any>{
    return this.http.post(`${this.url}/task/filter`, filterData, { headers: this.getHeaders() })
  }


}

