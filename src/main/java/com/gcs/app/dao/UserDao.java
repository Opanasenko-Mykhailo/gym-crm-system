package com.gcs.app.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserDao {

    protected final SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public Set<String> getAllUsernames() {
        Session session = sessionFactory.getCurrentSession();

        List<String> usernames = session.createQuery("SELECT u.username FROM User u", String.class).getResultList();

        return new HashSet<>(usernames);
    }
}
