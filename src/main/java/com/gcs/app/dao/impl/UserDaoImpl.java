package com.gcs.app.dao.impl;

import com.gcs.app.dao.UserDao;
import com.gcs.app.dao.transaction.GymTransactional;
import com.gcs.app.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    protected final SessionFactory sessionFactory;

    @GymTransactional
    @Override
    public User update(User user) {
        Session session = sessionFactory.getCurrentSession();

        User merged = session.merge(user);

        log.debug("Updated user: {}", merged.getUsername());
        return merged;
    }

    @GymTransactional(readOnly = true)
    @Override
    public Set<String> findAllUsernames() {
        Session session = sessionFactory.getCurrentSession();

        List<String> usernames = session
                .createQuery("SELECT u.username FROM User u", String.class)
                .getResultList();

        log.debug("Loaded usernames: {}", usernames);

        return new HashSet<>(usernames);
    }

    @GymTransactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();

        Optional<User> result = session
                .createQuery("FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();

        log.debug("findByUsername('{}') -> {}", username, result.map(u -> "found").orElse("not found"));

        return result;
    }
}
