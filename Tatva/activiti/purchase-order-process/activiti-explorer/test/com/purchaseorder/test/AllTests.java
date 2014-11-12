package com.purchaseorder.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.purchaseorder.test.steelpurchase.MaterialServiceTest;

@RunWith(Suite.class)
@SuiteClasses({ MaterialServiceTest.class })
public class AllTests {

}
