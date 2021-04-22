package hk.onedegree.persistence.rdbms;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.exceptions.RepositoryOperatorException;
import hk.onedegree.domain.auth.repository.UserRepository;
import hk.onedegree.persistence.rdbms.dao.UserDao;
import hk.onedegree.persistence.rdbms.entities.UserDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class RdbmsUserRepository implements UserRepository  {

    @Inject
    UserDao userDao;

    private static Logger logger = LoggerFactory.getLogger(RdbmsUserRepository.class);

    @Override
    public Optional<User> findByEmail(String email) throws RepositoryOperatorException {
        var userdo = userDao.findFirstByEmail(email);
        return convertUser(Optional.ofNullable(userdo));
    }

    @Override
    public Optional<User> findById(String id) throws RepositoryOperatorException {
        var optional = userDao.findById(id);
        return convertUser(optional);
    }

    private Optional<User> convertUser(Optional<UserDo> optional) throws RepositoryOperatorException {
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var userDo = optional.get();
        User user = null;
        try {
            user = new User(userDo.getId(), userDo.getEmail());
            user.setHashedPassword(userDo.getPassword());
            return Optional.of(user);
        } catch (InValidEmailException | InValidPasswordException e) {
            logger.error("Load user from db error: ", e);
            throw  new RepositoryOperatorException(e);
        }
    }

    @Override
    public void save(User user) {
        var userDo = new UserDo();
        userDo.setEmail(user.getEmail());
        userDo.setPassword(user.getHashedPassword());
        userDo.setId(user.getId());
        userDao.save(userDo);
    }

    @Override
    public void delete(User user) {
        var userDo = new UserDo();
        userDo.setEmail(user.getEmail());
        userDo.setPassword(user.getHashedPassword());
        userDo.setId(user.getId());
        userDao.delete(userDo);
    }
}
