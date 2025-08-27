export interface User {
    id: number,
    nombre: string;
    password: string;
    departmentsID: number[]; // IDs de los departamentos asociados
}
