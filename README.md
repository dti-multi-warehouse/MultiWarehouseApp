# Alpha March - Multi-Warehouse E-Commerce Platform

## Table of Contents
- [Alpha March - Multi-Warehouse E-Commerce Platform](#alpha-march---multi-warehouse-e-commerce-platform)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Platform Description](#platform-description)
    - [Main Features](#main-features)
  - [Tech Stack](#tech-stack)
    - [Backend](#backend)
    - [Frontend](#frontend)
  - [Features](#features)
    - [Backend Features](#backend-features)
    - [Frontend Features](#frontend-features)
      - [User Pages](#user-pages)
      - [Dashboard Pages](#dashboard-pages)
  - [Installation](#installation)
    - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
    - [Backend](#backend-1)
    - [Frontend](#frontend-1)
  - [Project Structure](#project-structure)
    - [Backend](#backend-2)
    - [Frontend](#frontend-2)
  - [Endpoints Documentation](#endpoints-documentation)
    - [Product Endpoints](#product-endpoints)
    - [Cart Endpoints](#cart-endpoints)
    - [Order Endpoints](#order-endpoints)
  - [Authors](#authors)

## Overview
Alpha March is a multi-warehouse e-commerce platform designed to enhance shipping efficiency and reduce delivery costs. By utilizing multiple warehouses, orders are automatically routed to the nearest warehouse based on the user's address. The system offers a seamless shopping experience, with users unaware of the multi-warehouse infrastructure. Stock levels displayed to users represent the total available stock across all warehouses.

---

## Platform Description
### Main Features
- **Multi-Storage Support**:
  - Orders are processed by the nearest warehouse to the user's location.
  - Shipping costs are calculated based on the distance between the selected warehouse and the user's address.
- **Stock Mutation**:
  - Admins can manually request stock transfers between warehouses.
  - Automatic stock mutation is triggered when the selected warehouse lacks sufficient stock, ensuring order fulfillment.
- **Order Statuses**:
  - **Waiting for Payment**: User has created an order but has not completed payment.
  - **Awaiting Payment Confirmation**: Admin reviews the uploaded payment proof.
  - **Processing**: Order is being prepared, including any necessary stock mutations.
  - **Shipped**: Order has been dispatched to the user.
  - **Order Confirmed**: User confirms receipt of the order or it is auto-confirmed after 7 days.
  - **Cancelled**: Orders can be cancelled by the user (before payment) or by admin (refunds handled manually).

---

## Tech Stack
### Backend
- Java Spring Boot 3.3.2
- Hibernate ORM
- PostgreSQL
- RESTful API
- Redis
- Typesense
- Midtrans
- RajaOngkir
- SMTP
- Cloudinary

### Frontend
- Next.js
- TailwindCSS
- TypeScript
- React Query
- Next-Auth
- Zustand
- Leaflet Open Source Map
- OpenCage API
- Clerk

---

## Features
### Backend Features
- **User Authentication and Authorization**:
  - JWT-based sessions with Redis caching.
  - RSAKey-secured tokens.
- **Product Management**:
  - CRUD operations for products and categories.
  - Pagination and filtering for product listings.
- **Checkout System**:
  - Payment integration via Midtrans.
  - Manual payment proof upload.
  - Shipping cost calculation using RajaOngkir API.
- **Admin Dashboard**:
  - CRUD operations for warehouses and warehouse admins.
  - Sales reports and stock mutation tracking.

### Frontend Features
#### User Pages
1. **Homepage**: Product discovery and categories with a hero section for promotions.
2. **Product Page**: Detailed product listings and filtering.
3. **Profile Page**: Manage personal details, email verification, and photo uploads.
4. **Cart Page**: Add, update, or remove products.
5. **Checkout Page**: Select shipping addresses and view order summaries.
6. **Transaction Status**: Track orders and manage confirmations.
7. **Address Management**: Use map pinpoints or search input for location.

#### Dashboard Pages
1. **Sales Reports**: Analyze sales by warehouse, category, and product.
2. **Category Management**: CRUD for product categories.
3. **Product Management**: Manage inventory and upload product images.
4. **Order Management**: Handle user orders and confirm payments.
5. **Warehouse Management**: Assign warehouse admins and manage warehouse details.
6. **User Management**: Manage user roles and permissions.

---

## Installation
### Prerequisites
- **Backend**:
  - Java 21 or higher.
  - Maven.
  - PostgreSQL.
- **Frontend**:
  - Node.js 18 or higher.
  - Next.js 14.2 or higher.
  - npm.

1. **The Repository**:
   
   **Backend**:
   ```bash
   https://github.com/dti-multi-warehouse/MultiWarehouseApp
   ```
   **Frontend**:
   ```bash
   https://github.com/dti-multi-warehouse/MultiWarehouseWeb
   ```
---

## Configuration
### Backend
- Environment variables are configured in `application.properties`.

### Frontend
- Environment variables are configured in `.env`.

---

## Project Structure
### Backend
```
repository-root/
|-- backend/
|   |-- src/
|       |-- main/
|           |-- java/
|               |-- com.dti.alphamarch/
|                   |-- feature/
|                       |-- controller
|                       |-- entity/dao
|                       |-- repository
|                       |-- service
```

### Frontend
```
repository-root/
|-- frontend/
|   |-- app/
|   |   |-- auth/
|   |       |-- pages/  // User Auth Pages
|   |   |-- root/
|   |       |-- pages/  // User Pages
|   |   |-- dashboard/
|   |       |-- pages/  // Dashboard Admin Pages
|   |-- components/
|   |-- hooks/
|   |-- public/
|       |-- assets/
```

---

## Endpoints Documentation
### Product Endpoints
**Base URL:** `/api/v1/product`
- `POST /`: Add product.
- `GET /`: Display products with paging and filter.
- `GET /featured`: Get featured products for the homepage.
- `GET /{id}`: Get product details.

### Cart Endpoints
**Base URL:** `/api/v1/cart`
- `GET /`: Display cart.
- `POST /`: Add product to cart.
- `PUT /increment/{id}`: Increment product quantity in cart.
- `PUT /decrement/{id}`: Decrement product quantity in cart.

### Order Endpoints
**Base URL:** `/api/v1/order`
- `POST /`: Create an order.
- `GET /{orderId}`: Get order details.

---

## Authors
- **Nur Rizki Amalia** - Full-stack Developer
  - GitHub: [nurrizkiamalia](https://github.com/nurrizkiamalia)
  - Email: rizkiamel9@gmail.com
- **Gerry Sheva** - Full-stack Developer
  - GitHub: [gerry-sheva](https://github.com/gerry-sheva)
  - Email: gerry.sheva@proton.me

