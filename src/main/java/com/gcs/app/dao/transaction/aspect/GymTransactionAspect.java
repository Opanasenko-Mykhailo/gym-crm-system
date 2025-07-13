package com.gcs.app.dao.transaction.aspect;

import com.gcs.app.dao.transaction.GymTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class GymTransactionAspect {

    private final SessionFactory sessionFactory;

    private static final ThreadLocal<Boolean> isTransactionActive = ThreadLocal.withInitial(() -> false);

    @Around("@annotation(transactional)")
    public Object wrapInTransaction(ProceedingJoinPoint pjp, GymTransactional transactional) throws Throwable {
        boolean outermost = !isTransactionActive.get();
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;

        if (outermost) {
            tx = session.beginTransaction();
            session.setDefaultReadOnly(transactional.readOnly());
            isTransactionActive.set(true);
            log.debug("Transaction started [{}], readOnly={}", pjp.getSignature(), transactional.readOnly());
        }

        try {
            Object result = pjp.proceed();

            if (outermost) {
                if (transactional.readOnly()) {
                    tx.rollback();
                    log.debug("Transaction rolled back (readOnly) [{}]", pjp.getSignature());
                } else {
                    tx.commit();
                    log.debug("Transaction committed [{}]", pjp.getSignature());
                }
            }

            return result;

        } catch (Throwable ex) {
            if (outermost && tx != null && tx.isActive()) {
                tx.rollback();
                log.warn("Transaction rolled back due to exception [{}]: {}", pjp.getSignature(), ex.getMessage());
            }
            throw ex;
        } finally {
            if (outermost) {
                isTransactionActive.remove();
            }
        }
    }
}
