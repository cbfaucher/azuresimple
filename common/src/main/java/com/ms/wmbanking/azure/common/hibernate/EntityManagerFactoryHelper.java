package com.ms.wmbanking.azure.common.hibernate;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Function;

public interface EntityManagerFactoryHelper {

    default Logger getLogger() {
        return LoggerFactory.getLogger(EntityManagerFactoryHelper.class);
    }

    EntityManagerFactory getEntityManagerFactory();

    @Transactional(Transactional.TxType.REQUIRED)
    default <R, T> R execute(final Function<EntityManager, R> fct) {

        val em = getEntityManagerFactory().createEntityManager();

        val txn = em.getTransaction();
        val txnActive = txn.isActive();
        if (!txnActive) {
            getLogger().info("HBM TXN NOT ACTIVE - Opening Txn...");
            txn.begin();
        }

        try {
            val returnValue = fct.apply(em);
            getLogger().info("FCT applied - Returning value: " + returnValue);
            if (!txnActive) {
                getLogger().info("Committing HBM TXN now.");
                txn.commit();
            }
            return returnValue;
        } catch (Exception e) {
            getLogger().warn("Caught exception while execution HBM FCT.", e);
            txn.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

}
