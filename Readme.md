# Starter für das LF08 Projekt

Erstellen Sie einen Fork dieses Projektes auf Github. Wählen Sie einen Namen und passen Sie diesen auch in der pom.xml in Zeile 12, 14 und 15 an.

## Requirements
* Docker https://docs.docker.com/get-docker/
* Docker compose (bei Windows und Mac schon in Docker enthalten) https://docs.docker.com/compose/install/

## Endpunkt
```
http://localhost:8089
```
## Swagger
```
http://localhost:8089/swagger
```


# Postgres
### Terminal öffnen
für alles gilt, im Terminal im Ordner docker/local sein
```bash
cd docker/local
```
### Postgres starten
```bash
docker compose up
```
Achtung: Der Docker-Container läuft dauerhaft! Wenn er nicht mehr benötigt wird, sollten Sie ihn stoppen.

### Postgres stoppen
```bash
docker compose down
```

### Postgres Datenbank wipen, z.B. bei Problemen
```bash
docker compose down
docker volume rm local_lf8_starter_postgres_data
docker compose up
```

### initialer Zustand der Datenbank (flyway)
siehe src/main/resources/db/migrations/V1.0__init.sql. Hier können auch Ergänzungen vorgenommen werden, allerdings muss dann die Datenbank gewipt werden. Alterniv eine weitere Datei V1.1__Name.sql erstellen und dort Erweiterungen vornehmen.

# Keycloak

### Keycloak Token
1. Auf der Projektebene [GetBearerToken.http](./GetBearerToken.http) öffnen.
2. Neben der Request auf den grünen Pfeil drücken
3. Aus dem Reponse das access_token kopieren