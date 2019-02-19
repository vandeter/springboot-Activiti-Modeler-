package com.example.springbootdemo.controller;
import com.example.springbootdemo.model.Item;
import com.example.springbootdemo.service.ItemService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @RequestMapping("/test")
    public String Test(){
        return "hello";
    }
    @RequestMapping("/DBTest")
    public Map<String,Object> DBTest(int id){
        Map<String, Object> map = new HashMap<>();
        try {
            Item item = itemService.selectByPrimaryKey(id);
            map.put("item",item);
            map.put("result",1);
            map.put("message","操作成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("result",0);
            map.put("message","操作失败");
            System.out.println("操作失败");
        }
        return map;
    }
    @RequestMapping("/deployment")
    public String deployment() throws Exception{
        Map<Object, Object> result = new HashMap<Object, Object>();
            Deployment deployment = repositoryService.createDeployment().name("请假流程").addClasspathResource("processes/LeaveBill.bpmn").deploy();
            System.out.println("部署Id" + deployment.getId());
        return deployment.getId();
    }
    @RequestMapping("/findProcessDefination")
    public List findProcessDefination(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId("1").list();
        return list;
    }
    @RequestMapping("/startProcess")
    public String startProcess(){
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("LeaveBill");
        System.out.println("流程实例Id" + pi.getId());
        return pi.getId();
    }
    @RequestMapping("/findUserTask")
    public void findUserTask(){
        String assigne = "李四";
         Task task = taskService.createTaskQuery().taskAssignee(assigne).singleResult();
        System.out.println(task.getId()+"有任务");
    }
    @RequestMapping("/complteTask")
    public void complteTask(){
        String taskId = "5002";
        taskService.complete(taskId);
        System.out.println("完成任务");
    }
}
