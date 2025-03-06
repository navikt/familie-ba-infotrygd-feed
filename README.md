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

### Test av InfotrygdFeedController med STS-token i preprod
For å teste InfotrygdFeedController så trenger man å få lagd ett STS-token.

1. Gå til swagger for [security-token-service](https://security-token-service.dev.adeo.no/swagger-ui/index.html?configUrl=/api/api-doc/swagger-config#/System%20OIDC%20Token/postOIDCToken)
2. Velg security-token-service.dev.adeo.no fra dropdown med navn Servers
3. Fyll inn brukernavn og passord i Authorize-feltet. Brukernavnet og passordet finner man i [vault for familie-integrasjoner](https://vault.adeo.no/ui/vault/secrets/kv%2Fpreprod%2Ffss/show/familie-integrasjoner/teamfamilie) som CREDENTIAL_USERNAME og CREDEENTIAL_PASSWORD.
4. Kjør POST /rest/v1/sts/token med client_credentials og openid
5. I swagger for [familie-ba-infotrygd-feed](https://familie-ba-infotrygd-feed.intern.dev.nav.no/swagger-ui/index.html#/infotrygd-feed-controller/feed), trykk Authorize. Skriv inn ```Bearer <token>``` 




## Kontaktinformasjon
For NAV-interne kan henvendelser om applikasjonen rettes til #team-familie på slack. Ellers kan man opprette et issue her på github.
