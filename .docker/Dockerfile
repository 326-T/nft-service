#############################################
# 1. build the application
#############################################
FROM openjdk:21-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw \
    --no-transfer-progress \
    clean install \
    -Dmaven.test.skip=true
#############################################
# 2. run the application
#############################################
FROM gcr.io/distroless/java21-debian12:nonroot as runner
ENV LANG=C.UTF-8
ENV TZ=Asia/Tokyo
WORKDIR /app
COPY --from=builder --chown=nonroot:nonroot /app/target/*.jar app.jar

USER nonroot
EXPOSE 8080
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]
