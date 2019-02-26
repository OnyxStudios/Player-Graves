package nerdhub.playergraves;

import nerdhub.playergraves.utils.YamlFile;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private YamlFile config;
    private Logger LOGGER = LogManager.getLogger("Player Graves");

    public Config() {
        config = new YamlFile(FabricLoader.getInstance().getConfigDirectory() + "/playergraves.yml");

        if(!config.exists()) {
            LOGGER.info("Creating a config file for Player Graves");

            InputStream defaultConfigFile = PlayerGraves.class.getClassLoader().getResourceAsStream("assets/playergraves/config.yml");
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(config.getConfigFile()))) {
                List<String> linesList = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(defaultConfigFile));

                while (reader.ready()) {
                    linesList.add(reader.readLine());
                }

                for (String string : linesList) {
                    printWriter.println(string);
                }

            } catch (IOException e) {
                LOGGER.fatal("You done borked something with your config", e);
            }
        }

        LOGGER.info("Loading the config file for Player Graves");
        config.load();
    }

    public boolean getBoolean(String name) {
        return getConfig().getBoolean(name);
    }

    public YamlFile getConfig() {
        return config;
    }
}
