#storm

##Docker
```
docker build -t osmstorm .
docker run -d --name osmstorm -v .:/maped/ -p 2181:2181 -p 8080:8080 osmstorm
```

##Usage
Build project:

```
mvn clean compile assembly:single
```

Run: (in Docker Container)

```
java -jar target/storm-1.0-SNAPSHOT-jar-with-dependencies.jar
```
