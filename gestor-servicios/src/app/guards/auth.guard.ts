import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {

  const platformId = inject(PLATFORM_ID);
  const router = inject(Router);
  const authService: AuthService = new AuthService;

  if(isPlatformBrowser(platformId)){
    const token = authService.getToken();
    if (token) {
      return true;
    }
  }

  // Si no hay token, redirige al login
  return router.createUrlTree(['/login']);


};
