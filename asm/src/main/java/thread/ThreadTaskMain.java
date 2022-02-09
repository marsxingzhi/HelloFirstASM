package thread;

/**
 * Created by JohnnySwordMan on 2/3/22
 */
class ThreadTaskMain {

    public static void main(String[] args) {
//        try {
//            Class<?> clazz = Class.forName("thread.ThreadTest");
//            ThreadTest instance = (ThreadTest) clazz.newInstance();
//            instance.query();
//            System.out.println(clazz);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        ThreadTest threadTest = new ThreadTest();
        threadTest.query();
    }
}
