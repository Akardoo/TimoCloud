package cloud.timo.TimoCloud.cord.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class CordFileManager {
    private File baseDirectory;
    private File configsDirectory;
    private File configFile;
    private Map<String, Object> config;

    public CordFileManager() {
        load();
    }

    public void load() {
        try {
            baseDirectory = new File("cord/");
            baseDirectory.mkdirs();
            configsDirectory = new File(baseDirectory, "configs/");
            configsDirectory.mkdirs();

            this.configFile = new File(getConfigsDirectory(), "config.yml");
            configFile.createNewFile();
            Yaml yaml = new Yaml();
            this.config = (Map<String, Object>) yaml.load(new FileReader(configFile));
            if (this.config == null) this.config = new HashMap<>();
            Map<String, Object> defaults = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/cord/config.yml"));
            for (String key : defaults.keySet()) {
                if (! config.containsKey(key)) config.put(key, defaults.get(key));
            }
            saveConfig();
        } catch (Exception e) {
            TimoCloudCord.getInstance().severe(e);
        }
    }

    private void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configFile);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(dumperOptions).dump(config, writer);
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while saving config: ");
            TimoCloudCord.getInstance().severe(e);
        }
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }

    public File getConfigsDirectory() {
        return configsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }

}
