package factorygirl4j;

import factorygirl4j.domain.Post;
import factorygirl4j.domain.User;
import factorygirl4j.dsl.SaveStrategy;
import factorygirl4j.exception.FactoryGirlException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static factorygirl4j.dsl.FactoryGirl.build;
import static factorygirl4j.dsl.FactoryGirl.create;
import static factorygirl4j.dsl.StartKeyValuePairs.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class FactoryIntegrationTest {

    @Mock
    SaveStrategy saveStrategy;

    @Test
    public void testWithFactoryNameAndType(){
        // When
        final User myObj = build("user");

        // Then
        assertThat(myObj.getFirstName(), is("John"));
        assertThat(myObj.getLastName(), is("Doe"));
        assertThat(myObj.getEmail(), is("test@example.com"));
    }

    @Test
    public void testWithFactoryNameOnlyNonambiguous(){
        // When
        final User myObj = build("author");

        // Then
        assertThat(myObj.getFirstName(), is("John"));
        assertThat(myObj.getLastName(), is("Doe"));
        assertThat(myObj.getEmail(), is("test@example.com"));
    }

    @Test
    public void testWithFactoryNameOnlyFactoryDoesNotExist(){
        // When
        final FactoryGirlException top = assertThrows(FactoryGirlException.class,
                () -> build("somefactoryname"));

        assertThat(top.getCause().getMessage(), is("factory named somefactoryname does not exist"));
    }

    @Test
    public void testWithFactoryNameOnlyAmbiguousType(){
        // When
        final FactoryGirlException top = assertThrows(FactoryGirlException.class,
                () -> build("otherclass"));

        assertThat(top.getCause(), is(instanceOf(IllegalArgumentException.class)));
        assertThat(top.getCause().getMessage(), is("found multiple classes for name otherclass"));
    }

    @Test
    public void testFactoryWithAssociationNameSameAsFactoryName(){
        // When
        final Post objectUnderTest = build("post");

        // Then
        assertThat(objectUnderTest.getAuthor().getFirstName(), is("John"));
        assertThat(objectUnderTest.getAuthor().getLastName(), is("Doe"));
        assertThat(objectUnderTest.getAuthor().getEmail(), is("test@example.com"));
    }

    @Test
    public void testFactoryWithAssociationNameDifferentThenFactoryName(){
        // When
        final Post objectUnderTest = build("postByFactory");

        // Then
        assertThat(objectUnderTest.getAuthor().getFirstName(), is("John"));
        assertThat(objectUnderTest.getAuthor().getLastName(), is("Writely"));
        assertThat(objectUnderTest.getAuthor().getEmail(), is("test@example.com"));
    }


    @Test
    public void testFactoryOverrideDuringBuild() {
        // When
        final User objectUnderTest = build("user",
                with("firstName", "Thomas")
                .with("lastName", "Anderson"));

        // Then
        assertThat(objectUnderTest.getFirstName(), is("Thomas"));
        assertThat(objectUnderTest.getLastName(), is("Anderson"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateWithNameOnly(){
        // Given
        given(saveStrategy.save(any())).willAnswer(invocation -> invocation.getArguments()[0]);

        // When
        final User saved = create("user", saveStrategy);

        // Then
        then(saveStrategy).should(times(1)).save(saved);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFactoryOverrideDuringCreate(){
        // Given
        given(saveStrategy.save(any())).willAnswer(invocation -> invocation.getArguments()[0]);

        // When
        final User objectUnderTest = create("user", saveStrategy,
                with("firstName", "Thomas")
                .with("lastName", "Anderson"));

        // Then
        assertThat(objectUnderTest.getFirstName(), is("Thomas"));
        assertThat(objectUnderTest.getLastName(), is("Anderson"));
    }

    @Test
    public void testFactoryWithOneBuildCallback(){
        // When
        final User objectUnderTest = build("oneBuildCallback");

        // Then
        assertThat(objectUnderTest.getFirstName(), is("JOHN"));
    }

    @Test
    public void testFactoryWithTwoBuildCallbacks(){
        // When
        final User objectUnderTest = build("twoBuildCallbacks");

        // Then
        assertThat(objectUnderTest.getFirstName(), is("JOHN"));
        assertThat(objectUnderTest.getLastName(), is(objectUnderTest.getFirstName()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFactoryWithOneCreateCallback(){
        // Given
        given(saveStrategy.save(any())).willAnswer(invocation -> invocation.getArguments()[0]);

        // When
        final User objectUnderTest = create("oneCreateCallback", saveStrategy);

        // Then
        then(saveStrategy).should(times(1)).save(any(User.class));
        assertThat(objectUnderTest.getFirstName(), is("JOHN"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFactoryWithOneCreateCallbackAndOneBuildCallback(){
        // Given
        given(saveStrategy.save(any())).willAnswer(invocation -> invocation.getArguments()[0]);

        // When
        final User objectUnderTest = create("mixedCallbacks", saveStrategy);

        // Then
        then(saveStrategy).should(times(1)).save(any(User.class));
        assertThat(objectUnderTest.getFirstName(), is("JOHN"));
        assertThat(objectUnderTest.getLastName(), is(objectUnderTest.getFirstName()));
    }
}
