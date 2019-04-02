package com.soeasy.wordfilter.service;

import com.soeasy.wordfilter.utils.MD5Util;
import org.junit.Test;

/**
 * IndexService Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>四月 2, 2019</pre>
 */
public class IndexServiceTest {


    /**
     * Method: checkUser(String name, String pwd)
     */
    @Test
    public void testCheckUser() throws Exception {

        System.out.println(MD5Util.md5("guest"));
    }


} 
