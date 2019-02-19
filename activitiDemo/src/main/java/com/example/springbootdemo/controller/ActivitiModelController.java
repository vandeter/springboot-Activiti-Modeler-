package com.example.springbootdemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class ActivitiModelController {
    private static final Logger log = LogManager.getLogger(ActivitiModelController.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    /**
     * 新建一个空模型
     */
    @RequestMapping("/create")
    public void newModel(HttpServletResponse response) throws IOException {
        //RepositoryService repositoryService = processEngine.getRepositoryService();
        //初始化一个空模型
        Model model = repositoryService.newModel();
        //设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String key = "process";
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);
        String id = model.getId();
        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace",                "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(id,editorNode.toString().getBytes("utf-8"));
        response.sendRedirect("/modeler.html?modelId="+id);    }
        /**     * 获取所有模型     */
        @RequestMapping("/modelList")
        @ResponseBody
        public Object modelList(){
            //RepositoryService repositoryService = processEngine.getRepositoryService();

            return repositoryService.createModelQuery().list();
        }
        /**     * 发布模型为流程定义     */
        @RequestMapping("/deploy")
        @ResponseBody
        public Object deploy(String modelId) throws Exception {
            //获取模型
            //RepositoryService repositoryService = processEngine.getRepositoryService();
            Model modelData = repositoryService.getModel(modelId);
            byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
            if (bytes == null) {
                return "模型数据为空，请先设计流程并成功保存，再进行发布。";
            }
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if(model.getProcesses().size()==0){
                return "数据模型不符要求，请至少设计一条主线流程。";
            }
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            //发布流程
             String processName = modelData.getName() + ".bpmn20.xml";
             Deployment deployment = repositoryService.createDeployment()
                     .name(modelData.getName())
                     .addString(processName, new String(bpmnBytes, "UTF-8"))
                     .deploy();
             modelData.setDeploymentId(deployment.getId());
             repositoryService.saveModel(modelData);
             return "SUCCESS";
        }     /**     *  启动流程     */
        @RequestMapping("/start")
        @ResponseBody
        public Object startProcess(String keyName) {
            ProcessInstance process = runtimeService.startProcessInstanceByKey(keyName);
            return process.getId() + " : " + process.getProcessDefinitionId();
        }
        /**     *  提交任务     */
        @RequestMapping("/run")
        @ResponseBody
        public Object run(String processInstanceId) {
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            //log.info("task {} find ",task.getId());
            taskService.complete(task.getId());
            return "SUCCESS";
        }
}
