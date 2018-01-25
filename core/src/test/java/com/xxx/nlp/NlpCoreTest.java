package com.xxx.nlp;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * Unit test for simple App.
 */
public class NlpCoreTest
        extends TestCase {

    @Test
    public void testNlpParser() {
        try {
            StanfordCoreNLP.main(new String[]{//
                    "-outputFormat", "json", //
                    "-file", "input.txt",//
                    "-props", "debug-chinese.properties"//
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChinese() throws IOException {
        String text = "克林顿说，华盛顿将逐步落实对韩国的经济援助。"
                + "金大中对克林顿的讲话报以掌声：克林顿总统在会谈中重申，他坚定地支持韩国摆脱经济危机。";
        Annotation document = new Annotation(text);
        // Setup Chinese Properties by loading them from classpath resources
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("debug-chinese.properties"));
        // Or this way of doing it also works
        // Properties props = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-chinese.properties"});
        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);
    }
}
