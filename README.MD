# ms-rabbit-admin

Microservicio REST encargado de administrar recursos de RabbitMQ.

## Descripción

`ms-rabbit-admin` permite crear y eliminar dinámicamente:

- colas;
- exchanges;
- bindings.

La lógica administrativa se encuentra encapsulada en un servicio específico.

## Tecnologías

- Java 21
- Spring Boot
- Spring Web
- Spring AMQP
- Bean Validation
- Lombok
- Docker
- RabbitMQ
- GitHub Actions

## Puerto

```text
8084
```

## Arquitectura

El proyecto se organiza en:

```text
config
controller
dto
exception
service
```

La lógica principal se encuentra en:

```text
RabbitAdminService
```

## Endpoints

### Crear cola

```http
POST /api/rabbit-admin/queues
```

### Eliminar cola

```http
DELETE /api/rabbit-admin/queues/{name}
```

### Crear exchange

```http
POST /api/rabbit-admin/exchanges
```

### Eliminar exchange

```http
DELETE /api/rabbit-admin/exchanges/{name}
```

### Crear binding

```http
POST /api/rabbit-admin/bindings
```

### Eliminar binding

```http
DELETE /api/rabbit-admin/bindings
```

# Pruebas funcionales

Las siguientes pruebas permiten validar que el microservicio administra correctamente colas, exchanges y bindings en RabbitMQ.

## 1. Verificar que el microservicio esté ejecutándose

```bash
docker ps
```

Debe aparecer un contenedor similar a:

```text
ms-rabbit-admin   Up ...   0.0.0.0:8084->8084/tcp
```

También debe aparecer RabbitMQ en estado activo.

## 2. Crear una cola

```bash
curl -i -X POST http://localhost:8084/api/rabbit-admin/queues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "prueba.admin.queue",
    "durable": true,
    "exclusive": false,
    "autoDelete": false
  }'
```

Resultado esperado:

```text
HTTP/1.1 201
```

Respuesta:

```json
{
  "message": "Cola creada correctamente"
}
```

## 3. Verificar que la cola exista en RabbitMQ

```bash
docker exec rabbitmq \
  rabbitmqctl list_queues \
  -p pedidos670 \
  name
```

Debe aparecer:

```text
prueba.admin.queue
```

## 4. Eliminar la cola

```bash
curl -i -X DELETE \
  http://localhost:8084/api/rabbit-admin/queues/prueba.admin.queue
```

Resultado esperado:

```text
HTTP/1.1 200
```

Respuesta:

```json
{
  "message": "Cola eliminada correctamente"
}
```

## 5. Verificar que la cola fue eliminada

```bash
docker exec rabbitmq \
  rabbitmqctl list_queues \
  -p pedidos670 \
  name
```

La cola:

```text
prueba.admin.queue
```

ya no debe aparecer.

## 6. Crear un exchange

```bash
curl -i -X POST http://localhost:8084/api/rabbit-admin/exchanges \
  -H "Content-Type: application/json" \
  -d '{
    "name": "prueba.admin.exchange",
    "type": "topic",
    "durable": true,
    "autoDelete": false
  }'
```

Resultado esperado:

```text
HTTP/1.1 201
```

Respuesta:

```json
{
  "message": "Exchange creado correctamente"
}
```

## 7. Verificar el exchange

```bash
docker exec rabbitmq \
  rabbitmqctl list_exchanges \
  -p pedidos670 \
  name type
```

Debe aparecer:

```text
prueba.admin.exchange   topic
```

## 8. Crear una cola para probar binding

```bash
curl -i -X POST http://localhost:8084/api/rabbit-admin/queues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "prueba.admin.queue",
    "durable": true,
    "exclusive": false,
    "autoDelete": false
  }'
```

## 9. Crear binding

```bash
curl -i -X POST http://localhost:8084/api/rabbit-admin/bindings \
  -H "Content-Type: application/json" \
  -d '{
    "queue": "prueba.admin.queue",
    "exchange": "prueba.admin.exchange",
    "routingKey": "prueba.test"
  }'
```

Resultado esperado:

```text
HTTP/1.1 201
```

Respuesta:

```json
{
  "message": "Binding creado correctamente"
}
```

## 10. Verificar binding

```bash
docker exec rabbitmq \
  rabbitmqctl list_bindings \
  -p pedidos670 \
  source_name destination_name routing_key
```

Debe aparecer:

```text
prueba.admin.exchange   prueba.admin.queue   prueba.test
```

## 11. Eliminar binding

```bash
curl -i -X DELETE http://localhost:8084/api/rabbit-admin/bindings \
  -H "Content-Type: application/json" \
  -d '{
    "queue": "prueba.admin.queue",
    "exchange": "prueba.admin.exchange",
    "routingKey": "prueba.test"
  }'
```

Resultado esperado:

```text
HTTP/1.1 200
```

## 12. Verificar eliminación del binding

```bash
docker exec rabbitmq \
  rabbitmqctl list_bindings \
  -p pedidos670 \
  source_name destination_name routing_key
```

La relación:

```text
prueba.admin.exchange → prueba.admin.queue → prueba.test
```

ya no debe aparecer.

## 13. Eliminar exchange

```bash
curl -i -X DELETE \
  http://localhost:8084/api/rabbit-admin/exchanges/prueba.admin.exchange
```

Resultado esperado:

```text
HTTP/1.1 200
```

## 14. Eliminar cola de prueba

```bash
curl -i -X DELETE \
  http://localhost:8084/api/rabbit-admin/queues/prueba.admin.queue
```

Resultado esperado:

```text
HTTP/1.1 200
```

## 15. Probar validación de entrada

Se intenta crear una cola con nombre vacío:

```bash
curl -i -X POST http://localhost:8084/api/rabbit-admin/queues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "durable": true,
    "exclusive": false,
    "autoDelete": false
  }'
```

Resultado esperado:

```text
HTTP/1.1 400 Bad Request
```

Esto demuestra que la API valida los parámetros recibidos.

## 16. Revisar logs

```bash
docker logs ms-rabbit-admin --tail 100
```

Los logs permiten verificar operaciones realizadas sobre RabbitMQ y detectar errores de administración.

## Resultado esperado de las pruebas

Al completar correctamente estas pruebas se demuestra que `ms-rabbit-admin` puede:

- crear colas;
- eliminar colas;
- crear exchanges;
- eliminar exchanges;
- crear bindings;
- eliminar bindings;
- validar datos de entrada;
- comunicarse correctamente con RabbitMQ.

## Variables de entorno

```text
SERVER_PORT

RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
RABBITMQ_VHOST
```

## Compilación

Windows:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Linux:

```bash
./mvnw clean package -DskipTests
```

## Docker

```bash
docker build -t ms-rabbit-admin .
```

Ejemplo:

```bash
docker run -d \
  --name ms-rabbit-admin \
  --restart unless-stopped \
  --network cloudnative-network \
  -p 8084:8084 \
  --env-file ~/cloudnative/rabbit-admin.env \
  ms-rabbit-admin
```

## Despliegue

```text
develop
   |
   v
deploy
   |
   v
GitHub Actions
   |
   v
EC2
   |
   v
Docker
```

## Seguridad

Este microservicio realiza operaciones administrativas sobre RabbitMQ.

En un entorno productivo debe protegerse y no exponerse libremente a Internet.