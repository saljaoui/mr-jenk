import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ApiClient } from '../../../../core/api/api-client.service';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../../../core/services/user.service';
import { ProductResponse, ProductService } from '../../../../shared/services/product-service';
import { ToastService } from '../../../../shared/services/toast-service';
import { SellerSidebarComponent } from '../../seller-sidebar/seller-sidebar.component';

@Component({
  selector: 'app-seller-product-list',
  imports: [RouterLink, SellerSidebarComponent],
  templateUrl: './seller-product-list.component.html',
  styleUrl: './seller-product-list.component.scss',
})
export class SellerProductListComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly productService = inject(ProductService);
  private readonly toastService = inject(ToastService);
  private readonly api = inject(ApiClient);

  user = signal<User | null>(null);
  products = signal<ProductResponse[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  initials = computed(() => this.getInitials(this.user()?.name ?? ''));

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user.set(user);
        this.productService.getProductsOwnedBy(user.id).subscribe({
          next: (products) => {
            this.products.set(products);
            this.isLoading.set(false);
          },
          error: (err) => {
            this.errorMessage.set(this.api.getErrorMessage(err, 'Unable to load your products.'));
            this.isLoading.set(false);
          },
        });
      },
      error: (err) => {
        this.errorMessage.set(this.api.getErrorMessage(err, 'Unable to load your seller account.'));
        this.isLoading.set(false);
      },
    });
  }

  deleteProduct(product: ProductResponse): void {
    if (!window.confirm(`Delete “${product.name}”? This cannot be undone.`)) return;
    this.productService.deleteProduct(product.id).subscribe({
      next: () => {
        this.toastService.success('Product deleted successfully.');
        this.products.update((products) => products.filter((item) => item.id !== product.id));
      },
      error: (err) =>
        this.toastService.error(this.api.getErrorMessage(err, 'Product could not be deleted.')),
    });
  }

  private getInitials(name: string): string {
    return (
      name
        .trim()
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0]?.toUpperCase() ?? '')
        .join('') || 'U'
    );
  }
}
