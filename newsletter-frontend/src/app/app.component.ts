import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NewsletterService } from './newsletter.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container">
      <h1>Newsletter Signup</h1>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label for="email">Email</label>
          <input id="email" type="email" formControlName="email" />

          <div class="error" *ngIf="form.controls.email.invalid && form.controls.email.touched">
            Please enter a valid email.
          </div>
        </div>

        <button type="submit" [disabled]="form.invalid || loading()">Subscribe</button>
      </form>

      <p class="success" *ngIf="success()">Thanks for subscribing!</p>
      <p class="error" *ngIf="error()">Something went wrong. Try again.</p>
    </div>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  private fb = inject(FormBuilder);
  private newsletter = inject(NewsletterService);

  loading = signal(false);
  success = signal(false);
  error = signal(false);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  async onSubmit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.success.set(false);
    this.error.set(false);

    try {
      await this.newsletter.subscribe(this.form.value.email!);
      this.success.set(true);
      this.form.reset();
    } catch {
      this.error.set(true);
    } finally {
      this.loading.set(false);
    }
  }
}
