# familie-ba-infotrygd-feed
Feed-kommunikasjon mellom Barnetrygd og Infotrygd

# Kjøring lokalt
`DevLauncher` kjører appen lokalt med Spring-profilen `dev` satt. Appen tilgjengeliggjøres da på `localhost:8092`.  

### Database

Dersom man vil kjøre med postgres, kan man bytte til Spring-profilen `postgres`. Dette kan feks gjøres ved å sette
 `-Dspring.profiles.active=postgres` under Edit Configurations -> VM Options.
Da må man sette opp postgres-databasen, dette gjøres slik:
```
docker run --name familie-ba-infotrygd-feed -e POSTGRES_PASSWORD=test -d -p 5432:5432 postgres
docker ps (finn container id)
docker exec -it <container_id> bash
psql -U postgres
CREATE DATABASE "familie-ba-infotrygd-feed";
```

## Kontaktinformasjon
For NAV-interne kan henvendelser om applikasjonen rettes til #team-familie på slack. Ellers kan man opprette et issue her på github.
