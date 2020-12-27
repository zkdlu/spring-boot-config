# Spring Cloud Config
분산시스템에서 설정파일을 외부로 분리하는 것을 지원함.

## 설정 파일을 관리 할 중앙 저장소 만들기
1. Github repository 생성 후 yaml 파일 저장
> https://github.com/zkdlu/my-config

```yml
# zkdlu-dev.yml
hello:
  world: dev
  
# zkdlu-prod.yml
hello:
  world: product
```
> 설정파일의 경우 {name}-{environment}.yml 형식으로 작성하도록 한다.

## Spring Cloud Config Server
1. Dependency 추가
```gradle
# build.gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-config-server'
}
```

2. application.yml 설정
```yml
# application.yml
spring:
  cloud:
    config:
      server:
        git:
          uri: "https://github.com/zkdlu/my-config"
          default-label: main
```
> 처음 시도 했을 때 **Could not fetch remote for master remote** 에러가 떴는데, 
>
> 찾아 보니 spring.cloud.config.server.git.refreshRate가 있는데 기본값이 0이라 healthcheck의 요청이 많아 생길 수 있으니 5초로 설정을 하라고 함
>
> 이것도 해보니 안되서 고민을 해보았는데, 에러 메시지에 **master**라는걸 보았을 때 브랜치명이 문제인 듯 하였다.
>
> git은 2.0부터는 기본 브랜치가 main으로 변경 되었기 때문에 기본 설정이 main으로 하여야 하나 봄.

3. EnableConfigServer 어노테이션 추가
```java
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
```

4. 확인
> http://localhost:8080/zkdlu/dev 로 접속 해보면 설정 파일을 읽어오는걸 확인할 수 있다.

## Spring Cloud Config Client
> **Spring boot 버전을 2.4.1로 시도를 하였는데 아직 지원을 안하나보다. 2.3.7버전으로 시도를 하니 바로 되었다.**

1. Dependency 추가
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
}
```

2. application.yml & bootstrap.yml 설정
> bootstrap.yml은 spring cloud 가 application.yml 보다 먼저 로드 하는 설정파일이다.
```yml
# application.yml
server:
  port: 8081
spring:
  application:
    name: zkdlu
```
> 중앙 저장소에 저장된 설정파일인 {application}-{environment}.yml의 형식에 맞춰 application name을 지정해준다.
```yml
# bootstrap.yml
spring:
  cloud:
    config:
      uri: http://localhost:8080
```

3. 사용
빌드 옵션으로 -Dspring.profiles.active=dev을 주면 zkdlu-dev.yml에 있는 설정을 @Value 어노테이션을 활용하여 간단하게 사용 가능하다.
```java
 @Value("${hello.world}")
private String test;
```

4. actuator를 활용한 Refresh
