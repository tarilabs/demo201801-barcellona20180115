package com.myteam.barcellona20180115;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class VeryVeryBasicTest extends JbpmJUnitBaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(VeryVeryBasicTest.class);

    public VeryVeryBasicTest() {
        super(true, true);
    }

    @Test
    public void testDMN() {
        KieServices kieServices = KieServices.Factory.get();

        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_ef55e351-da15-454b-90ef-b69638bfd011", "PON");
        {
            DMNContext dmnContext = dmnRuntime.newContext();
            dmnContext.set("N", 47);

            DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
            System.out.println(dmnResult);

            for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
                System.out.println("Decision '" + dr.getDecisionName() + "' : " + dr.getResult());
            }

            assertEquals("positive", dmnResult.getDecisionResultByName("PON").getResult());
        }
        {
            DMNContext dmnContext = dmnRuntime.newContext();
            dmnContext.set("N", -999);

            DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
            System.out.println(dmnResult);

            for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
                System.out.println("Decision '" + dr.getDecisionName() + "' : " + dr.getResult());
            }

            assertEquals("negative", dmnResult.getDecisionResultByName("PON").getResult());
        }
    }
}
