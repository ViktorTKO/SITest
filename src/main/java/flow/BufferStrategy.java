package flow;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;

public class BufferStrategy implements Strategy {

    @Override
    public void publish() throws InterruptedException {
        final int BUFFER = 8;

        SubmissionPublisher<Integer> publisherBP = new SubmissionPublisher<>(ForkJoinPool.commonPool(), BUFFER);

        Subscriber<Integer> subscriberBP = new Subscriber<>() {
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
        publisherBP.subscribe(subscriberBP);

        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " Publishing = " + i);
            publisherBP.submit(i);
        }

        publisherBP.close();

        Thread.sleep(100000);
    }
}

