# Kotlin Spring Rest Docs Demo

Kotlin 기반의 Spring API 를 Spring Rest docs 적용 Demo 입니다.


## 1. Spring Rest Docs 란?

> 테스트 기반으로 API 문서를 생성할 수 있는 툴입니다.<br/>
> Swagger에 비해 작성이 어려운 편 이지만 테스트가 통과하지 않을 경우 문서화가 되지 않고<br/>
> 코드가 수정 되었을 때 테스트 내 문서화 코드를 수정하지 않으면 테스트에 통과할 수 없어<br/>
> 유지보수에 더 유리합니다.

- 주의사항<br/>
해당 샘플은 통합 테스트 (@SpringBootTest) 기반의 설정을 전제로 합니다.<br/>
슬라이싱 테스트 시 설정이 달라질 수 있습니다.


## 2. Spring Rest Docs 의존성 추가

Spring Rest Docs 사용을 위한 의존성을 추가합니다. <br/>
테스트 시 사용되므로 test scope로 추가합니다.

```groovy
testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
```


## 3. 테스트 작성

### 3.1. BaseControllerTest 작성

여러 테스트 컨트롤러에서 상속받아 사용할 수 있도록 공통의 설정을 `BaseControllerTest`로 작성하였습니다.

```kotlin
@RunWith(SpringRunner::class) // 테스트러너 설정
@SpringBootTest // 스프링부트 통합테스트
@ActiveProfiles("test") // test profile 사용
@AutoConfigureRestDocs // spring rest docs 
@AutoConfigureMockMvc
@Import(RestDocsConfiguration::class) // 커스텀한 설정을 Import 함.
@Ignore // 상속받아 사용하는 클래스 이므로 테스트 돌릴때 무시한다.
class BaseControllerTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc // 테스트 요청을 보낼때 사용될 mockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper // Map(or Pojo) 를 Json String 으로 변환할때 사용 (Post 데이터 body에 사용)
}
```

### 3.2. 실제 컨트롤러 테스트 작성

실제 작성할 컨트롤러의 테스트를 요구사항과 의도에 맞게 작성합니다.

```kotlin
@Test
fun create_OK() {
    // given
    val body = mapOf(
        "name" to "그래들",
        "maker" to "나이키",
        "year" to 2000
    )

    // when
    val result = this.mockMvc.perform(
        post("/$END_POINT")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaTypes.HAL_JSON_UTF8)
            .content(this.objectMapper.writeValueAsBytes(body))
    )

    // then
    result
        // 데이터 검증
        .andExpect(status().isCreated)
        .andExpect(jsonPath("name").value(body.getValue("name")))
        .andExpect(jsonPath("maker").value(body.getValue("maker")))
        .andExpect(jsonPath("year").value(body.getValue("year")))
        // 테스트 결과 print (실패시에는 자동으로 찍히지만 성공시에는 자동으로 찍히지 않음)
        .andDo(print())
}
```

### 3.3. 문서화 코드 작성

위 내용은 일반적인 API 테스트 코드 입니다.<br/>
이에 조금 더 추가하여 문서조각을 생성하는 코드를 작성합니다.

```kotlin
@Test
fun create_OK() {
    // given
    val body = mapOf(
        "name" to "그래들",
        "maker" to "나이키",
        "year" to 2000
    )

    // when
    val result = this.mockMvc.perform(
        post("/$END_POINT")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaTypes.HAL_JSON_UTF8)
            .content(this.objectMapper.writeValueAsBytes(body))
    )

    // then
    result
        // 데이터 검증
        .andExpect(status().isCreated)
        .andExpect(jsonPath("name").value(body.getValue("name")))
        .andExpect(jsonPath("maker").value(body.getValue("maker")))
        .andExpect(jsonPath("year").value(body.getValue("year")))
        // 테스트 결과 print (실패시에는 자동으로 찍히지만 성공시에는 자동으로 찍히지 않음)
        .andDo(print())
        // 문서화 추가!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        .andDo(
            document(
                // 문서조각이 생성되는 디렉토리 지정 (build/generated-snippets 아래에 생성됩니다)
                "$END_POINT/create",
                // 요청 바디 문서화
                requestFields(
                    fieldWithPath("name").description("팀명"),
                    fieldWithPath("maker").description("브랜드"),
                    fieldWithPath("year").description("연식")
                ),
                // 응답 바디 문서화
                responseFields(
                    fieldWithPath("id").description("키"),
                    fieldWithPath("name").description("팀명"),
                    fieldWithPath("maker").description("브랜드"),
                    fieldWithPath("year").description("연식"),
                    fieldWithPath("state").description("상태"),
                    fieldWithPath("createdAt").description("생성일시"),
                    fieldWithPath("updatedAt").description("수정일시"),
                    fieldWithPath("_links.self.href").description("조회링크"),
                    fieldWithPath("_links.list.href").description("목록링크")
                )
            )
        )
}
```

### 3.4. Spring Rest Docs 자바 설정 추가 (Optional)

기본적으로는 자동설정(@AutoConfigureRestDocs) 을 사용하지만 커스텀하고 싶다면 테스트용 설정으로 작성할 수 있습니다.<br/>
해당 설정 작성 후 `BaseControllerTest`에 `@Import`에 추가해줍니다.

```kotlin
@TestConfiguration
class RestDocsConfiguration {

    @Bean
    fun restDocsMockMvcConfigurationCustomizer(): RestDocsMockMvcConfigurationCustomizer {
        return RestDocsMockMvcConfigurationCustomizer {
            it.operationPreprocessors()
                .withRequestDefaults(prettyPrint()) // 문서 조각 생성시 예쁘게 포매팅
                .withResponseDefaults(prettyPrint()) // 문서 조각 생성시 예쁘게 포매팅
        }
    }
}
```

## 4. 테스트 실행 및 문서조각 확인

- 테스트 실행

```
./gradlew clean test --info
```

- 문서조각 확인 (build/generated-snippets/ 아래)

![스크린샷 2019-03-26 오후 12 18 59](https://user-images.githubusercontent.com/12427330/54969418-60a1a700-4fc1-11e9-8f23-400a7cf571c6.png)


## 5. 문서조각을 기반으로 완성된 문서를 작성

위에 생성된 문서조각으로 입맛에 맞게 완성된 문서를 adoc으로 작성합니다.

### 5.1. `src/docs/asciidoc/index.adoc` 으로 파일을 생성 및 디자인합니다.

### 5.2. 문서조각 snippets 위치 지정

문서조각을 include 하기 위해 해당 경로를 변수(?)로 지정합니다.

```adoc
:snippets: ../../../build/generated-snippets
```

### 5.3. 문서조각 include

원하는 위치에 원하는 문서조각을 include 합니다. (인텔리제이의 Preview 를 사용하면 더 편합니다.)

```adoc
include::{snippets}/teams/list/curl-request.adoc[]
```


## 6. adoc to html

빌드 시 완성된 adoc 문서를 html로 변환합니다.<br/>

### 6.1. Asciidoctor gradle plugin 추가

빌드 시 adoc 파일을 html로 변환하여 서빙하기 위해 해당 플러그인 추가합니다.

- Plugin DSL
```groovy
plugins {
    id 'org.asciidoctor.convert' version '1.5.9.2'
    ...
}
```

- buildscript
```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'org.asciidoctor:asciidoctor-gradle-plugin:1.5.9.2'
    }
}

apply plugin: 'org.asciidoctor.convert'
```

### 6.2. 빌드 시 html 로 변환하기 위한 gradle task 추가

- gradle task 순서 : test -> asciidoctor -> build
- 해당 설정은 하기 나름입니다.

```groovy
// Spring Rest Docs 의 문서조각이 생성되는 기본 경로가 build/generated-snippets 입니다.
ext {
    snippetsDir = file('build/generated-snippets')
}

test {
    outputs.dir snippetsDir
}

asciidoctor {
    inputs.dir snippetsDir
    dependsOn test
}

// asciidoctor task 마지막에 할 작업
asciidoctor.doLast {
    copy {
        from 'build/asciidoc/html5' // asciidoctor로 변환된 html의 위치
        into 'src/main/resources/static/docs' // 변환된 api 문서 html을 이동시켜 서빙할 위치
    }
}

// asciidoctor task 이후에 build task 실행
build {
    dependsOn asciidoctor
}    
```

## 7. build, 실행, API 문서 html 확인

위 과정까지 완성이 되면 빌드하고 실행하여 문서를 확인해 볼 수 있습니다.

- 빌드
```
./gradlew clean build --info
```

- 실행
```
java -jar build/libs/restdocdemo-0.0.1-SNAPSHOT.jar # 현재 프로젝트 기준
```

- API 문서 확인
```
http://localhost:8080/docs/index.html
```
