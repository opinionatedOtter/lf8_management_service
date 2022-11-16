# Swagger Documentation:
Die Swagger Dokumentation ist über folgende URL erreichbar: http://localhost:8089/swagger-ui/index.html

Voraussetzungen:
- Postgres Datenbank Container läuft
- Anwendung läuft

Besonderheiten:
- `/index.html` am Ende der URL ist eine Voraussetzung, sonst gibt http://localhost:8089/swagger-ui/  HttpStatus: 404 zurück.

# Zusätzliche Features:
Force-Flag: Für die Update Project methode wurde eine optionale Force-Flag implementiert:
- Ohne Force-Flag wird das Projekt nur geändert, wenn alle Mitarbeiter im Neuen Zeitraum ebenfalls frei sind
- Mit Force-Flag werden alle Mitarbeiter, die nicht (mehr) im Zeitraum frei sind entfernt oder nicht hinzugefügt.
- Partielle Projekt-Aktualisierung
- Globaler authorization-header in Swagger.  Nicht jede Methode benötigt einen eigenen authorization-header, sondern es ist nur ein einmaliges Authentifizieren nötig.

# Test-Merkmale:
- Github Pipeline, die für jeden Push alle Integrations- und Unittests ausführt.
- Github Workflows (picture)
- Testabdeckung (Stand 16.11.22)
Testabdeckung (picture)
- Objekte in externen Ressourcen (Employee Service) werden vor jedem Testlauf angelegt und nach den Tests wieder gelöscht

# Technische Merkmale:
- Bei fehlerhaften HTTP-Requests gibt die Applikation neben dem Error-Code einen `ProblemDetails` response body zurück.   
Hierin findet sich eine  ausführliche und nach [RFC 7807: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc7807)
standardisierte Fehlermeldung. Problem Details kapselt sämtliche serverseitigen Fehler, inklusive Business-Logik Fehler, Datenbankfehler und SpringBoot Fehler.
- starke Typisierung: Wir haben domänenspezifische Datentypen, wie `ProjectDescription` oder `ActualEndDate` erstellt. Dadurch vermeiden wir generics, wie `String` oder `Long`.   
`public static Optional<ProjectTimespan> of(Optional<StartDate> startDate, Optional<RelevantEndDate> relevantEndDate)`   
Durch die starke Typisierung wird zum Beispiel bereits compilerseitig verhindert, dass die beiden Daten in den Funktionsparametern vertauscht sind.
- Kein `null` im Code, stattdessen wird `Optional<?>` verwendet, um unter Anderem null-checks und null-pointer-exceptions  auszuschließen.


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

### Intellij-Ansicht für Postgres Datenbank einrichten
```bash
1. Lasse den Docker-Container mit der PostgreSQL-Datenbank laufen
2. im Ordner resources die Datei application.properties öffnen und die URL der Datenbank kopieren
3. rechts im Fenster den Reiter Database öffnen
4. In der Database-Symbolleiste auf das Datenbanksymbol mit dem Schlüssel klicken
5. auf das Pluszeichen klicken
6. Datasource from URL auswählen
7. URL der DB einfügen und PostgreSQL-Treiber auswählen, mit OK bestätigen
8. Username lf8_starter und Passwort secret eintragen (siehe application.properties), mit Apply bestätigen
9. im Reiter Schemas alle Häkchen entfernen und lediglich vor lf8_starter_db und public Häkchen setzen
10. mit Apply und ok bestätigen 
```
# Keycloak

### Keycloak Token
1. Auf der Projektebene [GetBearerToken.http](./test-requests.http) öffnen.
2. Neben der Request auf den grünen Pfeil drücken
3. Aus dem Reponse das access_token kopieren
