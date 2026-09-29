import { Component, Input, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { ProductService } from '../../../shared/services/product-service';

export type SellerNavItem = 'dashboard' | 'products' | 'create-product' | 'media' | 'profile';

@Component({
  selector: 'app-seller-nav',
  imports: [RouterLink],
  templateUrl: './seller-nav.component.html',
  styles: [
    `
      .seller-nav-logout {
        width: 100%;
        border: 0;
        border-left: 2px solid transparent;
        background: transparent;
        text-align: left;
        font: inherit;
        cursor: pointer;
      }

      .seller-nav-logout:focus-visible {
        outline: 2px solid var(--gold);
        outline-offset: -2px;
      }

      .seller-nav-logout svg {
        width: 18px;
        height: 18px;
        flex: 0 0 auto;
      }
    `,
  ],
})
export class SellerNavComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly productService = inject(ProductService);
  private readonly router = inject(Router);

  @Input() activeItem: SellerNavItem = 'dashboard';
  @Input() productCount: number | null = null;
  protected loadedProductCount = 0;

  protected get visibleProductCount(): number {
    return this.productCount ?? this.loadedProductCount;
  }

  protected logout(): void {
    this.authService.clearSession();
    void this.router.navigate(['/login']);
  }

  ngOnInit(): void {
    if (this.productCount !== null) return;
    const user = this.authService.getStoredUser();
    if (!user) return;
    this.productService.getProductsOwnedBy(user.id).subscribe({
      next: (products) => {
        this.loadedProductCount = products.length;
      },
      error: () => {
        this.loadedProductCount = 0;
      },
    });
  }
}
