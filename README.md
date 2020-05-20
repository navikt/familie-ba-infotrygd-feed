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

## Produksjonssetting
Appen blir produksjonssatt ved å kjøre `tag.sh` som ligger i `.github`. Dette scriptet tagger den seneste commiten i master med det neste versjonsnummeret, og pusher tagen til github-repositoriet.

Hvis den siste tagen er `v0.5`, vil `tag.sh -M` pushe tagen `v1.0`, og `tag.sh -m` pushe tagen `v0.6`.

Ved push av en tag på formen `v*` vil Github Action-workflowen `Build-Deploy-Prod` trigges, som bygger en ny versjon av appen, lagrer imaget i Github Packages, og deployer appen til prod-fss.

## Kontaktinformasjon
For NAV-interne kan henvendelser om applikasjonen rettes til #team-familie på slack. Ellers kan man opprette et issue her på github.
