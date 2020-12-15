package com.dineshkrish.nerapplication.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dineshkrish.nerapplication.model.JiraTicket;
import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/api/v1")
public class JiraTicketController {

	@GetMapping(value = "/jiraticket/{jiraId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JiraTicket> getJiraTicket(@PathVariable("jiraId") String jiraId) throws IOException {
		List<JiraTicket> jiraTicketList = readAllJiraTicketJsonFile();
		List<JiraTicket> copyJiraTicket = null;
		if (jiraTicketList != null) {
			copyJiraTicket = new ArrayList<>(jiraTicketList);
		}
		for (JiraTicket ticket : copyJiraTicket) {
			if (ticket.getJiraId().equalsIgnoreCase(jiraId)) {
				return ResponseEntity.status(HttpStatus.OK).body(ticket);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@PostMapping(value = "/jiraticket", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createJiraTicket(@RequestBody JiraTicket jiraTicket) throws IOException {
		int random_int = (int) (Math.random() * (999999 - 111111 + 1) + 111111);
		String jiraId = "JT" + random_int;
		jiraTicket.setJiraId(jiraId);
		// read json file to find all existing records
		List<JiraTicket> jiraTicketList = readAllJiraTicketJsonFile();
		List<JiraTicket> copyJiraTicket = null;
		if (jiraTicketList != null) {
			copyJiraTicket = new ArrayList<>(jiraTicketList);
			copyJiraTicket.add(jiraTicket);
		}
		writeToJsonFile(copyJiraTicket);
		return ResponseEntity.status(HttpStatus.OK).body(jiraId);
	}

	@PutMapping(value = "/jiraticket/{jiraId}")
	public ResponseEntity<String> updateJiraTicket(@PathVariable("jiraId") String jiraId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "module", required = false) String module,
			@RequestParam(value = "severity", required = false) String severity,
			@RequestParam(value = "description", required = false) String description) throws IOException {

		List<JiraTicket> jiraTicketList = readAllJiraTicketJsonFile();
		List<JiraTicket> copyJiraTicket = null;
		if (jiraTicketList != null) {
			copyJiraTicket = new ArrayList<>(jiraTicketList);
		}
		for (JiraTicket ticket : copyJiraTicket) {
			if (ticket.getJiraId().equalsIgnoreCase(jiraId)) {
				if (title != null && !"null".equals(title))
					ticket.setTitle(title);
				if (module != null && !"null".equals(module))
					ticket.setModule(module);
				if (severity != null && !"null".equals(severity))
					ticket.setSeverity(severity);
				if (description != null && !"null".equals(description))
					ticket.setDescription(description);
			}
		}
		writeToJsonFile(copyJiraTicket);
		return ResponseEntity.status(HttpStatus.OK).body(jiraId);
	}

	private static List<JiraTicket> readAllJiraTicketJsonFile() throws IOException {

		String text = "";
		Gson gson = new Gson();
		ClassPathResource classPathResource = new ClassPathResource("jiraTicket.json");
		try {
			byte[] binaryData = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
			text = new String(binaryData, StandardCharsets.UTF_8);
			JiraTicket[] data = gson.fromJson(text, JiraTicket[].class);
			List<JiraTicket> orders = Arrays.asList(data);
			return orders;
		} catch (IOException e) {

		}
		return null;
	}

	private static void writeToJsonFile(List<JiraTicket> copyJiraTicket) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(bos);
	    oos.writeObject(copyJiraTicket);
	    byte[] bytes = bos.toByteArray();
		ClassPathResource classPathResource = new ClassPathResource("jiraTicket.json");
		FileCopyUtils.copy(bytes,classPathResource.getFile());
		
		oos.flush(); // flush data to file
		oos.close(); // close write
		bos.close();
	}
}
