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
    <version>0.0.1-SNAPSHOT</version>
</dependency>
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
@WebController(path = "ping")
public class PingController {
    
    @GetMapping
    public String ping() {
        return "pong";
    }
}
```