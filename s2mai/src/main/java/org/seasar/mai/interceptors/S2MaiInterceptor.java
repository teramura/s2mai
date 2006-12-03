/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.mai.interceptors;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.MethodUtil;
import org.seasar.mai.S2MaiConstants;
import org.seasar.mai.mail.MailExceptionHandler;
import org.seasar.mai.mail.SendMail;
import org.seasar.mai.mail.impl.MailExceptionHandlerImpl;
import org.seasar.mai.meta.MaiMetaData;
import org.seasar.mai.meta.MaiMetaDataFactory;
import org.seasar.mai.property.PropertyWriterForBean;
import org.seasar.mai.template.ContextHelper;
import org.seasar.mai.template.TemplateProcessor;

import com.ozacc.mail.Mail;
import com.ozacc.mail.MailException;

/**
 * @author Satoshi Kimura
 */
public class S2MaiInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = -3003054499497347849L;

    private Logger logger = Logger.getLogger(S2MaiInterceptor.class);

    private MaiMetaDataFactory maiMetaDataFactory;

    private SendMail sendMail;

    private MailExceptionHandler mailExceptionHandler = new MailExceptionHandlerImpl();

    private PropertyWriterForBean propertyWriter;

    private ContextHelper contextHelper;

    private TemplateProcessor templateProcessor;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (isGetSendMail(method)) {
            return sendMail.clone();
        }
        if (!MethodUtil.isAbstract(method)) {
            return invocation.proceed();
        }
        init();
        MaiMetaData metaData = maiMetaDataFactory.getMaiMetaData(getTargetClass(invocation));
        Object bean = getBean(invocation);
        Object context = contextHelper.createContext(bean);
        Mail mail = createMail(method, context, metaData);
        propertyWriter.setMailProperty(mail, bean);        
        SendMail sendMail = (SendMail) this.sendMail.clone();
        propertyWriter.setServerProperty(sendMail, bean);
        send(mail, sendMail);
        return null;
    }

    private boolean isGetSendMail(Method method) {
        if ("getSendMail".equals(method.getName()) && SendMail.class.equals(method.getReturnType())) {
            return true;
        } else {
            return false;
        }
    }
    
    private Object getBean(MethodInvocation invocation){
        Object[] arguments = invocation.getArguments();
        if (arguments == null || arguments.length == 0) {
            return null;
        }
        return arguments[0];
    }

    private void send(Mail mail, SendMail sendMail) {
        logger.debug("send mail...");
        logger.debug(mail);
        try {
            sendMail.send(mail);
        } catch (MailException e) {
            mailExceptionHandler.handle(e);
        }
        logger.debug("success send mail.");
    }

    private Mail createMail(Method method, Object context, MaiMetaData metaData) {
        Mail mail = metaData.getMail(method);
        String path = metaData.getTemplatePath(method);
        String text = templateProcessor.processResource(path, context);
        String subject = getSubject(text);
        text = getText(text);
        if(subject != null){
            mail.setSubject(subject);
        }
        mail.setText(text);
        return mail;
    }

    private void init() {
        templateProcessor.init();
    }

    private String getText(String text) {
        if (text.startsWith(S2MaiConstants.TEMPLATE_SUBJECT) == false) {
            return text;
        }
        return text.substring(text.indexOf("\n") + "\r\n".length() + 1, text.length());
    }

    private String getSubject(String text) {
        if (text.startsWith(S2MaiConstants.TEMPLATE_SUBJECT) == false) {
            return null;
        }
        return text.substring(S2MaiConstants.TEMPLATE_SUBJECT.length(), text.indexOf("\r"));
    }

    public void setMaiMetaDataFactory(MaiMetaDataFactory maiMetaDataFactory) {
        this.maiMetaDataFactory = maiMetaDataFactory;
    }

    public void setSendMail(SendMail sendMail) {
        this.sendMail = sendMail;
    }

    public void setMailExceptionHandler(MailExceptionHandler mailExceptionHandler) {
        this.mailExceptionHandler = mailExceptionHandler;
    }

    /**
     * @param propertyWriter The propertyWriter to set.
     */
    public void setPropertyWriter(PropertyWriterForBean propertyWriter) {
        this.propertyWriter = propertyWriter;
    }

    public void setContextHelper(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

}
