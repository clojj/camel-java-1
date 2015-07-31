package de.jwin;

import java.util.ArrayList;
import java.util.List;

public class PojoSplitter {

    public List splitBody(SimplePojo simplePojo) {
        List<Object> list = new ArrayList<>();
        list.add(simplePojo.getContent());
        list.add(simplePojo.getL());
        return list;
    }
}
