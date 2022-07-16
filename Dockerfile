FROM openjdk:17.0-slim

ARG BASE_PATH=/opt/musicservice
ARG CONFIG_DIR=${BASE_PATH}/config
ENV BASE_PATH=${BASE_PATH}
ENV CONFIG_DIR=${CONFIG_DIR}

WORKDIR $BASE_PATH
COPY build/libs/musicservice-0.0.1-SNAPSHOT.jar $BASE_PATH/musicservice-app.jar
COPY src/main/resources/application.yaml $CONFIG_DIR/application.yaml

ENTRYPOINT [ "sh", "-c", \
            "java \
            --add-opens java.base/java.lang=ALL-UNNAMED \
            -cp ${CONFIG_DIR}/:${CONFIG_DIR}/lib/*:$BASE_PATH/musicservice-app.jar org.springframework.boot.loader.JarLauncher \
            --spring.config.additional-location=file:${CONFIG_DIR}/application.yaml" ]