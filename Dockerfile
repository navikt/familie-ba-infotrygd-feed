FROM ghcr.io/navikt/baseimages/temurin:21

COPY ./target/familie-ba-infotrygd-feed.jar "app.jar"
