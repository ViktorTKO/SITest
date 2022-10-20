package flow;


import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;

public class DropStrategy implements Strategy {
    @Override
    public void publish() throws InterruptedException {
        SubmissionPublisher<Integer> publisherDBP = new SubmissionPublisher<>();

        Subscriber<Integer> subscriberDBP = new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("Subscribed");
                this.subscription = subscription;
                subscription.request(1);
            }
            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " Received = " + item);
                // 200 миллисекунд для симуляции медленного консьюмера
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + " ERROR = "
                        + throwable.getClass().getSimpleName() + " " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        };

        publisherDBP.subscribe(subscriberDBP);

        for (int i = 0; i < 260; i++) {
            System.out.println();
            publisherDBP.offer(i, (s, a) -> {
                s.onError(new Exception("Can't handle backpressure. Dropping value " + a));
                return true;
            });
        }

        publisherDBP.close();

        Thread.sleep(100000);
    }
}
