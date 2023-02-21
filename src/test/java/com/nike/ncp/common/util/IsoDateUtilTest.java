package com.nike.ncp.common.util;

import com.nike.ncp.common.service.RestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Date;



@RunWith(MockitoJUnitRunner.class)
public class IsoDateUtilTest {

    MockedStatic<IsoDateUtil> mock;

    @Before
    public void setUp() {
        mock = Mockito.mockStatic(IsoDateUtil.class);
    }

    @Test
    public void fromDate(){
        mock.when(()->IsoDateUtil.fromDate(new Date())).thenReturn("");
        mock.when(()->IsoDateUtil.toDate("")).thenReturn(new Date());
        mock.when(()->IsoDateUtil.toUtc(LocalDateTime.now())).thenReturn(LocalDateTime.now());
    }

}
