FROM navikt/java:11-appdynamics

ENV APPD_ENABLED=true
ENV APP_NAME=familie-ba-infotrygd-feed

COPY ./target/familie-ba-infotrygd-feed.jar "app.jar"
