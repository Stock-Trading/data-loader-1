---
applyTo: "src/test/**/*"
---

# Unit tests requirements
- use Behavior-Driven Development
- use JUnit 5 and its 'Assertions' (deliberate choice)
- prefer pure unit tests (no `@SpringBootTest`) for domain/service/mapper classes; reserve Spring context tests for integration-level tests
- use Mockito for mocking dependencies (constructor injection via `@Mock`/`@InjectMocks` or manual mocks)
- annotate test classes with `@ExtendWith(MockitoExtension.class)`
- use given, when, then convention with one line of space between each section; within the section do not use empty lines if not needed for clarity
- use `@DisplayName` annotation with given, when and then sections. Each section should start at new line and end with comma, use java text blocks for strings
- for test method name you can use just "should"

Full example combining the conventions above:

```java
@Test
@DisplayName("""
    given valid financial instrument model that is not yet subscribed,
    when subscribeToTheInstrument is called,
    then it should save the instrument and publish a subscription event
    """)
void should() {
    // given
    given(repository.existsByName(model.name())).willReturn(false);
    given(repository.existsBySymbol(model.symbol())).willReturn(false);
    given(repository.save(model)).willReturn(model);

    // when
    FinancialInstrumentModel result = service.subscribeToTheInstrument(model);

    // then
    assertEquals(model, result);
    verify(eventPublisher).publishEvent(any(FinancialInstrumentSubscriptionStateChangedEvent.class));
}
```

- usually write 2 or 3 "normal scenarios" (happy and unhappy paths) as well as corner cases if you figure out any. Include a test for each custom exception path the class under test can throw
- name test classes `<ClassUnderTest>Test` (singular) to distinguish from integration tests, unless testing package-private classes where the test must reside in the same package

In case of doubts, ask before proceeding

# Build and Test Tools
- Use Maven for running tests