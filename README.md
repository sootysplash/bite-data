# Bite Data

A small library specifically engineered to store Java primitive types (boolean, int, float, double, long, byte, char, short, null, CharSequence) as efficiently as possible

There are two main objects for storing data, the BiteMap (CharSequence key linked to a Primitive type) And the BiteArray (Primitive types that retain their order after being saved and loaded)

BiteMaps and BiteArrays can be nested inside each other, allowing for storage of complex objects with ease

Since data is stored as bytes and not as a string (cough gson), the only limit on how much data you can store is the Java heap

### Key Optimisations

- BiteMaps store every entry in [CharSequenceBytes, KeyByteForObject, ObjectBytes] form, BiteArrays store every object in [KeyByteForObject, ObjectBytes] form

- Numbers are automatically compressed if they will retain their value as a smaller a number (a double[8 Bytes] with a value of 110.0 will automatically be stored as a byte[1 byte]), all numbers can still be read as whatever primitive desired

- Booleans are stored in one of the three empty bits in the KeyByteForObject, saving a byte per Boolean

- The amount of Bytes required to read a nested BiteMap/Array length are stored in the empty bits of the KeyByteForObject, saving up to three Bytes per nested object

- Nulls don't require any bytes to store

## Usage

### Example
```
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
        map.add(null, (Number) null);

        // "true"
        System.out.println(map.get(null).isNull());

        BiteArray array = BiteArray.newInstance();

        map.add("array-value", array);

        array.add("sequence");
        array.add("of");
        array.add("words");
        array.add("in");
        array.add("order");
        array.add((Number) null);
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
```

### Building from source
- Clone this repository
- Run `mvn package`

### Contributing
Issue/Bug reports are very welcome, feature suggestions will be handled with a lower priority

Pull requests should aim to modify and fix existing code rather than adding new features

### Licensing
This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)