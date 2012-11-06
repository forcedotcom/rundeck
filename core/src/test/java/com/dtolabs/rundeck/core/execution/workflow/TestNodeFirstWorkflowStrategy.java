/*
 * Copyright 2011 DTO Solutions, Inc. (http://dtosolutions.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
* TestNodeFirstWorkflowStrategy.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: 3/30/11 9:45 AM
* 
*/
package com.dtolabs.rundeck.core.execution.workflow;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.FrameworkProject;
import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.SelectorUtils;
import com.dtolabs.rundeck.core.execution.*;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepException;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepExecutor;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepExecutorService;
import com.dtolabs.rundeck.core.execution.workflow.steps.node.NodeStepResult;
import com.dtolabs.rundeck.core.execution.dispatch.Dispatchable;
import com.dtolabs.rundeck.core.execution.dispatch.DispatcherResult;
import com.dtolabs.rundeck.core.execution.service.NodeExecutorResult;
import com.dtolabs.rundeck.core.tools.AbstractBaseTest;
import com.dtolabs.rundeck.core.utils.FileUtils;
import com.dtolabs.rundeck.core.utils.NodeSet;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.tools.ant.BuildListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TestNodeFirstWorkflowStrategy is ...
 *
 * @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
 */
public class TestNodeFirstWorkflowStrategy extends AbstractBaseTest {
    Framework testFramework;
    String testnode;
    private File extResourcesfile;
    private File extResourcesfile2;
    private static final String TEST_PROJECT = "TestNodeFirstWorkflowStrategy";

    public TestNodeFirstWorkflowStrategy(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestNodeFirstWorkflowStrategy.class);
    }

    protected void setUp() {
        super.setUp();
        testFramework = getFrameworkInstance();
        testnode = testFramework.getFrameworkNodeName();
        final FrameworkProject frameworkProject = testFramework.getFrameworkProjectMgr().createFrameworkProject(
            TEST_PROJECT);
        File resourcesfile = new File(frameworkProject.getNodesResourceFilePath());
        //copy test nodes to resources file
        try {
            FileUtils.copyFileStreams(new File("src/test/resources/com/dtolabs/rundeck/core/common/test-nodes1.xml"),
                resourcesfile);
        } catch (IOException e) {
            throw new RuntimeException("Caught Setup exception: " + e.getMessage(), e);
        }
        extResourcesfile = new File("src/test/resources/com/dtolabs/rundeck/core/common/test-nodes2.xml");
        extResourcesfile2 = new File("src/test/resources/com/dtolabs/rundeck/core/common/test-nodes4.xml");
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        File projectdir = new File(getFrameworkProjectsBase(), TEST_PROJECT);
        FileUtils.deleteDir(projectdir);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    static class testWorkflowCmdItem implements ExecutionItem {
        private String type;
        int flag = -1;

        @Override
        public String toString() {
            return "testWorkflowCmdItem{" +
                   "type='" + type + '\'' +
                   ", flag=" + flag +
                   '}';
        }

        public String getType() {
            return type;
        }
    }

    static class testListener implements ExecutionListenerOverride {
        public boolean isTerse() {
            return false;
        }

        public String getLogFormat() {
            return null;
        }

        public void log(int i, String s) {
            System.err.println(i + ": " + s);
        }

        public FailedNodesListener getFailedNodesListener() {
            return null;
        }

        public void beginExecution(ExecutionContext context, ExecutionItem item) {
        }

        public void finishExecution(StatusResult result, ExecutionContext context, ExecutionItem item) {
        }

        public void beginNodeExecution(ExecutionContext context, String[] command, INodeEntry node) {
        }

        public void finishNodeExecution(NodeExecutorResult result, ExecutionContext context, String[] command,
                                        INodeEntry node) {
        }

        public void beginNodeDispatch(ExecutionContext context, ExecutionItem item) {
        }

        public void beginNodeDispatch(ExecutionContext context, Dispatchable item) {
        }

        public void finishNodeDispatch(DispatcherResult result, ExecutionContext context, ExecutionItem item) {
        }

        public void finishNodeDispatch(DispatcherResult result, ExecutionContext context, Dispatchable item) {
        }

        public void beginFileCopyFileStream(ExecutionContext context, InputStream input, INodeEntry node) {
        }

        public void beginFileCopyFile(ExecutionContext context, File input, INodeEntry node) {
        }

        public void beginFileCopyScriptContent(ExecutionContext context, String input, INodeEntry node) {
        }

        public void finishFileCopy(String result, ExecutionContext context, INodeEntry node) {
        }

        public void beginInterpretCommand(ExecutionContext context, ExecutionItem item, INodeEntry node) {
        }

        public void finishInterpretCommand(NodeStepResult result, ExecutionContext context, ExecutionItem item,
                                           INodeEntry node) {
        }

        public BuildListener getBuildListener() {
            return null;
        }


        public ExecutionListenerOverride createOverride() {
            return this;
        }

        public void setTerse(boolean terse) {
        }

        public void setLogFormat(String format) {
        }

        public void setFailedNodesListener(FailedNodesListener listener) {
        }
    }

    static class testInterpreter implements NodeStepExecutor {
        List<ExecutionItem> executionItemList = new ArrayList<ExecutionItem>();
        List<ExecutionContext> executionContextList = new ArrayList<ExecutionContext>();
        List<INodeEntry> nodeEntryList = new ArrayList<INodeEntry>();
        int index = 0;
        List<NodeStepResult> resultList = new ArrayList<NodeStepResult>();
        boolean shouldThrowException = false;

        public NodeStepResult executeNodeStep(ExecutionContext executionContext,
                                                 ExecutionItem executionItem, INodeEntry iNodeEntry) throws
                                                                                                     NodeStepException {
            executionItemList.add(executionItem);
            executionContextList.add(executionContext);
            nodeEntryList.add(iNodeEntry);
            if (shouldThrowException) {
                throw new NodeStepException("testInterpreter test exception");
            }
            return resultList.get(index++);
        }
    }

    static class testResult implements NodeStepResult {
        boolean success;
        int flag;

        testResult(boolean success, int flag) {
            this.success = success;
            this.flag = flag;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public void testMultipleNodes() {

        {
            //test jobref item
            final NodeSet nodeset = new NodeSet();
            nodeset.createInclude().setName(".*");
            final ArrayList<ExecutionItem> commands = new ArrayList<ExecutionItem>();
            final testWorkflowCmdItem item = new testWorkflowCmdItem();
            item.type = "my-type";
            commands.add(item);
            final WorkflowImpl workflow = new WorkflowImpl(commands, 1, false,
                WorkflowStrategy.STEP_FIRST);
            final WorkflowExecutionItemImpl executionItem = new WorkflowExecutionItemImpl(workflow);
            final NodeFirstWorkflowStrategy strategy = new NodeFirstWorkflowStrategy(testFramework);
            final com.dtolabs.rundeck.core.execution.ExecutionContext context =
                new ExecutionContextImpl.Builder()
                    .frameworkProject(TEST_PROJECT)
                    .user("user1")
                    .nodeSelector(nodeset)
                    .executionListener(new testListener())
                    .framework(testFramework).build();

            //setup testInterpreter for all command types
            final NodeStepExecutorService interpreterService = NodeStepExecutorService.getInstanceForFramework(
                testFramework);
            testInterpreter interpreterMock = new testInterpreter();
            testInterpreter failMock = new testInterpreter();
            failMock.shouldThrowException = true;
            interpreterService.registerInstance("my-type", interpreterMock);
            interpreterService.registerInstance("exec", failMock);
            interpreterService.registerInstance("script", failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_NODE_FIRST, failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_STEP_FIRST, failMock);

            //set resturn result node 1
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 2
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });

            final WorkflowExecutionResult result = strategy.executeWorkflow(context, executionItem);

            assertNotNull(result);
            if (!result.isSuccess() && null != result.getException()) {
                result.getException().printStackTrace(System.err);
            }
            assertNull("threw exception: " + result.getException(), result.getException());
            assertTrue(result.isSuccess());
            assertEquals(2, interpreterMock.executionItemList.size());
            assertEquals(2, interpreterMock.executionContextList.size());
            {
                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(0);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(0);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("test1"), executionContext.getNodeSelector());
            }
            {

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(1);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(1);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("testnode2"), executionContext.getNodeSelector());
            }
        }
    }

    public void testMultipleNodesExtFile() {

        {
            //test jobref item
            final NodeSet nodeset = new NodeSet();
            nodeset.createInclude().setName(".*");
            final ArrayList<ExecutionItem> commands = new ArrayList<ExecutionItem>();
            final testWorkflowCmdItem item = new testWorkflowCmdItem();
            item.type = "my-type";
            commands.add(item);
            final WorkflowImpl workflow = new WorkflowImpl(commands, 1, false,
                WorkflowStrategy.STEP_FIRST);
            final WorkflowExecutionItemImpl executionItem = new WorkflowExecutionItemImpl(workflow);
            final NodeFirstWorkflowStrategy strategy = new NodeFirstWorkflowStrategy(testFramework);
            final com.dtolabs.rundeck.core.execution.ExecutionContext context =
                new ExecutionContextImpl.Builder()
                    .frameworkProject(TEST_PROJECT)
                    .user("user1")
                    .nodeSelector(nodeset)
                    .executionListener(new testListener())
                    .framework(testFramework)
                    .nodesFile(extResourcesfile)
                    .build();
                    //specify ext resources file

            //setup testInterpreter for all command types
            final NodeStepExecutorService interpreterService = NodeStepExecutorService.getInstanceForFramework(
                testFramework);
            testInterpreter interpreterMock = new testInterpreter();
            testInterpreter failMock = new testInterpreter();
            failMock.shouldThrowException = true;
            interpreterService.registerInstance("my-type", interpreterMock);
            interpreterService.registerInstance("exec", failMock);
            interpreterService.registerInstance("script", failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_NODE_FIRST, failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_STEP_FIRST, failMock);

            //set resturn result node 1
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 2
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 3
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });

            final WorkflowExecutionResult result = strategy.executeWorkflow(context, executionItem);

            assertNotNull(result);
            if (!result.isSuccess() && null != result.getException()) {
                result.getException().printStackTrace(System.err);
            }
            assertNull("threw exception: " + result.getException(), result.getException());
            assertTrue(result.isSuccess());
            assertEquals(3, interpreterMock.executionItemList.size());
            assertEquals(3, interpreterMock.executionContextList.size());
            {
                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(0);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(0);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("test1"), executionContext.getNodeSelector());
            }
            {

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(1);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(1);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("testnode2"), executionContext.getNodeSelector());
            }
            {

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(2);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(2);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("testnode3"), executionContext.getNodeSelector());
            }
        }
    }

    public void testMultipleNodesRanked() {

        {
            //default (name), default order
            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode1");
            expected.add("testnode2");
            expected.add("testnode3");
            expected.add("testnode4");
            expected.add("testnode5");

            assertRankedNodeResult(expected, null, null);
        }
        {
            //default (name), ascending
            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode1");
            expected.add("testnode2");
            expected.add("testnode3");
            expected.add("testnode4");
            expected.add("testnode5");

            assertRankedNodeResult(expected, true, null);
        }
        {
            //default (name), descending

            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode5");
            expected.add("testnode4");
            expected.add("testnode3");
            expected.add("testnode2");
            expected.add("testnode1");

            assertRankedNodeResult(expected, false, null);
        }
        {
            //set to attribute "rank" ascending

            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode5");
            expected.add("testnode4");
            expected.add("testnode3");
            expected.add("testnode2");
            expected.add("testnode1");

            assertRankedNodeResult(expected, true, "rank");
        }
        {
            //set to attribute "rank" descending

            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode1");
            expected.add("testnode2");
            expected.add("testnode3");
            expected.add("testnode4");
            expected.add("testnode5");


            assertRankedNodeResult(expected, false, "rank");
        }
        {
            //set to attribute "colorRank" ascending

            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode1");
            expected.add("testnode3");
            expected.add("testnode5");
            expected.add("testnode2");
            expected.add("testnode4");


            assertRankedNodeResult(expected, true, "colorRank");
        }
        {
            //set to attribute "colorRank" descending

            final ArrayList<String> expected = new ArrayList<String>();
            expected.add("testnode4");
            expected.add("testnode2");
            expected.add("testnode5");
            expected.add("testnode3");
            expected.add("testnode1");

            assertRankedNodeResult(expected, false, "colorRank");
        }
    }

    private void assertRankedNodeResult(final ArrayList<String> expected, final Boolean nodeRankOrderAscending,
                                        final String rankAttribute) {

        //default ranking should be node name ascending
        final ArrayList<ExecutionItem> commands = new ArrayList<ExecutionItem>();
        final testWorkflowCmdItem item = new testWorkflowCmdItem();
        item.type = "my-type";
        commands.add(item);
        final WorkflowImpl workflow = new WorkflowImpl(commands, 1, false,
            WorkflowStrategy.NODE_FIRST);
        final WorkflowExecutionItemImpl executionItem = new WorkflowExecutionItemImpl(workflow);
        final NodeFirstWorkflowStrategy strategy = new NodeFirstWorkflowStrategy(testFramework);
        final NodeSet nodeset = new NodeSet();
        nodeset.createInclude().setName(".*");
        final ExecutionContextImpl.Builder builder = new ExecutionContextImpl.Builder();
        if(null!=nodeRankOrderAscending){
            builder.nodeRankOrderAscending(nodeRankOrderAscending); //rank order
        }
        builder.nodeRankAttribute(rankAttribute); //rank attribute
        final com.dtolabs.rundeck.core.execution.ExecutionContext context =
            builder
                .frameworkProject(TEST_PROJECT)
                .user("user1")
                .nodeSelector(nodeset)
                .executionListener(new testListener())
                .framework(testFramework)
                .nodesFile(extResourcesfile2)
                .build();

        //setup testInterpreter for all command types
        final NodeStepExecutorService interpreterService = NodeStepExecutorService.getInstanceForFramework(
            testFramework);
        testInterpreter interpreterMock = new testInterpreter();
        testInterpreter failMock = new testInterpreter();
        failMock.shouldThrowException = true;
        interpreterService.registerInstance("my-type", interpreterMock);
        interpreterService.registerInstance("exec", failMock);
        interpreterService.registerInstance("script", failMock);
        interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_NODE_FIRST, failMock);
        interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_STEP_FIRST, failMock);

        for (final String s : expected) {
            //set resturn result
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
        }

        final WorkflowExecutionResult result = strategy.executeWorkflow(context, executionItem);

        assertNotNull(result);
        if (!result.isSuccess() && null != result.getException()) {
            result.getException().printStackTrace(System.err);
        }
        assertNull("threw exception: " + result.getException(), result.getException());
        assertTrue(result.isSuccess());
        assertEquals(5, interpreterMock.executionItemList.size());
        assertEquals(5, interpreterMock.executionContextList.size());
        assertEquals(5, interpreterMock.nodeEntryList.size());
        ArrayList<String> tested = new ArrayList<String>();
        for (final INodeEntry iNodeEntry : interpreterMock.nodeEntryList) {
            tested.add(iNodeEntry.getNodename());
        }
        assertEquals(expected, tested);
    }

    public void testMultipleItemsAndNodes() {

        {
            //test jobref item
            final NodeSet nodeset = new NodeSet();
            nodeset.createInclude().setName(".*");
            final ArrayList<ExecutionItem> commands = new ArrayList<ExecutionItem>();
            final testWorkflowCmdItem item = new testWorkflowCmdItem();
            item.flag = 0;
            item.type = "my-type";
            commands.add(item);
            final testWorkflowCmdItem item2 = new testWorkflowCmdItem();
            item2.flag = 1;
            item2.type = "my-type";
            commands.add(item2);
            final WorkflowImpl workflow = new WorkflowImpl(commands, 1, false,
                WorkflowStrategy.STEP_FIRST);
            final WorkflowExecutionItemImpl executionItem = new WorkflowExecutionItemImpl(workflow);
            final NodeFirstWorkflowStrategy strategy = new NodeFirstWorkflowStrategy(testFramework);
            final com.dtolabs.rundeck.core.execution.ExecutionContext context =
                new ExecutionContextImpl.Builder()
                    .frameworkProject(TEST_PROJECT)
                    .user("user1")
                    .nodeSelector(nodeset)
                    .executionListener(new testListener())
                    .framework(testFramework)
                    .build();

            //setup testInterpreter for all command types
            final NodeStepExecutorService interpreterService = NodeStepExecutorService.getInstanceForFramework(
                testFramework);
            testInterpreter interpreterMock = new testInterpreter();
            testInterpreter failMock = new testInterpreter();
            failMock.shouldThrowException = true;
            interpreterService.registerInstance("my-type", interpreterMock);
            interpreterService.registerInstance("exec", failMock);
            interpreterService.registerInstance("script", failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_NODE_FIRST, failMock);
            interpreterService.registerInstance(WorkflowExecutionItem.COMMAND_TYPE_STEP_FIRST, failMock);

            //set resturn result node 1 step 1
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 2 step 1
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 1 step 2
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });
            //set resturn result node 2 step 2
            interpreterMock.resultList.add(new NodeStepResult() {
                public boolean isSuccess() {
                    return true;
                }
            });

            final WorkflowExecutionResult result = strategy.executeWorkflow(context, executionItem);

            assertNotNull(result);
            if (!result.isSuccess() && null != result.getException()) {
                result.getException().printStackTrace(System.err);
            }
            assertNull("threw exception: " + result.getException(), result.getException());
            assertTrue(result.isSuccess());
            assertEquals(4, interpreterMock.executionItemList.size());
            assertEquals(4, interpreterMock.executionContextList.size());
            {//node 1 step 1
                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(0);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());
                assertEquals(0, execItem.flag);

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(0);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("test1"), executionContext.getNodeSelector());
            }
            {//node 2 step 1

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(1);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());
                assertEquals(1, execItem.flag);

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(1);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("test1"), executionContext.getNodeSelector());
            }
            {//node 1 step 2

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(2);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());
                assertEquals(0, execItem.flag);

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(2);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("testnode2"), executionContext.getNodeSelector());
            }
            {//node 2 step 2

                final ExecutionItem executionItem1 = interpreterMock.executionItemList.get(3);
                assertTrue("wrong class: " + executionItem1.getClass().getName(),
                    executionItem1 instanceof testWorkflowCmdItem);
                testWorkflowCmdItem execItem = (testWorkflowCmdItem) executionItem1;
                assertNotNull(execItem.getType());
                assertEquals("my-type", execItem.getType());
                assertEquals(1, execItem.flag);

                final ExecutionContext executionContext = interpreterMock.executionContextList.get(3);
                assertEquals(TEST_PROJECT, executionContext.getFrameworkProject());
                assertNull(executionContext.getArgs());
                assertNull(executionContext.getDataContext());
                assertEquals(0, executionContext.getLoglevel());
                assertEquals("user1", executionContext.getUser());
                assertEquals(SelectorUtils.singleNode("testnode2"), executionContext.getNodeSelector());
            }
        }
    }
}
