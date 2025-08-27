import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient } from '@angular/common/http';

const bootstrap = () => bootstrapApplication(AppComponent,
  { 
  providers: [
    provideAnimations(), // Activa las animaciones
    provideAnimationsAsync(),
    provideHttpClient(),
    config.providers
  ]
});

export default bootstrap;
