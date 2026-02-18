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
Below are the steps to create an Auth server at KeyClock
<img width="1512" height="776" alt="image" src="https://github.com/user-attachments/assets/1d4dd9bb-4c74-4cce-8fa1-3732d562cbca" />
<img width="1512" height="730" alt="image" src="https://github.com/user-attachments/assets/abd4bfab-eaf0-44d4-8278-776db463cc31" />
<img width="1510" height="772" alt="image" src="https://github.com/user-attachments/assets/0b1529ca-3b84-45fb-adad-8e0880c5571e" />
Now you can go to get Client Secret
<img width="1512" height="777" alt="image" src="https://github.com/user-attachments/assets/4f067ca6-003f-4e82-ba82-ee3b5007fd45" />

To create a new User
<img width="1512" height="792" alt="image" src="https://github.com/user-attachments/assets/8cd593cf-bdb9-4214-94ce-cc1cb0854fbe" />
To set password in this user 
<img width="1510" height="770" alt="image" src="https://github.com/user-attachments/assets/01266c51-0d76-4e4d-97b4-71eab7f2d536" />

To create a new Role
<img width="3024" height="1538" alt="image" src="https://github.com/user-attachments/assets/4263362e-0359-4bbf-9440-728402f8b558" />
And sign Real Role inside client hyperactive-challenge-cc
<img width="1512" height="791" alt="image" src="https://github.com/user-attachments/assets/13c5abe5-cd14-40f6-8ae6-feef1099220e" />
<img width="1510" height="789" alt="image" src="https://github.com/user-attachments/assets/ba47512c-0d00-4a82-95d6-c0cc567b67e3" />






