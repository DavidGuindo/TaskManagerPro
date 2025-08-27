import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'auth_token';

  constructor() { }

  // Guarda el token
  setToken(token: string){
    localStorage.setItem(this.tokenKey, token);
  }

  // Guarda la Id de usuario
  setUserId(userId: string){
    localStorage.setItem('userId', userId);
  }

  // Guarda la Id de rol
  setRolId(rolId: string){
    localStorage.setItem('rolId', rolId);
  }

  // Obtiene el token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  // Obtiene el userId
  getUserId(): string | null {
    return localStorage.getItem('userId');
  }
  // Obtiene el userId
  getRolId(): string | null {
    return localStorage.getItem('rolId');
  }

  // Comprueba si esta la sesion iniciada
  isLoggedIn(): boolean{
    return !!this.getToken();
  }

  // Cierra sesion
  logout(){
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('userId');
  }
}
