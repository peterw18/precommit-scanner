package scanner;
import java.util.regex.Pattern;

public class Key {
    // immutable attributes
    private String keyFor;
    private Pattern expression;

    // init
    public Key(String keyname, String regex){
        this.keyFor = keyname;
        this.expression = Pattern.compile(regex);
    }

    // getters
    public String getKeyFor(){
        return this.keyFor;
    }

    public Pattern getExpression(){
        return this.expression;
    }
}
