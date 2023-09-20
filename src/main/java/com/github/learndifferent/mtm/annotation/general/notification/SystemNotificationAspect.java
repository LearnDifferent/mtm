package com.github.learndifferent.mtm.annotation.general.notification;

import com.github.learndifferent.mtm.annotation.general.notification.SystemNotification.MessageType;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.IpUtils;
import com.github.learndifferent.mtm.utils.LoginUtils;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Send System Notification
 *
 * @author zhou
 * @date 2022/4/12
 */
@Aspect
@Component
public class SystemNotificationAspect {

    private final NotificationService notificationService;

    public SystemNotificationAspect(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Around("@annotation(systemNotification)")
    public Object around(ProceedingJoinPoint pjp, SystemNotification systemNotification) throws Throwable {

        MessageType messageType = systemNotification.messageType();

        switch (messageType) {
            case LOGOUT:
                return sendLogoutMessage(pjp);
            case LOGIN:
                return sendLoginMessage(pjp);
            case NEW_USER:
                return sendNewUserMessage(pjp);
            default:
                return doNotSend(pjp);
        }
    }

    private Object doNotSend(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

    private Object sendLogoutMessage(ProceedingJoinPoint pjp) throws Throwable {
        // before
        sendNotification("Logout (" + getTime() + ")", getUsername());

        return pjp.proceed();
    }

    private Object sendLoginMessage(ProceedingJoinPoint pjp) throws Throwable {

        Object result = pjp.proceed();
        // after
        sendNotification("Login (" + getTime() + ")", getUsername());

        return result;
    }

    private Object sendNewUserMessage(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        // get the ResultVO<String>
        ResultVO<String> r = (ResultVO<String>) result;
        sendNotification("New User", r.getData());

        return result;
    }

    private String getTime() {
        return "UTC " + DateTimeFormatter
                .ofPattern("HH:mm")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    private String getUsername() {
        boolean isGuest = LoginUtils.isGuest();
        // if the user is Guest, return user's IP
        return isGuest ? getIp() : LoginUtils.getCurrentUsername();
    }

    private String getIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        return IpUtils.getIp(request);
    }

    private void sendNotification(String title, String msg) {
        notificationService.sendSystemNotification(title, msg);
    }
}