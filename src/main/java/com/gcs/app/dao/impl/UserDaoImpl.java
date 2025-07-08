package com.gcs.app.dao.impl;

import com.gcs.app.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    protected final SessionFactory sessionFactory;

    @Override
    public Set<String> findAllUsernames() {
        Session session = sessionFactory.getCurrentSession();

        List<String> usernames = session.createQuery("SELECT u.username FROM User u", String.class).getResultList();

        return new HashSet<>(usernames);
    }
}
