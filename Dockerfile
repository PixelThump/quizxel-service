FROM openjdk:17
EXPOSE 8080
ADD /target/${JAR_NAME}.jar /${JAR_NAME}.jar
CMD java -jar /${JAR_NAME}.jar
