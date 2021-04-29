package com.cymetrics.persistence.rdbms.dao;

import com.cymetrics.persistence.rdbms.entities.UserDo;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.Optional;

public class JpaDev {
    UserDaoJpa userDaoJpa = new UserDaoJpa();

    @PersistenceUnit(unitName="dev")
    EntityManagerFactory emf;

    @Test
    public void test(){
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory( "dev" );
        userDaoJpa.emf = emf;
        Optional<UserDo> optional = userDaoJpa.findById("781c9b3b-6114-4706-a93c-7d92e7738900");
        if(optional.isEmpty()) {
            System.out.println("empty");
        } else {
            System.out.println(optional.get().getId());
        }
    }
}
