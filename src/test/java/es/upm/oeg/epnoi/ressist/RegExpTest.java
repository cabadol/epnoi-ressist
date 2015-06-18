package es.upm.oeg.epnoi.ressist;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by cbadenes on 18/06/15.
 */
public class RegExpTest {


    @Test
    public void split(){
        String creator = "Luque López, Antonio";

        String[] tokens = creator.split(",");
        String name = tokens[1].trim();
        String surname = tokens[0];

        Assert.assertEquals("Antonio",name);
        Assert.assertEquals("Luque López",surname);

    }
}
