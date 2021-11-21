import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {

        final File parent = new File(".");

        final File[] originalFiles = parent.listFiles();

        if (originalFiles == null) {
            return;
        }

        final List<File> files = new ArrayList<>();

        for (final File file : originalFiles) {
            if (!file.getName().endsWith(".dat")) {
                continue;
            }

            files.add(file);
        }

        final File f = new File("output.txt");

        if (!f.exists()) {
            f.createNewFile();
        }

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {

            final StringBuilder output = new StringBuilder();

            for (final File file : files) {

                final NBTCompound compound = NBTReader.readFile(file);

                output.append("UUID:").append(file.getName().replace(".dat", "")).append("\t");

                output.append("date:").append(file.lastModified()).append("\t");

                final var entrySet = compound.entrySet();

                appendKeys(output,
                        List.of(
                                "Pos",
                                "Dimension",
                                "SpawnX",
                                "SpawnY",
                                "SpawnZ",
                                "SpawnDimension",
                                "enteredNetherPosition",
                                "XpLevel",
                                "XpP"
                        ),
                        entrySet);

                output.append("\n");
            }
            writer.write(output.toString());

        } catch (
                final IOException exception) {
            exception.printStackTrace();
        }

        System.out.println("Transferred data from: " + files.size() + " to output.txt");
    }


    private static void appendKeys(
            final StringBuilder builder,
            final Collection<String> keys,
            final Collection<Map.Entry<String, Object>> entries) {
        for (final String key : keys) {
            builder.append(key).append(":");

            for (final var entry : entries) {
                final String k = entry.getKey();

                if (key.equalsIgnoreCase(k)) {

                    if (key.equalsIgnoreCase("Pos")) {
                        final String[] array = entry.
                                getValue().
                                toString().
                                replaceAll("[\\[\\] ]", "").
                                split(",");

                        try {

                            final int builderLength = builder.length();
                            final int posLength = "Pos:".length();

                            builder.delete(builderLength - posLength, builderLength);

                            builder.append("PosX:").append(array[0]).append("\t");
                            builder.append("PosY:").append(array[1]).append("\t");
                            builder.append("PosZ:").append(array[2]);

                        } catch (final IndexOutOfBoundsException ignored) {}
                    } else {
                        builder.append(entry.getValue());
                    }
                    break;
                }
            }

            builder.append("\t");
        }
    }
}
