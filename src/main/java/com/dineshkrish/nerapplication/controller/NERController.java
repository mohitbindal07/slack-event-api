package com.dineshkrish.nerapplication.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dineshkrish.nerapplication.model.Type;
import com.dineshkrish.nerapplication.model.WorkOrder;
import com.dineshkrish.nerapplication.model.WorkOrderFQN;
import com.dineshkrish.nerapplication.model.WorkOrderList;
import com.google.gson.Gson;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

@RestController
@RequestMapping(value = "/api/v1")
public class NERController {

   // @Autowired
   // private StanfordCoreNLP stanfordCoreNLP;

   /* @PostMapping
    @RequestMapping(value = "/ner")
    public Set<String> ner(@RequestBody final String input, @RequestParam final Type type) {
        CoreDocument coreDocument = new CoreDocument(input);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        return new HashSet<>(collectList(coreLabels, type));
    }*/

    private List<String> collectList(List<CoreLabel> coreLabels, final Type type) {

        return coreLabels
                .stream()
                .filter(coreLabel -> type.getName().equalsIgnoreCase(coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class)))
                .map(CoreLabel::originalText)
                .collect(Collectors.toList()); 
    }
    
    @GetMapping(value = "/workorder/{orderId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkOrder> workOrderDetail(@PathVariable("orderId") String orderId) throws IOException {
    	return ResponseEntity.status(HttpStatus.OK).body(readWorkOrderJsonFile(orderId));
    }
    
    @GetMapping(value = "/workorder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkOrderList> allWorkOrder() throws IOException {
    	return ResponseEntity.status(HttpStatus.OK).body(readAllWorkOrderJsonFile());
    }
	
	 
    public static void suTime(List<String> texts) {
    	Properties props = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        for (String text : texts) {
          Annotation annotation = new Annotation(text);
          annotation.set(CoreAnnotations.DocDateAnnotation.class, "2020-12-03");
          pipeline.annotate(annotation);
          System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));
          List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
          for (CoreMap cm : timexAnnsAll) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println(cm + " [from char offset " +
                tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) +
                " to " + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']' +
                " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
          }
          System.out.println("--");
        }
    }
    
    public static void generateWorkOrderJsonFile() throws IOException {
    	WorkOrderFQN fqn1 = new WorkOrderFQN();
		fqn1.setLength("210");
		fqn1.setStatus("underconstruction");
		
		WorkOrderFQN fqn2 = new WorkOrderFQN();
		fqn2.setLength("110");
		fqn2.setStatus("not started");
		
		WorkOrderFQN fqn3 = new WorkOrderFQN();
		fqn3.setLength("510");
		fqn3.setStatus("testing");
		
		WorkOrderFQN fqn4 = new WorkOrderFQN();
		fqn4.setLength("220");
		fqn4.setStatus("construction started");
		
		List<WorkOrderFQN> fqnList = new ArrayList<WorkOrderFQN>();
		fqnList.add(fqn1);
		fqnList.add(fqn2);
		fqnList.add(fqn3);
		fqnList.add(fqn4);
		
		WorkOrder workOrder = new WorkOrder("679860","open","Rahul",fqnList);
		WorkOrder workOrder2 = new WorkOrder("679861","inprogress","Rohit",fqnList);
		WorkOrder workOrder3 = new WorkOrder("679862","completed","Akshay",fqnList);
		WorkOrder workOrder4 = new WorkOrder("679863","open","Deepanshu",fqnList);
		WorkOrder workOrder5 = new WorkOrder("679864","completed","Karan",fqnList);
		List<WorkOrder>  workOrders = new ArrayList<WorkOrder>();
		workOrders.add(workOrder);
		workOrders.add(workOrder2);
		workOrders.add(workOrder3);
		workOrders.add(workOrder4);
		workOrders.add(workOrder5);
		
		Writer writer = new FileWriter("D:\\altran\\workOrderDetail.json");	
		new Gson().toJson(workOrders,writer);
		writer.flush(); //flush data to file   <---
        writer.close(); //close write          <---
    }
    
    public static WorkOrder readWorkOrderJsonFile(String orderId) throws IOException {
    	
    	Gson gson = new Gson();
    	File resource = new ClassPathResource("workOrderDetail.json").getFile();
		String text = new String(Files.readAllBytes(resource.toPath()));
    	WorkOrder[] data = gson.fromJson(text, WorkOrder[].class);
    	List<WorkOrder> orders = Arrays.asList(data);
    	for(WorkOrder order: orders) {
    		if(orderId.equals(order.getWorkOrderId())){
    			return order;
    		}
    	}
    	return null;
    }
    
    public static WorkOrderList readAllWorkOrderJsonFile() throws IOException {
    	
    	Gson gson = new Gson();
    	File resource = new ClassPathResource("workOrderDetail.json").getFile();
		String text = new String(Files.readAllBytes(resource.toPath()));
    	WorkOrder[] data = gson.fromJson(text, WorkOrder[].class);
    	List<WorkOrder> orders = Arrays.asList(data);
    	WorkOrderList workOrderList = new WorkOrderList();
    	workOrderList.setWorkorders(orders);
    	return workOrderList;
    }
}