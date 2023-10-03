package com.pn.once;
import com.pn.mapper.UserMapper;
import com.pn.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import javax.annotation.Resource;

/**
 * 导入用户任务
 * @author PN
 */
@Component  //弄成个bean
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)   定时器
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();//计时对象
        System.out.println("goodgoodgood");
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("PN");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
