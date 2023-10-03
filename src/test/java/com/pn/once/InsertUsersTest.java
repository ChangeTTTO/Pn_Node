package com.pn.once;
import com.pn.model.domain.User;
import com.pn.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * 导入用户测试
 * @author PN
 */
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    //新建一个线程池， 默认最小核心线程数40，最大线程数1000，如果实在是忙不过来了满了默认就会抛异常
    //10000：十分钟。这是非核心线程（即超过核心线程数的线程）的最大空闲时间。如果一个线程在空闲时间超过这个值，它将被终止并从线程池中移除。
    //new ArrayBlockingQueue<>(10000)：这是用于存储待执行任务的阻塞队列。
    // ArrayBlockingQueue 是一个有界队列，最多可以容纳10000个任务。当线程池中的线程都在执行任务时，新的任务会被放入这个队列中等待执行。
    /**
     * 这段代码创建了一个线程池，它有40个核心线程，最多可以有1000个线程，非核心线程的最大空闲时间为10分钟，
     * 任务队列可以容纳最多10000个待执行任务。这个线程池适用于需要处理大量任务但要限制线程数量的情况，以避免资源耗尽和性能问题。
     */
    private ExecutorService executorService =
            new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("测试机器人");
            user.setUserAccount("bot");
            user.setAvatarUrl("https://rxbby.oss-cn-guangzhou.aliyuncs.com/1200274f-eb49-4e59-969d-95b4812e78f3.jpg");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            userList.add(user);
        }
        // 20 秒 10 万条
        userService.saveBatch(userList, 10000);  //批量插入
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("测试机器人");
                user.setUserAccount("bot");
                user.setAvatarUrl("https://rxbby.oss-cn-guangzhou.aliyuncs.com/1200274f-eb49-4e59-969d-95b4812e78f3.jpg");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("11111111");
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行,CPU多少核就多少线程
            //java并发编程
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
