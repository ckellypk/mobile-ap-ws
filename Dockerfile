FROM openjdk:8
ADD out/artifacts/mobile_app_ws_jar/mobile-app-ws.jar user-mysql.jar
EXPOSE 8089
ENTRYPOINT ["java", "mobile-app-ws.jar"]
