package thread;

/**
 * Created by JohnnySwordMan on 2022/1/30
 */
public class ThreadTest {

    public void query() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("query---线程执行任务");
            }
        });
        thread.start();
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
