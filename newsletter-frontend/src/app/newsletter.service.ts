import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NewsletterService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/subscribe';

  subscribe(email: string): Promise<void> {
    return firstValueFrom(
      this.http.post<void>(this.apiUrl, { email })
    );
  }
}
