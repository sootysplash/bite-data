package me.sootysplash.bite.test;

import me.sootysplash.bite.BiteArray;
import me.sootysplash.bite.BiteMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        BiteMap map = BiteMap.newInstance();
        map.add("value behind key", 25);
        map.addCs(null, null);

        // "true"
        System.out.println(map.get(null).isNull());

        BiteArray array = BiteArray.newInstance();

        map.add("array-value", array);

        array.addCs("sequence");
        array.addCs("of");
        array.addCs("words");
        array.addCs("in");
        array.addCs("order");
        array.addCs(null);
        array.add(false);

        // this byte array can be stored to and loaded from anywhere, not just files
        byte[] data = map.getBytes();

        File configFile = new File("saved-data");
        configFile.deleteOnExit();
        Files.write(configFile.toPath(), data);

        byte[] readData = Files.readAllBytes(configFile.toPath());

        // "81", all that data is stored in just 81 bytes
        System.out.println(readData.length);

        BiteMap readFromBytes = BiteMap.fromBytes(readData);

        assert readFromBytes.get("value behind key").getInteger() == 25;

        if (!map.get("array-value").isArray()) {
            return;
        }
        BiteArray fromMap = map.get("array-value").getArray();

        List<String> charSequences = fromMap.asList().stream().filter(typeObject -> typeObject.isCharSequence()).map(typeObject -> typeObject.getCharSequence().toString()).collect(Collectors.toList());
        // "[sequence, of, words, in, order]"
        System.out.print(charSequences);
    }
}