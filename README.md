# Link Shortener (Spring Boot + PostgreSQL + Redis)

A URL shortener service built with **Spring Boot**, backed by **PostgreSQL** (persistent storage) and **Redis** (cache + rate limiting).

<img width="1544" height="699" alt="Screenshot 2026-01-28 092406" src="https://github.com/user-attachments/assets/de13a115-51d5-45fa-a12c-b3d743cf7316" />


---

## What you need (once)

### 1) Install Java
- Install **JDK 17+**
- Verify:
```bash
java -version
```

### 2) Install Git
Verify:
```bash
git --version
```

### 3) Install PostgreSQL (Windows .exe)
This project supports PostgreSQL installed via the Windows installer, which runs PostgreSQL as a Windows **Service**. [web:620]

After installing, verify the service:
- `Win + R` → `services.msc`
- Find something like: `postgresql-x64-<version>`
- Make sure it is **Running** [web:620]

You can also stop/start using command line (Admin CMD): [web:620]
```bat
net stop postgresql-x64-16
net start postgresql-x64-16
```
(Replace `16` with your installed version.) [web:620]

### 4) Install Redis (recommended on Windows via WSL Ubuntu)
If you’re on Windows, Redis is easiest via WSL Ubuntu.

Install + start Redis in Ubuntu (WSL):
```bash
sudo apt update
sudo apt install redis-server redis-tools -y
sudo service redis-server start
redis-cli ping
```

Expected output:
```text
PONG
```

---

## Clone the repo

```bash
git clone <YOUR_REPO_URL>
cd <YOUR_REPO_FOLDER>
```

---

## Configure the app

Update `src/main/resources/application.yml` (or `application.properties`) with your local config:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/link_shortener
    username: link_user
    password: link_pass

  jpa:
    hibernate:
      ddl-auto: update

  data:
    redis:
      host: localhost
      port: 6379
```

### Create DB + user (recommended)
Open `psql` and run:
```sql
CREATE DATABASE link_shortener;
CREATE USER link_user WITH ENCRYPTED PASSWORD 'link_pass';
GRANT ALL PRIVILEGES ON DATABASE link_shortener TO link_user;
```

---

## Run the application

This repo uses **Maven Wrapper**, so you don’t need Maven installed; you can run Spring Boot via `spring-boot:run`. [web:634][web:653]

### Windows (PowerShell or CMD)
```bat
mvnw.cmd spring-boot:run
```

### Linux / WSL / macOS
```bash
./mvnw spring-boot:run
```

---

## Verify it’s working

### 1) UI
Open:
- http://localhost:8080/index.html

### 2) Create short link (API)
Windows (CMD):
```bat
curl -X POST http://localhost:8080/api/v1/links ^
  -H "Content-Type: application/json" ^
  -d "{\"longUrl\":\"https://example.com\"}"
```



---

## Stop services

### Stop Spring Boot
Press:
- `Ctrl + C` in the terminal running the app

### Stop Redis (WSL Ubuntu)
```bash
sudo service redis-server stop
```

### Stop PostgreSQL (Windows)
GUI: `services.msc` → `postgresql-x64-<version>` → Stop [web:620]

CMD (Admin): [web:620]
```bat
net stop postgresql-x64-16
```

---

## Notes (common issues)

### PostgreSQL port already in use
Either stop the conflicting service or change Postgres/Spring port.

### Redis connection issues (Windows app -> WSL Redis)
If `localhost:6379` doesn’t work in your setup, set `spring.data.redis.host` to the WSL IP.

---
