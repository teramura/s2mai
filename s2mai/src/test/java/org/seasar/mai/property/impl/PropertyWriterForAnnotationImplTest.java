/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.mai.property.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.mai.mail.Mail;
import org.seasar.mai.mail.MailAddress;
import org.seasar.mai.property.PropertyWriterForAnnotation;

/**
 * @author rokugen
 */
public class PropertyWriterForAnnotationImplTest extends S2TestCase {
    private PropertyWriterForAnnotation propertyWriterForAnnotation;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PropertyWriterForAnnotationImplTest.class);
    }

    protected void setUp() throws Exception {
        include("PropertyWriterForAnnotationImplTest.dicon");
    }

    public void testSetProperty() throws SecurityException, NoSuchMethodException {
        Method method = AnnotationTestMai.class.getMethod("sendMail", null);
        Mail mail = new Mail();
        propertyWriterForAnnotation.setMailProperty(mail, method);

        assertEquals("method 1 to", "to@address", mail.getTo()[0].getAddress());
        assertEquals("method 1 from", "from@address", mail.getFrom().getAddress());
        assertEquals("method 1 cc", "cc@address", mail.getCc()[0].getAddress());
        assertEquals("method 1 bcc", "bcc@address", mail.getBcc()[0].getAddress());
        assertEquals("method 1 subject", "件名", mail.getSubject());
        assertEquals("method 1 reply-to", "reply-to@address", mail.getReplyTo().getAddress());
        assertEquals("method 1 return-path", "return-path@address", mail.getReturnPath().getAddress());

        method = AnnotationTestMai.class.getMethod("sendMail2", null);
        mail = new Mail();
        propertyWriterForAnnotation.setMailProperty(mail, method);

        assertEquals("method 2 to", "to@address", mail.getTo()[0].getAddress());
        assertEquals("method 2 from", "from@address", mail.getFrom().getAddress());
        assertEquals("method 2 cc", "cc@address", mail.getCc()[0].getAddress());
        assertEquals("method 2 bcc", "bcc@address", mail.getBcc()[0].getAddress());
        assertEquals("method 2 subject", "件名", mail.getSubject());
        assertEquals("method 2 reply-to", "reply-to@address", mail.getReplyTo().getAddress());
        assertEquals("method 2 return-path", "return-path@address", mail.getReturnPath().getAddress());

        method = AnnotationTestMai.class.getMethod("sendMail3", null);
        mail = new Mail();
        propertyWriterForAnnotation.setMailProperty(mail, method);

        assertEquals("method 3 to", "to2@address", mail.getTo()[0].getAddress());
        assertEquals("method 3 to name", "TO2送信先", mail.getTo()[0].getPersonal());
        assertEquals("method 3 from", "from2@address", mail.getFrom().getAddress());
        assertEquals("method 3 cc", "cc2@address", mail.getCc()[0].getAddress());
        assertEquals("method 3 bcc", "bcc2@address", mail.getBcc()[0].getAddress());
        assertEquals("method 3 subject", "件名2", mail.getSubject());
        assertEquals("method 3 reply-to", "reply-to2@address", mail.getReplyTo().getAddress());
        assertEquals("method 3 return-path", "return-path2@address", mail.getReturnPath().getAddress());

    }

    public void testSetPropertyWithArrayOrList() throws SecurityException, NoSuchMethodException {
        Method method = AnnotationTestMai2.class.getMethod("sendMail", null);
        Mail mail = new Mail();
        propertyWriterForAnnotation.setMailProperty(mail, method);

        assertEquals("to count", 2, mail.getTo().length);
        assertEquals("to 1", "to@address", mail.getTo()[0].getAddress());
        assertEquals("to 2", "to2@address", mail.getTo()[1].getAddress());
        assertEquals("cc count", 3, mail.getCc().length);
        assertEquals("cc 1", "cc@address", mail.getCc()[0].getAddress());
        assertEquals("cc 2", "cc2@address", mail.getCc()[1].getAddress());
        assertEquals("cc 3", "cc3@address", mail.getCc()[2].getAddress());

    }

    interface AnnotationTestMai {
        MailAddress TO = new MailAddress("to@address");

        MailAddress sendMail3_TO = new MailAddress("to2@address", "TO2送信先");

        String FROM = "from@address";

        String sendMail3_FROM = "from2@address";

        String CC = "cc@address";

        String sendMail3_CC = "cc2@address";

        String BCC = "bcc@address";

        String sendMail3_BCC = "bcc2@address";

        String SUBJECT = "件名";

        String sendMail3_SUBJECT = "件名2";

        String REPLY_TO = "reply-to@address";

        String sendMail3_REPLY_TO = "reply-to2@address";

        String RETURN_PATH = "return-path@address";

        String sendMail3_RETURN_PATH = "return-path2@address";

        void sendMail();

        void sendMail2();

        void sendMail3();
    }

    interface AnnotationTestMai2 {
        String[] TO = { "to@address", "to2@address" };

        List CC = Arrays.asList(new String[] { "cc@address", "cc2@address", "cc3@address" });

        void sendMail();
    }

    /**
     * @param propertyWriterForAnnotation
     *            The propertyWriterForAnnotation to set.
     */
    public final void setPropertyWriterForAnnotation(PropertyWriterForAnnotation propertyWriterForAnnotation) {
        this.propertyWriterForAnnotation = propertyWriterForAnnotation;
    }

}
