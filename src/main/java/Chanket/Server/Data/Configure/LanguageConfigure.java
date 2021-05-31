package Chanket.Server.Data.Configure;

import Chanket.Server.Common.Utils.IOReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@PropertySource({"classpath:chanket.properties"})
public class LanguageConfigure {

    private Resource[] source;

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
        try {
            // Source boot mode
            if(!new ClassPathResource("static/languages").exists() || !new ClassPathResource("static/languages").getFile().isDirectory()) {
                log.warn("Cannot load language directory, please check build-in language directory is not file! (Maybe not found language directory), next we will boot jar load mode!");
                throw new NullPointerException("Cannot load language, swap to jar load mode!");
            }

            for(File lang : new File(new ClassPathResource("static/languages").getURI().getPath()).listFiles()) {

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
        }

        catch(NullPointerException ignored) {
            // Jar load mode
            log.warn("Use source load mode cannot load language files. Using jar load mode!");
            source = new PathMatchingResourcePatternResolver().getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "BOOT-INF/**/*.language.json");

            for(Resource resource : source) {
                String name = resource.getFilename().substring(0, resource.getFilename().length() - 14);
                StringBuffer dbuffer = new StringBuffer();

                log.info("Now using jar load mode loading: " + name);

                // Load data from input stream
                try(InputStreamReader isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    BufferedReader bufferReader = new BufferedReader(isr)) {
                    String bstring;
                    while((bstring = bufferReader.readLine()) != null) {
                        dbuffer.append(bstring).append("\n");
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }

                // Commit data to language map
                i18nS.put(name, new JSONObject(dbuffer.toString()));

            // Save default language
                if(name.equals(selected)) i18n = new JSONObject(dbuffer.toString());
            }

            if(i18n == null) {
                log.error("Cannot read select language file(" + selected + "), please read README.md to check this language is a available language!");
                return null;
            }

            // Else return i18n
            return i18n;
        }

        // If that cycle didn't process target language, so selected language is not support
        // No support language process logic
        log.error("Cannot read select language file(" + selected + "), please read README.md to check this language is a available language!");
        return null;
    }
}
