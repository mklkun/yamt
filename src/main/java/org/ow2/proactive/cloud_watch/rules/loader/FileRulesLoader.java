package org.ow2.proactive.cloud_watch.rules.loader;


//import com.aol.micro.server.rest.jackson.JacksonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jersey.repackaged.com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.ow2.proactive.cloud_watch.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

@Component
public class FileRulesLoader implements RulesLoader {

	private final Logger logger = LogManager.getRootLogger();
	private final String rulesFilePath;
	private final JSONParser parser;

	@Autowired
	public FileRulesLoader(@Value("${rules.file.path}") String rulesFilePath) {
		this.rulesFilePath = rulesFilePath;
		this.parser = new JSONParser();
	}

	@Override
	public Set<Rule> load() {
		ObjectMapper mapper = new ObjectMapper();

		Set<Rule> rules = Sets.newHashSet();

		JSONArray allRules;
		try {
			allRules = (JSONArray) parser.parse(new FileReader(loadFile()));
			Iterator<JSONObject> loadedRules = allRules.iterator();
			loadedRules.forEachRemaining(rule -> {
				try {
					rules.add(mapper.readValue(rule.toString(), Rule.class));
				} catch (IOException e) {
					throw new RuntimeException("Error while loading the rules", e);
				}
			});

		} catch (IOException | ParseException e) {
			throw new RuntimeException("Error while loading the rules", e);
		}

		logger.info("All rules have been loaded successfully ");
		return rules;
	}

	private File loadFile() {
		return new File(rulesFilePath);
	}

}