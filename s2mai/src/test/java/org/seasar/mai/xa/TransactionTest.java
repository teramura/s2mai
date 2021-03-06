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
package org.seasar.mai.xa;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.mai.mail.AttachedFile;
import org.seasar.mai.mail.Mail;
import org.seasar.mai.unit.SendMailTestUtil;

/**
 * @author Satsohi Kimura
 */
public class TransactionTest extends S2TestCase {
    private TestService service;

    protected void setUp() throws Exception {
        include("test.dicon");
        SendMailTestUtil.init();
    }

    public void testCommit() {
        service.execute();
        Mail mail = SendMailTestUtil.getActualMail(0);
        assertNotNull(mail);
        try {
            SendMailTestUtil.getActualMail(1);
            fail();
        } catch (IndexOutOfBoundsException success) {
        }
    }

    public void testRollback() {
        try {
            service.throwSQLException();
            fail();
        } catch (SQLRuntimeException success) {
        }
        try {
            SendMailTestUtil.getActualMail(0);
            fail();
        } catch (IndexOutOfBoundsException success) {
        }
    }

    public void testRollbackAndCommit() {
        try {
            service.throwSQLException();
            fail();
        } catch (SQLRuntimeException success) {
        }
        try {
            SendMailTestUtil.getActualMail(0);
            fail();
        } catch (IndexOutOfBoundsException success) {
        }
        service.execute();

        Mail mail = SendMailTestUtil.getActualMail(0);
        assertNotNull(mail);
        try {
            SendMailTestUtil.getActualMail(1);
            fail();
        } catch (IndexOutOfBoundsException success) {
        }
    }
    
    public void testMultipleSending(){
        service.executeMultipleSending();
        Mail mail1 = SendMailTestUtil.getActualMail(0);
        Mail mail2 = SendMailTestUtil.getActualMail(1);
        String sub1 = mail1.getSubject();
        String sub2 = mail2.getSubject();
        
        assertFalse(sub1.equals(sub2));
        
        AttachedFile[] files1 =  mail1.getAttachedFiles();
        AttachedFile[] files2 =  mail2.getAttachedFiles();
        
        assertEquals(1, files1.length );
        assertEquals(1, files2.length );
        assertNotSame(files1[0].getFileName(),files2[0].getFileName());
        
    }
}
