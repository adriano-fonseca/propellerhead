# The architecture

This project uses Maven 3.3 or higher and was developed using Java 8 through the JavaEE Framework. It was tested in a Wildfly 10, that have a Data Source called AppDS. There are two ways to run this application.

**1** Running in a container Java Like WildFly or Jboss

Build the .war file and deploy it, copying this file in the deployments folder in a Wildfly or Jboss EAP. 

To build run the war

```
mvn clean package
```
PS: You will need to have Data Source AppDS seted in your server.


**2** Running the application in docker

Having docker and docker-compose installed.
Just copy the docker-compose.yml to some directory on your machine and run:


```
 docker-compose up
```

PS: You will need to have docker 1.13 or Higher and docker-compose 1.10 or higher.


The **docker-compose.yml** uses some images that I built and pushed to docker hub to run this assignment.



```yaml
version: '3'
services:

  java-api:
    image: adrianofonseca/propellerhead:latest
    ports:
     - "8080:8080"
     - "9990:9990"
    networks:
      - app
    depends_on:
      - postgres
    deploy:
     replicas: 1
     update_config:
       parallelism: 1
       delay: 30s
     restart_policy:
       condition: on-failure 
     labels: [info=backend]


  postgres:
      image: adrianofonseca/postgres:9.5
      ports:
          - "5432:5432"
      networks:
      - app
      environment:
          - DEBUG=false
          - DB_USER=app
          - DB_PASS=app
          - DB_NAME=app
      volumes:
          -  /opt/docker/docker-postgres/postgresql/:/var/lib/postgresql
      deploy:
        labels: [info=database]
        placement:
          constraints: [node.role == manager]

networks:
      app:

```

The application image to docker is build with docker-maven-plugin from Spotify (see on the pom.xml)

```xml

	<plugin>
		<groupId>com.spotify</groupId>
		<artifactId>docker-maven-plugin</artifactId>
		<version>0.4.12</version>
		<configuration>
			<serverId>docker-hub</serverId>
			<imageName>adrianofonseca/propellerhead</imageName>
			<baseImage>adrianofonseca/wildfly-base-image</baseImage>
			<resources>
				<resource>
					<targetPath>/opt/jboss/wildfly/standalone/deployments</targetPath>
					<directory>${project.build.directory}</directory>
					<include>${project.build.finalName}.war</include>
				</resource>
			</resources>
			<imageTags>
				<imageTag>${project.version}</imageTag>
				<imageTag>latest</imageTag>
			</imageTags>
		</configuration>
	</plugin>

```

**Images availiable in dockerhub**

![Alt](https://github.com/adriano-fonseca/propellerhead/blob/master/src/main/prints/docker_hub.png?raw=true "Docker Hub")




# The assignment

* Provide one http resources (Customer) <host>/v1/customer.

I could have the v1 accessible on the '/' setting this information in the jboss-web.xml localized in the WEB-INF Folder, but I decide to keep as usual in Wildfly server where each application raises a context. I did that for make a maven plugin (jaxrs-analyzer-maven-plugin), work properly and automatically to generate a documentation to API using Swagger.

That documentation are available in **http://<host>:8080/propellerhead/apidocs**

![Alt](https://github.com/adriano-fonseca/propellerhead/blob/master/src/main/prints/swaggerUI.png?raw=true "Swagger UI")

So, to access the end points you need to hit:

```
 http://<hots>:8080/propellerhead/v1/customer
```
# The test environment 

To Unit Test I am running Junit with Mockito and Powermock to be able to load static classes if needed.
For integration tests I am using Arquillian, to run Unit and Integration tests run the command bellow and the Arquillian Chameleon will download a Wildfly container and will configure a H2 Database (in memory bank) to run the integration tests.


```

mvn clean install -Pwildfly-as-managed -P tests

```


# Assumptions


The "API" has an entity called **Customer** that has 1:N relationship  with  **Note**. 

```
Customer -> {Note1, Note2}

```

# Extras

## About API

```
DELETE Customer - Sending DELETE HTTP Verb to  http://<hots>:8080/propellerhead/v1/customer/<ID>
GET All Customers  - Sending GET HTTP Verb to  http://<hots>:8080/propellerhead/v1/customer/>
GET Information from a specific Customer - http://<hots>:8080/propellerhead/v1/customer/<ID>
GET All Notes from a specific Customer - http://<hots>:8080/propellerhead/v1/customer/<ID>/notes

```

## About architecture

This project are Ready to be a multistage pipeline and enable to produce the Java artifact and the application in a Docker fashion.
This brings a huge advantage to scale the application horizontally. The docker compose is written in the version 3, which makes possible deploy this application as stack and easily scale up and down.  

# Interacting with API

There are two ways to do that. Using the Swagger in  **http://<host>:8080/propellerhead/apidocs** or Using some Http Client. I suggest to use Postman, you can find a export to all the endpoints and some initial data [here](https://github.com/adriano-fonseca/propellerhead-assigment/blob/master/src/main/postman/customer_propellerhead_postman.json) 

![Alt](https://github.com/adriano-fonseca/propellerhead/blob/master/src/main/prints/postman.png?raw=true "Postman")



# Improvements

In Order to pursue the HATEOS level 2 at least, I try to keep the conventions about the HTTP Verbs, to use post to create data, delete to remove and get to retrieve them. When some change in Data is tried using POST The system generate a HTTP 405 (method not allowed).
 
From the architecture view I think that would be interesting to put a NGINX as reverse proxy to the application providing HTTPS. Another thing to be improved is the security a JWT could be applied to authenticate the requests to the API. I simulated the login process but the user login information are mocked.