package scanner;
import java.util.regex.Pattern;

public class Key {
    // attributes
    private String keyFor;
    private Pattern expression;

    // getters and setters
    public String getKeyFor(){
        return this.keyFor;
    }

    public Pattern getExpression(){
        return this.expression;
    }

    public void setKeyFor(String name){
        this.keyFor = name;
    }

    public void setExpression(String regex){
        this.expression = Pattern.compile(regex);
    }

    // init method
    public Key(String keyname, String regex){
        this.setKeyFor(keyname);
        this.setExpression(regex);
    }
}
