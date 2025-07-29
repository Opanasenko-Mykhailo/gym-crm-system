package com.gcs.app.repository.transaction.aspect;

import com.gcs.app.repository.transaction.TransactionalContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymTransactionAspectTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private TransactionalContext annotation;

    @InjectMocks
    private TransactionalContextAspect aspect;

    @Test
    @DisplayName("should commit transaction when readOnly is false")
    void shouldCommitTransactionWhenReadOnlyFalse() throws Throwable {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(annotation.readOnly()).thenReturn(false);
        when(pjp.proceed()).thenReturn("result");

        Object actual = aspect.wrapInTransaction(pjp, annotation);

        assertEquals("result", actual);
        verify(session).setDefaultReadOnly(false);
        verify(transaction).commit();
        verify(transaction, never()).rollback();
        verify(pjp).proceed();
    }

    @Test
    @DisplayName("should rollback transaction when readOnly is true")
    void shouldRollbackTransactionWhenReadOnlyTrue() throws Throwable {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(annotation.readOnly()).thenReturn(true);
        when(pjp.proceed()).thenReturn("readonly");

        Object actual = aspect.wrapInTransaction(pjp, annotation);

        assertEquals("readonly", actual);
        verify(session).setDefaultReadOnly(true);
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(pjp).proceed();
    }

    @Test
    @DisplayName("should rollback transaction when exception occurs")
    void shouldRollbackTransactionOnException() throws Throwable {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(annotation.readOnly()).thenReturn(false);
        when(transaction.isActive()).thenReturn(true);
        when(pjp.proceed()).thenThrow(new IllegalStateException("Simulated failure"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> aspect.wrapInTransaction(pjp, annotation));

        assertEquals("Simulated failure", ex.getMessage());
        verify(transaction).rollback();
        verify(transaction, never()).commit();
        verify(pjp).proceed();
    }
}
