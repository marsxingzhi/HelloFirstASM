package thread;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTest {

    private int intValue = -1;
    private long longValue;

    public void query() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("query---线程执行任务");
            }
        });
        thread.start();

        Thread thread1 = new Thread("sss");
        thread1.start();
    }

//    public void queryShadow() {
//        ShadowThread thread = new ShadowThread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("queryShadow---线程执行任务");
//            }
//        }, "ShadowThread");
//        thread.start();
//    }
}
