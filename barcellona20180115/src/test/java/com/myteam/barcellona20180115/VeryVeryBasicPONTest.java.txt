package com.myteam.barcellona20180115;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class VeryVeryBasicPONTest extends JbpmJUnitBaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(VeryVeryBasicPONTest.class);

    public VeryVeryBasicPONTest() {
        super(true, true);
    }

    @Test
    public void test_basic() {
        Map<String, ResourceType> resources = new HashMap<>();
        resources.put("com/myteam/barcellona20180115/PONProcess.bpmn2", ResourceType.BPMN2);
        resources.put("com/myteam/barcellona20180115/PON.dmn", ResourceType.DMN);
        RuntimeManager manager = createRuntimeManager(resources);
        RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
        AuditService auditService = runtimeEngine.getAuditService();

        final String USER_ID = "krisv";

        ProcessInstance processInstance = ksession.startProcess("barcellona20180115.PONProcess");
        long processInstanceId = processInstance.getId();

        assertNodeTriggered(processInstanceId, "printout myProcessVariable");

        assertEquals("positive", auditService.findVariableInstances(processInstanceId, "myProcessVariable").get(0).getValue());
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
