package de.jwin;

import java.util.ArrayList;
import java.util.List;

public class PojoCreator {

    public List<Object> process(String body) {
        String lines[] = body.split("\\r?\\n");
        // todo java8 stream

        List<Object> list = new ArrayList<>();
        for (String line : lines) {
            list.add(new SimplePojo(line, 12345L));
        }
        return list;
    }
}
