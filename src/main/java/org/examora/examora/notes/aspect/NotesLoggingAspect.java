package org.examora.examora.notes.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
@Slf4j
@Aspect
@Component
public class NotesLoggingAspect {
    private static final long SEUIL_LENTEUR_MS = 500;
    @Pointcut("execution(* org.examora.examora.notes.service.*.*(..))")
    public void notesServicePointcut() {}
    @Pointcut("execution(* org.examora.examora.notes.controller.*.*(..))")
    public void notesControllerPointcut() {}
    @Around("notesServicePointcut()")
    public Object loggerAppelService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        log.debug("[NOTES-AOP] → {}.{}() | args: {}", className, methodName,
                Arrays.toString(args));
        long debut = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duree = System.currentTimeMillis() - debut;

            if (duree > SEUIL_LENTEUR_MS) {
                log.warn("[NOTES-AOP]  LENTEUR {}.{}() — {}ms (seuil: {}ms)",
                        className, methodName, duree, SEUIL_LENTEUR_MS);
            } else {
                log.debug("[NOTES-AOP] ← {}.{}() | {}ms", className, methodName, duree);
            }

            return result;

        } catch (Exception ex) {
            long duree = System.currentTimeMillis() - debut;
            log.error("[NOTES-AOP] ✗ {}.{}() ÉCHEC après {}ms | Erreur: {}",
                    className, methodName, duree, ex.getMessage());
            throw ex;
        }
    }
    @AfterThrowing(
            pointcut = "notesServicePointcut() || notesControllerPointcut()",
            throwing = "ex"
    )
    public void loggerException(JoinPoint joinPoint, Exception ex) {
        String className  = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("[NOTES-AOP] Exception dans {}.{}() — Type: {} | Message: {}",
                className, methodName, ex.getClass().getSimpleName(), ex.getMessage());
    }

    @Before("notesControllerPointcut()")
    public void loggerRequeteEntrant(JoinPoint joinPoint) {
        log.info("[NOTES-API]  Requête reçue : {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    @After("notesControllerPointcut()")
    public void loggerRequeteTerminee(JoinPoint joinPoint) {
        log.info("[NOTES-API]  Requête traitée : {}.{}()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }
}
