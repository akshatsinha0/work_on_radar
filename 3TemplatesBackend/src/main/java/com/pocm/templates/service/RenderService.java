package com.pocm.templates.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class RenderService {
  private final ObjectMapper mapper = new ObjectMapper();
  private final Handlebars handlebars = new Handlebars();

  public String renderHandlebars(String raw, Map<String,Object> variables) throws IOException {
    Template t = handlebars.compileInline(raw);
    return t.apply(variables);
  }
  public String renderLiquid(String raw, Map<String,Object> variables){
    try {
      return renderHandlebars(raw, variables);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // MJML compilation strategy: call external mjml CLI if available, else pass-through
  public String compileMjmlToHtml(String mjml){
    // right n putting a placeholder but in dev basically we
        // **can run mjml via node container or local CLI.
    //for now return mjml as-is. Leaving to integrate a wrapper later.
    return mjml;
  }

  public void validateVariablesAgainstSchema(JsonNode schema, Map<String,Object> variables){
    if(schema!=null && schema.has("required")){
      for(JsonNode req : schema.get("required")){
        if(!variables.containsKey(req.asText())){
          throw new IllegalArgumentException("Missing required variable: "+req.asText());
        }
      }
    }
  }

  public JsonNode parseJson(String json){
    try { return mapper.readTree(json); } catch (Exception e){ throw new IllegalArgumentException("Invalid JSON", e); }
  }
}
