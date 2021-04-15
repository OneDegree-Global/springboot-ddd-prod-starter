package hk.onedegree.persistence.rdbms.entities;

import hk.onedegree.persistence.rdbms.dao.UserDao;
import org.junit.jupiter.api.Test;

public class UserDaoTest {
    @Test
    public void forDev(){
        UserDao dao = new UserDao();
        UserDo userDo = new UserDo();
        userDo.setId("111");
        userDo.setEmail("111@gmail.com");
        userDo.setPassword("11111");

        dao.save(userDo);
    }
}
