import { Component, inject, NgModule } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router'
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthRequest } from '../../models/authRequest';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  constructor(private router: Router, private api: ApiService, private authService: AuthService){};
  error: string | null = null;
  modoRegistro: boolean = false;
  userId: number | null = null;

  formulario = new FormGroup({
    userName: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('') // Por defecto sin validacion, se le asignará cuando se indique que queremos registarnos

  });

  // Cuando se envia el formulario, coprobamos si debemos logearnos o registarnos
  onSubmit(){
    if(this.modoRegistro){
      this.register();
    } else {

      const credentials: AuthRequest = {
      userName: this.formulario.value.userName!,
      password: this.formulario.value.password!
      };
      this.login(credentials);
    }
  }

  // Metodo que cambia el formulario para que se pueda reguistrar un usuario
  openRegister(){
    this.modoRegistro = !this.modoRegistro;
    this.formulario.get('confirmPassword')?.setValidators([Validators.required]);
  }

  // Funcion para logear un usuario
  login(credentials: AuthRequest){
    if(this.formulario.valid){ // Si no nos han rellenado los datos, error
      this.api.login(credentials).subscribe({ // Hacemos llamada a la Api para logear
        next: (res) => {
          console.log("Logeado");
          console.log(res);
          this.authService.setToken(res.token);
          this.authService.setUserId(res.userId);
          this.authService.setRolId(res.rolId);
          this.router.navigate(['/tareas']);

        },
        error: (err) => {
          console.log("Error al logear");
          this.error = err.error;
          this.formulario.reset();
        }
      });
    } else {
      this.error = "Rellene todos los campos";
    }
  }

  // Funcion para registrar un nuevo usuario, si se registra correctamente se logeará después en caso contrario devolverá error
  register(){
    if(this.formulario.valid){
      if(this.formulario.get('password')?.value === this.formulario.get('confirmPassword')?.value){
        
        const credentials: AuthRequest = {
          userName: this.formulario.value.userName!,
          password: this.formulario.value.password!
        };

        this.api.register(credentials).subscribe({ // Hacemos llamada a la Api para crear usuario
          next: (res) => {
            console.log("Usuario registrado");
            this.login(credentials);
          },
          error: (err) => {
            console.log("Error al crear usuario");
            this.error = err.error;
            this.formulario.reset();
          }
        });


      } else {
        this.error = "La contraseña no coincide";
      }

    }else {
      this.error = "Debe rellenar todos los campos";
    }

  }
   

}
