import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
  public static void main(String[] args) {
    AtomicReference<Integer> count = new AtomicReference<>(0);

    PublishSubject<Long> subject = PublishSubject.create();

    Observable<Long> observable =
        Observable.interval(500, TimeUnit.MILLISECONDS)
          .flatMap(number -> performRequest(count, subject));

    observable.takeUntil(subject)
        .blockingSubscribe(
            number -> System.out.println(number),
            Throwable::printStackTrace
        );
  }

  public static Observable<Long> performRequest(AtomicReference<Integer> count, PublishSubject subject) {
    return Observable.create(s -> {
      if (count.get() == 10) subject.onComplete();

      count.set(count.get() + 1);
      System.out.println("Performing request " + count.get());
      Thread.sleep(300);
      s.onNext(123L);
      s.onComplete();
    });
  }
}
