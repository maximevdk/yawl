# Yet Another Web Library (framework?)
**Disclaimer**: This is a personal hobby project and is not intended for production use.  
I am developing my own web framework entirely from scratch, without any external assistance.  
With 11+ years of experience using Spring and Spring Boot, I have undoubtedly drawn some inspiration from them.  
However, while certain aspects may appear similar, all code has been written independently by me.

## Installation (maven)
```xml
<dependency>
    <groupId>com.maxime</groupId>
    <artifactId>yawl</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Building an executable jar
```xml

<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.7.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <archive>
            <manifest>
                <addClasspath>true</addClasspath>
                <mainClass>org.example.Application</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
</plugin>
```

## Usage

### Creating a new runnable application
```java
public class Application {
    public static void main(String[] args) {
        YawlApplication.run(Application.class, args);
    }
}
```

### Creating a controller

```java
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.WebController;

@WebController(path = "ping")
public class PingController {

    @GetMapping
    public String ping() {
        return "pong";
    }
}
```

### Creating a bean


#### Using @Service annotation
```java
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.Service;

@Service
public class PingService {
    public String ping() {
        return "pong";
    }
}
```

#### Using a configuration class
``` java
@Configuration
public class ApplicationConfiguration {
    @Bean(name = "pongDatabase")
    public InMemoryDatabase<String, Pong> pongDatabase() {
        return new InMemoryDatabase<>() {};
    }
}
```