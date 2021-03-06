package dev.laarryy.clippyv2.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyValStorage {

    private final Yaml yaml = new Yaml();
    private final LogStorage logStore = new LogStorage();
    private static final Logger logger = LoggerFactory.getLogger(KeyValStorage.class);

    private Map<String, Object> kvMap;
    private final File kvFile;

    public KeyValStorage(String path) {
        kvFile = new File(path);
        loadYaml();
        logStore.loadLog();
    }

    @SuppressWarnings("unchecked")
    protected synchronized void loadYaml() {
        try {
            kvFile.createNewFile();
            kvMap = yaml.load(new FileReader(kvFile));
        } catch (IOException e) {
            logger.error("Could not load key-value file!", e);
        }

        if (kvMap == null) kvMap = new HashMap<String, Object>();

    }

    protected synchronized void saveYaml() {
        try {
            yaml.dump(kvMap, new FileWriter(kvFile));
        } catch (IOException e) {
            logger.error("Could not save key-value file!", e);
        }
    }

    public boolean exists(String key) {
        return kvMap.containsKey(key);
    }

    public boolean existsValue(Object value) {
        return kvMap.containsValue(value);
    }

    protected boolean set(String key, Object value) {
        boolean hasKey = exists(key);
        kvMap.put(key, value);
        return hasKey;
    }

    protected void unset(String key) {
        kvMap.remove(key);
    }


    public Object get(String key) {
        return kvMap.get(key);
    }

    public Map<String, Object> getMap() {
        return Collections.unmodifiableMap(kvMap);
    }

}
