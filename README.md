# Hyperativa Cards API

API for managing credit/debit card records (single creation, batch upload via file, and lookup).

**Base URL**  
`127.0.0.1/api/cards/v1`

**Current Version** → `v1`

[![OpenAPI 3.1](https://img.shields.io/badge/OpenAPI-3.1-green)](https://spec.openapis.org/oas/v3.1.0)  
[![License: Proprietary](https://img.shields.io/badge/License-Proprietary-blue)](LICENSE)

## Quick Start

All requests require **Bearer JWT** authentication (OAuth2-compatible, issued by Keycloak).

Example (cURL):
```bash
curl -X POST https://api.hyperativa.com/api/cards/v1/create \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4539312345678901",
    "holderName": "Alexandre Candido",
    "expiryDate": "12/28",
    "brand": "VISA"
  }'
```

Authentication

Method: Authorization: Bearer <JWT>
Token must contain sub claim (UUID) → used as card owner identifier
All operations are automatically scoped to the authenticated user
No API keys or basic auth — JWT only

Endpoints
POST /create
Create one card record.
Request body (application/json)
```bash

{
  "cardNumber":   "4539312345678901",
}
```

Success (200)
JSON
```bash
{
    "cardNumber": "942a8de6160f49abd288f75ed7a463a0c4ef15f351db8be772a61f742e909f61",
    "status": "SUCCESS",
    "message": "Card created successfully",
    "savedId": "5665fa07-9667-46c2-9029-2ab26692c711"
}
```
ALREADY_EXISTS (200)
JSON
```bash
{
"cardNumber": "445681499999966",
"status": "ALREADY_EXISTS",
"message": "Card already registered",
"savedId": null
}
```
Common errors: 400, 422 (business/validation rule)

Endpoints
POST /upload
Upload CSV or Excel file → batch process multiple cards.
Request (multipart/form-data)

Form field: file (required) — .csv or .xlsx file

Success (200 OK) — processing summary (can be partial)
```bash
{
    "status": "SUCCESS",
    "message": "1 card(s) processed (1 unique), 1 saved, 0 already exist, 0 duplicate(s) ignored",
    "details": [
        {
            "cardNumber": "951ae34450af7056331d056dda6c5d83a2d8490ec52d8d08b8770cc604d1a81f",
            "status": "SUCCESS",
            "message": "Card created successfully",
            "savedId": "89cfe455-01bd-432a-8085-489d0d259c3f"
        }
    ]
}
```
FAILED (200 OK) 

```bash
{
"status": "FAILED",
"message": "10 card(s) processed (8 unique), 0 saved, 8 already exist, 2 duplicate(s) ignored",
"details": [
        {
        "cardNumber": "4456897999999999",
        "status": "ALREADY_EXISTS",
        "message": "Card already registered",
        "savedId": null
        }        
    ]
}
```
Endpoints
POST /lookup
Check if a card exists (scoped to the authenticated owner).
Request body (application/json)
Minimal: just cardNumber is required — same shape as /create
```bash
{
  "cardNumber": "4539312345678901"
}
```
Response (200 OK)
JSON
```bash
{
    "cardNumber": "445681499999967",
    "status": "ALREADY_EXISTS",
    "message": "Card already registered",
    "savedId": null
}
```
or
```bash
{
"found": false,
"card": null,
"message": "Invalid or missing card number"
}
```
