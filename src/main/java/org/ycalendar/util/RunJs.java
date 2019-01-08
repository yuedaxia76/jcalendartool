/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ycalendar.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
 

/**
 *
 * @author lenovo
 */
public class RunJs {

    public static final Logger log = LoggerFactory.getLogger(RunJs.class);
    private static final ScriptEngineManager manager = new ScriptEngineManager();

    public static Object evaluate(final String script, Map<String, Object> context) throws Exception {
        if (UtilValidate.isEmpty(script)) {
            log.warn("script Evaluation error. Empty script");
            return null;
        }
        try {

            ScriptEngine engine = manager.getEngineByName("JavaScript");

            ScriptContext scriptContext = createScriptContext(context);
            return engine.eval(script, scriptContext);
        } catch (Exception e) {
            String errMsg = "Error running JavaScript script [" + script + "]: " + e.toString();
            log.error( errMsg, e);

            throw new IllegalArgumentException(errMsg);
        }
    }

    public static ScriptContext createScriptContext(Map<String, Object> context) {
        if (context == null) {
            context = new HashMap<String, Object>();
        }
        context.put("jscontext", context);
        ScriptContext scriptContext = new SimpleScriptContext();

        Bindings bindings = new SimpleBindings(context);
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        return scriptContext;
    }
}
