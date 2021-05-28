package Chanket.Server.Data.Configure;

import Chanket.Server.Common.Utils.IOReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@PropertySource({"classpath:chanket.properties"})
public class LanguageConfigure {

    private JSONObject i18n;
    private Map<String, JSONObject> i18nS = new HashMap<>();

    @Value("${chanket.source.language}")
    private String selected;

    // Get default language function(i18n JSONObject)
    public JSONObject defaultLanguage() {
        if(i18n == null) return init();
        else return i18n;
    }

    // Get target language function
    public JSONObject language(String name) {
        if(i18nS.containsKey(name)) return i18nS.get(name);

        if(i18nS.isEmpty()) {
            init();
            return i18nS.getOrDefault(name, null);
        }

        return null;
    }

    @SneakyThrows
    private JSONObject init() {

        // Load languages
        log.info("Loading languages...");

        // Language directory is file or not found
        if(!new ClassPathResource("languages").exists() || !new ClassPathResource("languages").getFile().isDirectory()) {
            log.error("Cannot load language directory, please check build-in language directory is not file! (Maybe not found language directory)");
            return null;
        }

        for(File lang : new ClassPathResource("languages").getFile().listFiles()) {

            // Change to absolute file
            lang = lang.getAbsoluteFile();

            String file = lang.getName().substring(0, lang.getName().length() - 5);

            log.info("Now loading language: " + file);
            i18nS.put(file, new JSONObject(IOReader.read(new FileInputStream(lang))));

            if(selected.equals(file)) {

                log.info("Load language completed! Selected language: " + selected);
                i18n = new JSONObject(IOReader.read(new FileInputStream(lang)));
                return i18n;
            }
        }

        // If that cycle didn't process target language, so selected language is not support
        // No support language process logic
        log.error("Cannot read select language file(" + selected + "), please read README.md to check this language is a available language!");
        return null;
    }
}
