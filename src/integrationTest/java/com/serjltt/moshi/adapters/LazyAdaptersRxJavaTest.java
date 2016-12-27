package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** Tests if specific adapters behave properly with RxJava2. */
@RunWith(Parameterized.class)
public final class LazyAdaptersRxJavaTest {
  private static final Moshi MOSHI = new Moshi.Builder()
      .add(WrappedJsonAdapter.FACTORY)
      .build();

  private static Callable<String> failingCallable() {
    Set<Annotation> annotations =
        Collections.<Annotation>singleton(Wrapped.Factory.create(true, "one", "two", "three"));
    final JsonAdapter<String> adapter = MOSHI.adapter(String.class, annotations);

    return new Callable<String>() {
      @Override public String call() throws Exception {
        return adapter.fromJson("{\n"
            + "  \"one\": {\n"
            + "    \"two\": null\n"
            + "  }\n"
            + "}");
      }
    };
  }

  @Parameterized.Parameters
  public static Collection<TestObserver<?>> testData() {
    return Arrays.asList(new TestObserver<?>[] {
        Single.fromCallable(failingCallable()).test(),
        Observable.fromCallable(failingCallable()).test(),
        Maybe.fromCallable(failingCallable()).test()
    });
  }

  private final TestObserver<?> testObserver;

  public LazyAdaptersRxJavaTest(TestObserver<?> testObserver) {
    this.testObserver = testObserver;
  }

  @Test public void reactiveTypeYieldsAppropriateError() throws Exception {
    testObserver
        .assertError(JsonDataException.class)
        .assertErrorMessage(
            "Wrapped Json expected at path: [one, two, three]. Found null at $.one.two");
  }
}
