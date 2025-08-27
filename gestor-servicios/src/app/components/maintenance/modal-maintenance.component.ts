import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { User } from '../../models/user';
import { State } from '../../models/state';
import { Department } from '../../models/department';
import { AuthRequest } from '../../models/authRequest';


@Component({
  selector: 'app-modal-maintenance',
  templateUrl: './modal-maintenance.component.html',
  styleUrls: ['./modal-maintenance.component.scss'],
  imports: [FormsModule, CommonModule]
})
export class ModalMaintenanceComponent implements OnInit {
  @Output() cerrarModal = new EventEmitter<void>();
  menuOptions = ['Usuarios', 'Departamentos', 'Estados'];
  selectedMenu = 'Usuarios';
  items: any[] = [];
  selectedItem: any = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  departments: any[] = []; // Lista de departamentos para el checklist
  roles: any[] = []; // Lista de roles para el checklist

  constructor(private api: ApiService) {}

  /** Al arrancar el componente, se cargan los items del menu por defecto que es el de usuarios*/
  ngOnInit() {
    this.loadItems();
  }

  /** Función para seleccionar el menu */
  selectMenu(option: string) {
    this.selectedMenu = option;
    this.selectedItem = null;
    this.loadItems();
    if (option === 'Usuarios') {
      this.api.getDepartment().subscribe(data => this.departments = data);
    }
  }

  /** Función para cargar los items de cada menu */ // TODO: Hacerlo con un servicio
  loadItems() {
    if (this.selectedMenu === 'Usuarios') {
      this.api.getUsers().subscribe(data => this.items = data);
      this.api.getDepartment().subscribe({
        next: (data) => {
          this.departments = data;
        },
        error: (err) => {
          console.error('Error al cargar departamentos:', err);
          this.departments = [];
        }
      });

      this.api.getAllRoles().subscribe({
        next: (data) => {
          this.roles = data;
        },
        error: (err) => {
          console.error('Error al cargar roles:', err);
          this.departments = [];
        }
      });

    } else if (this.selectedMenu === 'Departamentos') {
      this.api.getDepartment().subscribe(data => this.items = data);
    } else if (this.selectedMenu === 'Estados') {
      this.api.getStates().subscribe(data => this.items = data);
    }
    console.log(this.items);

  }

  /** Función para seleccionar un item */
  selectItem(item: any) {
    this.selectedItem = { ...item };
    // Si es usuario, aseguramos que departmentsID sea un array
    if (this.selectedMenu === 'Usuarios') {
      if (!this.selectedItem.departmentsID) {
        this.selectedItem.departmentsID = [];
      }
    }
  }

  /** Función para añadir un item */
  addItem() {
    if (this.selectedMenu === 'Usuarios') {
      this.selectedItem = { id: 0, userName: '', password: '', rolID: '', departmentsID: [] };
    } else if (this.selectedMenu === 'Departamentos') {
      this.selectedItem = { id: 0, nombre: '' };
    } else if (this.selectedMenu === 'Estados') {
      this.selectedItem = { id: 0, nombre: '' };
    }
  }

  /** Función para eliminar un item */
  deleteItem(item: any) {
    if (!item) return;
    if (this.selectedMenu === 'Usuarios') {
      this.api.deleteUser(item.id).subscribe({
        next: (res) => {
          this.successMessage = res;
          console.log("res",res);
          setTimeout(() => {
            this.successMessage = null
          }, 2000);
          this.loadItems()
        },
        error: (err) => {
          this.errorMessage = err.error;
          console.log("res",err);
          setTimeout(() => {
            this.errorMessage = null
          }, 2000);
        }
      });
        
    } else if (this.selectedMenu === 'Departamentos') {
      this.api.deleteDepartment(item.id).subscribe({
        next: (res) => {
          this.successMessage = res;
          console.log("res",res);
          setTimeout(() => {
            this.successMessage = null
          }, 2000);
          this.loadItems()
        },
        error: (err) => {
          this.errorMessage = err.error;
          console.log("res",err);
          setTimeout(() => {
            this.errorMessage = null
          }, 2000);
        }
      });
    } else if (this.selectedMenu === 'Estados') {
      this.api.deleteState(item.id).subscribe({
        next: (res) => {
          this.successMessage = res;
          console.log("res",res);
          setTimeout(() => {
            this.successMessage = null
          }, 2000);
          this.loadItems()
        },
        error: (err) => {
          this.errorMessage = err.error;
          console.log("res",err);
          setTimeout(() => {
            this.errorMessage = null
          }, 2000);
        }
      });
    }
    this.selectedItem = null;
  }

  /** Función para guardar un item */
  saveItem(item: any) {
    if (this.selectedMenu === 'Usuarios') {
      // Actualiza
      if (item.id && item.id !== 0) {
        console.log("Usuario a guardar/actualizar");
        console.log(item);
        this.api.updateUser(item).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("res",res);
            setTimeout(() => {
              this.successMessage = null
            }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("res",err);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          }
        });
      // Crea
      } else {
        const credentials: AuthRequest = {
          userName: item.userName,
          password: item.password,
          departmentsID: item.departmentsID,
          rolID: item.rolID
        };

        // Se crea con rol por defecto
        this.api.register(credentials).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("res",res);
            console.log("msj", this.successMessage);
          setTimeout(() => {
            this.successMessage = null
          }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("res",err);
            console.log("msj", this.errorMessage);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          } 
        });
          
      }
    } else if (this.selectedMenu === 'Departamentos') {
      // Actualiza
      if (item.id && item.id !== 0) {
        this.api.updateDepartment(item).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("res",res);
            setTimeout(() => {
              this.successMessage = null
            }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("res",err);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          }
        });
      // Crea
      } else {
        this.api.createDepartment(item).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("msj", this.successMessage);
            setTimeout(() => {
              this.successMessage = null
            }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("msj", this.errorMessage);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          }
        });
      }
    } else if (this.selectedMenu === 'Estados') {
      // Actualiza
      if (item.id && item.id !== 0) {
        this.api.updateState(item).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("msj", this.successMessage);
            setTimeout(() => {
              this.successMessage = null
            }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("msj", this.errorMessage);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          }
        });
      // Crea
      } else {
        this.api.createState(item).subscribe({
          next: (res) => {
            this.successMessage = res;
            console.log("msj", this.successMessage);
            setTimeout(() => {
              this.successMessage = null
            }, 2000);
            this.loadItems()
          },
          error: (err) => {
            this.errorMessage = err.error;
            console.log("msj", this.errorMessage);
            setTimeout(() => {
              this.errorMessage = null
            }, 2000);
          }
        });
      }
    }
    this.selectedItem = null;
  }

  /** Función para cerrar el modal */
  cerrar() {
    this.cerrarModal.emit();
  }

  // Función para controlar los departamentos seleccionados de un nuevo usuario
  onDepartmentCheck(event: any, deptId: number) {
    const id = Number(deptId);
    if (event.target.checked) {
      if (!this.selectedItem.departmentsID.includes(id)) {
        this.selectedItem.departmentsID.push(id);
      }
    } else {
      this.selectedItem.departmentsID = this.selectedItem.departmentsID.filter((d: number) => d !== id);
    }
  }
}
