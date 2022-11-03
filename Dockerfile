FROM ghcr.io/navikt/baseimages/temurin:17

COPY ./target/familie-ba-infotrygd-feed.jar "app.jar"
