FROM openjdk:8-jdk-alpine

COPY ./build/docker/ /app/

CMD ["/app/run.sh"]
