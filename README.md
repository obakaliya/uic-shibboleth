# UIC Shibboleth Spring Boot Application

A Spring Boot application that integrates with University of Illinois Chicago's Shibboleth Single Sign-On (SSO) system for authentication and user management.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Authentication Modes](#authentication-modes)
- [Apache Configuration](#apache-configuration)
- [SSL Configuration](#ssl-configuration)
- [Database](#database)
- [API Endpoints](#api-endpoints)
- [Development](#development)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

## Overview

This application provides a secure web interface that authenticates users through UIC's Shibboleth identity provider. It supports both production Shibboleth authentication and local development authentication for testing purposes.

## Features

- **Shibboleth SSO Integration**: Seamless authentication with UIC's identity system
- **Dual Authentication Modes**: Production (Shibboleth) and Development (local) modes
- **User Management**: Automatic user creation from Shibboleth attributes
- **Attribute Mapping**: Maps Shibboleth attributes to user entities
- **Error Handling**: Custom error pages for common HTTP errors
- **H2 Database**: In-memory database for development and testing
- **REST API**: JSON endpoints for user data retrieval
- **Bootstrap UI**: Clean, responsive web interface

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│   Apache HTTPD  │    │  Spring Boot App │    │    H2 Database      │
│  (Shibboleth)   │────│   (Port 8443)    │────│   (In-Memory)       │
│   (Port 443)    │    │                  │    │                     │
└─────────────────┘    └──────────────────┘    └─────────────────────┘
```

## Prerequisites

### Development Environment
- **Java 17** or higher
- **Maven 3.6+**
- **Git**

### Production Environment
- **Java 17** or higher
- **Apache HTTP Server** with mod_ssl and mod_shib
- **Shibboleth Service Provider** configured for UIC
- **SSL Certificates** (*.engr.uic.edu)

## Project Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd uic-shibboleth
```

### 2. Build the Application
```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Or using system Maven
mvn clean package
```

### 3. Verify Build
```bash
ls target/uic-shibboleth-*.jar
```

## Configuration

The application uses Spring Boot's configuration system with YAML files.

### Main Configuration (`application.yml`)

#### Production Configuration
```yaml
app:
  auth:
    auto-create-user: true
    local-dev-mode: false

server:
  port: 8443
  ssl:
    enabled: true
    key-store: file:/etc/pki/tls/keystore.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: tomcat
```

#### Development Configuration (Profile: local)
```yaml
spring:
  config:
    activate:
      on-profile: local

app:
  auth:
    auto-create-user: true
    local-dev-mode: true

server:
  port: 8080
  ssl:
    enabled: false
```

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `app.auth.auto-create-user` | Automatically create users from Shibboleth attributes | `true` |
| `app.auth.local-dev-mode` | Enable local development authentication | `false` |
| `server.port` | Application port | `8443` (prod), `8080` (dev) |
| `server.ssl.enabled` | Enable SSL/TLS | `true` (prod), `false` (dev) |

## Running the Application

### Development Mode (Local Authentication)
```bash
# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Or set environment variable
export SPRING_PROFILES_ACTIVE=local
./mvnw spring-boot:run
```

**Access:** http://localhost:8080

### Production Mode (Shibboleth Authentication)
```bash
# Run with default (production) profile
./mvnw spring-boot:run

# Or as JAR
java -jar target/uic-shibboleth-*.jar
```

**Access:** https://test.engr.uic.edu (through Apache proxy)

## Authentication Modes

### 1. Local Development Mode

**Purpose**: Development and testing without Shibboleth infrastructure

**Features**:
- Mock user authentication with predefined test users
- Simple email/password login form
- Session-based authentication
- No external dependencies

**Test Users**:
| Email | Password | Role | Department |
|-------|----------|------|------------|
| john.dev@uic.edu | password | Faculty | Computer Science |
| jane.test@uic.edu | password | Staff | Information Technology |
| admin@uic.edu | password | Employee | Administration |
| student@uic.edu | password | Student | Engineering |

**Usage**:
1. Start application with `local` profile
2. Navigate to http://localhost:8080
3. Click "Local Development Login"
4. Use any test user email with password "password"

### 2. Production Mode (Shibboleth)

**Purpose**: Production authentication through UIC's Shibboleth IdP

**Features**:
- Integration with UIC's identity provider
- Automatic user attribute mapping
- Apache mod_shib integration
- SSL/TLS encryption

**User Flow**:
1. User accesses https://test.engr.uic.edu
2. Apache redirects to Shibboleth IdP
3. User authenticates with UIC credentials
4. Shibboleth returns user attributes as HTTP headers
5. Spring Boot creates/updates user record
6. User gains access to application

## Apache Configuration

### Virtual Host Configuration (`test.engr.uic.edu.conf`)

```apache
<VirtualHost *:443>
    ServerName test.engr.uic.edu

    # SSL Configuration
    SSLEngine on
    SSLCertificateFile /etc/pki/tls/certs/*.engr.uic.edu.crt
    SSLCertificateKeyFile /etc/pki/tls/private/*.engr.uic.edu.key
    SSLCertificateChainFile /etc/pki/tls/certs/server-chain.crt

    # Shibboleth Handler
    <Location /Shibboleth.sso>
        SetHandler shib
    </Location>

    # Shibboleth Protection
    <Location />
        AuthType shibboleth
        ShibRequestSetting requireSession true
        ShibUseHeaders On
        Require shib-session
        Require valid-user
    </Location>

    # Proxy to Spring Boot
    ProxyPass / https://localhost:8443/
    ProxyPassReverse / https://localhost:8443/
    SSLProxyEngine On
    SSLProxyVerify none
    SSLProxyCheckPeerCN off
    SSLProxyCheckPeerName off
    SSLProxyCheckPeerExpire Off

    # Logging
    ErrorLog /var/log/httpd/shibboleth-spring-boot_error.log
    CustomLog /var/log/httpd/shibboleth-spring-boot_access.log combined
</VirtualHost>
```

### Key Apache Directives Explained

- **`SetHandler shib`**: Routes Shibboleth SSO requests to mod_shib
- **`ShibUseHeaders On`**: Exposes Shibboleth attributes as HTTP headers
- **`ProxyPass`/`ProxyPassReverse`**: Forwards requests to Spring Boot application
- **`SSLProxyEngine On`**: Enables SSL for backend communication

## SSL Configuration

### 1. Create Keystore for Spring Boot
```bash
# Convert PEM certificates to PKCS12 format
openssl pkcs12 -export \
    -in /etc/pki/tls/certs/*.engr.uic.edu.crt \
    -inkey /etc/pki/tls/private/*.engr.uic.edu.key \
    -out /etc/pki/tls/keystore.p12 \
    -name tomcat \
    -password pass:password
```

### 2. Update Application Configuration
```yaml
server:
  ssl:
    key-store: file:/etc/pki/tls/keystore.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: tomcat
```

## Database

### H2 In-Memory Database

**Configuration**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Access H2 Console**: http://localhost:8080/h2-console (dev mode)

### User Entity Schema

```sql
CREATE TABLE users (
    uid VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255),
    given_name VARCHAR(255),
    surname VARCHAR(255),
    mail VARCHAR(255),
    edu_person_principal_name VARCHAR(255),
    edu_person_primary_affiliation VARCHAR(255),
    edu_person_scoped_affiliation VARCHAR(255),
    i_trust_suppress BOOLEAN,
    organization_name VARCHAR(255),
    i_trust_home_dept_code VARCHAR(255),
    i_trust_uin VARCHAR(255),
    organizational_unit VARCHAR(255),
    title VARCHAR(255)
);
```

## API Endpoints

### Web Endpoints
| Method | Path | Description | Authentication |
|--------|------|-------------|----------------|
| GET | `/` | Home page | Public |
| GET | `/login` | Local login page | Public (dev mode) |
| POST | `/local-login` | Local authentication | Public (dev mode) |
| POST | `/local-logout` | Local logout | Public (dev mode) |
| GET | `/user` | User attributes page | Required |
| GET | `/error` | Error handler | Public |

### REST API Endpoints
| Method | Path | Description | Response |
|--------|------|-------------|----------|
| GET | `/api/user` | Get current user data | JSON user object |

### Example API Response
```json
{
  "authenticated": true,
  "user": {
    "uid": "dev001",
    "displayName": "John Developer",
    "mail": "john.dev@uic.edu",
    "givenName": "John",
    "surname": "Developer",
    "eduPersonPrimaryAffiliation": "faculty",
    "organizationName": "University of Illinois Chicago"
    // additional attributes...
  }
}
```

## Development

### IDE Setup

#### IntelliJ IDEA
1. Import as Maven project
2. Set Project SDK to Java 17
3. Enable Spring Boot support
4. Run configurations:
   - Main class: `UicShibbolethApplication`
   - VM options: `-Dspring.profiles.active=local`

#### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Open folder in VS Code
4. Use Command Palette: "Java: Run Spring Boot App"

### Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### Hot Reload (Development)
```bash
# Enable Spring Boot DevTools
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Deployment

### 1. Build Production JAR
```bash
./mvnw clean package -DskipTests
sudo mkdir -p /opt/uic-shibboleth
sudo cp target/uic-shibboleth-0.0.1-SNAPSHOT.jar /opt/uic-shibboleth/uic-shibboleth.jar
```

### 2. Create System Service
```bash
# Create service file
sudo vim /etc/systemd/system/uic-shibboleth.service
```

```ini
[Unit]
Description=UIC Shibboleth Spring Boot Application
After=network.target

[Service]
Type=simple
User=uic-app
ExecStart=/usr/bin/java -jar /opt/uic-shibboleth/uic-shibboleth.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 3. Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable uic-shibboleth
sudo systemctl start uic-shibboleth
sudo systemctl status uic-shibboleth
```

### 4. Configure Apache
```bash
# Copy virtual host configuration
sudo vim test.engr.uic.edu.conf # write your proxy
sudo systemctl restart httpd
```

## Troubleshooting

### Common Issues

#### 1. SSL Handshake Failures
**Symptoms**: Cannot connect to HTTPS endpoints
**Solutions**:
- Verify keystore path and password
- Check certificate validity
- Ensure proper file permissions

#### 2. Shibboleth Attribute Issues
**Symptoms**: User creation fails, missing attributes
**Solutions**:
- Check Apache logs: `/var/log/httpd/shibboleth-spring-boot_error.log`
- Verify Shibboleth configuration
- Test attribute release with Shibboleth IdP

#### 3. Database Connection Issues
**Symptoms**: JPA/Hibernate errors
**Solutions**:
- Check H2 console accessibility
- Verify datasource configuration
- Review application logs

#### 4. Local Development Login Issues
**Symptoms**: Cannot login with test users
**Solutions**:
- Ensure `local-dev-mode: true`
- Use exact email addresses from test data
- Verify password is "password"

### Logging Configuration

```yaml
logging:
  level:
    edu.uic.uic_shibboleth: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
  file:
    name: /var/log/uic-shibboleth/application.log
```
