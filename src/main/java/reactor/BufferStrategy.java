package reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;


public class BufferStrategy implements Strategy {
    @Override
    public void publish() {

        Flux.range(1, 100)
                .log()
                .subscribe(new Subscriber<>() {
                    private Subscription s;
                    int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(8);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(Thread.currentThread().getName() + " Received = " + integer);
                        onNextAmount++;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (onNextAmount % 8 == 0) {
                            s.request(8);
                        }
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
                });
    }
}
